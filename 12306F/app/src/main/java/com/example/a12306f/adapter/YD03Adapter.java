package com.example.a12306f.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a12306f.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YD03Adapter extends BaseAdapter {

    private Context context;
    private List<Map<String, Object>> list;
    private LayoutInflater layoutInflater;

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (holder == null){
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_yuding03,null);
            holder.imageView_YD03 = convertView.findViewById(R.id.imageView_delect_YD03);
            holder.textView_ID_YD03 = convertView.findViewById(R.id.textView_ID_YD03);
            holder.textView_name_YD03 = convertView.findViewById(R.id.textView_name_YD03);
            holder.textView_phone_YD03 = convertView.findViewById(R.id.textView_phone_YD03);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView_name_YD03.setText(list.get(position).get("name").toString());
        holder.textView_ID_YD03.setText(list.get(position).get("ID").toString());
        holder.textView_phone_YD03.setText(list.get(position).get("phone").toString());
        holder.imageView_YD03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
            }
        });
        return convertView;
    }

    public class ViewHolder{
        public TextView textView_name_YD03,textView_ID_YD03,textView_phone_YD03;
        private ImageView imageView_YD03;
    }

    public YD03Adapter(Context context,List<Map<String,Object>> list){
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);

    }


}
