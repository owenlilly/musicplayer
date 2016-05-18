package com.coderave.raveplayer.ui.fragments.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.coderave.raveplayer.R;
import com.coderave.raveplayer.models.FolderDetails;
import com.coderave.raveplayer.models.SongDetails;
import com.coderave.raveplayer.observers.song.AllSongsObserver;
import com.coderave.raveplayer.ui.components.BindableViewHolder;
import com.coderave.raveplayer.ui.fragments.BaseTabFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;


public class FoldersFragment extends BaseTabFragment {

    @Bind(R.id.song_list) RecyclerView songListView;

    private final Map<String, Integer> foldersAndSongCount = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_folders, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        FolderRecyclerAdapter adapter = new FolderRecyclerAdapter(new ArrayList<>());
        songListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songListView.setAdapter(adapter);

        Observable.create(new AllSongsObserver(getActivity().getContentResolver()))
                .filter(songDetails -> {

                    final String folderPath = getFolderPath(songDetails);

                    boolean exists = foldersAndSongCount.containsKey(folderPath);
                    if(exists){
                        foldersAndSongCount.put(folderPath, foldersAndSongCount.get(folderPath) + 1); // increment count
                    } else {
                        foldersAndSongCount.put(folderPath, 1); // initialize count
                    }

                    return !exists;
                })
                .map(songDetails -> {
                    final String folderPath = getFolderPath(songDetails);
                    final String folderDisplayNameName = getFolderDisplayName(folderPath);

                    return new FolderDetails(folderDisplayNameName, folderPath);
                })
                .forEach(adapter::append);
    }

    @Override
    public String getTitle() {
        return "Folders";
    }

    /**
     * Get the full path to folder, excluding the filename
     * @param s SongDetails object
     * @return Full folder path
     */
    private String getFolderPath(SongDetails s){
        return new File(s.getPath()).getParent();
    }

    /**
     * Gets simple folder name from folder path
     * @param folderPath
     * @return Display folder name
     */
    private String getFolderDisplayName(String folderPath){
        return folderPath.substring(folderPath.lastIndexOf('/')+1);
    }

    public class FolderRecyclerAdapter extends RecyclerView.Adapter<FolderRecyclerAdapter.FolderViewHolder> {

        private final List<FolderDetails> folders;

        public FolderRecyclerAdapter(List<FolderDetails> folders) {
            this.folders = folders;
        }

        @Override
        public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FolderViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(FolderViewHolder holder, int position) {
            FolderDetails folderDetails = folders.get(position);
            holder.folderName.setText(folderDetails.getName());
            holder.itemView.setOnClickListener(v -> Toast.makeText(getActivity(), folderDetails.getName(), Toast.LENGTH_SHORT).show());
        }

        @Override
        public int getItemCount() {
            return folders.size();
        }

        public void append(FolderDetails folder){
            final int pos = folders.size();
            folders.add(pos,folder);
            notifyItemInserted(pos);
        }

        public class FolderViewHolder extends BindableViewHolder{
            public FolderViewHolder(ViewGroup parent) {
                super(parent, R.layout.folder_item);
            }

            @Bind(R.id.txt_folder_name) TextView folderName;
        }
    }
}
