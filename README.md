# Rxjava
> Demo 小例子：传统请求网络 VS  RXJava方式

请求一张网络图片，然后设置在ImageView上

**传统方式：**

![6232359](image/6232359.png)



**RxJava方式：**

![6232348](image/6232348.png)

通过上面的对比，可以看到传统方式实现上代码结构不够清晰，而RxJava方式实现整个思维是比较清晰的，成链式的。

下面对上面Rx部分代码一些值得注意的地方作一下说明：

1. onSubscribe 表示此时预备要发送事件了,**这是第一步，**
2. Observable.just表示发送事件，这是第二步
3. map表示拦截转换事件，将String转换成Bitmap,这是第三步
4. onNext表示下游拿到最终事件了，这是第四步
5. onComplete标志事件完成,这是最后一步
6. onError当错误发生的时候回调



<u>*另外两个不可以忽视的点：*</u>

1. **subscribeOn(Schedulers.io())**  表示这句代码以上的代码运行在io线程中
2. **observeOn(AndroidSchedulers.mainThread())**  表示这句代码以下的代码运行在主线程

[^注意]: 以上代码都在RxJavaDemo工程

