package com.samsung.android.music.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sec.sesl.tester.R;

/**
 * Player Fragment for Samsung Music Player
 * Features glassy UI with One UI 10.0 design
 */
public class PlayerFragment extends Fragment {
    
    // UI Components
    private ImageView albumArtImageView;
    private TextView songTitleTextView;
    private TextView artistNameTextView;
    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private SeekBar seekBar;
    private ImageButton previousButton;
    private ImageButton playPauseButton;
    private ImageButton nextButton;
    private ImageButton shuffleButton;
    private ImageButton repeatButton;
    
    // State
    private boolean isPlaying = false;
    private boolean isShuffled = false;
    private int repeatMode = 0; // 0: off, 1: all, 2: one
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupClickListeners();
        setupSeekBar();
        
        // Initialize with demo data
        initializeDemoData();
    }
    
    private void initializeViews(View view) {
        albumArtImageView = view.findViewById(R.id.album_art);
        songTitleTextView = view.findViewById(R.id.song_title);
        artistNameTextView = view.findViewById(R.id.artist_name);
        currentTimeTextView = view.findViewById(R.id.current_time);
        totalTimeTextView = view.findViewById(R.id.total_time);
        seekBar = view.findViewById(R.id.seek_bar);
        previousButton = view.findViewById(R.id.btn_previous);
        playPauseButton = view.findViewById(R.id.btn_play_pause);
        nextButton = view.findViewById(R.id.btn_next);
        shuffleButton = view.findViewById(R.id.btn_shuffle);
        repeatButton = view.findViewById(R.id.btn_repeat);
    }
    
    private void setupClickListeners() {
        playPauseButton.setOnClickListener(v -> togglePlayPause());
        previousButton.setOnClickListener(v -> playPrevious());
        nextButton.setOnClickListener(v -> playNext());
        shuffleButton.setOnClickListener(v -> toggleShuffle());
        repeatButton.setOnClickListener(v -> toggleRepeat());
    }
    
    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Update current time display
                    updateCurrentTime(progress);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Pause updates while user is dragging
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Seek to new position
                seekToPosition(seekBar.getProgress());
            }
        });
    }
    
    private void initializeDemoData() {
        // Demo song data
        songTitleTextView.setText("Samsung Music Demo");
        artistNameTextView.setText("One UI 10.0");
        currentTimeTextView.setText("0:00");
        totalTimeTextView.setText("3:45");
        seekBar.setMax(225); // 3:45 in seconds
        seekBar.setProgress(0);
        
        // Set default album art
        albumArtImageView.setImageResource(R.drawable.default_album_art);
    }
    
    private void togglePlayPause() {
        isPlaying = !isPlaying;
        updatePlayPauseButton();
        
        if (isPlaying) {
            // Start playback
            startPlayback();
        } else {
            // Pause playback
            pausePlayback();
        }
    }
    
    private void updatePlayPauseButton() {
        if (isPlaying) {
            playPauseButton.setImageResource(R.drawable.ic_pause);
            playPauseButton.setContentDescription(getString(R.string.pause));
        } else {
            playPauseButton.setImageResource(R.drawable.ic_play);
            playPauseButton.setContentDescription(getString(R.string.play));
        }
    }
    
    private void playPrevious() {
        // Implement previous song logic
        // For demo, just reset to beginning
        seekBar.setProgress(0);
        currentTimeTextView.setText("0:00");
    }
    
    private void playNext() {
        // Implement next song logic
        // For demo, just go to end
        seekBar.setProgress(seekBar.getMax());
        currentTimeTextView.setText(totalTimeTextView.getText());
    }
    
    private void toggleShuffle() {
        isShuffled = !isShuffled;
        updateShuffleButton();
    }
    
    private void updateShuffleButton() {
        if (isShuffled) {
            shuffleButton.setImageResource(R.drawable.ic_shuffle_on);
            shuffleButton.setAlpha(1.0f);
        } else {
            shuffleButton.setImageResource(R.drawable.ic_shuffle);
            shuffleButton.setAlpha(0.6f);
        }
    }
    
    private void toggleRepeat() {
        repeatMode = (repeatMode + 1) % 3;
        updateRepeatButton();
    }
    
    private void updateRepeatButton() {
        switch (repeatMode) {
            case 0: // Off
                repeatButton.setImageResource(R.drawable.ic_repeat);
                repeatButton.setAlpha(0.6f);
                break;
            case 1: // All
                repeatButton.setImageResource(R.drawable.ic_repeat);
                repeatButton.setAlpha(1.0f);
                break;
            case 2: // One
                repeatButton.setImageResource(R.drawable.ic_repeat_one);
                repeatButton.setAlpha(1.0f);
                break;
        }
    }
    
    private void startPlayback() {
        // Implement actual playback logic here
        // For demo, just update UI
    }
    
    private void pausePlayback() {
        // Implement pause logic here
        // For demo, just update UI
    }
    
    private void seekToPosition(int position) {
        // Implement seek logic here
        updateCurrentTime(position);
    }
    
    private void updateCurrentTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        currentTimeTextView.setText(String.format("%d:%02d", minutes, secs));
    }
}
