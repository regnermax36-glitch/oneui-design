package com.samsung.android.music.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;

import com.samsung.android.music.R;
import com.samsung.android.music.activity.MainActivity;
import com.samsung.android.music.model.Song;

/**
 * Background music playback service for Samsung Music Player
 * Handles audio playback with proper lifecycle management
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    
    private static final String CHANNEL_ID = "SamsungMusicChannel";
    private static final int NOTIFICATION_ID = 1;
    
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private boolean isPlaying = false;
    private final IBinder musicBinder = new MusicBinder();
    
    // Service callbacks
    public interface MusicServiceCallback {
        void onSongChanged(Song song);
        void onPlaybackStateChanged(boolean isPlaying);
        void onProgressChanged(int progress);
    }
    
    private MusicServiceCallback callback;
    
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        initializeMediaPlayer();
        createNotificationChannel();
    }
    
    private void initializeMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Samsung Music Player",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Music playback controls");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }
    
    public void setCallback(MusicServiceCallback callback) {
        this.callback = callback;
    }
    
    public void playSong(Song song) {
        if (song == null || song.getUri() == null) return;
        
        try {
            currentSong = song;
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), song.getUri());
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void playPause() {
        if (mediaPlayer.isPlaying()) {
            pause();
        } else {
            play();
        }
    }
    
    public void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
            updateNotification();
            if (callback != null) {
                callback.onPlaybackStateChanged(true);
            }
        }
    }
    
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            updateNotification();
            if (callback != null) {
                callback.onPlaybackStateChanged(false);
            }
        }
    }
    
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying = false;
            updateNotification();
            if (callback != null) {
                callback.onPlaybackStateChanged(false);
            }
        }
    }
    
    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }
    
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }
    
    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }
    
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
    
    public Song getCurrentSong() {
        return currentSong;
    }
    
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        isPlaying = true;
        startForeground(NOTIFICATION_ID, createNotification());
        if (callback != null) {
            callback.onPlaybackStateChanged(true);
            callback.onSongChanged(currentSong);
        }
    }
    
    @Override
    public void onCompletion(MediaPlayer mp) {
        isPlaying = false;
        if (callback != null) {
            callback.onPlaybackStateChanged(false);
        }
    }
    
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        isPlaying = false;
        if (callback != null) {
            callback.onPlaybackStateChanged(false);
        }
        return false;
    }
    
    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        String songTitle = currentSong != null ? currentSong.getTitle() : "Samsung Music";
        String artist = currentSong != null ? currentSong.getArtist() : "One UI 10.0";
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(songTitle)
            .setContentText(artist)
            .setSmallIcon(R.drawable.ic_play_circle)
            .setContentIntent(pendingIntent)
            .setOngoing(isPlaying)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build();
    }
    
    private void updateNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, createNotification());
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
