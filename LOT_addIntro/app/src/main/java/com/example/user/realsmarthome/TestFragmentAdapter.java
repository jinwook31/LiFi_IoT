package com.example.user.realsmarthome;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.viewpagerindicator.IconPagerAdapter;

import org.apache.http.client.utils.CloneUtils;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

class TestFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

  //  static DB_Item db_item = new DB_Item("test1","test1","test1","test1");
 //   static DB_Item db_item2 = new DB_Item("test2","test2","test2","test2");

  //  protected static DB_Item DBObject[] = new DB_Item[]{};
    protected static String[] CONTENT = new String[0] ;
    public static ArrayList<DB_Item> DBObject = new ArrayList<>();
    int currentnum;

    /* protected static final int[] ICONS = new int[] {
             R.drawable.ic_next,
             R.drawable.ic_next,
             R.drawable.ic_next,
             R.drawable.ic_next
     };
 */
    private int mCount=0;
    public TestFragmentAdapter(FragmentManager fm) {//생성자
        super(fm);
       // printContent();
        mCount = CONTENT.length;
    }

    @Override
    public Fragment getItem(int position) {
        return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
        //  return TestFragment.newInstance(CONTENT.get(position % CONTENT.size()));
    }

    @Override
    public int getCount() {
        return mCount;
    }
    @Override
    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return TestFragmentAdapter.CONTENT[position % CONTENT.length];
        ///   return (CharSequence) TestFragmentAdapter.CONTENT.get(position % CONTENT.size());
    }

    @Override
    public int getIconResId(int index) {
        // return ICONS[index % ICONS.length];
        return 0;
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }

    public void add(DB_Item obj){
    //    CONTENT[mCount++] = obj;
    }

    public boolean setting(ArrayList<DB_Item> dbarraylist,String locate){
        currentnum=0;
        DBObject = new ArrayList<>();
        if(dbarraylist.size() != 0) {
            for (int i = 0; i < dbarraylist.size(); i++) {
                if ((!dbarraylist.get(i).getKeyID().equals("Directory")) && (dbarraylist.get(i).getLocation().equals(locate))) { //폴더가 아니고 locate가 자신이면
                    DBObject.add(dbarraylist.get(i));
                }
            }
         //   Log.e("DBObject size", String.valueOf(DBObject.size())); //7
            CONTENT = new String[DBObject.size()];
        }

        for(int i=0; i<DBObject.size(); i++){
            String item = DBObject.get(i).getName()+"/";
            item += DBObject.get(i).getKeyID()+"/";
            item += DBObject.get(i).getOnoffState()+"/";
            item += DBObject.get(i).getLocation();
         //   Log.e("실제로추가", CONTENT[currentnum]+","+item);
            CONTENT[currentnum++] = item; //string으로 넣어줌
            Log.e("Content 의 길이: ",CONTENT.length+"");
        }
        mCount = CONTENT.length;
        return true;
    }

    public void printContent(){

        for(int i=0; i<mCount; i++){
            Log.e("CONTENT size", String.valueOf(CONTENT.length));
            Log.e("mCount size", String.valueOf(mCount));
            Log.e("CONTENT",CONTENT[i]);
        }
    }
}