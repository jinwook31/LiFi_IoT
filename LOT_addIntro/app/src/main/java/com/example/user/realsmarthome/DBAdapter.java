package com.example.user.realsmarthome;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 3 on 2016-05-20.
 */
public class DBAdapter {
    private static final String DATABASE_NAME = "ITEM";
    private static final int DATABASE_VERSION = 1;
    //기본으로 주어지는 것들
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private final Context context;



    public DBAdapter(Context ctx) {
        context = ctx;
        dbHelper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBAdapter open() throws SQLException {//SQLException 통해서 디비어뎁터 열기
        db = dbHelper.getWritableDatabase();
        // 읽고 쓰기 위해 DB 연다. 권한이 없거나 디스크가 가득 차면 실패
        return this;
    }

    public void close() {
        db.close();
    }//디비 닫는 메소드

    private static final String ADRESS_KEY_NAME = "Name";
    private static final String ADRESS_KEY_KEYID = "KeyID";
    private static final String ADRESS_KEY_ONOFFSTATE = "onoffState";
    private static final String ADRESS_KEY_LOCATION= "Location";

    private static final int ADRESS_KEY_COLUMN_NAME = 1;
    private static final int ADRESS_KEY_COLUMN_KEYID = 2;
    private static final int ADRESS_KEY_COLUMN_ONOFFSTATE = 3;
    private static final int ADRESS_KEY_COLUMN_LOCATION= 4;


    private static final String ADRESS_DATABASE_TABLE = "memotable";
    private static final String ADRESS_DATABASE_CREATE = "create table "
            + ADRESS_DATABASE_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT," + ADRESS_KEY_NAME + " TEXT, "
            + ADRESS_KEY_KEYID  + " TEXT, "+ ADRESS_KEY_ONOFFSTATE  + " TEXT, "+ ADRESS_KEY_LOCATION  +  " TEXT);";

    public void insertAddress(DB_Item object) {//입력
        ContentValues newValues = new ContentValues();

        Cursor c = db.query(ADRESS_DATABASE_TABLE, null, null, null, null,
                null, null);

        c.moveToLast();

        newValues.put(ADRESS_KEY_NAME, object.getName());
        newValues.put(ADRESS_KEY_KEYID, object.getKeyID());
        newValues.put(ADRESS_KEY_ONOFFSTATE, object.getOnoffState());
        newValues.put(ADRESS_KEY_LOCATION, object.getLocation());

        db.insert(ADRESS_DATABASE_TABLE, null, newValues);

    }
    public DB_Item find(String KeyID,String Name) {//입력
       /* Cursor c = db.query(ADRESS_DATABASE_TABLE+" where title = "+"title", null, null, null, null,
                null, null);*/
        Cursor c = db.rawQuery( "select * from memotable where KeyID=" + "'" + KeyID + "'"+"and Name="+ "'" +Name+ "'"  , null);
        DB_Item temp =null;
        if(c.moveToFirst()) {           //테이블 내에서 _id와 같은 id위치 찾기
            temp = new DB_Item(
                    c.getString(ADRESS_KEY_COLUMN_NAME)
                    , c.getString(ADRESS_KEY_COLUMN_KEYID)
                    , c.getString(ADRESS_KEY_COLUMN_ONOFFSTATE)
                    , c.getString(ADRESS_KEY_COLUMN_LOCATION));

        }
        c.close();

        return temp;
    }

    public void editAddress(DB_Item object, int id) {
        ContentValues newValues = new ContentValues();

        Cursor c = db.query(ADRESS_DATABASE_TABLE, null, null, null, null,
                null, null);
        c.moveToPosition(id);//입력된 id로 이동

        newValues.put(ADRESS_KEY_NAME, object.getName());
        newValues.put(ADRESS_KEY_KEYID, object.getKeyID());
        newValues.put(ADRESS_KEY_ONOFFSTATE, object.getOnoffState());
        newValues.put(ADRESS_KEY_LOCATION, object.getLocation());


        db.update(ADRESS_DATABASE_TABLE, newValues, "_id = " + id, null);
    }

    public void editLocation(DB_Item object, String locate) {
        ContentValues newValues = new ContentValues();

        Cursor c = db.query(ADRESS_DATABASE_TABLE, null, null, null, null,
                null, null);
      //  c.moveToPosition(id);//입력된 id로 이동
        c.moveToFirst();
        if ((c.getCount() == 0) || !c.moveToFirst()) {

        } else if (c.moveToFirst()) {
            do {
                 if(object.getName().equals(c.getString(c.getColumnIndex("Name")) )){ //이름같은 디렉토리 db에서 찾음
                    newValues.put(ADRESS_KEY_NAME, object.getName());
                    newValues.put(ADRESS_KEY_KEYID, object.getKeyID());
                    newValues.put(ADRESS_KEY_ONOFFSTATE, object.getOnoffState());
                    newValues.put(ADRESS_KEY_LOCATION, locate);

                    db.update(ADRESS_DATABASE_TABLE, newValues, "Name = '" + object.getName()+"'", null);
                    break;
                }

            } while (c.moveToNext());
        }

    }
/*
public void editLocation(DB_Item object, String loaction) {
    ContentValues newValues = new ContentValues();

    Log.e("update 중 입니다.", "");
    Cursor c = db.query(ADRESS_DATABASE_TABLE, null, null, null, null,
            null, null);
    boolean exist=false;
    c.moveToFirst();
    do {
        if (c.getString(c.getColumnIndex("Name")).equals(object.getName())) {
            Log.e(c.getString(c.getColumnIndex("Name")),"");
            Log.e("업데이트 되었습니다.", "");
            exist=true;
            break;
        }
    } while (c.moveToNext());

    if(exist==true) {
        newValues.put(ADRESS_KEY_NAME, object.getName());
        newValues.put(ADRESS_KEY_KEYID, object.getKeyID());
        newValues.put(ADRESS_KEY_ONOFFSTATE, object.getOnoffState());
        newValues.put(ADRESS_KEY_LOCATION, loaction);


        db.update(ADRESS_DATABASE_TABLE, newValues, null, null);
        Log.e("업데이트 되었습니다.", "");
    }
    else{
        Log.e("업데이트 안됨 값 존재 x", "");
    }

}*/

    public DB_Item MoveLast(){
        Cursor c = db.query(ADRESS_DATABASE_TABLE, null, null, null, null,
                null, null);
        c.moveToLast();
        DB_Item temp = null;
        temp = new DB_Item(
                c.getString(ADRESS_KEY_COLUMN_NAME)
                , c.getString(ADRESS_KEY_COLUMN_KEYID)
                , c.getString(ADRESS_KEY_COLUMN_ONOFFSTATE)
                , c.getString(ADRESS_KEY_COLUMN_LOCATION));

        c.close();
        return temp;
    }

    public void delAddress(String key) {

        db.delete(ADRESS_DATABASE_TABLE, "KeyID = '" + key + "'", null);

    }
    public void delName(String name) {

        db.delete(ADRESS_DATABASE_TABLE,"Name = '"+name+"'", null);

    }
    public DB_Item select(int id) {
        Cursor c = db.query(ADRESS_DATABASE_TABLE+" where _id = "+id, null, null, null, null,
                null, null);
        DB_Item temp =null;
        if(c.moveToFirst()) {           //테이블 내에서 _id와 같은 id위치 찾기
            temp = new DB_Item(
                    c.getString(ADRESS_KEY_COLUMN_NAME)
                    , c.getString(ADRESS_KEY_COLUMN_KEYID)
                    , c.getString(ADRESS_KEY_COLUMN_ONOFFSTATE)
                    , c.getString(ADRESS_KEY_COLUMN_LOCATION));

        }
        c.close();

        return temp;
    }

    public ArrayList<DB_Item> selectAllPersonList() {
        ArrayList<DB_Item> returnValue = new ArrayList<>();

        Cursor c = db.query(ADRESS_DATABASE_TABLE, null, null, null, null,
                null, null);

        if ((c.getCount() == 0) || !c.moveToFirst()) {

        } else if (c.moveToFirst()) {
            do {
                DB_Item temp = new DB_Item(
                        c.getString(ADRESS_KEY_COLUMN_NAME)
                        , c.getString(ADRESS_KEY_COLUMN_KEYID)
                        , c.getString(ADRESS_KEY_COLUMN_ONOFFSTATE)
                        , c.getString(ADRESS_KEY_COLUMN_LOCATION));
                returnValue.add(temp);

            } while (c.moveToNext());
        }
        c.close();

        return returnValue;
    }

    public void editOnOff(String Keyid, String onoff) {
        ContentValues newValues = new ContentValues();

        Log.e("update 중 입니다.", "");
        Cursor c = db.query(ADRESS_DATABASE_TABLE, null, null, null, null,
                null, null);
        boolean exist=false;
        c.moveToFirst();
        do {
            if (c.getString(c.getColumnIndex("KeyID")).equals(Keyid)) {
                Log.e(c.getString(c.getColumnIndex("KeyID")),"");
                exist=true;
                break;
            }
        } while (c.moveToNext());

        if(exist==true) {
            db.execSQL("UPDATE "+ADRESS_DATABASE_TABLE+" SET onoffState = '"+onoff+"' where KeyID = '"+Keyid+"';");
            Log.e("업데이트 되었습니다.", "");
        }
        else{
            Log.e("업데이트 안됨 값 존재 x", "");
        }
        c.close();
    }


    private static class DBHelper extends SQLiteOpenHelper {//디비 헬퍼

        public DBHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory,
                        int version) {//생성자
            super(context, dbName, factory, version);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ADRESS_DATABASE_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + ADRESS_DATABASE_TABLE);

        }
    }

    public void Clean(){
        db.execSQL("delete from "+"memotable"); //디비 모두 삭제
        Log.e("DB clean","clean");


    }


}
