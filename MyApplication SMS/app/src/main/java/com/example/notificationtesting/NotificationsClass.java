package com.example.notificationtesting;

public class NotificationsClass {
    String datetime;
    String deviceid;
    String email;
    String message;
    String phone;
    String read;
    String title;
    String type;
    String userid;
    String link;

    public NotificationsClass(){

    }

    public NotificationsClass(String datetime,
                              String deviceid,
                              String email,
                              String message,
                              String phone,
                              String read,
                              String title,
                              String type,
                              String userid,
                              String link){

        this.datetime = datetime;
        this.deviceid = deviceid;
        this.email = email;
        this.message =message;
        this.phone = phone;
        this.read = read;
        this.title = title;
        this.type =type;
        this.userid =userid;
        this.link =link;

    }

}
