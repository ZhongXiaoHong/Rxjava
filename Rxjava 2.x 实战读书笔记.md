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