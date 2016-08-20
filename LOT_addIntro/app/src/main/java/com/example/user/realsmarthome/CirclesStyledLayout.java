package com.example.user.realsmarthome;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.util.ArrayList;


public class CirclesStyledLayout extends BaseSampleActivity {
    private DBAdapter db;
    String m_location;
    String m_totalnum;
    ArrayList<DB_Item> dbarraylist;
    int num;
    String tempKey;
    Context context;
    String tatalnumsave;
    String locationsave;

    String curlocate;
    String server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datail_main);
        Intent intent = getIntent();
        locationsave = m_location = intent.getStringExtra("location");
        tatalnumsave = m_totalnum = intent.getStringExtra("totalnum");
     //   tv = (TextView) findViewById(R.id.servercontent);

        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
     //   mAdapter.setCONTENT(m_location,m_totalnum);
        m_totalnum = m_totalnum.substring(0,1);
        Log.e("m_totalnum",m_totalnum+"");
        num = Integer.parseInt(m_totalnum);
      //  mAdapter.setCount(Integer.parseInt(m_totalnum));
    //    int devicesize=0;

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
      //  mIndicator = (TitlePageIndicator)findViewById(R.id.indicator);

        mIndicator.setViewPager(mPager);

        db = new DBAdapter(this);
        db.open();

        dbarraylist = db.selectAllPersonList();
        db.close();
        if(dbarraylist.size() != 0) {
            mAdapter.setting(dbarraylist, m_location);
            mAdapter.notifyDataSetChanged();
        }

        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false); //아이콘
        View mcustom = LayoutInflater.from(this).inflate(R.layout.actionbar_custom,null);
        mActionBar.setCustomView(mcustom);
        mActionBar.setDisplayShowCustomEnabled(true);
    }


    public void onAddButtonClick(View v){ //디바이스 추가

        AlertDialog.Builder builder;

        Context mContext = CirclesStyledLayout.this;
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_add_detail_item,(ViewGroup) findViewById(R.id.layout_root));

        final EditText name = (EditText)layout.findViewById(R.id.dlgDisplayName);
        final EditText number = (EditText)layout.findViewById(R.id.dlgDeviceID);

        builder = new AlertDialog.Builder(mContext);
        builder.setView(layout);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(number.getText())) {//텍스트안에가 비어있는지 확인.
                    Toast.makeText(getApplicationContext(), "name is empty", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean alreadyIn = false;

                    if(isNetworkConnected()){

                    //디바이스 추가
                    DB_Item myobject = new DB_Item(name.getText().toString(), number.getText().toString(), "0", m_location);
                    db.open();
                    dbarraylist = db.selectAllPersonList();
                    db.close();
                    for (int i = 0; i < dbarraylist.size(); i++) {
                        if ((dbarraylist.get(i).getKeyID().equals(myobject.getKeyID()))) {
                            alreadyIn = true;

                        }//이미 KEY있으면

                    }
                    Log.e("already", alreadyIn + "");
                    if (alreadyIn) {
                        Toast.makeText(CirclesStyledLayout.this, "already exist device", Toast.LENGTH_SHORT).show();
                        alreadyIn = false;
                    } else {
                        db.open();
                        db.insertAddress(myobject);//디비에 추가
                        db.close();
                        /*
                        *서버에 던져주는 부분!!!
                        * 디바이스 추가! add url
                         */
                        try {
                            //데이터를 웹서버에 보내고 받아온 결과를 출력합니다.
                            String IP = setedIP();
                            if(IP.equals("no")){

                            }else{
                                ServerTest.setURI(IP,"/addControlList.php");
                                server = (ServerTest.sendData(myobject.getName().toString(), myobject.getKeyID().toString(), myobject.getOnoffState().toString(), myobject.getLocation().toString())); //디바이스 추가

                            }
                        } catch (ClientProtocolException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        for (int i = 0; i < dbarraylist.size(); i++) {
                            if ((dbarraylist.get(i).getKeyID().equals("Directory")) && (dbarraylist.get(i).getName().equals(m_location))) {//디렉토리 찾음
                                num += 1;
                                Log.e("totalnum", m_totalnum);
                                db.open();
                                db.editLocation(dbarraylist.get(i), num + "");//디렉토리
                                db.close();
                                dbarraylist.get(i).setLocation("" + num);
                                Log.e("기기추가 시 연결기기수", dbarraylist.get(i).getLocation());
                               // dbPrint();

                                /*
                                * 바뀐거 디비에 추가!!! 서버에서 검사해서 이미 있으면 update, 없으면 add
                                * 폴더 location값 바뀌었으므로 보내줌, add url
                                 */
                                try {
                                    //데이터를 웹서버에 보내고 받아온 결과를 출력합니다.
                                    String IP = setedIP();
                                    if(IP.equals("no")){

                                    }else{
                                        ServerTest.setURI(IP,"/addControlList.php");
                                        server = (ServerTest.sendData(dbarraylist.get(i).getName(),dbarraylist.get(i).getKeyID(), dbarraylist.get(i).getOnoffState(), dbarraylist.get(i).getLocation())); //디바이스 업데이트

                                    }
                                } catch (ClientProtocolException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                            }//추가할 때 해당 디렉토리에 연결 기기 수 업데이트
                        }
                        //화면에 추가해주는 부분 추가해야함.
                        db.open();
                        dbarraylist = db.selectAllPersonList();
                        if (dbarraylist.size() != 0) {
                            mAdapter.setting(dbarraylist, m_location);
                            mAdapter.notifyDataSetChanged();
                        }
                        db.close();
                    }

                       }
                        else{
                            Log.e("인터넷 연결안됨.", "이거");
                            AlertDialog.Builder alert = new AlertDialog.Builder(CirclesStyledLayout.this);
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                            alert.setMessage("Network ERROR \n: Connection Refused");
                            alert.show();
                        }

                    dbPrint();

                    dialog.dismiss();
                }
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


    public void onDelButtonClick(final View v){
      //  Toast.makeText(this,"del버튼",Toast.LENGTH_SHORT).show();


        context = CirclesStyledLayout.this;
        final String fragkey;

        final int position = mPager.getCurrentItem();
        Log.e("position", position + "");
        TestFragment frag = new TestFragment();
        fragkey = frag.delete(getSupportFragmentManager().getFragments().get(position));//현재프래그먼트넘겨서 key가져옴.
        Log.e("가져온 key값은?", fragkey + "");
       // curlocate = TestFragment.getCurlocate();

        AlertDialog.Builder ab = new AlertDialog.Builder(CirclesStyledLayout.this);
        ab.setMessage("are you sure?");
        ab.setPositiveButton("ok", new DialogInterface.OnClickListener() {//ok 눌렀을 때
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isNetworkConnected()){

                db.open();
                dbarraylist = db.selectAllPersonList();
                db.close();
                for (int i = 0; i < dbarraylist.size(); i++) {
                    if (dbarraylist.get(i).getKeyID().equals(fragkey)) {//db에서 삭제할 key값 찾음
                        Log.e("삭제할 값의 key와 locate는? ", dbarraylist.get(i).getName() + "," + dbarraylist.get(i).getLocation());
                        curlocate = dbarraylist.get(i).getLocation();
                        db.open();
                        db.delAddress(dbarraylist.get(i).getKeyID());//db에서 삭제
                        db.close();
                        Log.e("삭제했습니다", fragkey);

                    /*
                    *서버에서 해당 key 삭제부분!!
                    *  delete url
                     */
                        try {
                            //데이터를 웹서버에 보내고 받아온 결과를 출력합니다.
                            String IP = setedIP();
                            if(IP.equals("no")){

                            }else{
                                ServerTest.setURI(IP,"/removeControlList.php");
                                server = (ServerTest.sendData(dbarraylist.get(i).getName(),dbarraylist.get(i).getKeyID(), dbarraylist.get(i).getOnoffState(), dbarraylist.get(i).getLocation())); //디바이스 추가

                            }
                        } catch (ClientProtocolException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }//
                Log.e("끝나면 도는지", "asdfsdaf");
                db.open();
                dbarraylist = db.selectAllPersonList();
                db.close();
                for (int i = 0; i < dbarraylist.size(); i++) {
                    Log.e("keyID 오는지 - 디렉토리 여야함", dbarraylist.get(i).getKeyID());
                    Log.e("name 오는지", curlocate);
                    if ((dbarraylist.get(i).getKeyID().contains("Directory"))) { //디렉토리이고, 삭제할 디바이스의 폴더이면
                        Log.e("디렉토리 if문 돌고", dbarraylist.get(i).getKeyID());
                        if(dbarraylist.get(i).getName().equals(curlocate)) {
                            Log.e("원래 num", num + "얼마냐");
                            num--;
                            Log.e("바뀐 num", num + "얼마냐");
                            db.open();
                            db.editLocation(dbarraylist.get(i), num + "");
                            db.close();
                            dbarraylist.get(i).setLocation(""+ num);
                            Log.e("기기추가 시 연결기기수", dbarraylist.get(i).getLocation());

                        /*
                        * 서버에서 삭제 key의 location 업데이트 부분!
                         *add url
                         */
                            try {
                                //데이터를 웹서버에 보내고 받아온 결과를 출력합니다.
                                String IP = setedIP();
                                if(IP.equals("no")){

                                }else{
                                    ServerTest.setURI(IP,"/addControlList.php");
                                    server = (ServerTest.sendData(dbarraylist.get(i).getName(), dbarraylist.get(i).getKeyID(), dbarraylist.get(i).getOnoffState(), dbarraylist.get(i).getLocation())); //디바이스 업데이트

                                }
                            } catch (ClientProtocolException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            break;
                        }
                    }
                    Log.e("if문 도는지", "eawfaw");
                }
                db.open();
                dbarraylist = db.selectAllPersonList();
                db.close();
                if (dbarraylist.size() != 0) {
                    Intent intent = new Intent(CirclesStyledLayout.this,CirclesStyledLayout.class);
                    intent.putExtra("location",locationsave );
                    intent.putExtra("totalnum", tatalnumsave);
                    startActivity(intent);
                    finish();
                    //mPager.setAdapter(mAdapter);
                }


                    }
                    else{
                        Log.e("인터넷 연결안됨.", "이거");
                        AlertDialog.Builder alert = new AlertDialog.Builder(CirclesStyledLayout.this);
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        });
                        alert.setMessage("Network ERROR \n: Connection Refused");
                        alert.show();
                    }
                dialog.dismiss();
            }
        });
        ab.setNegativeButton("cancel", new DialogInterface.OnClickListener() {//cancel 눌렀을 때
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ab.show();
        //setAdapter있는지 찾거나 없으면 새로 Adpater new해.





    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void dbPrint(){
        db.open();
        dbarraylist = db.selectAllPersonList();
        db.close();
        for(int i = 0 ; i < dbarraylist.size(); i++) {
            Log.e("DB_Item list : ", dbarraylist.get(i).getName()
                    + " " + dbarraylist.get(i).getKeyID()
                    + " " + dbarraylist.get(i).getOnoffState()
                    + " " + dbarraylist.get(i).getLocation());
        }
    }

    public String setedIP(){
        String tempURI="http://";

        SharedPreferences setting;
        setting = getSharedPreferences("setting", 0);
        String currentip = setting.getString("currentip", "no");
        Log.e("CurrentIP",currentip);
        if(currentip != null && !currentip.equals("no")  && currentip != "") {
            if(currentip.length() == 12) {
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
            AlertDialog.Builder alert = new AlertDialog.Builder(CirclesStyledLayout.this);
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