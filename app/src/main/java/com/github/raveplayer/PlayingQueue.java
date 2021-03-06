package com.github.raveplayer;


import com.github.raveplayer.models.SongDetails;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class PlayingQueue {
    private static final int NO_POSITION = -1;
    private final List<SongDetails> playlist = new ArrayList<>();
    private static volatile PlayingQueue instance;
    private int currentPos;
    private String listName = "";
    private LoopStyle loopStyle;
    private EventBus bus = EventBus.getDefault();

    private PlayingQueue(){
        setCurrent(NO_POSITION);
        setLoopStyle(LoopStyle.NO_LOOP);
    }

    public static PlayingQueue getInstance(){
        if(instance == null) {
            synchronized (PlayingQueue.class) {
                if(instance == null) {
                    instance = new PlayingQueue();
                }
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

    public boolean isEmpty(){
        return playlist.isEmpty();
    }

    public int getCurrentPos(){
        return currentPos;
    }

    public SongDetails current(){
        if(!isEmpty()){
            if(currentPos > NO_POSITION){
                return playlist.get(currentPos);
            }

            setCurrent(0);
            return playlist.get(currentPos);
        }

        return null;
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
        bus.post(new OnLoopStyleChangedEvent(loopStyle));
    }

    public LoopStyle getLoopStyle(){
        return loopStyle;
    }

    public boolean hasPrev() {
        return playlist.size() > 0 && (currentPos-1) > NO_POSITION;
    }

    public boolean hasNext() {
        return playlist.size() > (currentPos+1) ;
    }

    public SongDetails prev(){
        if(!hasPrev()){
            setCurrent(playlist.size()-1);
        }
        currentPos -= 1;
        return playlist.get(currentPos);
    }

    public SongDetails next() {
        if(!hasNext()){
            setCurrent(NO_POSITION);
        }
        currentPos += 1;
        return playlist.get(currentPos);
    }

    public void remove() {
        remove(currentPos);
    }

    public void remove(int pos){
        if(pos > NO_POSITION && pos < playlist.size()) {
            playlist.remove(pos);
            bus.post(new OnSongRemovedEvent());
        }
    }

    public enum LoopStyle {
        LOOP_CURRENT,
        LOOP_LIST,
        NO_LOOP
    }

    public static class OnLoopStyleChangedEvent{
        private final LoopStyle loopStyle;

        public OnLoopStyleChangedEvent(LoopStyle loopStyle) {
            this.loopStyle = loopStyle;
        }

        public LoopStyle getLoopStyle() {
            return loopStyle;
        }
    }

    public static class OnSongRemovedEvent { }
}
