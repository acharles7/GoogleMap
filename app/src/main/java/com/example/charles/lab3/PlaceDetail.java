package com.example.charles.lab3;

import java.io.Serializable;

public class PlaceDetail implements Serializable {
    private String name;
    private String address;

    @Override
    public String toString() {
        return "PlaceDetail{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", rating=" + rating +
                '}';
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





    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }


    public PlaceDetail(String name, String address, double rating) {

        this.name = name;
        this.address = address;
        this.rating = rating;

    }
    private double rating;

}
