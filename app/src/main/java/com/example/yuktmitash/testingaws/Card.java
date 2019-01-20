package com.example.yuktmitash.testingaws;

import android.graphics.Bitmap;

public class Card {
    private Bitmap bitmap;
    private String make;
    private String Model;
    private String hashcode;
    private int age;
    private String ownerFireId;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHashcode() {
        return hashcode;
    }

    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }

    public String getOwnerFireId() {
        return ownerFireId;
    }

    public void setOwnerFireId(String ownerFireId) {
        this.ownerFireId = ownerFireId;
    }

    public Card(Bitmap bitmap, String make, String model, int age, String hashcode, String ownerFireId) {
        this.bitmap = bitmap;
        this.make = make;
        Model = model;
        this.age = age;
        this.hashcode = hashcode;
        this.ownerFireId = ownerFireId;

    }
}
