package com.example.originalfilemanager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * ファイルを追加するアクティビティ
 */
public class AddFileActivity extends AppCompatActivity {

    //DBヘルパー
    private FileDatabaseHelper _helper;

    //DBに保存されるﾌｧｲﾙ名
    private String _fileName;

    //画面の構成要素
    private EditText etTitle;
    private EditText etMemo;
    private ImageView addIcon;
    private TextView tvInfoAddFile;
    private Button addFile;

    //キーボードを隠すために必要な要素
    private ConstraintLayout layout;
    private InputMethodManager inputMethodManager;

    //現在のフォルダ情報
    private String _folderName;
    private String _folderId;
    private String _fileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //タップ時に取得したデータ受取
        Intent intent = getIntent();
        if (intent != null) {
            _folderName = intent.getStringExtra("FOLDER_NAME");
            _folderId = intent.getStringExtra("FOLDER_ID");
            _fileId = intent.getStringExtra("FILE_ID");
        }
        if (TextUtils.isEmpty(_folderId) && TextUtils.isEmpty(_fileId)) {
            //TODO:フォルダかファイルが取得できてないとエラー
        }

        setContentView(R.layout.activity_add_file);

        layout = (ConstraintLayout) findViewById(R.id.activityAddFile);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //ファイル追加アナウンスの要素セット
        tvInfoAddFile = findViewById(R.id.tvInfoAddFile);

        //追加アイコンをタップされた時の動作
        addIcon = findViewById(R.id.ivAddIcon);
        addIcon.setOnClickListener(new addIconClickListener());

        //保存ボタンをタップした時の動作
        addFile = findViewById(R.id.btnAddFile);
        addFile.setOnClickListener(new addFileClickListener());

        //新規の時はボタンを非活性
        etTitle = findViewById(R.id.etTitle);
        addFile.setEnabled(!TextUtils.isEmpty(etTitle.getText()));
        //EditTextの変更を検知
        etTitle.addTextChangedListener(new textWatcher());
        etMemo = findViewById(R.id.etMemo);
        etMemo.addTextChangedListener(new textWatcher());

        //DBヘルパーオブジェクトの生成
        _helper = new FileDatabaseHelper(AddFileActivity.this);
        if (!TextUtils.isEmpty(_folderId)) {
            createViewAdd();
        } else if (!TextUtils.isEmpty(_fileId)) {
            createViewEdit();
        }

    }

    //新規の時の動作
    private void createViewAdd() {

    }

    //編集の時の動作
    private void createViewEdit() {
        FileModel file = new FileModel(getApplicationContext());
        //編集データのセット
        Map<String, Object> data = file.getData(_fileId);
        etTitle.setText((String) data.get("title"));
        etMemo.setText((String) data.get("memo"));
        _fileName = (String) data.get("filename");
        _folderId = (String) data.get("folder_id");
        //画像表示
        InputStream is = null;
        try {
            is = getApplicationContext().openFileInput(_fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                //ImageViewに生成したBitmapをセット
                addIcon.setImageBitmap(bitmap);
                //アナウンスのTextViewを非表示
                tvInfoAddFile.setVisibility(View.GONE);
            } else {
                //TODO:画像表示できなければエラー
            }
        } catch (FileNotFoundException ex) {
            //TODO:ファイルが見つからない時のエラー
            ex.printStackTrace();
        } catch (Exception ex) {
            //TODO:謎のエラー
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        //DBヘルパーオブジェクトの解放
        _helper.close();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //キーボードを隠す
        inputMethodManager.hideSoftInputFromWindow(layout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        //背景にフォーカスを移す
        layout.requestFocus();
        return false;
    }


    /**
     * ファイルの取得部分
     */
    //Activity Result APIを使う
    //ActivityResultLauncher　→　アクティビティを開始するためのクラス
    //registerForActivityResult　→　コールバックを登録するメソッド。ActivityResultLauncherを返す。
    //ActivityResultContracts　→　複数ファイルを選択できるようにしたり、処理の設定をする
    //ActivityResultCallback →　結果を受け取


    private ActivityResultContract actResContractImage = new ActivityResultContracts.GetContent();

    private ActivityResultContract actResContractVideo = new ActivityResultContracts.TakeVideo();

    private ActivityResultCallback<Uri> actResCallback = new ActivityResultCallback<Uri>() {
        //結果が戻ってきた時に実行されるのがonActivityResult()
        @Override
        public void onActivityResult(Uri result) {
            //結果が返ってこなかったら終了
            if (result == null) {
                return;
            }

            //Uriのスキームの判定（fileまたはcontent）、MIME Typeの取得
            String strUri = result.toString();
            String mimeType = "";
            if (result.getScheme().equals("file")) {
                //Uriから拡張子を取得
                String extension = MimeTypeMap.getFileExtensionFromUrl(strUri);
                //拡張子からMIME Typeを取得
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.getDefault()));
                //ﾌｧｲﾙ名の取得
                _fileName = new File(result.getPath()).getName();
            } else {
                //contextからcontentResolver取得
                ContentResolver contentResolver = getApplicationContext().getContentResolver();
                //ContentResolverからMIME Typeを取得
                mimeType = contentResolver.getType(result);
                //ﾌｧｲﾙ名の取得
                String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                Cursor cursor = contentResolver.query(result, projection, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        _fileName = cursor.getString(
                                cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
                    }
                    cursor.close();
                }
            }

            //MIMETypeから画像か動画か判別
            if (mimeType.contains("image")) {
                //画像の場合の処理
                try {
                    //UriからBitmapを生成
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                    //ImageViewに生成したBitmapをセット
                    addIcon.setImageBitmap(bitmap);
                    //アナウンスのTextViewを非表示
                    tvInfoAddFile.setVisibility(View.GONE);

                } catch (IOException ex) {
                    //TODO: エラーメッセージ表示
                    ex.printStackTrace();
                }
            } else if (mimeType.contains("video")) {
                //動画の場合の処理
            } else {
                //画像でも動画でもない場合の処理(エラー)
            }
        }
    };

    private ActivityResultLauncher<String> mGetFile = registerForActivityResult(actResContractImage, actResCallback);

    /**
     * 画像追加アイコンをクリックしたときのリスナクラス
     **/
    private class addIconClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //ActivityResultLauncher<Intent>を使う場合(動画取込でいろいろ試した名残)
//           Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//           intent.addCategory(Intent.CATEGORY_OPENABLE);
//           intent.setType("*/*");
//           intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
            //設定しておいたActivityResultLauncherをlaunchするだけで動いてくれる（詳細不明）
            mGetFile.launch("image/*");
        }
    }

    /**
     * 保存ボタンをクリックしたときのリスナクラス
     **/
    private class addFileClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //ファイルが取得できてなければエラー
            if (TextUtils.isEmpty(_fileName) || TextUtils.isEmpty(_fileId)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplication());
                builder.setMessage(R.string.filename_empty_dialog_message);
                builder.setPositiveButton(R.string.dialog_btn_ok, null);
                builder.show();
                return;
            }

            //既に保存しているファイルかチェック
            //TODO:重複保存のテストする
            boolean isDuplicated = true;
            try{
                InputStream is = getApplicationContext().openFileInput(_fileName);
            }catch (FileNotFoundException ex){
                isDuplicated = false;
            }

            //画像の保存
            if(!isDuplicated) {
                try {
                    FileOutputStream out = null;
                    try {
                        out = getApplicationContext().openFileOutput(_fileName, Context.MODE_PRIVATE);

                        ImageView selectImage = findViewById(R.id.ivAddIcon);
                        Bitmap bitmap = ((BitmapDrawable) selectImage.getDrawable()).getBitmap();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    } catch (FileNotFoundException ex) {
                        //TODO: エラーメッセージ表示
                    } finally {
                        if (out != null) {
                            out.close();
                            out = null;
                        }
                    }
                } catch (IOException ex) {
                    //TODO: エラーメッセージ表示
                    ex.printStackTrace();
                }
            }

            //タイトル取得
            etTitle = findViewById(R.id.etTitle);
            String title =etTitle.getText().toString();
            //メモ取得
            etMemo = findViewById(R.id.etMemo);
            String memo = etMemo.getText().toString();
            //TODO:空白の時はどうする？（PKとかも）

            //DBヘルパーからDB接続オブジェクト取得
            SQLiteDatabase db = _helper.getWritableDatabase();
            SQLiteStatement stmt = null;
            db.beginTransaction();
            //既に登録されているか検索
            if (!TextUtils.isEmpty(_fileId)) {
                //更新
                String sql = "SELECT * FROM filem where _id = " + _fileId;
                //検索結果があればUPDATE
                if (db.rawQuery(sql, null).moveToNext()) {
                    try {
                        sql = "UPDATE filem SET title = ?, memo = ?, filename = ?, folder_id = ? where _id = ?";
                        stmt = db.compileStatement(sql);
                        //Updateの部分
                        stmt.bindString(1, title);
                        stmt.bindString(2, memo);
                        stmt.bindString(3, _fileName);
                        stmt.bindLong(4, Integer.parseInt(_folderId));
                        stmt.bindLong(5, Integer.parseInt(_fileId));
                        stmt.executeUpdateDelete();
                        db.setTransactionSuccessful();
                    } finally {
                        if (stmt != null) {
                            stmt.close();
                        }
                        db.endTransaction();
                    }
                }
            } else {
                //登録
                try {
                    //インサート&アップデート文(ON DUPLICATE KEY UPDATE)
                    String insertSql = "INSERT INTO filem (title, memo, filename, folder_id) VALUES (?, ?, ?, ?)";
                    //プリペアードステートメント取得
                    stmt = db.compileStatement(insertSql);
                    //変数バインド
                    //Insertの部分
                    stmt.bindString(1, title);
                    stmt.bindString(2, memo);
                    stmt.bindString(3, _fileName);
                    stmt.bindLong(4, Integer.parseInt(_folderId));
                    //インサート文実行
                    long rowid = stmt.executeInsert();
                    //インサートした行Noを格納
                    //TODO:idとリンクできているかのチェック
                    _fileId = String.valueOf(rowid);
                    db.setTransactionSuccessful();
                } finally {
                    if (stmt != null) {
                        stmt.close();
                    }
                    db.endTransaction();
                }
            }
            //登録内容保持
            etTitle.setText(title);
            etMemo.setText(memo);
            //保存ボタンを非活性に
            addFile.setEnabled(false);

            //登録完了ﾒｯｾｰｼﾞ
            Toast.makeText(getApplicationContext(), "保存しました。", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * EditTextの変更を検知するクラス
     **/
    private class textWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //入力がされていたらアクティブにする
            addFile.setEnabled(!TextUtils.isEmpty(s));
        }
    }
}