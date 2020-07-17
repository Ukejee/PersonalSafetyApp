package com.ukejee.das.models;

public class Spot{
    private String name;
    private double lng;
    private double lat;

    public Spot(String name, double lat, double lng){
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public void setName(String name){this.name = name;}

    public String getName(){return name;}

    public void setLng(double lng){
        this.lng = lng;
    }

    public double getLng(){
        return lng;
    }

    public void setLat(double lat){
        this.lat = lat;
    }

    public double getLat(){
        return lat;
    }
}