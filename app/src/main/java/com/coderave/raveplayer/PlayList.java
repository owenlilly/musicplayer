package com.coderave.raveplayer;


import com.coderave.raveplayer.models.SongDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class PlayList {
    private static final int NO_POSITION = -1;
    private final List<SongDetails> playlist = new ArrayList<>();
    private static volatile PlayList instance;
    private int currentPos;
    private String listName = "";
    private LoopStyle loopStyle;

    private PlayList(){
        setCurrent(NO_POSITION);
        setLoopStyle(LoopStyle.NO_LOOP);
    }

    public static PlayList getInstance(){
        if(instance == null) {
            synchronized (PlayList.class) {
                instance = new PlayList();
            }
        }

        return instance;
    }

    public void clear(){
        playlist.clear();
        listName = "";
        setCurrent(NO_POSITION);
    }

    public void setCurrent(int pos){
        if(pos <= playlist.size()) {
            currentPos = pos;
        }
    }

    public int getCurrentPos(){
        return currentPos;
    }

    public SongDetails prev(){
        currentPos -= 1;
        return playlist.get(currentPos);
    }

    public SongDetails current(){
        return playlist.get(currentPos);
    }

    public void setListName(String name){
        listName = name;
    }

    public String getListName(){
        return listName;
    }

    public void add(SongDetails song){
        playlist.add(song);
    }

    public void add(int pos, SongDetails song){
        playlist.add(pos, song);
    }

    public void addAll(Collection<SongDetails> songs){
        playlist.addAll(songs);
    }

    public void setLoopStyle(LoopStyle loopStyle){
        this.loopStyle = loopStyle;
    }

    public LoopStyle getLoopStyle(){
        return loopStyle;
    }

    public boolean hasPrev() {
        return playlist.size() > 0 && (currentPos-1) > NO_POSITION;
    }

    public boolean hasNext() {
        return playlist.size() > (currentPos+1);
    }

    public SongDetails next() {
        currentPos += 1;
        return playlist.get(currentPos);
    }

    public void remove() {
        remove(currentPos);
    }

    public void remove(int pos){
        if(pos > NO_POSITION && pos < playlist.size()) {
            playlist.remove(pos);
        }
    }

    public enum LoopStyle {
        LOOP_CURRENT,
        LOOP_LIST,
        NO_LOOP
    }
}
