package com.example.a12306f.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.a12306f.R;

import java.util.ArrayList;
import java.util.HashMap;


public class JAdapter extends BaseAdapter {


    private ArrayList arrayList;
    private Context context;
    private int layout;

    public JAdapter(Context context, ArrayList arrayList, int layout) {
        this.context = context;
        this.arrayList = arrayList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(layout, null);
            holder = new ViewHolder();
            holder.startC = view.findViewById(R.id.startC);
            holder.line = view.findViewById(R.id.line);
            holder.endC = view.findViewById(R.id.endC);
            view.setTag(holder);
        }else{
            holder=(ViewHolder) view.getTag();
        }


        HashMap map = (HashMap)getItem(i);

        holder.startC.setText(map.get("startC").toString());
        holder.endC.setText(map.get("endC").toString());

            return view;
        }
    public class ViewHolder {
        TextView startC;
        TextView line;
        TextView endC;
    }


    }
