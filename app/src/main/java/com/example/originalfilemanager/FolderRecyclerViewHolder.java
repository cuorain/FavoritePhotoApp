package com.example.originalfilemanager;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class FolderRecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView _tvFolderName;
    public TextView _tvFolderId;
    public ConstraintLayout _layoutFolderItem;
    public FolderRecyclerViewHolder(View itemView){
        super(itemView);

        //表示に使われるTextViewとConstraintLayout取得
        _tvFolderName = itemView.findViewById(R.id.tvFolderName);
        _layoutFolderItem = itemView.findViewById(R.id.layoutFolderItem);
        //ファイルマスタのID保持
        _tvFolderId = itemView.findViewById(R.id.tvFolderId);
    }
}
