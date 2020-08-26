package com.example.a12306f.my;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
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
import java.util.concurrent.Phaser;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    String[] k1 = {"姓名","证件类型","证件号码","乘客类型","手机号"};
    String[] k2 = {""};
    Integer[] k3 = {R.drawable.forward_25};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_contact_search,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private ContentResolver contentResolver;
    private ListView listView_dialog;
//    private List<String> mContactsName = new ArrayList<>();
//    private List<String> mContactsPhone = new ArrayList<>();
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
                listView_dialog = findViewById(R.id.lv_AMC_dialog_search);
                searchAdapter = new SimpleAdapter(this,searchData,R.layout.lv_search_dialog_mca,
                        new String[]{"name","number"},new int[]{R.id.TV_name_search,R.id.TV_phone_search});
                listView_dialog.setAdapter(searchAdapter);
                listView_dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name_lv = searchData.get(position).get("name").toString();
                        String phone = searchData.get(position).get("number").toString();
                        value_search= new String[]{name_lv, "", "", "", phone};
                        for (int i = 0;i<value_search.length;i++){
                            HashMap map_search = new HashMap();
                            map_search.put("k2",value_search[i]);
                            data.add(map_search);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

//                Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
//                Cursor cursor = contentResolver.query(uri,null,null,null,null);
//                while (cursor.moveToNext()){
//                    int id =cursor.getInt(cursor.getColumnIndex("_id"));
//                    String name = cursor.getString(cursor.getColumnIndex("display_name"));
//                    Log.i("test",id+" "+name);
//
//                    Uri uriData = Uri.parse("content://com.android.contacts/raw_contacts/"+id+"/data");
//                    Cursor cursorData = contentResolver.query(uriData,null,null,null,null);
//
//                    while (cursorData.moveToNext()){
//                        String data1 = cursorData.getString(cursorData.getColumnIndex("data1"));
//                        String type = cursorData.getString(cursorData.getColumnIndex("mimetype"));
//                        Log.i("test"," "+data1+":"+type);
//                        mContactsName.add(name);
//                        mContactsPhone.add(data1);
//                    }
//                }/

                //获取联系人
                Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                        new String[]{"_id","display_name"},null,null,null);

                while (cursor.moveToNext()){
                    HashMap map1 = new HashMap();
                    int _id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String display_name = cursor.getString(cursor.getColumnIndex("display_name"));
                    Log.d("My12306",_id+","+display_name);
                    map1.put("name",display_name);
//                    mContactsName.add(display_name);
                    Cursor cursor2 = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+_id,
                            new String[]{_id+""},null);//传入联系人_id
                    //获取电话
                    String number = null;
                    while (cursor2.moveToNext()){
                        number = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        mContactsPhone.add(number);
                        map1.put("number",number);
                    }
                    cursor2.close();
                    searchData.add(map1);
                }
                cursor.close();

                View searchView = getLayoutInflater().inflate(R.layout.dialog_mycontact_search,null);

                new AlertDialog.Builder(MyContactAdd.this)
                        .setTitle("请选择")
                        .setView(searchView)
//                        .setAdapter(new SearchAdapter(), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                                String[] value ={}
////                                Map map2 = new HashMap();
////                                map2.put("k2",mContacts.get(which));
////                                data.add(map2);
//
//                            }
//                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
        }
        return super.onOptionsItemSelected(item);
    }
//    class SearchAdapter extends BaseAdapter{
//        private TextView tv_name,tv_phone;
//
//        @Override
//        public int getCount() {
//            return mContactsName.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return position;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View view = getLayoutInflater().inflate(R.layout.lv_search_dialog_mca,null);
//            tv_name = view.findViewById(R.id.TV_name_search);
//            tv_phone = view.findViewById(R.id.TV_phone_search);
//            tv_name.setText(mContactsName.get(position));
//            tv_phone.setText(mContactsPhone.get(position));
//            return view;
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact_add);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        myContactAdd = findViewById(R.id.lv_my_contact_add);
        btn_Save = findViewById(R.id.contact_add_save);

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

        //保存点击事件
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.checkNet(MyContactAdd.this)){
                    Toast.makeText(MyContactAdd.this,"当前网络不可用",Toast.LENGTH_LONG).show();
                    return;
                }
                if (data.get(0).get("k2").toString().isEmpty()||data.get(1).get("k2").toString().isEmpty()
                ||data.get(2).get("k2").toString().isEmpty()||data.get(3).get("k2").toString().isEmpty()
                ||data.get(4).get("k2").toString().isEmpty()){
                    Toast.makeText(MyContactAdd.this,"数据不完善，保存失败",Toast.LENGTH_SHORT).show();
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
        Intent intent = getIntent();
//        Map<String,Object> contact = (HashMap<String, Object>) getIntent().getSerializableExtra("row");

        data = new ArrayList<Map<String, Object>>();

//        String name = (String) contact.get("name");
//        String name = "";
//        map1.put("k1","姓名");
//        //以左括号进行分割，取第一段
//        map1.put("k2",name);
//        map1.put("k3",R.drawable.forward_25);
//        data.add(map1);
//
//        Map<String,Object> map2 = new HashMap<>();
//        String idType = "";
//        map2.put("k1","证件类型");
//        //以冒号进行分割，取第一段
//        map2.put("k2",idType);
//        map2.put("k3",R.drawable.forward_25);
//        data.add(map2);
//
//        Map<String,Object> map3 = new HashMap<>();
////        String idCard = (String) contact.get("idCard");
//        String idCard = "";
//        map3.put("k1","证件号码");
//        //以冒号进行分割，取第一段
//        map3.put("k2",idCard);
//        map3.put("k3",R.drawable.forward_25);
//        data.add(map3);
//
//        Map<String,Object> map4 = new HashMap<>();
////        String age = (String) contact.get("age");
//        String age = "";
//        map4.put("k1","乘客类型");
//        map4.put("k2",age);
//        map4.put("k3",R.drawable.forward_25);
//        data.add(map4);
//
//        Map<String,Object> map5 = new HashMap<>();
////        String tel = (String) contact.get("tel");
//        String tel = "";
//        map5.put("k1","手机号");
//        map5.put("k2",tel);
//        map5.put("k3",R.drawable.forward_25);
//        data.add(map5);
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

        myContactAdd.setAdapter(adapter);

        myContactAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                switch(position){
                    case 0:
                        final EditText editName = new EditText(MyContactAdd.this);
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
                            final String[] data0 = {"身份证","学生证"};
                            new AlertDialog.Builder(MyContactAdd.this)
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
                        final EditText editIdNum = new EditText(MyContactAdd.this);
                        editIdNum.setText((String) data.get(position).get("k2"));
                        new AlertDialog.Builder(MyContactAdd.this)
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
//                                            Matcher m2 = p.matcher(newId);
                                            if((m2.matches() || (m.matches() && m1.matches()))) {
                                                DialogClose.setClosable(dialog, true);
                                                data.get(position).put("k2", newId);
                                                adapter.notifyDataSetChanged();

                                            }else {
                                                Toast.makeText(MyContactAdd.this, "请输入有效证件号码", Toast.LENGTH_SHORT).show();
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
                        new AlertDialog.Builder(MyContactAdd.this)
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
                                        }else if (newTel.length() != 11){
                                            DialogClose.setClosable(dialog,false);
                                            editTel.setError("请输入11位有效电话号码");
                                            editTel.requestFocus();
                                        }
                                        else{
                                            Pattern p = Pattern.compile("[0-9]*");
                                            Matcher m = p.matcher(newTel);
                                            if(!m.matches() ){
                                                Toast.makeText(MyContactAdd.this,"请输入有效电话号码", Toast.LENGTH_SHORT).show();
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
}