package com.example.a12306f.my;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a12306f.R;
import com.example.a12306f.adapter.ContactsAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddContactsActivity extends AppCompatActivity {
    private ListView listView;
    private List<Map<String,Object>> list;
    private List<Map<String,Object>> contactData;
    private ContactsAdapter contactsAdapter;
    private TextView textView_addren03;
    private ProgressDialog progressDialog;
    private SimpleAdapter adapter;

    private String[] name_AC = {"冬不拉(成人)","陈为飞(成人)","艾米(学生)"};
    private String[] ID_AC = {"123456789012345678","42092423249678320","680243223487866"};
    private String[] phone_AC = {"12345678901","67886867687","57546754378"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("添加联系人" );
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        listView = findViewById(R.id.addcontacts_lv03);
        textView_addren03 = findViewById(R.id.textView_add_adcts);
        list =getItems();
        list  = new ArrayList<>();
        contactData = new ArrayList<>();
        for (int i = 0;i<name_AC.length;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("name",name_AC[i]);
            map.put("idCard",ID_AC[i]);
            map.put("tel",phone_AC[i]);
            list.add(map);
        }
        adapter = new SimpleAdapter(
                AddContactsActivity.this,
                list,
                R.layout.item_add_passager_contact,
                new String[]{"name","idCard","tel"},
                new int[]{R.id.textView_name_adcts,R.id.textView_id_adcts,R.id.textView_phone_adcts})
        {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final View view = super.getView(position, convertView, parent);
                CheckBox checkBox = view.findViewById(R.id.checkBox_adcts);
                //TODO 对复选框实现点击监听
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            contactData.add(list.get(position));
                        }else {
                            contactData.remove(position);
                        }
                    }
                });
                return view;
            }
        };
        listView.setAdapter(adapter);
//        contactsAdapter = new ContactsAdapter(this,list);
//        listView.setAdapter(contactsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (contactsAdapter.hasChecked(position)){
//                    name = list.get(position).get("name").toString();
//                    ID = list.get(position).get("idCard").toString();
//                    phone = list.get(position).get("tel").toString();
//                    bundle2.putString("name_ACA2",list.get(position).get("name").toString());
//                    bundle2.putString("ID_ACA2",list.get(position).get("ID").toString());
//                    bundle2.putString("phone_ACA2",list.get(position).get("phone").toString());
//                    intent2.putExtras(bundle2);
//                    bundle2.putSerializable("data", (Serializable) list);
//                    intent2.putExtras(bundle2);
//                }
                Intent intent = new Intent(AddContactsActivity.this,ACA03AddActivity.class);
//                Bundle bundle = new Bundle();
////                bundle.putSerializable("data", (Serializable) list);
//                bundle.putString("name_ACA",list.get(position).get("name").toString());
//                bundle.putString("ID_ACA",list.get(position).get("idCard").toString());
//                bundle.putString("phone_ACA",list.get(position).get("tel").toString());
//                intent.putExtras(bundle);
                intent.putExtra("row", (Serializable) list.get(position));
                intent.setClass(AddContactsActivity.this, MyContactEdit.class);
                startActivity(intent);
            }
        });
        textView_addren03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent2 = new Intent(AddContactsActivity.this, YuDing03.class);
//                Bundle bundle2 = new Bundle();
//                bundle2.putString("name_YD03",name);
//                bundle2.putString("ID_YD03",ID);
//                bundle2.putString("phone_YD03",phone);
//                intent2.putExtras(bundle2);
//                startActivity(intent2);
                Intent intent = new Intent();
                intent.putExtra("contactdata", (Serializable) contactData);
                //startActivity(intent);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    private List<Map<String, Object>> getItems() {
        List<Map<String,Object>> list = new ArrayList<>();
        for (int i = 0; i < name_AC.length; i++){
            Map<String,Object> map = new HashMap<>();
            map.put("name",name_AC[i]);
            map.put("ID",ID_AC[i]);
            map.put("phone",phone_AC[i]);
            list.add(map);
        }
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_contact_add,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                Intent intent = new Intent();
                intent.setClass(AddContactsActivity.this,MyContactAdd.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}