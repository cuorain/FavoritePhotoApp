package com.example.originalfilemanager;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.originalfilemanager.dummy.DummyContent.DummyItem;

import java.util.List;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FileRecyclerViewAdapter extends RecyclerView.Adapter<FileRecyclerViewHolder> {

    //リストデータを保持
    private List<Map<String, Object>> _listData;
    private Context _context;

    public FileRecyclerViewAdapter(List<Map<String, Object>> listData, Context context) {
        _listData = listData;
        _context = context;
    }

    @Override
    public FileRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_list_item, parent, false);
        return new FileRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FileRecyclerViewHolder holder, int position) {
        //リストデータから１行分のデータ取得
        Map<String, Object> item = _listData.get(position);
        //ファイルタイトル取得
        String title = (String) item.get("title");
        //ビューホルダのTextViewに設定
        holder._tvFileName.setText(title);
        //クリックリスナを設定
        holder._layoutFileItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick(view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _listData.size();
    }

    public void onItemClick(View view) {
        //TODO:タップされた画像表示
    }

}