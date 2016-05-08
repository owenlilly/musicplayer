package com.coderave.raveplayer.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.coderave.raveplayer.MediaController;
import com.coderave.raveplayer.R;
import com.coderave.raveplayer.ui.fragments.BaseTabFragment;
import com.coderave.raveplayer.ui.fragments.main.AlbumsFragment;
import com.coderave.raveplayer.ui.fragments.main.AllTracksFragment;
import com.coderave.raveplayer.utils.TabBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.txt_song_title)  TextView txtSongTitle;
    @Bind(R.id.btn_play_pause)  Button btnPlayPause;
    @Bind(R.id.tabs)            TabLayout tabLayout;
    @Bind(R.id.toolbar)         Toolbar toolbar;
    @Bind(R.id.viewpager)       ViewPager viewPager;

    private EventBus mBus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        initTabLayout();
        initPlayPauseClickListener();

        updatePlayPauseButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    protected void onStop() {
        mBus.unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onSongChanged(MediaController.OnSongChangedEvent event){
        txtSongTitle.setText(event.getSongDetails().getTitle());
    }

    @Subscribe
    public void onPlayerStateChange(MediaController.OnPlayerStateChangeEvent event){
        updatePlayPauseButton();
    }

    private void initTabLayout(){
        TabBuilder.with(getSupportFragmentManager())
                .setTabLayout(tabLayout)
                .setViewPager(viewPager)
                .setTabList(Arrays.<BaseTabFragment>asList(new AllTracksFragment(), new AlbumsFragment()))
                .build();
    }

    private void initPlayPauseClickListener(){
        btnPlayPause.setOnClickListener(v -> {
            if(MediaController.getInstance().getPlayerState() == MediaController.PlayerState.Playing){
                MediaController.getInstance().pause();
            } else if(MediaController.getInstance().getPlayerState() == MediaController.PlayerState.Paused){
                MediaController.getInstance().resume();
            }
        });
    }

    private void updatePlayPauseButton(){
        switch (MediaController.getInstance().getPlayerState()){
            case Playing:
                btnPlayPause.setText(R.string.btn_text_pause);
                break;
            case Stopped:
            case Paused:
                btnPlayPause.setText(R.string.btn_text_play);
                break;
        }
    }
}
