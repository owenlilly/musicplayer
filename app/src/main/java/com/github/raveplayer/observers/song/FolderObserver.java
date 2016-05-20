package com.github.raveplayer.observers.song;


import android.content.ContentResolver;
import android.provider.MediaStore;


public class FolderObserver extends AllSongsObserver {

    private final String folderPath;

    public FolderObserver(ContentResolver contentResolver, String folderPath) {
        super(contentResolver);
        this.folderPath = folderPath;
    }

    @Override
    public String getQuery() {
        return String.format("%s LIKE '%s/%%'", MediaStore.Audio.Media.DATA, folderPath);
    }
}
