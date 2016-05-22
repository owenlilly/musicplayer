package com.github.raveplayer.models;


public class PlayingState extends SongDetails {

    int _id;
    int progress;

    public PlayingState(SongDetails songDetails, int progress) {
        super(songDetails.getId(), songDetails.getArtist(), songDetails.getTitle(),
                songDetails.getDisplayName(), String.valueOf(songDetails.duration),
                songDetails.path, songDetails.getAlbumId());
        this.progress = progress;
        _id = 0;
    }

    public int getProgress() {
        return progress;
    }
}
