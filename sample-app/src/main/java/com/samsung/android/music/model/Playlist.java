package com.samsung.android.music.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Playlist model for Samsung Music Player
 * Manages a collection of songs with playback order
 */
public class Playlist {
    
    private long id;
    private String name;
    private List<Song> songs;
    private int currentIndex;
    private boolean isShuffled;
    private RepeatMode repeatMode;
    
    public enum RepeatMode {
        OFF, ALL, ONE
    }
    
    public Playlist() {
        this.songs = new ArrayList<>();
        this.currentIndex = 0;
        this.isShuffled = false;
        this.repeatMode = RepeatMode.OFF;
    }
    
    public Playlist(String name) {
        this();
        this.name = name;
    }
    
    public Playlist(long id, String name, List<Song> songs) {
        this.id = id;
        this.name = name;
        this.songs = songs != null ? songs : new ArrayList<>();
        this.currentIndex = 0;
        this.isShuffled = false;
        this.repeatMode = RepeatMode.OFF;
    }
    
    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public List<Song> getSongs() { return songs; }
    public void setSongs(List<Song> songs) { 
        this.songs = songs != null ? songs : new ArrayList<>();
        if (currentIndex >= this.songs.size()) {
            currentIndex = 0;
        }
    }
    
    public int getCurrentIndex() { return currentIndex; }
    public void setCurrentIndex(int currentIndex) { 
        if (currentIndex >= 0 && currentIndex < songs.size()) {
            this.currentIndex = currentIndex;
        }
    }
    
    public boolean isShuffled() { return isShuffled; }
    public void setShuffled(boolean shuffled) { this.isShuffled = shuffled; }
    
    public RepeatMode getRepeatMode() { return repeatMode; }
    public void setRepeatMode(RepeatMode repeatMode) { this.repeatMode = repeatMode; }
    
    // Playlist operations
    public void addSong(Song song) {
        if (song != null) {
            songs.add(song);
        }
    }
    
    public void addSong(int index, Song song) {
        if (song != null && index >= 0 && index <= songs.size()) {
            songs.add(index, song);
            if (index <= currentIndex) {
                currentIndex++;
            }
        }
    }
    
    public boolean removeSong(Song song) {
        int index = songs.indexOf(song);
        if (index != -1) {
            songs.remove(index);
            if (index < currentIndex) {
                currentIndex--;
            } else if (index == currentIndex && currentIndex >= songs.size()) {
                currentIndex = songs.size() - 1;
            }
            return true;
        }
        return false;
    }
    
    public Song getCurrentSong() {
        if (songs.isEmpty() || currentIndex < 0 || currentIndex >= songs.size()) {
            return null;
        }
        return songs.get(currentIndex);
    }
    
    public Song getNextSong() {
        if (songs.isEmpty()) return null;
        
        int nextIndex = getNextIndex();
        return nextIndex != -1 ? songs.get(nextIndex) : null;
    }
    
    public Song getPreviousSong() {
        if (songs.isEmpty()) return null;
        
        int prevIndex = getPreviousIndex();
        return prevIndex != -1 ? songs.get(prevIndex) : null;
    }
    
    public boolean moveToNext() {
        int nextIndex = getNextIndex();
        if (nextIndex != -1) {
            currentIndex = nextIndex;
            return true;
        }
        return false;
    }
    
    public boolean moveToPrevious() {
        int prevIndex = getPreviousIndex();
        if (prevIndex != -1) {
            currentIndex = prevIndex;
            return true;
        }
        return false;
    }
    
    private int getNextIndex() {
        if (songs.isEmpty()) return -1;
        
        switch (repeatMode) {
            case ONE:
                return currentIndex;
            case ALL:
                return (currentIndex + 1) % songs.size();
            case OFF:
            default:
                return currentIndex + 1 < songs.size() ? currentIndex + 1 : -1;
        }
    }
    
    private int getPreviousIndex() {
        if (songs.isEmpty()) return -1;
        
        switch (repeatMode) {
            case ONE:
                return currentIndex;
            case ALL:
                return currentIndex > 0 ? currentIndex - 1 : songs.size() - 1;
            case OFF:
            default:
                return currentIndex > 0 ? currentIndex - 1 : -1;
        }
    }
    
    public boolean isEmpty() {
        return songs.isEmpty();
    }
    
    public int size() {
        return songs.size();
    }
    
    public void clear() {
        songs.clear();
        currentIndex = 0;
    }
    
    public boolean hasNext() {
        return getNextIndex() != -1;
    }
    
    public boolean hasPrevious() {
        return getPreviousIndex() != -1;
    }
    
    /**
     * Get total duration of all songs in playlist
     */
    public long getTotalDuration() {
        long total = 0;
        for (Song song : songs) {
            total += song.getDuration();
        }
        return total;
    }
    
    /**
     * Format total duration in hh:mm:ss format
     */
    public String getFormattedTotalDuration() {
        long totalSeconds = getTotalDuration() / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
    
    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", songs=" + songs.size() +
                ", currentIndex=" + currentIndex +
                ", isShuffled=" + isShuffled +
                ", repeatMode=" + repeatMode +
                '}';
    }
}
