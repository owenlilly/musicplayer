package com.coderave.raveplayer.observers.song;


import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import com.coderave.raveplayer.models.SongDetails;

public class AllSongsObserver extends BaseSongObserver<SongDetails> {

    public AllSongsObserver(ContentResolver contentResolver) {
        super(contentResolver);
    }

    @Override
    public SongDetails mapFromCursor(Cursor cursor) {
        int id              = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
        String artist       = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        String duration     = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
        String displayName  = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
        String title        = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        String path         = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        int albumId         = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
        return new SongDetails(id, artist, title, displayName, duration, path, albumId);
    }

    @Override
    public String getQuery() {
        return MediaStore.Audio.Media.IS_MUSIC + " != 0";
    }
}
