package com.github.raveplayer.models;


public class SongDetails {

    int _id;
    String artist;
    String title;
    String displayName;
    int duration;
    String path;
    int albumId;

    public SongDetails(int id, String artist, String title, String displayName, String duration, String path){
        this(id, artist, title, displayName, duration, path, -1);
    }

    public SongDetails(int id, String artist, String title, String displayName, String duration, String path, int albumId){
        this._id = id;
        this.artist = artist;
        this.title = title;
        this.displayName = displayName;
        this.duration = Integer.parseInt(duration);
        this.path = path;
        this.albumId = albumId;
    }

    public int getId() {
        return _id;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }

    public int getAlbumId() {
        return albumId;
    }
}
