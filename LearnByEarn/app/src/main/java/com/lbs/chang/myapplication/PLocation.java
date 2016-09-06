package com.lbs.chang.myapplication;

import android.location.Location;
import android.location.LocationManager;

/**
 * Created by chang on 4/18/2015.
 */
public class PLocation {
    private double longitude;
    private double latitude;
    private String name;
    private String information;
    private String type;

    PLocation(double latitude, double longitude, String name, String information, String type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.information = information;
        this.setType(type);
    }
    public void setAll(double longitude, double latitude, String name, String information,
                       String type){
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.information = information;
        this.setType(type);
    }
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }


    public double getDistance(Location location) {
        Location location1 = new Location(LocationManager.GPS_PROVIDER);
        location1.setLatitude(this.latitude);
        location1.setLongitude(this.longitude);
//      System.out.println("locaiton inner"+this.latitude+" "+this.longitude);
//        System.out.println("locaiton one"+location.getLatitude()+" "+location.getLongitude());
        double distance = location.distanceTo(location1);
//        System.out.println("distance"+location.distanceTo(location1));
        return distance;


    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}