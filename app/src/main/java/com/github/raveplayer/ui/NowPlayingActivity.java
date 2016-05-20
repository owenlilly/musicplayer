package com.github.raveplayer.ui;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.raveplayer.MediaController;
import com.github.raveplayer.PlayList;
import com.github.raveplayer.R;
import com.github.raveplayer.models.SongDetails;
import com.github.raveplayer.utils.PlayerUtils;
import com.github.raveplayer.utils.TimeUtils;
import com.github.raveplayer.utils.Utils;

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
    @Bind(R.id.seekbar)         SeekBar seekBar;
    @Bind(R.id.progress)        TextView txtProgress;
    @Bind(R.id.duration)        TextView txtDuration;

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
        initSeekBar();
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
        updateMediaInfo();
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
    public void onProgressUpdateEvent(MediaController.OnProgressUpdateEvent event){
        seekBar.setProgress(event.getProgress());
    }

    @Subscribe
    public void onPlayStateChangedEvent(MediaController.OnPlayerStateChangeEvent e){
        updatePlayPauseButton();
        updateNextButtonState();
        updatePrevButtonState();
    }

    @Subscribe
    public void onSongChangedEvent(MediaController.OnSongChangedEvent e){
        updateMediaInfo();
    }

    private void updateMediaInfo(){
        updateActionBar();
        final SongDetails song = mediaController.getCurrentSong();
        seekBar.setMax(song.getDuration());
        Utils.loadLargeCoverOrDefaultArt(coverImage, song);
        txtDuration.setText(TimeUtils.millisToFormatedTime(song.getDuration()));
    }

    private void updateActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            if(mediaController.getCurrentSong() != null){
                actionBar.setTitle(mediaController.getCurrentSong().getTitle());
            }
        }
    }

    private void initSeekBar(){
        if(mediaController.getCurrentSong() != null){
            seekBar.setMax(mediaController.getCurrentSong().getDuration());
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtProgress.setText(TimeUtils.millisToFormatedTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaController.prepareToSeek();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaController.seekTo(seekBar.getProgress());
            }
        });
    }

    private void initBuildSpecificEnhancements(){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            Utils.applyKitKatToolbarPadding(toolbar);
        }
    }

    private void initActionBar(){
        setSupportActionBar(toolbar);
        updateActionBar();
    }

    private void initCoverArtIfSongSelected(){
        Utils.loadLargeCoverOrDefaultArt(coverImage, mediaController.getCurrentSong());
    }

    private void initButtonClickListeners(){
        btnPrev.setOnClickListener(v -> PlayerUtils.prev());
        btnNext.setOnClickListener(v -> PlayerUtils.next());
        btnPlayPause.setOnClickListener(v -> PlayerUtils.togglePlayPause());
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
