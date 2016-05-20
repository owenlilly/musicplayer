package com.github.raveplayer.models;


import android.database.Cursor;
import android.provider.MediaStore;

public class SongAttributeColumns {

    private final int id;
    private final int artist;
    private final int title;
    private final int data;
    private final int displayName;
    private final int duration;

    public SongAttributeColumns(Cursor cursor) {
        id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
        artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        displayName = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
        duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

    }

    public int getId() {
        return id;
    }

    public int getArtist() {
        return artist;
    }

    public int getTitle() {
        return title;
    }

    public int getData() {
        return data;
    }

    public int getDisplayName() {
        return displayName;
    }

    public int getDuration() {
        return duration;
    }
}
