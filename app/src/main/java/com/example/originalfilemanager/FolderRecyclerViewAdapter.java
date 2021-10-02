package com.example.originalfilemanager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewHolder> {
    //リストデータを保持
    private List<Map<String, Object>> _listData;
    private Context _context;

    public FolderRecyclerViewAdapter(List<Map<String, Object>> listData, Context context) {
        _listData = listData;
        _context = context;
    }

    @NonNull
    @Override
    public FolderRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //レイアウトインフレータを取得
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //１行分の画面部品取得
        View view = inflater.inflate(R.layout.folder_list_item, parent, false);
        //ビューを入れたビューホルダを生成して返す
        return new FolderRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderRecyclerViewHolder holder, int position) {
        //リストデータから１行分のデータ取得
        Map<String, Object> item = _listData.get(position);
        //フォルダ名取得
        String folderName = (String) item.get("folderName");
        //ビューホルダのTextViewに設定
        holder._tvFolderName.setText(folderName);
        //クリックリスナを設定
        holder._layoutFolderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick(view);
            }
        });
    }

    @Override
    public int getItemCount() {
        //リストデータの件数をリターン
        return _listData.size();
    }


    public void onItemClick(View view) {
        String folderName = ((TextView)view.findViewById(R.id.tvFolderName)).getText().toString();
        //TODO:取得したフォルダ名から、フォルダアイテムを表示
        //遷移先のファイル内を表示するフラグメント
        ItemFragment fragment = new ItemFragment();
        //値を渡す
        Bundle bundle = new Bundle();
//        bundle.putInt("SELECTED_FOLDER_POSITION", position);
        bundle.putString("FOLDER_NAME", folderName);
        fragment.setArguments(bundle);
        //画面呼び出し
        //フラグメントマネージャー取得
        FragmentManager fragmentManager = ((AppCompatActivity)_context).getSupportFragmentManager();
        //トランザクション開始
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //メインアクティビティにフラグメント追加
        transaction.add(R.id.activityMain, fragment);
        // 戻るボタンで戻ってこれるように
        transaction.addToBackStack(null);
        //フラグメントを置き換える（やらないと２重になる）
        transaction.replace(R.id.activityMain, fragment);
        //コミットして、フラグメント反映
        transaction.commit();
    }
}
