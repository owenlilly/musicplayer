package com.github.raveplayer.ui.fragments.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.raveplayer.R;
import com.github.raveplayer.models.AlbumDetails;
import com.github.raveplayer.observers.song.AlbumsObserver;
import com.github.raveplayer.ui.fragments.BaseTabFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;


public class AlbumsFragment extends BaseTabFragment {

    @Bind(R.id.song_list) RecyclerView songListView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_tracks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        AlbumRecyclerAdapter adapter = new AlbumRecyclerAdapter(new ArrayList<>());
        songListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songListView.setAdapter(adapter);

        Observable.create(new AlbumsObserver(getActivity().getContentResolver()))
                .forEach(adapter::append);
    }

    @Override
    public String getTitle() {
        return "Albums";
    }

    public class AlbumRecyclerAdapter extends RecyclerView.Adapter<AlbumRecyclerAdapter.AlbumViewHolder> {

        private final List<AlbumDetails> albums;

        public AlbumRecyclerAdapter(List<AlbumDetails> albums) {
            this.albums = albums;
        }

        @Override
        public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AlbumViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(AlbumViewHolder holder, int position) {
            AlbumDetails song = albums.get(position);
            holder.songTitle.setText(song.getAlbum());
            holder.itemView.setOnClickListener(v -> Toast.makeText(getActivity(), song.getAlbum(), Toast.LENGTH_SHORT).show());
        }

        @Override
        public int getItemCount() {
            return albums.size();
        }

        public void append(AlbumDetails song){
            final int pos = albums.size();
            albums.add(pos,song);
            notifyItemInserted(pos);
        }

        public class AlbumViewHolder extends RecyclerView.ViewHolder {
            public AlbumViewHolder(ViewGroup parent) {
                this(LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, parent, false));
            }

            private AlbumViewHolder(View itemView) {
                super(itemView);
                songTitle = (TextView) itemView.findViewById(android.R.id.text1);
            }

            TextView songTitle;
        }
    }
}
