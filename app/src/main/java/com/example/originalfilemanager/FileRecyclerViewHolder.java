package com.example.originalfilemanager;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class FileRecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView _tvFileName;
    public TextView _tvFileId;
    public ConstraintLayout _layoutFileItem;
    public FileRecyclerViewHolder(View itemView) {
        super(itemView);
        //表示に使われるTextViewとConstraintLayout取得
        _tvFileName = itemView.findViewById(R.id.tvFileName);
        _tvFileId = itemView.findViewById(R.id.tvFileId);
        _layoutFileItem = itemView.findViewById(R.id.layoutFileItem);
    }
}
