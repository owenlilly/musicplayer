package com.github.raveplayer.observers.song;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import com.github.raveplayer.models.AlbumDetails;


public class AlbumsObserver extends BaseSongObserver<AlbumDetails> {
    private int albumId = -1;

    public AlbumsObserver(ContentResolver contentResolver) {
        super(contentResolver);
    }

    @Override
    public AlbumDetails mapFromCursor(Cursor cursor) {
        int id            = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
        String album      = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
        String artist     = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
        //String albumArt   = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
//        int numberOfSongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));

        return new AlbumDetails(id,album,artist, "", 0);
    }

    public void setAlbumId(int id){
        this.albumId = id;
    }

    @Override
    public String[] getProjection() {
        return new String[] { MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST };
    }

    @Override
    public String getQuery() {
        if(albumId == -1)
            return null;

        return MediaStore.Audio.Media.ALBUM_ID + "=" + albumId + " AND " + MediaStore.Audio.Media.IS_MUSIC + "=1";
    }
}
