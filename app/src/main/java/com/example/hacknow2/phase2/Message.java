package com.example.hacknow2.phase2;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Message {

    //required empty default constructor
    public Message(){

    }

    public Message(String user, long rating, long shares, boolean isTextOnly, boolean isPublic, String date, String body) {
        this.user = user;
        this.rating = rating;
        this.shares = shares;
        this.isTextOnly = isTextOnly;
        this.isPublic = isPublic;
        this.date = date;
        this.body = body;
        this.uid = UUID.randomUUID().toString();
    }

    public String uid;
    public String user;
    public long rating;
    public long shares;
    public boolean isTextOnly;
    public boolean isPublic;
    public String date;
    public String body;

    public Map<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();

        map.put("user", user);
        map.put("rating", rating);
        map.put("shares", shares);
        map.put("isTextOnly", isTextOnly);
        map.put("isPublic", isPublic);
        map.put("date", date);
        map.put("body", body);
        map.put("uid", uid);

        return map;
    }

    public static String fromDateTime(DateTime dt, String pattern){
        return pattern==null? dt.toString() : dt.toString(pattern);
    }

    public static DateTime toDateTime(String dt){
        return DateTime.parse(dt);
    }
}
