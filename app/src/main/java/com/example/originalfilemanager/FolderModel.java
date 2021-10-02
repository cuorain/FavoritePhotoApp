package com.example.originalfilemanager;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

public class FolderModel {
    public String folderName;
    //コンストラクタ
    FolderModel(String folderName){
        this.folderName = folderName;
    }

    public int AddFolder(FileDatabaseHelper _helper){
        //DBヘルパーからDB接続オブジェクト取得
        SQLiteDatabase db = _helper.getWritableDatabase();
        SQLiteStatement stmt = null;
        db.beginTransaction();
        int result;
        try {
            //インサート文
            String insertSql = "INSERT INTO folderm (folderName) VALUES (?)";
            //プリペアードステートメント取得
            stmt = db.compileStatement(insertSql);
            //変数バインド
            stmt.bindString(1, folderName);
            //インサート文実行 RowIdを返す（エラーなら-1）
            result = (int) stmt.executeInsert();
            db.setTransactionSuccessful();
        }finally {
            if(stmt != null) {
                stmt.close();
            }
            db.endTransaction();
        }
        return result;
    }
}
