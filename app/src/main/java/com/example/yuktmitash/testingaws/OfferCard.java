package com.example.yuktmitash.testingaws;

import android.graphics.Bitmap;

public class OfferCard {
    private Offer offer;
    private String username;
    private Bitmap bitmap;

    public OfferCard(Offer offer, String username, Bitmap bitmap) {
        this.offer = offer;
        this.username = username;
        this.bitmap = bitmap;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
