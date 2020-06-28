> 标准观察者模式

![6241016](C:\Users\lx\Desktop\Rxjava\image\6241016.jpg)

**被观察者接口**

```java
public interface Observable<T> {

    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
    void pushMessage(T t);
}
```



**被观察者具体实现**

```java
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
```



**观察者接口**

```java
public interface Observer<T> {

   void  onReceive(T t);
}
```



**观察者具体实现**

```java
public class User implements Observer<String> {

    private String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public void onReceive(String s) {
        System.out.println(name+" 收到推送：【 "+s+" 】");
    }

}
```

**运行测试**

```java
public static void main(String[] args) {
        WechatPublicService  service = new WechatPublicService();
        User xiaoming = new User("小明");
        User xiaohong = new User("小红");
        User xiaohua = new User("小华");
        service.addObserver(xiaohong);
        service.addObserver(xiaoming);
        service.addObserver(xiaohua);
        service.pushMessage("白天干开发，晚上搞副业");
}
```

**结果：**

小红 收到推送：【 白天干开发，晚上搞副业 】
小明 收到推送：【 白天干开发，晚上搞副业 】
小华 收到推送：【 白天干开发，晚上搞副业 】



> 