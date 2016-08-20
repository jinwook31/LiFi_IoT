package com.example.user.lot_floating;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 3 on 2016-05-15.
 */
public class ListAdapter extends ArrayAdapter<ListInfoitem> {

        private Context context;
        private ViewHolder viewHolder = null;
        private LayoutInflater inflater = null;

        public ListAdapter(Context context, int resource, ArrayList<ListInfoitem> arrayList) {
            super(context, resource, arrayList);
            inflater = LayoutInflater.from(context);
            this.context = context;
        }



    @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if(v == null){
                viewHolder = new ViewHolder();
                v = inflater.inflate(R.layout.activity_listview_item, null);

                viewHolder.item_name = (TextView) v.findViewById(R.id.list_item_name);
                viewHolder.item_info = (TextView) v.findViewById(R.id.list_item_info);

                v.setTag(viewHolder);

            }else {
                viewHolder = (ViewHolder) v.getTag();
            }

            viewHolder.item_name.setText(getItem(position).getItem_name());
            viewHolder.item_info.setText(getItem(position).getItem_info());

            return v;
        }

        class ViewHolder {

            TextView item_name = null;
            TextView item_info = null;

        }


}
