package com.example.user.lot_floating;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.util.ArrayList;

public final class TestFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private static final String KEY_CONTENT = "TestFragment:Content";
    private static DBAdapter db;
    private static ArrayList<DB_Item> dbarraylist;

    private String mContent ;
    TextView mylocation=null;
    TextView myName=null;
    TextView myKey=null;
    Switch onoffswitch;
    static String curlocate;

    View view=null;
    public static TestFragment newInstance(String content) {
        TestFragment fragment = new TestFragment();
/*
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            builder.append(content).append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        fragment.mContent = builder.toString();
*/
        fragment.mContent = content;
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); //서버 Exception 방지를 위해 사용

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    /*    View v = inflater.inflate(R.layout.activity_detail, container, false);
        TextView mylocation = (TextView) v.findViewById(R.id.locate);
        TextView myName = (TextView) v.findViewById(R.id.device_name);
        TextView myKey = (TextView) v.findViewById(R.id.device_key);

        mylocation.setText("mylocation test");
        myName.setText("myName test");
        myKey.setText("MyKey test");
        //여기 DB에서 가져온 내용 뿌려주면 됨.

        return v;
*/
        Log.i("mContent", mContent.toString());

        view = inflater.inflate(R.layout.activity_detail, container, false);
        Context context;
        context = getActivity();
        db = new DBAdapter(context);
        mylocation = (TextView) view.findViewById(R.id.locate);
        myName = (TextView) view.findViewById(R.id.device_name);
        myKey = (TextView) view.findViewById(R.id.device_key);
        onoffswitch = (Switch) view.findViewById(R.id.onoffswitch);
        onoffswitch.setOnCheckedChangeListener(this);
        //    mylocation.setText(mContent.getLocation());
        //    myName.setText(mContent.getName());
        //    myKey.setText(mContent.getKeyID());
        String[] myreal = mContent.split("/"); //Name/ ID/ onoffState/ Location
        mylocation.setText(myreal[3]);
        myName.setText(myreal[0]);
        myKey.setText(myreal[1]);

        if(myreal[2].equals("1")) {
            onoffswitch.setChecked(true);
        }
        else{
            onoffswitch.setChecked(false);
        }

        return view;

    }//뿌려주는부분

    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        String statenum="0";
        Log.e("현재화면의 device id: " + myKey.getText().toString(), "  왜안나와");
        if(onoffswitch.isChecked()){

            if(isNetworkConnected()){


            db.open();
            db.editOnOff(myKey.getText().toString(), "1"); //db값 on으로 바꿔줌, (key값, onoff값)
            db.close();
            statenum="1";
            /*
            * on 서버에 값 전달!
            * on url
             */
            try {
                //데이터를 웹서버에 보내고 받아온 결과를 출력합니다.
                String IP = setedIP();
                if(IP.equals("no")){

                }else{
                    ServerTest.setURI(IP,"/controlOn.php"); //onoff url로 변경할것
                    ServerTest.sendData(myName.getText().toString(), myKey.getText().toString(), statenum, mylocation.getText().toString());
                    Log.e(myName.getText().toString() + "," + myKey.getText().toString() + "," + statenum + "," + mylocation.getText().toString(), "이값 보냄! on에서");
                }
               } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


                }
                else{
                    Log.e("인터넷 연결안됨.", "이거");
                    AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
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
        else {

            if(isNetworkConnected()) {

                db.open();
                db.editOnOff(myKey.getText().toString(), "0"); //db값 off로 바꿔줌
                db.close();
                statenum = "0";
                try {
                    //데이터를 웹서버에 보내고 받아온 결과를 출력합니다.
                    String IP = setedIP();
                    if(IP.equals("no")){

                    }else{
                        ServerTest.setURI(IP, "/controlOff.php"); //onoff url로 변경할것
                        ServerTest.sendData(myName.getText().toString(), myKey.getText().toString(), statenum, mylocation.getText().toString());
                    }
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
                else{
                    Log.e("인터넷 연결안됨.", "이거");
                    AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
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



    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public String delete(Fragment frag){
        TextView textkey;
        final String tempKey;
        TextView currentlocate;

        textkey = (TextView) frag.getView().findViewById(R.id.device_key);
        tempKey = textkey.getText().toString();
        Log.e("프래그먼트 tempKey", tempKey);
        currentlocate = (TextView) frag.getView().findViewById(R.id.device_key);
        curlocate = currentlocate.getText().toString();//폴더명

        return tempKey;
    }
    public String setedIP(){
        String tempURI="http://";

        SharedPreferences setting;
        setting = getActivity().getSharedPreferences("setting", 0);
        String currentip = setting.getString("currentip", "no");
        Log.e("CurrentIP", currentip);
        Log.e("CurrentIP", currentip);
        if(currentip != null && ! currentip.equals("no") && currentip != "") {

            return tempURI+currentip;
        }
        else{
            Log.e("IP 연결안됨.", "이거");
            AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
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
    public static String getCurlocate(){
        return curlocate;
    }
}