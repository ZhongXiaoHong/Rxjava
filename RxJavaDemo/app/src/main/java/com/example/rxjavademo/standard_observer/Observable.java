package com.example.rxjavademo.standard_observer;

public interface Observable<T> {

    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
    void pushMessage(T t);
}
