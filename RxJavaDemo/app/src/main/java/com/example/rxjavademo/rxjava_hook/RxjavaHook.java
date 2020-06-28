package com.example.rxjavademo.rxjava_hook;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;

public class RxjavaHook {

    public static void main(String[] args) {


        RxJavaPlugins.setOnObservableAssembly(new Function<Observable, Observable>() {
            @Override
            public Observable apply(Observable observable) throws Exception {
                return observable;
            }
        });


        Observable.just("132")
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                    }
                });
    }
}
