package com.example.a12306f.my;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddContactAdd extends AppCompatActivity {
    private ListView addContactAdd;
    private Button btn_Save;
    private SimpleAdapter adapter;
    private List<Map<String,Object>> data;
    private ProgressDialog progressDialog;
    final private String TAG = "AddContactAddActivity";

    String[] k1 = {"姓名","证件类型","证件号码","乘客类型","手机号"};
    String[] k2 = {""};
    Integer[] k3 = {R.drawable.forward_25};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact_add);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        addContactAdd = findViewById(R.id.lv_add_contact_add);
        btn_Save = findViewById(R.id.add_contact_add_save);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (progressDialog != null){
                    progressDialog.dismiss();
                }
                switch (msg.what){
                    case 1:
                        String result = msg.obj.toString();
                        if ("1".equals(result)){
                            Toast.makeText(AddContactAdd.this,"保存成功!",Toast.LENGTH_LONG).show();
                            AddContactAdd.this.finish();
                        }else if ("-1".equals(result)){
                            Toast.makeText(AddContactAdd.this,"保存失败!",Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 2:
                        Toast.makeText(AddContactAdd.this,"服务器错误!",Toast.LENGTH_LONG).show();
                }
            }
        };

        //保存点击事件
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.checkNet(AddContactAdd.this)){
                    Toast.makeText(AddContactAdd.this,"当前网络不可用",Toast.LENGTH_LONG).show();
                    return;
                }
                if (data.get(0).get("k2").toString().isEmpty()||data.get(1).get("k2").toString().isEmpty()
                        ||data.get(2).get("k2").toString().isEmpty()||data.get(3).get("k2").toString().isEmpty()
                        ||data.get(4).get("k2").toString().isEmpty()){
                    Toast.makeText(AddContactAdd.this,"数据不完善，保存失败",Toast.LENGTH_SHORT).show();
                    finish();
                }else {
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
            }
        });

        //TODO 获取上个页面传来的数据
        data = new ArrayList<>();

        for (int i = 0;i <k1.length;i++){
            Map<String,Object> map1 = new HashMap<>();
            map1.put("k1",k1[i]);
            map1.put("k2",k2[0]);
            map1.put("k3",k3[0]);
            data.add(map1);
        }


        adapter = new SimpleAdapter(
                this,
                data,
                R.layout.item_my_contact_edit,
                new String[]{"k1","k2","k3"},
                new int[]{R.id.tv_my_contact_edit_key,R.id.tv_my_contact_edit_value,R.id.img_my_contact_edit_flag});

        addContactAdd.setAdapter(adapter);

        addContactAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                switch(position){
                    case 0:
                        final EditText editName = new EditText(AddContactAdd.this);
                        editName.setText((String) data.get(position).get("k2"));
                        new AlertDialog.Builder(AddContactAdd.this)
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
                        final String[] data0 = {"身份证","学生证"};
                        new AlertDialog.Builder(AddContactAdd.this)
                                .setTitle("请选择证件类型")
                                .setSingleChoiceItems(data0, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int is) {
                                        String type = data0[is];
                                        data.get(position).put("k2",type);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DialogClose.setClosable(dialogInterface,true);
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                .create()
                                .show();
                        break;
                    case 2:
                        final EditText editIdNum = new EditText(AddContactAdd.this);
                        editIdNum.setText((String) data.get(position).get("k2"));
                        new AlertDialog.Builder(AddContactAdd.this)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle("请输入证件号码")
                                .setView(editIdNum)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int witch) {
                                        String newId = editIdNum.getText().toString();
                                        if(TextUtils.isEmpty(newId)){
                                            DialogClose.setClosable(dialog,false);
                                            editIdNum.setError("请输入证件号码");
                                            editIdNum.requestFocus();
                                        }else if (newId.length() != 18){
                                            DialogClose.setClosable(dialog,false);
                                            editIdNum.setError("请输入18位有效证件号码");
                                            editIdNum.requestFocus();
                                        }
                                        else{
                                            String str= newId.substring(0,17);//身份证前17位
                                            String str1 = newId.substring(newId.length()-1);//身份证最后一位
                                            String str2= newId.substring(newId.length());
                                            Pattern p = Pattern.compile("[0-9]*");
                                            Pattern p2 = Pattern.compile("[0-9]*");
                                            Pattern p1 = Pattern.compile("X");
                                            Matcher m = p.matcher(str);//比较
                                            Matcher m1 = p1.matcher(str1);
                                            Matcher m2 = p2.matcher(str2);
                                            if((m2.matches() || (m.matches() && m1.matches()))) {
                                                DialogClose.setClosable(dialog, true);
                                                data.get(position).put("k2", newId);
                                                adapter.notifyDataSetChanged();

                                            }else {
                                                Toast.makeText(AddContactAdd.this, "请输入有效证件号码", Toast.LENGTH_SHORT).show();
                                                DialogClose.setClosable(dialog, false);
                                                editIdNum.setError("请输入18位有效证件号码");
                                                editIdNum.requestFocus();
                                            }
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
                        new AlertDialog.Builder(AddContactAdd.this)
                                .setTitle("请选择乘客类型")
                                .setSingleChoiceItems(data1, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int is) {
                                        String type = data1[is];
                                        data.get(position).put("k2",type);
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        adapter.notifyDataSetChanged();
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
                    case 4:
                        final EditText editTel = new EditText(AddContactAdd.this);
                        editTel.setText((String) data.get(position).get("k2"));
                        new AlertDialog.Builder(AddContactAdd.this)
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
                                        }else if (newTel.length() != 11){
                                            DialogClose.setClosable(dialog,false);
                                            editTel.setError("请输入11位有效电话号码");
                                            editTel.requestFocus();
                                        }
                                        else{
                                            Pattern p = Pattern.compile("[0-9]*");
                                            Matcher m = p.matcher(newTel);
                                            if(!m.matches() ){
                                                Toast.makeText(AddContactAdd.this,"请输入有效电话号码", Toast.LENGTH_SHORT).show();
                                                DialogClose.setClosable(dialog,false);
                                                editTel.setError("请输入11位有效电话号码");
                                                editTel.requestFocus();
                                            }else {
                                                DialogClose.setClosable(dialog, true);
                                                data.get(position).put("k2", newTel);
                                                adapter.notifyDataSetChanged();
                                            }
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

    private ContentResolver contentResolver;
    private ListView listView_dialog;
    private SimpleAdapter searchAdapter;
    private List<HashMap<String,Object>> searchData;
    private String[] value_search;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.my_contact_search:
                contentResolver = getContentResolver();
                View searchView = getLayoutInflater().inflate(R.layout.dialog_mycontact_search,null);

//                View view = this.getLayoutInflater().inflate(R.layout.dialog_mycontact_search,null);
                listView_dialog = searchView.findViewById(R.id.lv_AMC_dialog_search);
                searchData = new ArrayList<>();
                SimpleAdapter searchAdapter = new SimpleAdapter(AddContactAdd.this
                        ,searchData
                        ,R.layout.lv_search_dialog_mca
                        ,new String[]{"display_name","_id"}
                        ,new int[]{R.id.TV_name_search,R.id.TV_phone_search});
//                listView_dialog.setAdapter(searchAdapter);
//                for (int i = 0;i<search1.length;i++){
//                    Map<String,Object> map = new HashMap<>();
//                    map.put("name",search1[i]);
//                    map.put("number",search2[i]);
//                    searchData.add((HashMap<String, Object>) map);
//                }
                listView_dialog.setAdapter(searchAdapter);
                listView_dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name_lv = searchData.get(position).get("display_name").toString();
                        String phone = searchData.get(position).get("_id").toString();
                        value_search= new String[]{name_lv, "", "", "", phone};
                        data.clear();
                        for (int i = 0;i<value_search.length;i++){
                            HashMap map_search = new HashMap();
                            map_search.put("k1",k1[i]);
                            map_search.put("k2",value_search[i]);
                            map_search.put("k3",k3[0]);
                            data.add(map_search);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

                //获取联系人
                Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                        new String[]{"_id","display_name"},null,null,null);

                while (cursor.moveToNext()){
                    HashMap map1 = new HashMap();
                    int _id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String display_name = cursor.getString(cursor.getColumnIndex("display_name"));
                    Log.d("My12306",_id+","+display_name);
                    map1.put("display_name",display_name);
//                    mContactsName.add(display_name);
                    Cursor cursor2 = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?"+_id,
                            new String[]{_id+""},null);//传入联系人_id
                    //获取电话
                    String number = null;
                    while (cursor2.moveToNext()){
                        number = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        mContactsPhone.add(number);
                        map1.put("_id",number);
                    }
                    cursor2.close();
                    searchData.add(map1);
                }
                cursor.close();

//                View searchView = getLayoutInflater().inflate(R.layout.dialog_mycontact_search,null);

                new AlertDialog.Builder(AddContactAdd.this)
                        .setTitle("请选择")
                        .setView(searchView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DialogClose.setClosable(dialog,true);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DialogClose.setClosable(dialog,true);
                            }
                        }).create().show();
        }
        return super.onOptionsItemSelected(item);
    }

}