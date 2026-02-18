package com.samsung.android.music.fragment;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.samsung.android.music.R;
import com.samsung.android.music.model.Song;
import com.samsung.android.music.repository.MusicRepository;
import com.samsung.android.music.service.MusicService;

import java.util.ArrayList;
import java.util.List;

/**
 * Library Fragment for Samsung Music Player
 * Features real music library browsing with One UI 10.0 design
 */
public class LibraryFragment extends Fragment {
    
    private ListView songListView;
    private ProgressBar loadingProgressBar;
    private TextView statusTextView;
    
    private MusicRepository musicRepository;
    private List<Song> allSongs = new ArrayList<>();
    private SongAdapter songAdapter;
    
    // Music service connection
    private MusicService musicService;
    private boolean isServiceBound = false;
    
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            isServiceBound = true;
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupMusicRepository();
        bindMusicService();
        checkPermissionsAndLoadMusic();
    }
    
    private void initializeViews(View view) {
        try {
            songListView = view.findViewById(R.id.song_list_view);
            loadingProgressBar = view.findViewById(R.id.loading_progress);
            statusTextView = view.findViewById(R.id.library_placeholder);
            
            // Setup song adapter
            songAdapter = new SongAdapter(requireContext(), allSongs);
            if (songListView != null) {
                songListView.setAdapter(songAdapter);
                songListView.setOnItemClickListener(this::onSongItemClick);
            }
            
        } catch (Exception e) {
            // Handle view initialization error
            if (statusTextView != null) {
                statusTextView.setText("Error initializing library view");
            }
        }
    }
    
    private void setupMusicRepository() {
        musicRepository = new MusicRepository(requireContext());
    }
    
    private void bindMusicService() {
        Intent intent = new Intent(requireContext(), MusicService.class);
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    
    private void checkPermissionsAndLoadMusic() {
        if (hasAudioPermissions()) {
            loadMusicLibrary();
        } else {
            showPermissionMessage();
        }
    }
    
    private boolean hasAudioPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 33) { // API 33 = Android 13
            return ContextCompat.checkSelfPermission(requireContext(), 
                "android.permission.READ_MEDIA_AUDIO") == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    private void showPermissionMessage() {
        if (statusTextView != null) {
            statusTextView.setText("Music Library\n\n" +
                "Permission Required\n\n" +
                "Please grant audio/storage permissions\n" +
                "to access your music files.\n\n" +
                "Go to Settings > Apps > Samsung Music Player > Permissions");
        }
        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(View.GONE);
        }
    }
    
    private void loadMusicLibrary() {
        if (statusTextView != null) {
            statusTextView.setText("Loading music library...");
        }
        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(View.VISIBLE);
        }
        
        // Load music in background thread
        new LoadMusicTask().execute();
    }
    
    private void onSongItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < allSongs.size()) {
            Song selectedSong = allSongs.get(position);
            playSong(selectedSong, position);
        }
    }
    
    private void playSong(Song song, int position) {
        if (isServiceBound && musicService != null) {
            // Set the entire playlist and start playing from selected song
            musicService.setPlaylist(allSongs, position);
            musicService.playSong(song);
            
            Toast.makeText(requireContext(), 
                "Playing: " + song.getTitle(), 
                Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), 
                "Music service not available", 
                Toast.LENGTH_SHORT).show();
        }
    }
    
    private class LoadMusicTask extends AsyncTask<Void, Void, List<Song>> {
        
        @Override
        protected List<Song> doInBackground(Void... voids) {
            try {
                return musicRepository.getAllSongs();
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }
        
        @Override
        protected void onPostExecute(List<Song> songs) {
            if (getActivity() == null) return; // Fragment might be destroyed
            
            allSongs.clear();
            allSongs.addAll(songs);
            
            if (loadingProgressBar != null) {
                loadingProgressBar.setVisibility(View.GONE);
            }
            
            if (songs.isEmpty()) {
                if (statusTextView != null) {
                    statusTextView.setText("Music Library\n\n" +
                        "No music files found\n\n" +
                        "Make sure you have music files\n" +
                        "stored on your device.\n\n" +
                        "Supported formats:\n" +
                        "• MP3\n• AAC\n• FLAC\n• OGG");
                }
            } else {
                if (statusTextView != null) {
                    statusTextView.setVisibility(View.GONE);
                }
                if (songListView != null) {
                    songListView.setVisibility(View.VISIBLE);
                    songAdapter.notifyDataSetChanged();
                }
                
                Toast.makeText(requireContext(), 
                    "Loaded " + songs.size() + " songs", 
                    Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * Custom adapter for displaying songs in the list
     */
    private static class SongAdapter extends ArrayAdapter<Song> {
        
        public SongAdapter(Context context, List<Song> songs) {
            super(context, android.R.layout.simple_list_item_2, songs);
        }
        
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            
            Song song = getItem(position);
            if (song != null) {
                TextView titleView = view.findViewById(android.R.id.text1);
                TextView subtitleView = view.findViewById(android.R.id.text2);
                
                if (titleView != null) {
                    titleView.setText(song.getTitle());
                    titleView.setTextColor(ContextCompat.getColor(getContext(), R.color.text_primary));
                }
                
                if (subtitleView != null) {
                    String subtitle = song.getArtist() + " • " + song.getFormattedDuration();
                    subtitleView.setText(subtitle);
                    subtitleView.setTextColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
                }
            }
            
            return view;
        }
    }
    
    public void refreshLibrary() {
        if (hasAudioPermissions()) {
            loadMusicLibrary();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isServiceBound) {
            requireContext().unbindService(serviceConnection);
            isServiceBound = false;
        }
    }
}
