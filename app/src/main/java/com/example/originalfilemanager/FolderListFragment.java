package com.example.originalfilemanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * フォルダの一覧表示画面
 */
public class FolderListFragment extends Fragment {

    RecyclerView rvFolder;
    List<Map<String, Object>> folderList;
    FolderRecyclerViewAdapter adapter;


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
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //フラグメント内でオプションメニューを使う時に必須の設定
        setHasOptionsMenu(true);
        //所属しているアクティビティ取得
        MainActivity mainActivity = (MainActivity) getActivity();

        //表示する画面をインフレート
        View view = inflater.inflate(R.layout.folder_list, container, false);
        //アクションバーにタイトルセット
        mainActivity.setTitle("フォルダ選択");

        //リサイクラビュー取得
        rvFolder = view.findViewById(R.id.rvFolderList);
        //LayoutManager生成
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        //RecyclerViewにレイアウトマネージャー設定
        rvFolder.setLayoutManager(layoutManager);

        //リストデータ取得
        folderList = getFolderList();
        //データがなければ、フォルダ作成するようにﾒｯｾｰｼﾞ
        if (folderList.size() == 0) {
            TextView tvNoFolder = view.findViewById(R.id.tvNoFolderMessage);
            tvNoFolder.setVisibility(View.VISIBLE);
        }
        //アダプタオブジェクト生成
        adapter = new FolderRecyclerViewAdapter(folderList, view.getContext());
        //リサイクラビューにアダプタオブジェクトをセット
        rvFolder.setAdapter(adapter);
        //区切り線を入れる
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mainActivity, layoutManager.getOrientation());
        rvFolder.addItemDecoration(dividerItemDecoration);

        //子フラグメントから結果を受け取るための設定
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener(){
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String getResult = result.getString("result");
                //OK返ってきたらリスト更新
                if(getResult.equals("OK")){
                    //一旦リストをクリアしてから再検索データを挿入。
                    folderList.clear();
                    folderList.addAll(getFolderList());
                    //データが変わったことを通知。
                    adapter.notifyDataSetChanged();
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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



    private List<Map<String, Object>> getFolderList() {
        //DBヘルパー生成
        FileDatabaseHelper _helper = new FileDatabaseHelper(getActivity());
        return _helper.getAllFolderList();
    }

    public void reloadFolderList(){
        rvFolder.getAdapter().notifyDataSetChanged();
    }
}