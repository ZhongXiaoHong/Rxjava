package com.example.rxjavademo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.rxjavademo.api.WanAndroidApi;
import com.example.rxjavademo.bean.ProjectCategory;
import com.example.rxjavademo.net.NetManager;

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
import io.reactivex.functions.Consumer;
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
        dialog.show();//TODO 显示加载框

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = donet(path);//TODO 请求网络
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

    public static <UD> ObservableTransformer<UD, UD> rxdu() {
        return new ObservableTransformer<UD, UD>() {
            @Override
            public ObservableSource<UD> apply(Observable<UD> upstream) {
                return upstream.subscribeOn(Schedulers.io())//TODO 表示上面一节运行在io线程
                        .observeOn(AndroidSchedulers.mainThread());//TODO  下面一节运行在MainThread
            }
        };
    }

    //   .compose(rxdu())//TODO 对以下线程切换封装


    public void showImgByRx(View view) {
        ProgressDialog dialog = new ProgressDialog(this);

        Observable
                .just(path2)//TODO【2.分发事件】
                .map(new Function<String, Bitmap>() {//TODO【3.卡片式拦截事件，将String转换成Bitmap】
                    @Override
                    public Bitmap apply(String s) throws Exception {
                        return donet(s);//请求服务器
                    }
                })
                .subscribeOn(Schedulers.io())//TODO 表示上面一节运行在io线程
                .observeOn(AndroidSchedulers.mainThread())//TODO  下面一节运行在MainThread
                .subscribe(new Observer<Bitmap>() {

                    //TODO 【1.预备分发事件】
                    @Override
                    public void onSubscribe(Disposable d) {
                        dialog.show();
                    }

                    //TODO 【4.拿到事件】
                    @Override
                    public void onNext(Bitmap bitmap) {
                        img.setImageBitmap(bitmap);
                    }

                    //出现错误
                    @Override
                    public void onError(Throwable e) {

                    }

                    //TODO 【5.完成事件】
                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                    }
                });


    }


    public void showImgByRx2(View view) {
        ProgressDialog dialog = new ProgressDialog(this);

        Observable
                .just(path2)//TODO【2.分发事件】
                .map(new Function<String, Bitmap>() {//TODO【3.卡片式拦截事件，将String转换成Bitmap】
                    @Override
                    public Bitmap apply(String s) throws Exception {
                        return donet(s);//请求服务器
                    }
                })
                .map(new Function<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap apply(Bitmap bitmap) throws Exception {
                        return addFlagForBitmap(bitmap);//调用增加水印的方法
                    }
                })
                .compose(rxdu())
//                .subscribeOn(Schedulers.io())//TODO 表示上面一节运行在io线程
//                .observeOn(AndroidSchedulers.mainThread())//TODO  下面一节运行在MainThread
                .subscribe(new Observer<Bitmap>() {

                    //TODO 【1.预备分发事件】
                    @Override
                    public void onSubscribe(Disposable d) {
                        dialog.show();
                    }

                    //TODO 【4.拿到事件】
                    @Override
                    public void onNext(Bitmap bitmap) {
                        img.setImageBitmap(bitmap);
                    }

                    //出现错误
                    @Override
                    public void onError(Throwable e) {

                    }

                    //TODO 【5.完成事件】
                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                    }
                });


    }

    private Bitmap addFlagForBitmap(Bitmap bitmap) {
        //TODO 这里假装增加了水印
        return bitmap;
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


    public void getProjectCategory(View view) {
        NetManager.getInstance()
                .createService(WanAndroidApi.class)
                .getProjectCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ProjectCategory>() {
                    @Override
                    public void accept(ProjectCategory projectCategory) throws Exception {
                        System.out.println(projectCategory.toString());
                    }
                });
    }

}
