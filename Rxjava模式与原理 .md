> Rxjava的Hook点

```java
Observable.just("")
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {

            }
        });
```

比如调用Observable.just("")，实际上是创建了ObservableJust

```java
  public static <T> Observable<T> just(T item) {
        ObjectHelper.requireNonNull(item, "item is null");//检查空值
        return RxJavaPlugins.onAssembly(new ObservableJust<T>(item));//创建ObservableJust
    }

```

见上面代码，创建ObservableJust对象后，还会使用RxJavaPlugins.onAssembly包装它，如下：

```java
   public static <T> Observable<T> onAssembly(@NonNull Observable<T> source) {
       //TODO 默认onObservableAssembly是空的，下面if代码块不执行
        Function<? super Observable, ? extends Observable> f = onObservableAssembly;
        if (f != null) {
            return apply(f, source);
        }
        return source;
    }
```

默认onObservableAssembly是空的，if代码块是不执行，实际上onAssembly是相当于预留了后门，方便Hook作全局控制，因为onAssembly是静态的方法，实际上在使用各种操作符的过程，是发生下图所示的过程

![6261700](image/6261700.png)



> RxJava的观察者模式

```java
        //TODO 【1】创建一个被观察者
        Observable observable=   Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("AAAAA");
            }
        });
        
        //TODO【2】 创建一个观察者
        Observer observer =  new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        
        //TODO 【3】观察者订阅被观察者
        observable.subscribe(observer);
```

 首先看第一步【1】创建一个被观察者

```java
public static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
        ObjectHelper.requireNonNull(source, "source is null");//TODO  空值检查
        //TODO onAssembly---是全局Hook预留操作
        //TODO 可以看到creat方法会创建一个ObservableCreate对象
        //TODO 并且传入一个参数source，属于ObservableOnSubscribe<T>类型
        return RxJavaPlugins.onAssembly(new ObservableCreate<T>(source));
    }
```

```java
public interface ObservableOnSubscribe<T> {
    
    void subscribe(@NonNull ObservableEmitter<T> emitter) throws Exception;
}
```

执行完上面代码，一个Observable持有一个ObservableOnSubscribe：

![6262102](image/6262102.png)

 【2】 创建一个观察者

![6262106](image/6262106.png)

```java
【3】观察者订阅被观察者
observable.subscribe(observer);
```

```java
  public final void subscribe(Observer<? super T> observer) {
      //TODO 空值校验，不是重点
        ObjectHelper.requireNonNull(observer, "observer is null");
        try {
            
            observer = RxJavaPlugins.onSubscribe(this, observer);
    //TODO 空值校验，不是重点
            ObjectHelper.requireNonNull(observer, "The RxJavaPlugins.onSubscribe hook returned a null Observer. Please change the handler provided to RxJavaPlugins.setOnObservableSubscribe for invalid null returns. Further reading: https://github.com/ReactiveX/RxJava/wiki/Plugins");
            
		//TODO【关键】 调用了subscribeActual
            subscribeActual(observer);
        } catch (NullPointerException e) { // NOPMD
            throw e;
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            // can't call onError because no way to know if a Disposable has been set or not
            // can't call onSubscribe because the call might have set a Subscription already
            RxJavaPlugins.onError(e);

            NullPointerException npe = new NullPointerException("Actually not, but can't throw other exceptions due to RS");
            npe.initCause(e);
            throw npe;
        }
    }
```

可以看到接着会调用抽象方法的subscribeActual，这个例子中是调用ObservableCreate的subscribeActual方法，如下：

```java
   protected void subscribeActual(Observer<? super T> observer) {
       //ToDO 创建一个发射器
        CreateEmitter<T> parent = new CreateEmitter<T>(observer);
       //TODO 调用 observer的onSubscribe方法，
       //TODO 这就是为什么事件还没开始发射，observer的onSubscribe方法
       //TODO 就被回调的原因
        observer.onSubscribe(parent);

        try {
            //TODO  这里的source是什么？
            //就是Observable持有的ObservableOnSubscribe类型对象
            //并将发射器作为参数传入
            source.subscribe(parent);
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            parent.onError(ex);
        }
    }
```

到此为止这个过程的调用可以用下图表示：![6262144](image/6262202.png)





> 标准的观察者模式 VS   RXJava的观察这模式

**标准的观察者模式：**

只有一个被观察者，多个观察者，被观察者改变发出通知后观察者才能观察到

**RxJava观察者模式：**

多个被观察者，一个观察者，需要起点和终点在一次订阅之后，才能发出通知，终点（观察者）才能观察到。

![6262211](image/6262211.png)

相比于标准的观察者模式，RxJava的观察者模式更加解耦，被观察者不必持有观察者，而是通过一个抽象层发射器来转发通知观察者。

