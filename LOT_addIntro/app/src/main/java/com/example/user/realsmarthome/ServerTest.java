package com.example.user.realsmarthome;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

/**
 * Created by 3 on 2016-05-22.
 */
public class ServerTest extends Activity {
    private static String URI="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public static void setURI(String settedIP, String uri){
       // URI="http://124.195.180.241";
        URI = settedIP;
        URI += uri;
        Log.e("uri",URI);
    }



    public static String getURI(){
        return URI;
    }

    public static String sendData(String Name, String KeyID, String onoffState, String Location) throws ClientProtocolException, IOException {
        // TODO Auto-generated method stub
        Log.e("uri",URI);
        String result = null;
        try {
            HttpPost request = makeHttpPost( Name, KeyID, onoffState, Location, URI ) ;
            // Get 방식일경우
            //HttpPost request = makeHttpGet( id, pwd, "http://www.shop-wiz.com/android_post.php" ) ;
            HttpClient client = new DefaultHttpClient() ;
            ResponseHandler<String> reshandler = new BasicResponseHandler() ;
            result = client.execute( request, reshandler ) ;
        }
        catch(ClientProtocolException e){
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return result ;
    }
    //Post 방식일경우
    private static HttpPost makeHttpPost(String Name, String KeyID, String onoffState, String Location, String url) {
        // TODO Auto-generated method stub
        HttpPost request = new HttpPost( url ) ;
        Vector<NameValuePair> nameValue = new Vector<NameValuePair>() ;
        nameValue.add(new BasicNameValuePair("Name", Name) ) ;
        nameValue.add( new BasicNameValuePair( "KeyID", KeyID ) ) ;
        nameValue.add(new BasicNameValuePair("onoffState", onoffState) ) ;
        nameValue.add( new BasicNameValuePair( "Location", Location ) ) ;
        request.setEntity(makeEntity(nameValue) ) ;
        return request ;
    }
    private static HttpEntity makeEntity( Vector<NameValuePair> nameValue ) {
        HttpEntity result = null ;
        try {
            result = new UrlEncodedFormEntity( nameValue ) ;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result ;
    }


}
