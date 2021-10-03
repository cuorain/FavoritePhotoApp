package com.example.originalfilemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Map;

public class FileViewFragment extends Fragment {

    private String fileId = "";
    private String title = "";
    Map<String, Object> data;

    public FileViewFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            fileId = bundle.getString("FILE_ID");
            title = bundle.getString("TITLE");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //フラグメント内でオプションメニューを使う時に必須の設定
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_file_view, container, false);
        //所属しているアクティビティ取得
        MainActivity mainActivity = (MainActivity) getActivity();
        //アクションバーにタイトルセット
        mainActivity.setTitle(title);

        //表示データ取得
        FileModel file = new FileModel(this.getContext());
        data = file.getData(fileId);
        //TODO:データがなければ、エラー
        //データ値をセットする
        TextView tvMemo = view.findViewById(R.id.tvMemo);
        tvMemo.setText((String)data.get("memo"));
        return view;
    }

}
