package com.example.rxjavademo.standard_observer;

public class User implements Observer<String> {

    private String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public void onReceive(String s) {
        System.out.println(name+" 收到推送：【 "+s+" 】");
    }

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
}
