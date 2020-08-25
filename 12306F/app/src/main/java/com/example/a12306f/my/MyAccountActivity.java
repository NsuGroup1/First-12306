package com.example.a12306f.my;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.aware.PublishConfig;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a12306f.R;
import com.example.a12306f.a.Account;
import com.example.a12306f.a.Passenger;
import com.example.a12306f.utils.Constant;
import com.example.a12306f.utils.DialogClose;
import com.example.a12306f.utils.Md5Utils;
import com.example.a12306f.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;


import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyAccountActivity extends AppCompatActivity {
    private ListView myAccountlist;
    private Button btn_myAccountSave;
    private SimpleAdapter simpleAdapter;
    private List<Map<String,Object>> data;
    private ArrayList<HashMap<String, Object>> datalist;
    private ProgressDialog progressDialog;
    private String action = "";
    private static final String TAG = "MyAccountActivity";

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            if (progressDialog != null){
//                progressDialog.dismiss();
//            }
            switch (msg.what){
                case 1:
                    data.clear();
                    Account account = (Account) msg.obj;
                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("key","用户名");
                    map1.put("value",account.getUsername());
                    map1.put("img",R.drawable.flg_null);
                    data.add(map1);
                    Map<String,Object> map2 = new HashMap<>();
                    map2.put("key","姓名");
                    map2.put("value",account.getName());
                    map2.put("img",R.drawable.flg_null);
                    data.add(map2);
                    Map<String,Object> map3 = new HashMap<>();
                    map3.put("key","证件类型");
                    map3.put("value",account.getIdType());
                    map3.put("img",R.drawable.flg_null);
                    data.add(map3);
                    Map<String,Object> map4 = new HashMap<>();
                    map4.put("key","证件号码");
                    map4.put("value",account.getId());
                    map4.put("img",R.drawable.flg_null);
                    data.add(map4);
                    Map<String,Object> map5 = new HashMap<>();
                    map5.put("key","乘客类型");
                    map5.put("value",account.getType());
                    map5.put("img",R.drawable.forward_25);
                    data.add(map5);
                    Map<String,Object> map6 = new HashMap<>();
                    map6.put("key","电话");
                    map6.put("value",account.getTel());
                    map6.put("img",R.drawable.forward_25);
                    data.add(map6);
                    simpleAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    Toast.makeText(MyAccountActivity.this,"服务器错误，请重试！",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MyAccountActivity.this,"请重新登录！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
//            if (progressDialog != null){
//                progressDialog.dismiss();
//            }
            switch (message.what){
                case 1:
                    String result = message.obj.toString();
                    Log.d("result",result);
                    Toast.makeText(MyAccountActivity.this,"修改成功！",Toast.LENGTH_SHORT).show();
                    MyAccountActivity.this.finish();
                    break;
                case 2:
                    Toast.makeText(MyAccountActivity.this,"修改失败！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setLogo(R.mipmap.ic_launcher);
//        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        myAccountlist = findViewById(R.id.lv_account);
        btn_myAccountSave = findViewById(R.id.my_account_save);

        data = new ArrayList<>();
//        String[] key1 = {"用户名","姓名","证件类型","证件号码","乘客类型","电话"};
//        String[] key2 = {"dong","冬不拉","身份证","11010119910511947X","成人","13812345678"};
//        Integer[] key3 = {R.drawable.flg_null,R.drawable.flg_null,R.drawable.flg_null,R.drawable.flg_null,R.drawable.forward_25,R.drawable.forward_25};
//        for (int i=0;i<key1.length;i++){
//            Map<String,Object> map = new HashMap<>();
//            map.put("key",key1[i]);
//            map.put("value",key2[i]);
//            map.put("img",key3[i]);
//            data.add(map);
//        }

         simpleAdapter = new SimpleAdapter(this,
                data,
                R.layout.item_my_contact_edit,
                new String[]{"key","value","img"},
                new int[]{R.id.tv_my_contact_edit_key,R.id.tv_my_contact_edit_value,R.id.img_my_contact_edit_flag});

        myAccountlist.setAdapter(simpleAdapter);

        myAccountlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                switch (position){
                    case 4:
                        final String[] data1 = {"成人", "学生", "儿童", "其他"};
                        new AlertDialog.Builder(MyAccountActivity.this)
                                .setTitle("请选择乘客类型")
                                .setSingleChoiceItems(data1, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int is) {
                                        String type = data1[is];
                                        data.get(position).put("value",type);
                                        simpleAdapter.notifyDataSetChanged();
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .create()
                                .show();
                        break;
                    case 5:
                        final EditText editTel = new EditText(MyAccountActivity.this);
                        editTel.setText((String) data.get(position).get("value"));
                        new AlertDialog.Builder(MyAccountActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle("请输入电话号码")
                                .setView(editTel)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int witch) {
                                        String newTel = editTel.getText().toString();
                                        if(TextUtils.isEmpty(newTel)){
                                            DialogClose.setClosable(dialog,false);
                                            editTel.setError("请输入电话号码");
                                            editTel.requestFocus();
                                        }else{
                                            DialogClose.setClosable(dialog,true);
                                            data.get(position).put("value",newTel);
                                            simpleAdapter.notifyDataSetChanged();
                                        }
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        DialogClose.setClosable(dialog,true);
                                    }
                                })
                                .create()
                                .show();
                        break;
                }
            }
        });
//保存修改
        btn_myAccountSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.checkNet(MyAccountActivity.this)){
                    Toast.makeText(MyAccountActivity.this,"当前网络不可用",Toast.LENGTH_SHORT).show();
                    return;
                }
//                progressDialog = ProgressDialog.show(
//                        MyAccountActivity.this,
//                        null,
//                        "正在加载中....",
//                        false,true);
                new Thread(){
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                        String sessionid = sharedPreferences.getString("Cookie", "");

                        String result = "";
                        Message message = handler1.obtainMessage();
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("用户名",data.get(0).get("value").toString())
                                .add("姓名",data.get(1).get("value").toString())
                                .add("证件类型",data.get(2).get("value").toString())
                                .add("证件号码",data.get(3).get("value").toString())
                                .add("乘客类型",data.get(4).get("value").toString())
                                .add("电话", data.get(5).get("value").toString())
                                .add("action","update")
                                .build();
                        Request request = new Request.Builder()
                                .url(Constant.Host+"/otn/Account")
                                .addHeader("Cookie", sessionid)
                                .post(requestBody)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            Log.d(TAG, "response:"+response);
                            String responsedata = response.body().string();
                            Log.d(TAG, "获取服务器数据:" + responsedata);

                            //解析成功接收到的数据
                            if (response.isSuccessful()) {
                                //解析Json
                                Gson gson = new GsonBuilder().create();
                                Account account = gson.fromJson(responsedata,Account.class);
                                message.what = 1;
                                message.obj=account;
                            }
                             else {
                                message.what = 2;
                                Log.d(TAG,"what2:");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            message.what = 2;
                            Log.d(TAG,"what2:");
                        }
                        handler.sendMessage(message);

                    }
                }.start();
            }
        });
    }

    //查询显示
    @Override
    protected void onResume() {
        super.onResume();
        if (!NetworkUtils.checkNet(MyAccountActivity.this)){
            Toast.makeText(MyAccountActivity.this,"当前网络不可用",Toast.LENGTH_SHORT).show();
            return;
        }
//        progressDialog = ProgressDialog.show(MyAccountActivity.this,
//                null,
//                "正在加载中....",
//                false,true);
        new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = handler.obtainMessage();
                String action = "query";
                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                String sessionid = sharedPreferences.getString("Cookie", "");
                Log.d(TAG, "sessionid： " + sessionid);
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new  FormBody.Builder()
                            .add("action",action)
                            .build();
                    Request request = new Request.Builder()
                            .url(Constant.Host + "/otn/Account")
                            .addHeader("Cookie", sessionid)
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d(TAG, "获取的服务器数据： " + responseData);

                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().create();

                        Account accounts = gson.fromJson(responseData,Account.class);
                        Log.d(TAG, "accounts： " + accounts);
                        message.what = 1;
                        message.obj = accounts;
                    } else {
                        message.what = 2;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    message.what = 2;
                }catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    message.what = 3;
                }
            handler.sendMessage(message);
            }
        }.start();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.my_contact_add:
                Intent intent = new Intent();
                intent.setClass(MyAccountActivity.this,MyContactAdd.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
