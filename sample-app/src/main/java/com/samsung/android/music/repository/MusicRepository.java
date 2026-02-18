package com.samsung.android.music.repository;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.samsung.android.music.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing music data from MediaStore
 * Handles scanning and loading music files from device storage
 */
public class MusicRepository {
    
    private static final String TAG = "MusicRepository";
    private final Context context;
    private final ContentResolver contentResolver;
    
    // MediaStore projection for audio files
    private static final String[] AUDIO_PROJECTION = {
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.GENRE,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.MIME_TYPE,
        MediaStore.Audio.Media.DATE_ADDED,
        MediaStore.Audio.Media.DATE_MODIFIED
    };
    
    // Selection criteria for audio files
    private static final String AUDIO_SELECTION = 
        MediaStore.Audio.Media.IS_MUSIC + " = 1 AND " +
        MediaStore.Audio.Media.DURATION + " > 30000"; // At least 30 seconds
    
    // Sort order
    private static final String AUDIO_SORT_ORDER = 
        MediaStore.Audio.Media.TITLE + " ASC";
    
    public MusicRepository(Context context) {
        this.context = context.getApplicationContext();
        this.contentResolver = this.context.getContentResolver();
    }
    
    /**
     * Load all music files from device storage
     */
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        
        try {
            Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                AUDIO_PROJECTION,
                AUDIO_SELECTION,
                null,
                AUDIO_SORT_ORDER
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Song song = createSongFromCursor(cursor);
                    if (song != null) {
                        songs.add(song);
                    }
                }
                cursor.close();
            }
            
            Log.d(TAG, "Loaded " + songs.size() + " songs from MediaStore");
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading songs from MediaStore", e);
        }
        
        return songs;
    }
    
    /**
     * Get songs by artist
     */
    public List<Song> getSongsByArtist(String artist) {
        List<Song> songs = new ArrayList<>();
        
        String selection = AUDIO_SELECTION + " AND " + 
            MediaStore.Audio.Media.ARTIST + " = ?";
        String[] selectionArgs = {artist};
        
        try {
            Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                AUDIO_PROJECTION,
                selection,
                selectionArgs,
                AUDIO_SORT_ORDER
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Song song = createSongFromCursor(cursor);
                    if (song != null) {
                        songs.add(song);
                    }
                }
                cursor.close();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading songs by artist: " + artist, e);
        }
        
        return songs;
    }
    
    /**
     * Get songs by album
     */
    public List<Song> getSongsByAlbum(String album) {
        List<Song> songs = new ArrayList<>();
        
        String selection = AUDIO_SELECTION + " AND " + 
            MediaStore.Audio.Media.ALBUM + " = ?";
        String[] selectionArgs = {album};
        
        try {
            Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                AUDIO_PROJECTION,
                selection,
                selectionArgs,
                MediaStore.Audio.Media.TRACK + " ASC"
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Song song = createSongFromCursor(cursor);
                    if (song != null) {
                        songs.add(song);
                    }
                }
                cursor.close();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading songs by album: " + album, e);
        }
        
        return songs;
    }
    
    /**
     * Get all unique artists
     */
    public List<String> getAllArtists() {
        List<String> artists = new ArrayList<>();
        
        String[] projection = {MediaStore.Audio.Media.ARTIST};
        String selection = AUDIO_SELECTION;
        String sortOrder = MediaStore.Audio.Media.ARTIST + " ASC";
        
        try {
            Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            );
            
            if (cursor != null) {
                String lastArtist = "";
                while (cursor.moveToNext()) {
                    String artist = cursor.getString(0);
                    if (artist != null && !artist.equals(lastArtist)) {
                        artists.add(artist);
                        lastArtist = artist;
                    }
                }
                cursor.close();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading artists", e);
        }
        
        return artists;
    }
    
    /**
     * Get all unique albums
     */
    public List<String> getAllAlbums() {
        List<String> albums = new ArrayList<>();
        
        String[] projection = {MediaStore.Audio.Media.ALBUM};
        String selection = AUDIO_SELECTION;
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";
        
        try {
            Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            );
            
            if (cursor != null) {
                String lastAlbum = "";
                while (cursor.moveToNext()) {
                    String album = cursor.getString(0);
                    if (album != null && !album.equals(lastAlbum)) {
                        albums.add(album);
                        lastAlbum = album;
                    }
                }
                cursor.close();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading albums", e);
        }
        
        return albums;
    }
    
    /**
     * Search songs by title, artist, or album
     */
    public List<Song> searchSongs(String query) {
        List<Song> songs = new ArrayList<>();
        
        String selection = AUDIO_SELECTION + " AND (" +
            MediaStore.Audio.Media.TITLE + " LIKE ? OR " +
            MediaStore.Audio.Media.ARTIST + " LIKE ? OR " +
            MediaStore.Audio.Media.ALBUM + " LIKE ?)";
        
        String searchQuery = "%" + query + "%";
        String[] selectionArgs = {searchQuery, searchQuery, searchQuery};
        
        try {
            Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                AUDIO_PROJECTION,
                selection,
                selectionArgs,
                AUDIO_SORT_ORDER
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Song song = createSongFromCursor(cursor);
                    if (song != null) {
                        songs.add(song);
                    }
                }
                cursor.close();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error searching songs with query: " + query, e);
        }
        
        return songs;
    }
    
    /**
     * Create Song object from cursor
     */
    private Song createSongFromCursor(Cursor cursor) {
        try {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
            long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            int track = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK));
            int year = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
            String genre = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE));
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
            String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
            long dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));
            
            // Create content URI for the song
            Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
            
            // Create Song object
            Song song = new Song(id, title, artist, album, uri, duration);
            song.setTrack(track);
            song.setGenre(genre);
            song.setDateAdded(dateAdded);
            song.setSize(size);
            
            // Get album art URI
            Uri albumArtUri = ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"), albumId);
            song.setAlbumArt(albumArtUri.toString());
            
            return song;
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating song from cursor", e);
            return null;
        }
    }
    
    /**
     * Get song by ID
     */
    public Song getSongById(long songId) {
        String selection = MediaStore.Audio.Media._ID + " = ?";
        String[] selectionArgs = {String.valueOf(songId)};
        
        try {
            Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                AUDIO_PROJECTION,
                selection,
                selectionArgs,
                null
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                Song song = createSongFromCursor(cursor);
                cursor.close();
                return song;
            }
            
            if (cursor != null) {
                cursor.close();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading song by ID: " + songId, e);
        }
        
        return null;
    }
}
