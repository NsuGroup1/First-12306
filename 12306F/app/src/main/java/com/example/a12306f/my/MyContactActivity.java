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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a12306f.R;
import com.example.a12306f.a.Passenger;
import com.example.a12306f.utils.Constant;
import com.example.a12306f.utils.Md5Utils;
import com.example.a12306f.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
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

public class MyContactActivity extends AppCompatActivity {
    private ListView lv_contact;
    private List<Map<String, Object>> data;
    private ProgressDialog progressDialog;
    private SimpleAdapter adapter;
    private Passenger passenger;
    private String action = "";
    final private String TAG = "MyContactActivity";
//    private ActionBar actionBar = this.getActionBar();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            data.clear();
            switch (msg.what){
                case 1:
                    Passenger[] passengers = (Passenger[]) msg.obj;
                    for (Passenger passenger:passengers){
                        Map<String,Object> map = new HashMap<>();
                        //
                        map.put("name",passenger.getName());
                        map.put("type","("+passenger.getType()+")");
                        map.put("idType",passenger.getIdType()+"：");
                        map.put("id",passenger.getId());
                        map.put("tel",passenger.getTel());
                        data.add(map);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    Toast.makeText(MyContactActivity.this,"服务器错误，请重试！",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MyContactActivity.this,"请重新登录！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);
        //actionbar
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setLogo(R.mipmap.ic_launcher);
//        actionBar.setDisplayUseLogoEnabled(true);

        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        //显示联系人
        lv_contact = findViewById(R.id.lv_contact);
        data = new ArrayList<>();
        adapter = new SimpleAdapter(this, data, R.layout.item_contact,
                new String[]{"name", "type", "idType","id", "tel"},
                new int[]{R.id.contact_name, R.id.contact_age,R.id.idType, R.id.id_card_number, R.id.phone_number});
        // 绑定
        lv_contact.setAdapter(adapter);

        //编辑内容
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putString("name", "xxxxxx");
//                intent.putExtra("name", "");
                intent.putExtra("row", (Serializable) data.get(position));
                intent.setClass(MyContactActivity.this, MyContactEdit.class);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!NetworkUtils.checkNet(MyContactActivity.this)){
            Toast.makeText(MyContactActivity.this,"当前网络不可用",Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread() {
            @Override
            public void run() {
//                try {
//                   URL url = new URL(Constant.Host+"/otn/PassengerList");
//
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                    httpURLConnection.setRequestMethod("POST");// 提交
//                    httpURLConnection.setConnectTimeout(Constant.REQUEST_TIMEOUT);//连接超时
//                    httpURLConnection.setReadTimeout(Constant.SO_TIMEOUT);//读取超时
//                    // 发送POST请求
//                    httpURLConnection.setDoOutput(true);
//                    httpURLConnection.setDoInput(true);
//                    //不使用缓存
//                    httpURLConnection.setUseCaches(false);
//                    SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
//                    String value = sharedPreferences.getString("Cookie","");
//                    //设置请求属性
//                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
//                    httpURLConnection.setRequestProperty("Charset", "UTF-8");
//                    httpURLConnection.setRequestProperty("Cookie",value);
//                    // 获取URLConnection对象对应的输出流
//                    PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
//                    //发送请求参数
//                    String params = "";
//                    Log.d("xx",params);
//                    printWriter.write(params);
//                    // flush输出流的缓冲
//                    printWriter.flush();
//                    printWriter.close();
//
//                    int resultCode = httpURLConnection.getResponseCode();
//                    if (resultCode == HttpURLConnection.HTTP_OK) {
//                        InputStream in = httpURLConnection.getInputStream();
//                        StringBuffer sb = new StringBuffer();
//                        String readLine = new String();
//                        BufferedReader reader = new BufferedReader(
//                                new InputStreamReader(in, "UTF-8"));
//                        while ((readLine = reader.readLine()) != null) {
//                            sb.append(readLine).append("\n");
//                        }
//                        String result = sb.toString();
//                        //解析Json
//                        Gson gson = new GsonBuilder().create();
//                        Passenger[] passengers = gson.fromJson(result, Passenger[].class);
//
//                        msg.what = 1;
//                        msg.obj = passengers;
//                        in.close();
                    Message message = new Message();
                    action = "update";
                    SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                    String sessionid = sharedPreferences.getString("Cookie", "");
                    Log.d(TAG, "sessionid： " + sessionid);
                    try {
                        OkHttpClient okHttpClient = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(Constant.Host + "/otn/PassengerList")
                                .addHeader("Cookie", sessionid)
                                .get()
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
            case R.id.my_contact_add:
                Intent intent = new Intent();
            intent.setClass(MyContactActivity.this,MyContactAdd.class);
            startActivity(intent);
            break;
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.my_contact_add,menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //返回菜单的处理
    public boolean MenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }return super.onMenuItemSelected(featureId, item);
    }
}
