import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //データベースﾌｧｲﾙ名の定数
    private static final String DATABASE_NAME = "myfilememo.db";
    //バージョン情報
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //テーブル作成SQL
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE myfilememo (");
        sb.append("_id INTEGER PRIMARY KEY,");
        sb.append("title TEXT,");
        sb.append("memo TEXT,");
        sb.append("filename TEXT);");
        String sql = sb.toString();

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
