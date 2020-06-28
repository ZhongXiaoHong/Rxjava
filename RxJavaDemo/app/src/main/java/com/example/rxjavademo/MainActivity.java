package com.example.rxjavademo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rxjavademo.api.WanAndroidApi;
import com.example.rxjavademo.bean.LoginResponse;
import com.example.rxjavademo.bean.ProjectBean;
import com.example.rxjavademo.bean.ProjectItem;
import com.example.rxjavademo.bean.RegisterResponse;
import com.example.rxjavademo.net.NetManager;
import com.jakewharton.rxbinding2.view.RxView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.observers.BiConsumerSingleObserver;
import io.reactivex.plugins.RxJavaPlugins;
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

                .compose(rxdu())//TODO 对以下线程切换封装
//              .subscribeOn(Schedulers.io())//TODO 表示上面一节运行在io线程
//              .observeOn(AndroidSchedulers.mainThread())//TODO  下面一节运行在MainThread
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


    @SuppressLint("CheckResult")
    public void getProjectCategory(View view) {
        NetManager.createService(WanAndroidApi.class)
                .getProjectCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ProjectBean>() {
                    @Override
                    public void accept(ProjectBean projectCategory) throws Exception {
                        System.out.println(projectCategory.toString());
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void getProjectItem(View view) {
        NetManager.createService(WanAndroidApi.class)
                .getItemsInCategory(1, 294)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ProjectItem>() {
                    @Override
                    public void accept(ProjectItem projectItem) throws Exception {
                        System.out.println(projectItem.toString());
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void getProjectItemList(View view) {
        RxView.clicks(view).throttleFirst(2000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object unit) {

                        NetManager.createService(WanAndroidApi.class)
                                .getProjectCategory()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<ProjectBean>() {
                                    @Override
                                    public void accept(ProjectBean projectCategory) throws Exception {

                                        for (ProjectBean.DataBean datum : projectCategory.getData()) {

                                            NetManager.createService(WanAndroidApi.class)
                                                    .getItemsInCategory(1, datum.getId())
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Consumer<ProjectItem>() {
                                                        @Override
                                                        public void accept(ProjectItem projectItem) throws Exception {
                                                            System.out.println(projectItem.toString());
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                });
    }
//Observable.just("")
//        .observeOn(Schedulers.io())//TODO 以下代码执行网络请求 切换io线程执行
//            .flatMap(new Function<Object, ObservableSource<ProjectBean>>() {
//        @Override
//        public ObservableSource<ProjectBean> apply(Object o) throws Exception {
//            System.out.println("****getProjectCategory*********");
//            return NetManager.createService(WanAndroidApi.class).getProjectCategory();//TODO 获取总的数据集
//        }
//    })


    ArrayList<ProjectItem> datas = new ArrayList<>();

    @SuppressLint("CheckResult")
    public void getProjectItemList2(View view) {

//        Observable.just("1","2","3","4","5")
//                .collect(new Callable<ArrayList<String>>(){
//
//                    @Override
//                    public ArrayList<String> call() throws Exception {
//                        return new ArrayList<>();
//                    }
//                }, new BiConsumer<ArrayList<String>, String>() {
//                    @Override
//                    public void accept(ArrayList<String> strings, String s) throws Exception {
//                        strings.add(s);
//                    }
//                }).subscribe(new Consumer<ArrayList<String>>() {
//            @Override
//            public void accept(ArrayList<String> strings) throws Exception {
//                System.out.println("****accept*********"+strings.toString());
//            }
//        });
        Observable.just(1,2,3,4)
                .collect(new Callable<List<Integer>>() { //创建数据结构
                    @Override
                    public List<Integer> call() {
                        return new ArrayList<Integer>();
                    }
                }, new BiConsumer<List<Integer>, Integer>() { //收集器
                    @Override
                    public void accept(@NonNull List<Integer> integers, @NonNull Integer integer) throws Exception {
                        integers.add(integer);
                    }
                })
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(@NonNull List<Integer> integers) throws Exception {

                        System.out.println("****【" + 4 + "】*********" +integers.toString());
                    }
                });



        RxView.clicks(view).throttleFirst(2000, TimeUnit.MILLISECONDS)//TODO 这个再主线程执行
                .observeOn(Schedulers.io())//TODO 以下代码执行网络请求 切换io线程执行
                .flatMap(new Function<Object, ObservableSource<ProjectBean>>() {
                    @Override
                    public ObservableSource<ProjectBean> apply(Object o) throws Exception {
                        return NetManager.createService(WanAndroidApi.class).getProjectCategory();//TODO 获取总的数据集
                    }
                })
                .flatMap(new Function<ProjectBean, ObservableSource<ProjectBean.DataBean>>() {
                    @Override
                    public ObservableSource<ProjectBean.DataBean> apply(ProjectBean projectBean) throws Exception {
                        return Observable.fromIterable(projectBean.getData());//TODO 相当于for循环，分发每一个数据
                    }
                })
                .flatMap(new Function<ProjectBean.DataBean, ObservableSource<ProjectItem>>() {//TODO 会多次被调用，每次接收一个item
                    @Override
                    public ObservableSource<ProjectItem> apply(ProjectBean.DataBean dataBean) throws Exception {
                        return NetManager.createService(WanAndroidApi.class).getItemsInCategory(1, dataBean.getId());
                    }
                })
                .subscribe(new Consumer<ProjectItem>() {
                    @Override
                    public void accept(ProjectItem projectItem) throws Exception {
                        System.out.println(SystemClock.currentThreadTimeMillis()+"*************" +projectItem.toString());
                    }
                });
  /*              .collect(new Callable<ArrayList<ProjectItem>>() {
                    @Override
                    public ArrayList<ProjectItem> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<ArrayList<ProjectItem>, ProjectItem>() {
                    @Override
                    public void accept(ArrayList<ProjectItem> projectItems, ProjectItem projectItem) throws Exception {
                        projectItems.add(projectItem);

                    }
                })
//                .observeOn(AndroidSchedulers.mainThread())//TODO 以下代码需要更新UI,切换主线程执行
//                .subscribe(new SingleObserver<ArrayList<ProjectItem>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        System.out.println("****onSubscribe*********");
//                    }
//
//                    @Override
//                    public void onSuccess(ArrayList<ProjectItem> projectItems) {
//                        System.out.println("****onSuccess*********"+projectItems.toString());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        System.out.println("*****onError********"+e.toString());
//                    }
//                });
                .subscribe(new SingleObserver<ArrayList<ProjectItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("***********");
                    }

                    @Override
                    public void onSuccess(ArrayList<ProjectItem> projectItems) {
                        for (int i = 0; i < projectItems.size(); i++) {
                            System.out.println("****【" + i + "】*********" + projectItems.get(i).toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("***********");
                    }
                });*/
    }

    //TODO 请求注册---->更新UI------->请求登录--------->更新UI
    @SuppressLint("CheckResult")
    public void doOnnext(View view) {

        NetManager.createService(WanAndroidApi.class)
                .register("laofang", "888168", "888168")
                .subscribeOn(Schedulers.io())//TODO 上面代码在io线程执行
                .observeOn(AndroidSchedulers.mainThread())//TODO 下面代码在主线程执行
//                .subscribe(new Consumer<RegisterResponse>() {
//                    @Override
//                    public void accept(RegisterResponse registerResponse) throws Exception {
//
//                    }
//                })
                .doOnNext(new Consumer<RegisterResponse>() {
                    @Override
                    public void accept(RegisterResponse registerResponse) throws Exception {
                        Toast.makeText(MainActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                    }
                })
                .observeOn(Schedulers.io())//TODO 下面代码在io线程执行
                .flatMap(new Function<RegisterResponse, ObservableSource<LoginResponse>>() {
                    @Override
                    public ObservableSource<LoginResponse> apply(RegisterResponse registerResponse) throws Exception {
                        return NetManager.createService(WanAndroidApi.class).login("laofang", "888168");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//TODO 下面代码在主线程执行
                .subscribe(new Consumer<LoginResponse>() {
                    @Override
                    public void accept(LoginResponse loginResponse) throws Exception {
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    }
                });

    }

    public void test() {

        //TODO ObservableCreat  自定义source传进去
        Observable.create(

                //TODO 【自定义source】
                new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        emitter.onNext("AAAAA");
                    }
                }
        )
                //TODO 相当于ObservableCreat.map
                .map(new Function<String, Integer>() {

                    @Override
                    public Integer apply(String s) throws Exception {
                        return 10086;
                    }
                })
                //TODO 相当于ObservableMap.subscribe
                .subscribe(

                        //TODO 【自定义观察者】终点
                        new Observer<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Integer integer) {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        }
                );


    }

    Disposable resultDisposable;
    public void testIntecept() {

        //TODO ObservableCreat  自定义source传进去
         resultDisposable = Observable.just("")
                 .subscribe(new Consumer<String>() {
                                @Override
                                 public void accept(String s) throws Exception {

                                  }
                 }
        );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resultDisposable!=null){
            resultDisposable.dispose();
        }
    }


    public void testSubcribeOn() {

        //TODO ObservableCreat  自定义source传进去
        Observable.create(

                //TODO 【自定义source】
                new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        emitter.onNext("AAAAA");
                    }
                }
        )
        .subscribeOn(
                Schedulers.io()
        )

        .subscribe(

         //TODO 【自定义观察者】终点
         new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(String str) {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        }
         );


    }
}
