package com.coderave.raveplayer.ui.components;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class BindableViewHolder extends RecyclerView.ViewHolder {
    public BindableViewHolder(ViewGroup parent, int layoutResourceId){
        this(LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false));
    }

    private BindableViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
