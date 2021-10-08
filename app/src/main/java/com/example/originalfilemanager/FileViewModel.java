package com.example.originalfilemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AlertDialog;
import androidx.core.os.HandlerCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileViewModel extends ViewModel {
    public String _title;
    public MutableLiveData<String> _memo;
    public MutableLiveData<String> _filename;
    public MutableLiveData<Bitmap> _image;
    public Context _context;

    public FileViewModel(Context context){
        _context = context;
    }

    public void loadData(String fileId){
        FileModel file = new FileModel(_context);
        Map<String, Object> data = file.getData(fileId);
        _memo = new MutableLiveData<>();
        _memo.setValue((String)data.get("memo"));
        _filename = new MutableLiveData<>();
        _filename.setValue((String)data.get("filename"));
        //画像描画のための非同期処理
        Looper looper = Looper.getMainLooper();
        Handler handler = HandlerCompat.createAsync(looper);
//        ImageViewBackgroundDrawing backgroundDrawing = new ImageViewBackgroundDrawing(handler);
        ImageViewBackgroundDrawing backgroundDrawing = new ImageViewBackgroundDrawing();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(backgroundDrawing);
    }

    public MutableLiveData<String> getMemo(){
        if(_memo == null){
            _memo = new MutableLiveData<String>();
        }
        return _memo;
    }
    public MutableLiveData<Bitmap> getImage(){
        if(_image == null){
            _image = new MutableLiveData<Bitmap>();
        }
        return _image;
    }

    private class ImageViewBackgroundDrawing implements Runnable{
        //一旦消してるUiにもどさずにいけるかもしれないので
//        //スレッドセーフにするため、finalつける
//        public final Handler _handler;
//
//        //コンストラクタ
//        public ImageViewBackgroundDrawing(Handler handler){
//            _handler = handler;
//        }

        //ワーカースレッドで非同期処理をする
        @WorkerThread
        @Override
        public void run() {
            InputStream is = null;
            try{
                is = _context.openFileInput(_filename.getValue());
            }catch (FileNotFoundException ex){
                //TODO:ファイルが見つからない時のエラー
                ex.printStackTrace();
            }catch (Exception ex){
                //TODO:謎のエラー
                ex.printStackTrace();
            }
            _image = new MutableLiveData<>();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                _image.postValue(bitmap);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            String s = "";
        }
    }
}
