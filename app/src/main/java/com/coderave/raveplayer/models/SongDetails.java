package com.coderave.raveplayer.models;


public class SongDetails {

    private final int id;
    private final String artist;
    private final String title;
    private final String displayName;
    private final String duration;
    private final String path;


    public SongDetails(int id, String artist, String title, String displayName, String duration, String path){
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.displayName = displayName;
        this.duration = duration;
        this.path = path;
    }

    public int getId() {
        return id;
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

    public String getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }
}
