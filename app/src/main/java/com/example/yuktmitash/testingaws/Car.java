package com.example.yuktmitash.testingaws;

import java.util.Date;
import java.util.Objects;

public class Car {
    private String make;
    private String mode;
    private int year;
    private double lattitude;
    private double longitude;
    private String fireid;

    private int hashcode;

    public Car(String make, String mode, int year, double lattitude, double longitude, String fireid) {
        this.make = make;
        this.mode = mode;
        this.year = year;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.fireid = fireid;
    }

    public Car() {}

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFireid() {
        return fireid;
    }

    public void setFireid(String fireid) {
        this.fireid = fireid;
    }

    public String getMake() {
        return make;
    }



    @Override
    public int hashCode() {
        if (hashcode == 0) {
            int x = ((this.getYear() * 17) + (this.getMake().hashCode() * 12)) * this.getMode().hashCode();
            hashcode = x;
            return x;
        } else {
            return hashcode;
        }
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
