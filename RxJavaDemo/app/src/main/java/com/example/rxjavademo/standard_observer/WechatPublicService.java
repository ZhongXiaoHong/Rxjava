package com.example.rxjavademo.standard_observer;

import java.util.ArrayList;
import java.util.List;

public class WechatPublicService implements Observable<String> {


    //TODO 保存所有的观察者
    private List<Observer> observers = new ArrayList<>();

    private String msg;

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.onReceive(msg);
        }
    }

    @Override
    public void pushMessage(String s) {
        this.msg = s;
        //TODO 发消息通知观察者
        notifyObservers();
    }
}
