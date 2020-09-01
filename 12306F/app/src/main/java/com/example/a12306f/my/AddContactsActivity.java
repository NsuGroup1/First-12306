package com.example.a12306f.my;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a12306f.R;
import com.example.a12306f.a.Passenger;
import com.example.a12306f.adapter.ContactsAdapter;
import com.example.a12306f.utils.Constant;
import com.example.a12306f.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddContactsActivity extends AppCompatActivity {

    private static final String TAG = "AddContactsActivity";
    private ListView listView;
    private List<Map<String,Object>> list;
    private List<Map<String,Object>> contactData;
    private ContactsAdapter contactsAdapter;
    private TextView textView_addren03;
    private ProgressDialog progressDialog;
    private SimpleAdapter adapter;
//    private int COUNT = 0;

//    private String[] name_AC = {"冬不拉(成人)","陈为飞(成人)","艾米(学生)"};
//    private String[] ID_AC = {"123456789012345678","42092423249678320","680243223487866"};
//    private String[] phone_AC = {"12345678901","67886867687","57546754378"};

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null){
                progressDialog.dismiss();
            }
            switch (msg.what){
                case 1:
                    list  = new ArrayList<>();
                    contactData = new ArrayList<>();
                    Passenger[] passengers = (Passenger[]) msg.obj;
                    for (Passenger passenger:passengers){
                        Map<String,Object> map = new HashMap<>();
                        //
                        map.put("name",passenger.getName()+"("+passenger.getType()+")");
                        map.put("idCard",passenger.getIdType()+":"+passenger.getId());
                        map.put("tel","电话:"+passenger.getTel());
                        map.put("passenger",passenger);
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
//                                        COUNT++;
                                    }else {
                                        contactData.remove(position);
//                                        COUNT--;
                                    }
                                }
                            });
                            return view;
                        }
                    };
                    listView.setAdapter(adapter);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);


        listView = findViewById(R.id.addcontacts_lv03);
        textView_addren03 = findViewById(R.id.textView_add_adcts);
//        list =getItems();

//        contactsAdapter = new ContactsAdapter(this,list);
//        listView.setAdapter(contactsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.putExtra("row", (Serializable) list.get(position));
                intent.setClass(AddContactsActivity.this, AddContactsEdit.class);
                startActivity(intent);
            }
        });
        textView_addren03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("contactdata", (Serializable) contactData);
                //startActivity(intent);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!NetworkUtils.checkNet(AddContactsActivity.this)){
            Toast.makeText(AddContactsActivity.this,"当前网络不可用",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog = ProgressDialog.show(
                AddContactsActivity.this,
                null,
                "正在加载中....",
                false,true);
        new Thread(){
            @Override
            public void run() {
                Message message = new Message();
                String action = "query";

                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                String sessionid = sharedPreferences.getString("Cookie", "");
                Log.d(TAG, "sessionid： " + sessionid);

                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new  FormBody.Builder()
                            .add("action",action)
                            .build();
                    Request request = new Request.Builder()
                            .url(Constant.Host +"/otn/TicketPassengerList")
                            .addHeader("Cookie", sessionid)
//                            .get()
                            .post(requestBody)
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d(TAG, "获取的服务器数据： " + responseData);
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().create();
                        Passenger[] passengers = gson.fromJson(responseData, Passenger[].class);
                        message.what = 1;
                        message.obj = passengers;
                    } else {
                        message.what = 2;
                    }

                }
                catch (IOException e) {
                    e.printStackTrace();
                    message.what = 2;
                }
                catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    message.what = 3;
                }
                handler.sendMessage(message);
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_contact_add,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                finish();
                break;
            case R.id.my_contact_add:
                Intent intent = new Intent();
                intent.setClass(AddContactsActivity.this,AddContactAdd.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}