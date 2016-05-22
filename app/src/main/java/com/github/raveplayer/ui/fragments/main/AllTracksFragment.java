package com.github.raveplayer.ui.fragments.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.raveplayer.MediaController;
import com.github.raveplayer.PlayingQueue;
import com.github.raveplayer.R;
import com.github.raveplayer.models.SongDetails;
import com.github.raveplayer.observers.song.AllSongsObserver;
import com.github.raveplayer.ui.components.BindableViewHolder;
import com.github.raveplayer.ui.fragments.BaseTabFragment;
import com.github.raveplayer.ui.components.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;


public class AllTracksFragment extends BaseTabFragment {

    @Bind(R.id.song_list) RecyclerView songListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_tracks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        AllTracksRecyclerAdapter adapter = new AllTracksRecyclerAdapter(new ArrayList<>());
        songListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        songListView.setAdapter(adapter);

        Observable.create(new AllSongsObserver(getActivity().getContentResolver()))
                  .forEach(adapter::append);
    }

    @Override
    public String getTitle() {
        return "All";
    }

    public class AllTracksRecyclerAdapter extends RecyclerView.Adapter<AllTracksRecyclerAdapter.SimpleViewHolder> {

        private final List<SongDetails> mTitles;
        private final PlayingQueue playList = PlayingQueue.getInstance();

        public AllTracksRecyclerAdapter(List<SongDetails> titles) {
            mTitles = titles;
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SimpleViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
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
            if(!playList.getListName().equals("AllSongsList")){
                playList.clear();
                playList.addAll(mTitles);
                playList.setListName("AllSongsList");
            }
            playList.setCurrent(position);
        }

        public class SimpleViewHolder extends BindableViewHolder {

            public SimpleViewHolder(ViewGroup parent) {
                super(parent, R.layout.track_list_item);
            }

            @Bind(R.id.track_number) TextView number;
            @Bind(R.id.track_artist) TextView artist;
            @Bind(R.id.track_title)  TextView songTitle;
        }
    }
}
