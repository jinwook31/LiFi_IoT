package com.example.user.lot_floating;

/**
 * Created by 3 on 2016-05-15.
 */
public class ListInfoitem {
    private String item_name;
    private String item_info;

    ListInfoitem(String item_name, String item_info){
        this.item_name = item_name;
        this.item_info = item_info;
    }
    ListInfoitem(String item_name){
        this.item_name = item_name;
    }



    public void setItem_name(String item_name){
        this.item_name = item_name;
    }
    public void setItem_info(String item_info){
        this.item_info = item_info;
    }

    public String getItem_name(){
        return item_name;
    }
    public String getItem_info(){
        return item_info;
    }
}
