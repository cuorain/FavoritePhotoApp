package com.example.originalfilemanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileDatabaseHelper extends SQLiteOpenHelper {
    //データベースﾌｧｲﾙ名の定数
    private static final String DATABASE_NAME = "myfilememo.db";
    //バージョン情報
    private static final int DATABASE_VERSION = 1;

    public FileDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //テーブル作成SQL
        //filem ファイルマスタ作成
        String sql = "CREATE TABLE filem (_id INTEGER PRIMARY KEY autoincrement, title TEXT, memo TEXT, filename TEXT, folder_id INTEGER);";
        db.execSQL(sql);

        //folderm フォルダマスタ作成
        sql = "CREATE TABLE folderm (_id INTEGER PRIMARY KEY autoincrement, folderName TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Map<String, Object>> getAllFolderList(){
        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        //select文
        String selectSql = "SELECT folderName FROM folderm";
        //戻り値のリスト
        List<Map<String, Object>> result = new ArrayList<>();

        //select実行 try-with-resource文
        try(Cursor cursor = db.rawQuery(selectSql, null)) {
            //取得したデータ(Cursor)をList化
            while (cursor.moveToNext()) {
                Map<String, Object> folder = new HashMap<>();
                folder.put("folderName", cursor.getString(cursor.getColumnIndex("folderName")));
                result.add(folder);
            }
        }
        db.endTransaction();
        return result;
    }
}
