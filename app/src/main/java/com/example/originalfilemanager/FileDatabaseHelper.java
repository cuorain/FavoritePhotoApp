package com.example.originalfilemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        StringBuilder sb1 = new StringBuilder();
        sb1.append("CREATE TABLE filem (");
        sb1.append("_id INTEGER PRIMARY KEY autoincrement,");
        sb1.append("title TEXT,");
        sb1.append("memo TEXT,");
        sb1.append("filename TEXT,");
        sb1.append("folder_id INTEGER);");
        String sql1 = sb1.toString();
        db.execSQL(sql1);

        StringBuilder sb2 = new StringBuilder();
        sb2.append("CREATE TABLE folderm (");
        sb2.append("_id INTEGER PRIMARY KEY autoincrement,");
        sb2.append("folderName TEXT);");
        String sql2 = sb2.toString();

        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
