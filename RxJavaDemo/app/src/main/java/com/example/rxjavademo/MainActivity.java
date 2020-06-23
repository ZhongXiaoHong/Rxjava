package com.example.rxjavademo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    ImageView img;
    String path = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2310890073,3469009192&fm=26&gp=0.jpg";
    String path2 = "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1424239015,525755483&fm=26&gp=0.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.img);
    }

    public void showImg(View view) {

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = donet(path);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            img.setImageBitmap(bitmap);
                        }
                    });
                    dialog.dismiss();
                } catch (Exception e) {
                    System.out.println("-----------" + e.toString());
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public static ObservableTransformer rxdu() {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream.subscribeOn(Schedulers.io())//TODO 表示上面一节运行在io线程
                        .observeOn(AndroidSchedulers.mainThread());//TODO  下面一节运行在MainThread
            }
        };
    }


    public void showImgByRx(View view) {
        ProgressDialog dialog = new ProgressDialog(this);

        Observable
                .just(path2)
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) throws Exception {
                        return donet(s);
                    }
                })

                .compose(rxdu())//TODO 对以下线程切换封装
//                .subscribeOn(Schedulers.io())//TODO 表示上面一节运行在io线程
//                .observeOn(AndroidSchedulers.mainThread())//TODO  下面一节运行在MainThread
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        dialog.show();
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        img.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                    }
                });


    }

    Bitmap donet(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(8000);
        InputStream in = connection.getInputStream();

        Bitmap bitmap = BitmapFactory.decodeStream(in);
        in.close();
        connection.disconnect();
        return bitmap;
    }

}
