package com.example.xiao.gowhere.Model;

/**
 * Created by Ricky on 2017/2/1.
 */

public class HotelItem {

    private String id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private int rating;
    private int price;
    private String imgUrl;

    public HotelItem(){ }

    public void setId(String id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
    public void setRating(int rating){
        this.rating = rating;
    }
    public void setPrice(int price){
        this.price = price;
    }
    public void setImgUrl(String url){
        imgUrl = url;
    }

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getAddress(){
        return address;
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public int getRating(){
        return rating;
    }
    public int getPrice(){
        return price;
    }
    public String getImgUrl(){
        return imgUrl;
    }
}
