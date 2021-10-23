package com.example.originalfilemanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.HashMap;
import java.util.Map;

public class FileModel {
    private Context _context;

    public FileModel(Context context) {
        _context = context;
    }

    public Map<String, Object> getData(String id) {
        FileDatabaseHelper dbHelper = new FileDatabaseHelper(_context);
        //DBヘルパーからDB接続オブジェクト取得
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        db.beginTransaction();
        //select文
        Map<String, Object> file = new HashMap<>();
        String selectSql = "SELECT title, memo, filename, folder_id FROM filem WHERE _id = " + id;
        //select実行 try-with-resource文
        try (Cursor cursor = db.rawQuery(selectSql, null)) {
            //取得したデータ(Cursor)を格納
            cursor.moveToNext();
            file.put("title", cursor.getString(cursor.getColumnIndex("title")));
            file.put("memo", cursor.getString(cursor.getColumnIndex("memo")));
            file.put("filename", cursor.getString(cursor.getColumnIndex("filename")));
            file.put("folder_id", cursor.getString(cursor.getColumnIndex("folder_id")));
        }
        db.endTransaction();

        return file;
    }

    public int DeleteFile(String fileId) {
        FileDatabaseHelper dbHelper = new FileDatabaseHelper(_context);
        //DBヘルパーからDB接続オブジェクト取得
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteStatement stmt = null;
        int result = -1;
        try {
            db.beginTransaction();
            String sql = "DELETE FROM filem where _id = ?";
            stmt = db.compileStatement(sql);
            stmt.bindString(1, fileId);
            result = stmt.executeUpdateDelete();
            db.setTransactionSuccessful();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            db.endTransaction();
        }
        return result;
    }
}
