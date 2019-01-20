package com.example.yuktmitash.testingaws;

public class Offer {

    private double offerNumber;
    private String from;
    private String to;
    private String carId;

    public Offer(double offerNumber, String from, String to, String carId) {
        this.offerNumber = offerNumber;
        this.from = from;
        this.to = to;
        this.carId = carId;
    }

    public Offer(){}
    public double getOfferNumber() {
        return offerNumber;
    }

    public void setOfferNumber(double offerNumber) {
        this.offerNumber = offerNumber;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }
}
