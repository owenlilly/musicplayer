package com.coderave.raveplayer.ui;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.coderave.raveplayer.MediaController;
import com.coderave.raveplayer.PlayList;
import com.coderave.raveplayer.R;
import com.coderave.raveplayer.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;


public class NowPlayingActivity extends AppCompatActivity {

    @Bind(R.id.btn_prev)        Button btnPrev;
    @Bind(R.id.btn_play_pause)  Button btnPlayPause;
    @Bind(R.id.btn_next)        Button btnNext;
    @Bind(R.id.cover_image)     ImageView coverImage;
    @Bind(R.id.toolbar)         Toolbar toolbar;

    private final MediaController mediaController = MediaController.getInstance();
    private final PlayList playList               = PlayList.getInstance();
    private final EventBus bus                    = EventBus.getDefault();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        ButterKnife.bind(this);
        bus.register(this);

        initActionBar();
        initButtonClickListeners();
        initCoverArtIfSongSelected();
        initBuildSpecificEnhancements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayPauseButton();
        updateNextButtonState();
        updatePrevButtonState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onPlayStateChangedEvent(MediaController.OnPlayerStateChangeEvent e){
        updatePlayPauseButton();
        updateNextButtonState();
        updatePrevButtonState();
    }

    @Subscribe
    public void onSongChangedEvent(MediaController.OnSongChangedEvent e){
        Utils.loadLargeCoverOrDefaultArt(coverImage, e.getSongDetails());
    }

    private void initBuildSpecificEnhancements(){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            Utils.applyKitKatToolbarPadding(toolbar);
        }
    }

    private void initActionBar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initCoverArtIfSongSelected(){
        Utils.loadLargeCoverOrDefaultArt(coverImage, mediaController.getCurrentSong());
    }

    private void initButtonClickListeners(){
        btnPrev.setOnClickListener(v -> {
            if(playList.hasPrev()){
                mediaController.play(playList.prev());
            }
        });

        btnNext.setOnClickListener(v -> {
            if(playList.hasNext()){
                mediaController.play(playList.next());
            }
        });

        btnPlayPause.setOnClickListener(v -> {
            if(mediaController.isPlaying()){
                mediaController.pause();
            } else if (mediaController.isPaused()){
                mediaController.resume();
            }
        });
    }

    private void updatePlayPauseButton(){
        if(mediaController.isPlaying()){
            btnPlayPause.setText("Pause");
        } else {
            btnPlayPause.setText("Play");
        }
    }

    private void updatePrevButtonState(){
        if(!playList.hasPrev()){
            btnPrev.setEnabled(false);
        } else {
            btnPrev.setEnabled(true);
        }
    }

    private void updateNextButtonState(){
        if(!playList.hasNext()){
            btnNext.setEnabled(false);
        } else {
            btnNext.setEnabled(true);
        }
    }
}
