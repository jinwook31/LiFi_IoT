package com.example.user.lot_floating;

import java.io.Serializable;

/**
 * Created by 3 on 2016-05-20.
 */
public class DB_Item implements Serializable {

    private String Name ="";
    private String KeyID ="";
    private String onoffState ="";
    private String Location ="";

    public void setName(String Name){
        this.Name = Name;
    }
    public void setKeyID(String KeyID){
        this.KeyID = KeyID;
    }
    public void setOnoffState(String onoffState){
        this.onoffState = onoffState;
    }
    public void setLocation(String Location){
        this.Location = Location;
    }
    public String getName(){
        return Name;
    }
    public String getKeyID(){
        return KeyID;
    }
    public String getOnoffState(){
        return onoffState;
    }
    public String getLocation(){
        return Location;
    }

    public DB_Item(String Name, String KeyID, String onoffState, String Location){
        this.Name = Name;
        this.KeyID = KeyID;
        this.onoffState = onoffState;
        this.Location = Location;
    }
    public DB_Item(String Name, String KeyID){
        this.Name = Name;
        this.KeyID = KeyID;
    }
    public DB_Item(String Name, String KeyID, String Location){
        this.Name = Name;
        this.KeyID = KeyID;
        this.Location = Location;
    }
}
