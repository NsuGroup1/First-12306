package com.example.a12306f.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a12306f.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String,Object>> list;
    private LayoutInflater listContainer;
    public Map<Integer,Boolean> cbxFlag = null;

    public class ViewHolder{
        public ImageView imageView_CA;
        public CheckBox checkBox_CA;
        public TextView name_CA,ID_CA,phone_CA;
    }

    public ContactsAdapter(Context context,List<Map<String,Object>> list){
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.list = list;
        cbxFlag = new HashMap<>();
        init();
    }

    private void init() {
        for (int i = 0;i <list.size(); i++){
            cbxFlag.put(i,false);
        }
    }

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
        return position;
    }

    public boolean hasChecked(int position){
        return cbxFlag.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int selectID = position;
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = listContainer.inflate(R.layout.item_add_passager_contact,null);
            holder.checkBox_CA = convertView.findViewById(R.id.checkBox_adcts);
            holder.ID_CA = convertView.findViewById(R.id.textView_id_adcts);
            holder.imageView_CA = convertView.findViewById(R.id.imageView_adcts);
            holder.name_CA = convertView.findViewById(R.id.textView_name_adcts);
            holder.phone_CA = convertView.findViewById(R.id.textView_phone_adcts);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.phone_CA.setText((String) list.get(position).get("phone"));
        holder.name_CA.setText((String) list.get(position).get("name"));
        holder.ID_CA.setText((String) list.get(position).get("ID"));

        holder.checkBox_CA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
//                     Intent intent = new Intent();
//                     intent.setClass(ContactsAdapter.this,yuding03Activity.class);
                    cbxFlag.put(selectID,true);
                }else {
                    cbxFlag.put(selectID,false);
                }
            }
        });
        holder.checkBox_CA.setChecked(cbxFlag.get(selectID));
        return convertView;
    }
}
