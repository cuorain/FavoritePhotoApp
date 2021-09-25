package com.example.originalfilemanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.originalfilemanager.dummy.DummyContent;

/**
 * フォルダ内のデータ一覧表示のフラグメント
 */
public class ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private int position = -1;
    private String folderName = "";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (getArguments() != null) {
            position = args.getInt("SELECTED_FOLDER_POSITION");
            folderName = args.getString("FOLDER_NAME");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //フラグメント内でオプションメニューを使う時に必須の設定
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        //所属しているアクティビティ取得
        MainActivity mainActivity = (MainActivity) getActivity();
        //アクションバーにタイトルセット
        mainActivity.setTitle(folderName);

        //TODO:選択したフォルダ内のファイルを表示させる
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(DummyContent.ITEMS));
        }
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //追加ボタン押したときの処理
            case R.id.menu_add:
                    Intent intent = new Intent(getActivity(), AddFileActivity.class);
                    intent.putExtra("FOLDER_NAME", folderName);
                    startActivity(intent);
                    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}