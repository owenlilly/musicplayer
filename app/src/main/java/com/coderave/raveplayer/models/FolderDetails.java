package com.coderave.raveplayer.models;


public class FolderDetails {
    private final String name;
    private final String path;

    public FolderDetails(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
}
