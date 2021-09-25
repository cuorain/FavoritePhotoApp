package com.example.originalfilemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addFragment(new FolderListFragment());
    }

    private void addFragment(Fragment fragment){
        //フラグメントマネージャー取得
        FragmentManager fragmentManager = getSupportFragmentManager();
        //トランザクション開始
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //メインアクティビティにフラグメント追加
        transaction.add(R.id.activityMain, fragment);
        // 戻るボタンで戻ってこれるように
        transaction.addToBackStack(null);
        //コミットして、フラグメント反映
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }
}