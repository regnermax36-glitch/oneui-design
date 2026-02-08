package com.samsung.android.music.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Song model for Samsung Music Player
 * Represents a single music track with metadata
 */
public class Song implements Parcelable {
    
    private long id;
    private String title;
    private String artist;
    private String album;
    private String albumArt;
    private Uri uri;
    private long duration;
    private int track;
    private String genre;
    private long dateAdded;
    private long size;
    
    public Song() {
        // Default constructor
    }
    
    public Song(long id, String title, String artist, String album, 
                Uri uri, long duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.uri = uri;
        this.duration = duration;
    }
    
    protected Song(Parcel in) {
        id = in.readLong();
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        albumArt = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
        duration = in.readLong();
        track = in.readInt();
        genre = in.readString();
        dateAdded = in.readLong();
        size = in.readLong();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeString(albumArt);
        dest.writeParcelable(uri, flags);
        dest.writeLong(duration);
        dest.writeInt(track);
        dest.writeString(genre);
        dest.writeLong(dateAdded);
        dest.writeLong(size);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }
        
        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
    
    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getTitle() { return title != null ? title : "Unknown Title"; }
    public void setTitle(String title) { this.title = title; }
    
    public String getArtist() { return artist != null ? artist : "Unknown Artist"; }
    public void setArtist(String artist) { this.artist = artist; }
    
    public String getAlbum() { return album != null ? album : "Unknown Album"; }
    public void setAlbum(String album) { this.album = album; }
    
    public String getAlbumArt() { return albumArt; }
    public void setAlbumArt(String albumArt) { this.albumArt = albumArt; }
    
    public Uri getUri() { return uri; }
    public void setUri(Uri uri) { this.uri = uri; }
    
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    
    public int getTrack() { return track; }
    public void setTrack(int track) { this.track = track; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public long getDateAdded() { return dateAdded; }
    public void setDateAdded(long dateAdded) { this.dateAdded = dateAdded; }
    
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    
    /**
     * Format duration in mm:ss format
     */
    public String getFormattedDuration() {
        long minutes = duration / 1000 / 60;
        long seconds = (duration / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Song song = (Song) obj;
        return id == song.id;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
    
    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", duration=" + duration +
                '}';
    }
}
