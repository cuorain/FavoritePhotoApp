package com.example.originalfilemanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * フォルダの一覧表示画面
 */
public class FolderListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FolderListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FolderListFragment newInstance(String param1, String param2) {
        FolderListFragment fragment = new FolderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //フラグメント内でオプションメニューを使う時に必須の設定
        setHasOptionsMenu(true);
        //表示する画面をインフレート
        View view = inflater.inflate(R.layout.folder_list, container, false);
        //所属しているアクティビティ取得
        MainActivity mainActivity = (MainActivity) getActivity();
        //アクションバーにタイトルセット
        mainActivity.setTitle("フォルダ選択");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //リストのフォルダをタップされた時のイベントリスナをセット
        ListView lvFolderList = view.findViewById(R.id.lvFolderList);
        lvFolderList.setOnItemClickListener(new ListItemClickListener());
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String folderName = (String) parent.getItemAtPosition(position);
            //TODO:取得したフォルダ名から、フォルダアイテムを表示
            //遷移先のファイル内を表示するフラグメント
            ItemFragment fragment = new ItemFragment();
            //値を渡す
            Bundle bundle = new Bundle();
            bundle.putInt("SELECTED_FOLDER_POSITION", position);
            bundle.putString("FOLDER_NAME", (String)parent.getItemAtPosition(position));
            fragment.setArguments(bundle);
            //画面呼び出し
            //フラグメントマネージャー取得
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //追加ボタン押したときの処理
            case R.id.menu_add:
                AddFolderFragment addFolderFragment = new AddFolderFragment();
                addFolderFragment.show(getParentFragmentManager(), "AddFolderFragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}