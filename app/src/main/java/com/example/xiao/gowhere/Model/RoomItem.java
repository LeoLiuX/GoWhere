package com.example.xiao.gowhere.Model;

/**
 * Created by Ricky on 2017/2/1.
 */

public class RoomItem {
    private String id;
    private String style;
    private int price;
    private String imgUrl;

    public RoomItem(){ }

    public void setId(String id){
        this.id = id;
    }
    public void setStyle(String style){
        this.style = style;
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
    public String getStyle(){
        return style;
    }
    public int getPrice(){
        return price;
    }
    public String getImgUrl(){
        return imgUrl;
    }
}
