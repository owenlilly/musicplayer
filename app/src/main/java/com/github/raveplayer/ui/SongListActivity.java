package com.github.raveplayer.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.raveplayer.MediaController;
import com.github.raveplayer.PlayList;
import com.github.raveplayer.R;
import com.github.raveplayer.models.FolderDetails;
import com.github.raveplayer.models.SongDetails;
import com.github.raveplayer.observers.song.FolderObserver;
import com.github.raveplayer.ui.components.BindableViewHolder;
import com.github.raveplayer.ui.components.DividerItemDecoration;
import com.github.raveplayer.ui.fragments.main.FoldersFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;


public class SongListActivity extends AppCompatActivity {

    @Bind(R.id.song_list_view) RecyclerView songListView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        ButterKnife.bind(this);

        SongListRecyclerAdapter adapter = new SongListRecyclerAdapter(new ArrayList<>());
        songListView.setLayoutManager(new LinearLayoutManager(this));
        songListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        songListView.setAdapter(adapter);

        final Intent intent = getIntent();
        final String action = intent.getAction(); // don't bother checking if action null, if it is we need it to crash

        if(action.equals(FoldersFragment.ACTION_LOAD_FOLDER)) {
            final FolderDetails folderDetails = intent.getParcelableExtra(FoldersFragment.KEY_FOLDER_DETAILS);
            final String folderPlath = folderDetails.getPath();

            Observable.create(new FolderObserver(getContentResolver(), folderPlath))
                      .forEach(adapter::append);
        }
    }

    public class SongListRecyclerAdapter extends RecyclerView.Adapter<SongListRecyclerAdapter.SongListViewHolder> {

        private final List<SongDetails> mTitles;
        private final PlayList playList = PlayList.getInstance();

        public SongListRecyclerAdapter(List<SongDetails> titles) {
            mTitles = titles;
        }

        @Override
        public SongListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SongListViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(SongListViewHolder holder, int position) {
            final SongDetails song = mTitles.get(position);

            holder.songTitle.setText(song.getTitle());
            holder.number.setText(String.valueOf(position+1));
            holder.artist.setText(song.getArtist());
            holder.itemView.setOnClickListener(v -> playSong(song, position));
        }

        @Override
        public int getItemCount() {
            return mTitles.size();
        }

        public void append(SongDetails song){
            final int pos = mTitles.size();
            mTitles.add(pos,song);
            notifyItemInserted(pos);
        }

        private void playSong(SongDetails song, int position){
            MediaController.getInstance().play(song);
            if(!playList.getListName().equals("SongsList")){
                playList.clear();
                playList.addAll(mTitles);
                playList.setListName("SongsList");
            }
            playList.setCurrent(position);
        }

        public class SongListViewHolder extends BindableViewHolder {

            public SongListViewHolder(ViewGroup parent) {
                super(parent, R.layout.track_list_item);
            }

            @Bind(R.id.track_number) TextView number;
            @Bind(R.id.track_artist) TextView artist;
            @Bind(R.id.track_title)  TextView songTitle;
        }
    }
}
