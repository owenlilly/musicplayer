package com.coderave.raveplayer.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.coderave.raveplayer.Constants;
import com.coderave.raveplayer.MediaController;
import com.coderave.raveplayer.R;
import com.coderave.raveplayer.models.SongDetails;
import com.coderave.raveplayer.services.PlayerService;
import com.coderave.raveplayer.ui.fragments.BaseTabFragment;
import com.coderave.raveplayer.ui.fragments.main.AlbumsFragment;
import com.coderave.raveplayer.ui.fragments.main.AllTracksFragment;
import com.coderave.raveplayer.ui.widgets.MaterialPlayPauseButton;
import com.coderave.raveplayer.utils.TabBuilder;
import com.coderave.raveplayer.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.txt_song_title)  TextView txtSongTitle;
    @Bind(R.id.txt_artist)      TextView txtArtist;
    @Bind(R.id.btn_play_pause)  MaterialPlayPauseButton btnPlayPause;
    @Bind(R.id.tabs)            TabLayout tabLayout;
    @Bind(R.id.toolbar)         Toolbar toolbar;
    @Bind(R.id.viewpager)       ViewPager viewPager;
    @Bind(R.id.cover_image)     ImageView coverImage;

    public static Context appContext;

    private final MediaController mediaController = MediaController.getInstance();
    private final EventBus mBus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        initBuildSpecificEnhancements();

        appContext = getApplicationContext();

        initTabLayout();
        initPlayPauseButton();
        initCoverImage();
        updatePlayPauseButton();
        startPlayerService();
        registerReceiver(exitBroadcastReceiver, new IntentFilter(Constants.ACTION.CLOSE_ACTION));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(exitBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayPauseButton();
    }

    @Subscribe
    public void onSongChanged(MediaController.OnSongChangedEvent event) {
        final SongDetails song = event.getSongDetails();

        txtSongTitle.setText(song.getTitle());
        txtArtist.setText(song.getArtist());
        Utils.loadSmallCoverOrDefaultArt(coverImage, song);
    }

    @Subscribe
    public void onPlayCompleted(MediaController.OnPlayCompletedEvent event) {
        updatePlayPauseButton();
    }

    @Subscribe
    public void onPlayerStateChange(MediaController.OnPlayerStateChangeEvent event) {
        updatePlayPauseButton();
    }

    private void initBuildSpecificEnhancements() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Utils.applyKitKatToolbarPadding(toolbar);
        }
    }

    private void startNowPlayingActivity(){
        Intent i = new Intent(this, NowPlayingActivity.class);
        startActivity(i);
    }

    private void initTabLayout(){
        final List<BaseTabFragment> tabList = Arrays.<BaseTabFragment>asList(new AllTracksFragment(), new AlbumsFragment());

        TabBuilder.with(getSupportFragmentManager())
                    .setTabLayout(tabLayout)
                    .setViewPager(viewPager)
                    .setTabList(tabList)
                    .build();
    }

    private void initCoverImage(){
        coverImage.setOnClickListener(v -> {
            if(mediaController.getCurrentSong() != null){
                startNowPlayingActivity();
            }
        });
    }

    private void initPlayPauseButton(){
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnPlayPause.setColor(Color.WHITE);
        btnPlayPause.setAnimDuration(100);
    }

    private void startPlayerService(){
        startService(new Intent(this, PlayerService.class));
    }

    private void togglePlayPause(){
        if (mediaController.isPlaying()) {
            MediaController.getInstance().pause();
        } else if (mediaController.isPaused()) {
            MediaController.getInstance().resume();
        }
    }

    private void updatePlayPauseButton() {
        if (mediaController.isPlaying()) {
            btnPlayPause.setToPause();
        } else {
            btnPlayPause.setToPlay();
        }
    }

    private final BroadcastReceiver exitBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}
