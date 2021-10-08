package com.example.originalfilemanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class FileViewFragment extends Fragment {

    private String fileId = "";
    private String title = "";

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
        //ビューモデル取得
        //HACK: MVVMでやろうとするとRoomを使わないといけないのはめんどくさいので、ごりおす
//        FileViewModel viewModel = new ViewModelProvider(this).get(FileViewModel.class);
        FileViewModel viewModel = new FileViewModel(getContext());
        viewModel.loadData(fileId);
        //TODO:データがなければ、エラー
        //データ値をセットする
        TextView tvMemo = view.findViewById(R.id.tvMemo);
        final Observer<String> memoObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvMemo.setText(s);
            }
        };
        viewModel.getMemo().observe(getActivity(), memoObserver);
        ImageView ivFile = view.findViewById(R.id.ivFile);
        final Observer<Bitmap> imageObserver = new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap b) {
                ivFile.setImageBitmap(b);
            }
        };
        viewModel.getImage().observe(getActivity(), imageObserver);
        return view;
    }



}
