package com.example.charles.lab3;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {

    private String name;
    private String address;
    private String phoneNumber;
    private String id;
    private Uri website;
    private LatLng latLng;
    private double rating;
    private String attribution;


    private String placename;
    private String placeaddress;
    //private Double placerating;

    public PlaceInfo(String name, String address, String phoneNumber, String id, Uri website, LatLng latLng, double rating, String attribution) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.website = website;
        this.latLng = latLng;
        this.rating = rating;
        this.attribution = attribution;
    }

    public PlaceInfo() {

    }

    public PlaceInfo(String placeName, Double placeRating, String vicinity) {
        this.placename = placeName;
        this.rating = placeRating;
        this.placeaddress = vicinity;
        this.name = "ff";
        this.address ="ff";
        this.phoneNumber ="ff";


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getWebsite() {
        return website;
    }

    public void setWebsite(Uri website) {
        this.website = website;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", id='" + id + '\'' +
                ", website=" + website +
                ", latLng=" + latLng +
                ", rating=" + rating +
                ", attribution='" + attribution + '\'' +
                '}';
    }
}

