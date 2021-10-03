package com.example.originalfilemanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.originalfilemanager.dummy.DummyContent;

import java.util.List;
import java.util.Map;

/**
 * フォルダ内のデータ一覧表示のフラグメント
 */
public class FileListFragment extends Fragment {

    RecyclerView rvFile;
    List<Map<String, Object>> fileList;
    FileRecyclerViewAdapter adapter;
    private String folderName = "";
    private String folderId = "";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FileListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            folderName = bundle.getString("FOLDER_NAME");
            folderId = bundle.getString("FOLDER_ID");
        }
        if (folderId.isEmpty()) {
            //TODO:フォルダが取得できてないとエラー
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //フラグメント内でオプションメニューを使う時に必須の設定
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.file_list, container, false);
        //所属しているアクティビティ取得
        MainActivity mainActivity = (MainActivity) getActivity();
        //アクションバーにタイトルセット
        mainActivity.setTitle(folderName);

        //リサイクラビュー取得
        rvFile = view.findViewById(R.id.rvFileList);
        //LayoutManager生成
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        //RecyclerViewにレイアウトマネージャー設定
        rvFile.setLayoutManager(layoutManager);
        //リストデータ取得
        fileList = getFileList(Integer.parseInt(folderId));
        //データがなければ、フォルダ作成するようにﾒｯｾｰｼﾞ
        if (fileList.size() == 0) {
            TextView tvNoFile = view.findViewById(R.id.tvNoFileMessage);
            tvNoFile.setVisibility(View.VISIBLE);
        }
        //区切り線を入れる
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mainActivity, layoutManager.getOrientation());
        rvFile.addItemDecoration(dividerItemDecoration);
        rvFile.setAdapter(new FileRecyclerViewAdapter(fileList, view.getContext()));
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
                intent.putExtra("FOLDER_ID", folderId);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private List<Map<String, Object>> getFileList(int id) {
        //DBヘルパー生成
        FileDatabaseHelper _helper = new FileDatabaseHelper(getActivity());
        return _helper.getFileList(id);
    }
}