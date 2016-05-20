package com.github.raveplayer.models;


public class AlbumDetails {
    int id;
    String album;
    String artist;
    String albumArt;
    int numberOfSongs;

    public AlbumDetails(int id, String album, String artist, String albumArt, int numberOfSongs) {
        this.id = id;
        this.album = album;
        this.artist = artist;
        this.albumArt = albumArt;
        this.numberOfSongs = numberOfSongs;
    }

    public int getId() {
        return id;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }
}
