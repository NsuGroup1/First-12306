package com.example.a12306f.my;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a12306f.R;
import com.example.a12306f.utils.Constant;
import com.example.a12306f.utils.DialogClose;
import com.example.a12306f.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyContactAdd extends AppCompatActivity {

    private ListView myContactAdd;
    private Button btn_Save;
    private SimpleAdapter adapter;
    private List<Map<String,Object>> data;
    private ProgressDialog progressDialog;
    final private String TAG = "MyContactAddActivity";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
//                if (progressDialog != null){
//                    progressDialog.dismiss();
//                }
            switch (msg.what){
                case 1:
                    String result = msg.obj.toString();
                    if ("1".equals(result)){
                        Toast.makeText(MyContactAdd.this,"保存成功!",Toast.LENGTH_LONG).show();
                        MyContactAdd.this.finish();
                    }else if ("-1".equals(result)){
                        Toast.makeText(MyContactAdd.this,"保存失败!",Toast.LENGTH_LONG).show();
                    }
                    break;
                case 2:
                    Toast.makeText(MyContactAdd.this,"服务器错误!",Toast.LENGTH_LONG).show();
            }
        }
    };

    String[] k1 = {"姓名","证件类型","证件号码","乘客类型","手机号"};
    String[] k2 = {""};
    Integer[] k3 = {R.drawable.forward_25};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact_add);

        myContactAdd = findViewById(R.id.lv_my_contact_add);
        btn_Save = findViewById(R.id.contact_add_save);
        //保存点击事件
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.checkNet(MyContactAdd.this)){
                    Toast.makeText(MyContactAdd.this,"当前网络不可用",Toast.LENGTH_LONG).show();
                    return;
                }
//                progressDialog = progressDialog.show(MyContactAdd.this,null,"正在加载中..."
//                ,false,true);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Message msg = handler.obtainMessage();
                        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                        String sessionid = sharedPreferences.getString("Cookie", "");
                        Log.d(TAG, "sessionid： " + sessionid);
                        OkHttpClient okHttpClient = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("姓名",data.get(0).get("k2").toString())
                                .add("证件类型",data.get(1).get("k2").toString())
                                .add("证件号码",data.get(2).get("k2").toString())
                                .add("乘客类型",data.get(3).get("k2").toString())
                                .add("电话",data.get(4).get("k2").toString())
                                .add("action","new")
                                .build();
                        Request request = new Request.Builder()
                                .url(Constant.Host + "/otn/Passenger")
                                .addHeader("Cookie", sessionid)
                                .post(requestBody)
                                .build();
                        try {
                            Response response = okHttpClient.newCall(request).execute();
                            String responseData = response.body().string();
                            Log.d(TAG, "获取的服务器数据： " + responseData);
                            if (response.isSuccessful()){
                                Gson gson = new GsonBuilder().create();
                                String result = gson.fromJson(responseData,String.class);
                                msg.what = 1;
                                msg.obj = result;
                            }else {
                                msg.what = 2;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            msg.what = 2;
                        }
                        handler.sendMessage(msg);
                    }
                }.start();
            }
        });
        //TODO 获取上个页面传来的数据
        Intent intent = getIntent();
//        Map<String,Object> contact = (HashMap<String, Object>) getIntent().getSerializableExtra("row");

        data = new ArrayList<Map<String, Object>>();

        Map<String,Object> map1 = new HashMap<>();
//        String name = (String) contact.get("name");
        String name = "";
        map1.put("k1","姓名");
        //以左括号进行分割，取第一段
        map1.put("k2",name);
        map1.put("k3",R.drawable.forward_25);
        data.add(map1);

        Map<String,Object> map2 = new HashMap<>();
        String idType = "";
        map2.put("k1","证件类型");
        //以冒号进行分割，取第一段
        map2.put("k2",idType);
        map2.put("k3",R.drawable.forward_25);
        data.add(map2);

        Map<String,Object> map3 = new HashMap<>();
//        String idCard = (String) contact.get("idCard");
        String idCard = "";
        map3.put("k1","证件号码");
        //以冒号进行分割，取第一段
        map3.put("k2",idCard);
        map3.put("k3",R.drawable.forward_25);
        data.add(map3);

        Map<String,Object> map4 = new HashMap<>();
//        String age = (String) contact.get("age");
        String age = "";
        map4.put("k1","乘客类型");
        map4.put("k2",age);
        map4.put("k3",R.drawable.forward_25);
        data.add(map4);

        Map<String,Object> map5 = new HashMap<>();
//        String tel = (String) contact.get("tel");
        String tel = "";
        map5.put("k1","手机号");
        map5.put("k2",tel);
        map5.put("k3",R.drawable.forward_25);
        data.add(map5);

        adapter = new SimpleAdapter(
                this,
                data,
                R.layout.item_my_contact_edit,
                new String[]{"k1","k2","k3"},
                new int[]{R.id.tv_my_contact_edit_key,R.id.tv_my_contact_edit_value,R.id.img_my_contact_edit_flag});

        myContactAdd.setAdapter(adapter);

        myContactAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                switch(position){
                    case 0:
                        final TextView editName = new EditText(MyContactAdd.this);
                        editName.setText((String) data.get(position).get("k2"));
                        new AlertDialog.Builder(MyContactAdd.this)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle("请输入姓名")
                                .setView(editName)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int witch) {
                                        String newName = editName.getText().toString();
                                        if(TextUtils.isEmpty(newName)){
                                            DialogClose.setClosable(dialog,false);
                                            editName.setError("请输入姓名");
                                            editName.requestFocus();
                                        }else{
                                            DialogClose.setClosable(dialog,true);
                                            data.get(position).put("k2",newName);
                                            adapter.notifyDataSetChanged();
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
                    case 1:
                            final String[] data0 = {"身份证"};
                            new AlertDialog.Builder(MyContactAdd.this)
                                    .setTitle("请选择证件类型")
                                    .setSingleChoiceItems(data0, 0, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int is) {
                                            String type = data0[is];
                                            data.get(position).put("k2",type);
                                            adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .create()
                                    .show();
                            break;
                    case 2:
                        final EditText editIdNum = new EditText(MyContactAdd.this);
                        editIdNum.setText((String) data.get(position).get("k2"));
                        new AlertDialog.Builder(MyContactAdd.this)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle("请输入证件号码")
                                .setView(editIdNum)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int witch) {
                                        String newTel = editIdNum.getText().toString();
                                        if(TextUtils.isEmpty(newTel)){
                                            DialogClose.setClosable(dialog,false);
                                            editIdNum.setError("请输入证件号码");
                                            editIdNum.requestFocus();
                                        }else{
                                            DialogClose.setClosable(dialog,true);
                                            data.get(position).put("k2",newTel);
                                            adapter.notifyDataSetChanged();
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
                    case 3:
                        final String[] data1 = {"成人", "学生", "儿童", "其他"};
                        new AlertDialog.Builder(MyContactAdd.this)
                                .setTitle("请选择乘客类型")
                                .setSingleChoiceItems(data1, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int is) {
                                        String type = data1[is];
                                        data.get(position).put("k2",type);
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .create()
                                .show();
                        break;
                    case 4:
                        final EditText editTel = new EditText(MyContactAdd.this);
                        editTel.setText((String) data.get(position).get("k2"));
                        new AlertDialog.Builder(MyContactAdd.this)
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
                                            data.get(position).put("k2",newTel);
                                            adapter.notifyDataSetChanged();
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
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_contact_search,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.my_contact_search:

                TextView TV_search = new TextView(MyContactAdd.this);
                new AlertDialog.Builder(MyContactAdd.this)
                        .setTitle("请选择")
                        .setView(TV_search)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
        }
        return super.onOptionsItemSelected(item);
    }
}