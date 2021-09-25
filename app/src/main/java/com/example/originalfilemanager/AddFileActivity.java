package com.example.originalfilemanager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
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
import java.util.Locale;

/**
 * ファイルを追加するアクティビティ
 */
public class AddFileActivity extends AppCompatActivity {

    //DBヘルパー
    private FileDatabaseHelper _helper;

    //DBに保存されるﾌｧｲﾙ名
    private String fileName;

    //画面の構成要素
    private EditText etTitle;
    private EditText etMemo;
    private ImageView addIcon;
    private TextView tvInfoAddFile;
    private Button addFile;

    //キーボードを隠すために必要な要素
    private ConstraintLayout layout;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);

        layout = (ConstraintLayout)findViewById(R.id.activityAddFile);
        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

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

        //DBヘルパーオブジェクトの生成
        _helper = new FileDatabaseHelper(AddFileActivity.this);
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
        return  false;
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
            if(result == null) {
                return;
            }

            //Uriのスキームの判定（fileまたはcontent）、MIME Typeの取得
            String strUri = result.toString();
            String mimeType = "";
            if(result.getScheme().equals("file")){
                //Uriから拡張子を取得
                String extension = MimeTypeMap.getFileExtensionFromUrl(strUri);
                //拡張子からMIME Typeを取得
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.getDefault()));
                //ﾌｧｲﾙ名の取得
                fileName = new File(result.getPath()).getName();
            }else{
                //contextからcontentResolver取得
                ContentResolver contentResolver = getApplicationContext().getContentResolver();
                //ContentResolverからMIME Typeを取得
                mimeType = contentResolver.getType(result);
                //ﾌｧｲﾙ名の取得
                String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                Cursor cursor = contentResolver.query(result, projection, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        fileName = cursor.getString(
                                cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
                    }
                    cursor.close();
                }
            }

            //MIMETypeから画像か動画か判別
            if(mimeType.contains("image")){
                //画像の場合の処理
                try {
                    //UriからBitmapを生成
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                    //ImageViewに生成したBitmapをセット
                    addIcon.setImageBitmap(bitmap);
                    //アナウンスのTextViewを非表示
                    tvInfoAddFile.setVisibility(View.GONE);

                }catch (IOException ex){
                    //TODO: エラーメッセージ表示
                    ex.printStackTrace();
                }
            }else if(mimeType.contains("video")){
                //動画の場合の処理
            }else{
                //画像でも動画でもない場合の処理(エラー)
            }
        }
    };

    private ActivityResultLauncher<String> mGetFile = registerForActivityResult(actResContractImage, actResCallback);

    /**
     * 画像追加アイコンをクリックしたときのリスナクラス
     **/
    private class addIconClickListener implements View.OnClickListener{
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
    private class addFileClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //ファイルが取得できてなければエラー
            if(TextUtils.isEmpty(fileName)){
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplication());
                builder.setMessage(R.string.filename_empty_dialog_message);
                builder.setPositiveButton(R.string.dialog_btn_ok, null);
                builder.show();
                return;
            }
            //画像の保存
            try {
                FileOutputStream out = null;
                try {
                    out = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);

                    ImageView selectImage = findViewById(R.id.ivAddIcon);
                    Bitmap bitmap = ((BitmapDrawable)selectImage.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (FileNotFoundException ex) {
                    //TODO: エラーメッセージ表示
                } finally {
                    if (out != null) {
                        out.close();
                        out = null;
                    }
                }
            }
            catch (IOException ex){
                //TODO: エラーメッセージ表示
                ex.printStackTrace();
            }

            //タイトル取得
            etTitle = findViewById(R.id.etTitle);
            //メモ取得
            etMemo = findViewById(R.id.etMemo);
            //TODO:空白の時はどうする？（PKとかも）

            //DBヘルパーからDB接続オブジェクト取得
            SQLiteDatabase db = _helper.getWritableDatabase();
            SQLiteStatement stmt = null;
            db.beginTransaction();
            try {
                //インサート文
                //TODO:フォルダIDの登録
                String insertSql = "INSERT INTO myfilememo (title, memo, filename) VALUES (?, ?, ?)";
                //プリペアードステートメント取得
                stmt = db.compileStatement(insertSql);
                //変数バインド
                stmt.bindString(1, etTitle.getText().toString());
                stmt.bindString(2, etMemo.getText().toString());
                stmt.bindString(3, fileName);
                //インサート文実行
                stmt.executeInsert();
            }finally {
                if(stmt != null) {
                    stmt.close();
                }
                db.endTransaction();
            }

            //入力を空にする
            etTitle.setText("");
            etMemo.setText("");
            //保存ボタンを非活性に
            addFile.setEnabled(false);
        }
    }

    /**
     * EditTextの変更を検知するクラス
     **/
    private class textWatcher implements TextWatcher{
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