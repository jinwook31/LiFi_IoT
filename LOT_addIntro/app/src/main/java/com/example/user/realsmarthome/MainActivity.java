package com.example.user.realsmarthome;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    ListAdapter m_listAdapter;
    ListView m_listView;
    private DBAdapter db;

    ArrayList<ListInfoitem> array;
    ListInfoitem myitem;

    ArrayList<DB_Item> dbarraylist;

    String delname;

    ArrayList<HashMap<String, String>> personList;
    String myJSON;
    JSONArray peoples = null;
    private static final String TAG_RESULTS="result";
    private static final String TAG_NAME = "Name";
    private static final String TAG_KeyID = "KeyID";
    private static final String TAG_onoffState ="onoffState";
    private static final String TAG_Location ="Location";
    ProgressDialog asyncDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, LoadingActivity.class));
        //인트로

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); //서버 Exception 방지를 위해 사용

        asyncDialog = new ProgressDialog(MainActivity.this);
        m_listView = (ListView) findViewById(R.id.device_listview);
        array = new ArrayList<ListInfoitem>();

        db = new DBAdapter(MainActivity.this);
         /*
        *갱신하는 부분
         */
        db.open();
        db.Clean();
        db.close();
        dbPrint();
        if(isNetworkConnected()){
            Log.e("인터넷 연결됨.","이거");
        personList = new ArrayList<HashMap<String,String>>();
            String IP = setedIP();
            if(IP.equals("no")){

            }else{
                ServerTest.setURI(IP,"/androidInit.php");
                Log.e("Getdata",ServerTest.getURI());
                getData(ServerTest.getURI());
            }
        }
        else{
            Log.e("인터넷 연결안됨.", "이거");
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                }
            });
            alert.setMessage("Network ERROR \n: Connection Refused");
            alert.show();
        }


        m_listView.setOnItemClickListener(this);
        m_listView.setOnItemLongClickListener(this);

        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false); //아이콘
        View mcustom = LayoutInflater.from(this).inflate(R.layout.actionbar_ipsetting,null);
        mActionBar.setCustomView(mcustom);
        mActionBar.setDisplayShowCustomEnabled(true);



    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this,CirclesStyledLayout.class);
        intent.putExtra("location", array.get(position).getItem_name());
        intent.putExtra("totalnum", array.get(position).getItem_info());
        startActivity(intent);
    }


    public void onAddListButtonClick(View v){//리스트 추가
        AlertDialog.Builder builder;

        Context mContext = MainActivity.this;
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_add_list_item,(ViewGroup) findViewById(R.id.layout_list_root));
        final EditText mylistName = (EditText) layout.findViewById(R.id.listName);

        builder = new AlertDialog.Builder(mContext);
        builder.setView(layout);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Boolean alreadyIn = false;
                //디렉토리 추가
                DB_Item myobject = new DB_Item(mylistName.getText().toString(), "Directory", "2", "0");
                db.open();
                dbarraylist = db.selectAllPersonList();
                db.close();

                for (int i = 0; i < dbarraylist.size(); i++) {
                    if ((dbarraylist.get(i).getName().equals(myobject.getName())) && (dbarraylist.get(i).getKeyID().equals("Directory"))) {
                        alreadyIn = true;
                    }//이미있으면
                }
                if (alreadyIn) {
                    Toast.makeText(MainActivity.this, "already exist directory", Toast.LENGTH_SHORT).show();
                    alreadyIn = false;
                } else {
                    if (isNetworkConnected()) {

                        db.open();
                        db.insertAddress(myobject);//디비에추가
                        db.close();
                        myitem = new ListInfoitem(mylistName.getText().toString() + "", "0 device connecting");
                        array.add(myitem);//리스트의 array에 추가
                        m_listAdapter.notifyDataSetChanged();

                            /*
                            *서버에 값 전달하는 부분 작성!
                            *여기는 디렉토리 추가. DB에 추가할 값 던져줌
                            *
                             */

                        try {
                            //데이터를 웹서버에 보내고 받아온 결과를 출력합니다.
                            String IP = setedIP();
                            if(IP.equals("no")){

                            }else{
                                ServerTest.setURI(IP, "/addControlList.php");
                                ServerTest.sendData(myobject.getName().toString(), myobject.getKeyID().toString(), myobject.getOnoffState().toString(), myobject.getLocation().toString());
                            }
                           } catch (ClientProtocolException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    } else {
                        Log.e("인터넷 연결안됨.", "이거");
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        });
                        alert.setMessage("Network ERROR \n: Connection Refused");
                        alert.show();
                    }
                }

                // dbPrint();

                dialog.dismiss();

            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//다이얼로그 닫기.
            }
        });
        builder.show();
    }


    public void onIPSetting(View view){//ip setting 버튼 눌렀을 때
        AlertDialog.Builder builder;

        Context mContext = MainActivity.this;
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_ip_setting,(ViewGroup) findViewById(R.id.layout_ip_root));

        final EditText m_ip = (EditText)layout.findViewById(R.id.ip_setting);
        final Button m_editbtn = (Button)layout.findViewById(R.id.ipsetting_btn);

        SharedPreferences setting;
        setting = getSharedPreferences("setting", 0);
        String currentip = setting.getString("currentip", "no");
        Log.e("현재상태 : ", currentip);//현재 setting된 ip 가져옴
        m_ip.setText(currentip);
        m_ip.setFocusable(false);
        m_ip.setClickable(false);
        m_ip.setEnabled(false);
        m_ip.setFocusableInTouchMode(false);
        //현재 setting된 ip 표시해주고, 편집금지

        m_editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit버튼 누르면
                m_ip.setText("no");
                m_ip.setFocusable(true);
                m_ip.setClickable(true);
                m_ip.setEnabled(true);
                m_ip.setFocusableInTouchMode(true);
                //편집 가능하게 하고

            }
        });



        builder = new AlertDialog.Builder(mContext);
        builder.setView(layout);
        builder.setTitle("IP Setting");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences setting;
                setting = getSharedPreferences("setting", 0);
                SharedPreferences.Editor editor;
                editor = setting.edit();
                editor.putString("currentip", m_ip.getText().toString());
                Log.e("mip : ", m_ip.getText().toString());//현재 setting된 ip 프리퍼런스에 저장
                editor.commit();

                /*
                 * 여기 ip설정해주는 부분
                 */

                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//다이얼로그 닫기.
            }
        });
        builder.show();


    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {//오래 눌렀을 때 디렉토리 삭제
        final int temp_position = position;

        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
        ab.setMessage("are you sure? \nall device deleted !");
        ab.setPositiveButton("ok", new DialogInterface.OnClickListener() {//ok 눌렀을 때
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //db에서 삭제하는 부분, 디렉토리 하위 디바이스도 모두 삭제
                delname = array.get(temp_position).getItem_name();//삭제할 디렉토리의 이름 가져옴.
                Log.e("삭제할 디렉토리 이름 ", delname); //
                if(isNetworkConnected()){

                db.open();
                dbarraylist = db.selectAllPersonList();
                db.close();
                for (int i = 0; i < dbarraylist.size(); i++) {
                    if ((dbarraylist.get(i).getKeyID().contains("Directory"))) { //삭제할 디렉토리 찾음
                        Log.e("디렉토리 삭제 검사부분 ", dbarraylist.get(i).getKeyID()); //여기 돌았다
                        Log.e("삭제할 디렉토리 getName ", dbarraylist.get(i).getName()); //

                        if (dbarraylist.get(i).getName().equals(delname)) {
                            db.open();
                            db.delName(delname);//db에서 디렉토리 삭제
                            db.close();
                            Log.e("디렉토리삭제", delname);

                             /*
                            *   서버에 디렉토리 삭제 값 보냄!
                             *delete url
                             */
                            try {
                                //데이터를 웹서버에 보내고 받아온 결과를 출력합니다.
                                String IP = setedIP();
                                if(IP.equals("no")){

                                }else{
                                    ServerTest.setURI(IP, "/removeControlList.php");
                                    ServerTest.sendData(delname, dbarraylist.get(i).getKeyID(), dbarraylist.get(i).getOnoffState(), dbarraylist.get(i).getLocation());
                                }
                              } catch (ClientProtocolException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            dbPrint();
                        }
                    }
                    break;
                }
                dbPrint();
                Log.e("구분", "구분");
                db.open();
                dbarraylist = db.selectAllPersonList();
                db.close();
                for (int i = 0; i < dbarraylist.size(); i++) {
                    if (dbarraylist.get(i).getLocation().equals(delname)) { //삭제할 디바이스 찾음. dealname은 삭제한 디렉토리 이름
                        Log.e("삭제할 디바이스의 location ", dbarraylist.get(i).getLocation());
                        db.open();
                        db.delAddress(dbarraylist.get(i).getKeyID()); //여기 내 db에서 삭제됬는지 확인하고
                        db.close();
                        /*
                        *서버에 삭제한 디렉토리에 해당하는 디바이스 모두 삭제!
                         */
                        try {
                            //데이터를 웹서버에 보내고 받아온 결과를 출력합니다.
                            String IP = setedIP();
                            if(IP.equals("no")){

                            }else{
                                ServerTest.setURI(IP, "/removeControlList.php");
                                ServerTest.sendData(dbarraylist.get(i).getName(), dbarraylist.get(i).getKeyID(), dbarraylist.get(i).getOnoffState(), delname);
                            }
                        } catch (ClientProtocolException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }//db에서 디렉토리에 해당하는 디바이스 삭제
                }
                dbPrint();
                array.remove(temp_position);
                m_listAdapter.notifyDataSetChanged(); //리스트에서 삭제


                    }
                    else{
                        Log.e("인터넷 연결안됨.", "이거");
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        });
                        alert.setMessage("Network ERROR \n: Connection Refused");
                        alert.show();
                    }

            }
        });
        ab.setNegativeButton("cancel", new DialogInterface.OnClickListener() {//cancel 눌렀을 때
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ab.show();
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        array = new ArrayList<>();
        Log.d("TestAppActivity", "onRestart");
        db = new DBAdapter(MainActivity.this);
        db.open();
        dbarraylist = db.selectAllPersonList();
        db.close();
        for (int i = 0; i < dbarraylist.size(); i++) {
            if (dbarraylist.get(i).getKeyID().equals("Directory")) {
                myitem = new ListInfoitem(dbarraylist.get(i).getName(), dbarraylist.get(i).getLocation()+" device connecting");
                array.add(myitem);
            }
        }
        m_listAdapter = new ListAdapter(this, R.layout.activity_listview_item, array);
        m_listView.setAdapter(m_listAdapter);
    }

    public void dbPrint(){
        db.open();
        dbarraylist = db.selectAllPersonList();
        db.close();
        for(int i = 0 ; i < dbarraylist.size(); i++) {
            Log.e("DB_Item list : ",dbarraylist.get(i).getName()
                    +" "+dbarraylist.get(i).getKeyID()
                    +" "+dbarraylist.get(i).getOnoffState()
                    +" "+dbarraylist.get(i).getLocation());
        }
    }


    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);
            DB_Item object;

            for(int i=0;i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
                String Name = c.getString(TAG_NAME);
                String KeyID = c.getString(TAG_KeyID);
                String onoffState = c.getString(TAG_onoffState);
                String Location = c.getString(TAG_Location);

                HashMap<String,String> persons = new HashMap<String,String>();

                Log.e("가져온 값",Name+","+KeyID+","+onoffState+","+Location);
                object = new DB_Item(Name, KeyID, onoffState, Location);
                db.open();
                db.insertAddress(object);
                db.close();
            }
            db.open();
            dbarraylist = db.selectAllPersonList();
            db.close();
            for (int i = 0; i < dbarraylist.size(); i++) {
                if(dbarraylist.get(i).getKeyID().equals("Directory")){
                    myitem = new ListInfoitem(dbarraylist.get(i).getName(), dbarraylist.get(i).getLocation()+" device connecting");
                    Log.e("갱신되었니",dbarraylist.get(i).getLocation());
                    array.add(myitem);
                }
            }

            m_listAdapter = new ListAdapter(this, R.layout.activity_listview_item,array);
            m_listView.setAdapter(m_listAdapter);

            asyncDialog.dismiss();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void getData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    Log.e("URI",uri);
                    Log.e("con.getInputStream()",con.getInputStream()+"");
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    if(bufferedReader == null){
                        Log.e("여기","null 입니다");
                    }
                    else{
                        Log.e("여기는","null이아닙니다");
                    }


                    Log.e("bufferReader",bufferedReader+"");

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        Log.e("stringBuffer",json+"");
                        sb.append(json+"\n");
                    }


                    return sb.toString().trim();

                }catch(Exception e){
                    return null;
                }



            }

            @Override
            protected void onPreExecute() {  //로그인 전 AsyncTask
                super.onPreExecute();
                asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                asyncDialog.setMessage("Device loading...");

                // show dialog           super.onPreExecute();
                asyncDialog.show();
            }
            @Override
            protected void onPostExecute(String result){
                Log.e("받아온 값",result+"");
                  if(result != null) {
                myJSON = result;
                showList();
                  }
            }

        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public String setedIP(){
        String tempURI="http://";

        SharedPreferences setting;
        setting = getSharedPreferences("setting", 0);
        String currentip = setting.getString("currentip", "no");
        Log.e("CurrentIP",currentip);
        if(currentip != null && ! currentip.equals("no") && currentip != "") {
            if(currentip.length()==12) {
                tempURI += (currentip.substring(0, 3) + ".");
                tempURI += (currentip.substring(3, 6) + ".");
                tempURI += (currentip.substring(6, 9) + ".");
                tempURI += (currentip.substring(9, 12));
                Log.e("tempURI_12", tempURI);

            }else{
                tempURI += (currentip.substring(0, 3) + ".");
                tempURI += (currentip.substring(3, 6) + ".");
                tempURI += (currentip.substring(6, 9) + ".");
                tempURI += (currentip.substring(9, 11));
                Log.e("tempURI_11", tempURI);
            }
            return tempURI;
        }
        else{
            Log.e("IP 연결안됨.", "이거");
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                }
            });
            alert.setMessage("Network ERROR \n: Connection Refused");
            alert.show();
            return "no";
        }
    }
}
