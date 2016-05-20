package com.github.raveplayer.models;


import android.os.Parcel;
import android.os.Parcelable;

public class FolderDetails implements Parcelable {
    private final String name;
    private final String path;

    public FolderDetails(String name, String path) {
        this.name = name;
        this.path = path;
    }

    protected FolderDetails(Parcel in) {
        name = in.readString();
        path = in.readString();
    }

    public static final Creator<FolderDetails> CREATOR = new Creator<FolderDetails>() {
        @Override
        public FolderDetails createFromParcel(Parcel in) {
            return new FolderDetails(in);
        }

        @Override
        public FolderDetails[] newArray(int size) {
            return new FolderDetails[size];
        }
    };

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
    }
}
