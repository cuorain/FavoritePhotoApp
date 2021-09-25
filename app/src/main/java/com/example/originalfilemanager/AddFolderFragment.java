package com.example.originalfilemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddFolderFragment extends DialogFragment {
    private FileDatabaseHelper _helper;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //DBヘルパー生成
        _helper = new FileDatabaseHelper(getActivity());
        //アラートダイアログビルダーを生成
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //ダイアログのレイアウト
        View content = getLayoutInflater().inflate(R.layout.dialog_add_folder, null);
        builder.setView(content);
        // Positive Button
        builder.setPositiveButton(R.string.dialog_btn_confirm, new DialogButtonClickListener());
        // Negative Button
        builder.setNegativeButton(R.string.dialog_btn_ng, new DialogButtonClickListener());
        //ダイアログ生成
        return builder.create();
    }

    private class DialogButtonClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //処理終了時のトーストメッセージ
            String msg = "";
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //入力されたフォルダ名取得
                    EditText text = ((Dialog) dialog).findViewById(R.id.etFolderName);
                    String folderName = text.getText().toString();
                    if(TextUtils.isEmpty(folderName)){
                        //フォルダ名空はエラー
                        msg = getString(R.string.dialog_empty_error_toast);
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    }else {
                        //フォルダマスタへの登録
                        FolderModel folder = new FolderModel(folderName);
                        if(folder.AddFolder(_helper) == -1){
                            //DB登録エラー
                            msg = getString(R.string.dialog_db_error_toast);
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                        }else{
                            //登録成功
                            //TODO: ROWID使える？
                            msg = getString(R.string.dialog_success_toast);
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                        }
                    }
                case DialogInterface.BUTTON_NEGATIVE:
                    //何もせず閉じる
                    break;
            }
        }
    }
}
