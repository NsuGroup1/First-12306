package com.example.a12306f.ticket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a12306f.R;
import com.example.a12306f.a.Account;
import com.example.a12306f.a.Order;
import com.example.a12306f.a.Passenger;
import com.example.a12306f.adapter.YD03Adapter;
import com.example.a12306f.my.AddContactsActivity;
import com.example.a12306f.my.MyAccountActivity;
import com.example.a12306f.utils.Constant;
import com.example.a12306f.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class YuDing03 extends AppCompatActivity {

    private static final String TAG = "YuDing03";
    private ProgressDialog progressDialog;
    private TextView textView_fachetime03,textView_arrivaltime03,textView_date03
            ,textView_liechehao03,textView_leixing03,textView_price03,textView_addren03,
            start_city_03,arrive_city_03;

    private SimpleAdapter adapter;
    private Passenger[] passengers;
    private TextView textView_tijiao03,textView_jieusuan03;
    private ListView listView_YD03;
    private List<Map<String,Object>> list_YD03;
    private YD03Adapter yd03Adapter;
    private String name;
    private String ID;
    private String phone;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(progressDialog != null){
                progressDialog.dismiss();
            }
            switch (msg.what){
                case 1:
                    Order order = (Order) msg.obj;
                    Log.d("handleMessage: ", String.valueOf(order));
                    Intent intent = new Intent();
                    intent.setClass(YuDing03.this,YuDing04.class);
                    intent.putExtra("order",order);
                    startActivity(intent);
                    break;
                case 2:
                    Log.d("handleMessage:", "123");
                    break;
            }
        }
    };
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yu_ding03);

        ActionBar actionBar = getSupportActionBar();
//        actionBar.setLogo(R.mipmap.ic_launcher);
//        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);


        start_city_03 = findViewById(R.id.start_city_03);
        arrive_city_03 = findViewById(R.id.arrive_city_03);
        textView_fachetime03 = findViewById(R.id.textView_fachetime03);
        textView_arrivaltime03 = findViewById(R.id.textView_arrivaltime03);
        textView_leixing03 = findViewById(R.id.textView_leixing03);
        textView_date03 = findViewById(R.id.textView_date03);
        textView_price03 = findViewById(R.id.textView_price03);
        textView_addren03 = findViewById(R.id.textView_addren03);
        textView_tijiao03 = findViewById(R.id.textView_tijiao03);
        textView_jieusuan03 = findViewById(R.id.textView_jiesuan03);
        listView_YD03 = findViewById(R.id.yuding03_listView);
        textView_liechehao03 = findViewById(R.id.textView_liechehao03);

        textView_liechehao03.setText(getIntent().getStringExtra("LieCheHao"));
        textView_liechehao03.setTextColor(Color.BLACK);
        start_city_03.setText(getIntent().getStringExtra("StationName").split("-")[0]);
        arrive_city_03.setText(getIntent().getStringExtra("StationName").split("-")[1]);
        textView_fachetime03.setText(getIntent().getStringExtra("FromToTime").split("-")[0]);
        textView_arrivaltime03.setText(getIntent().getStringExtra("FromToTime").split("-")[1].split("\\(")[0]);
        textView_date03.setText(getIntent().getStringExtra("TicketDate").split(" ")[0] + "(" + getIntent().getIntExtra("day", 0) + "日)");
        textView_date03.setTextColor(Color.BLACK);
        textView_leixing03.setText(getIntent().getStringExtra("Seat"));
        textView_price03.setText("￥"+getIntent().getStringExtra("SeatPrice"));
        textView_price03.setTextColor(R.color.orange);
//        SeatNum = getIntent().getStringExtra("SeatNum");

//        list_YD03 = new ArrayList<>();
//        yd03Adapter = new YD03Adapter(this,list_YD03);
//        listView_YD03.setAdapter(yd03Adapter);

        textView_addren03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YuDing03.this, AddContactsActivity.class);
                startActivityForResult(intent, 1001);
            }
        });
        listView_YD03.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list_YD03.remove(position);
                yd03Adapter.notifyDataSetChanged();
                listView_YD03.invalidate();
                Log.d(TAG, "on   "+list_YD03.size());

                String money = textView_price03.getText().toString().split("￥")[1].split("\\.")[0];
                int m = Integer.valueOf(money);
                textView_jieusuan03.setText("订单总额:￥" + (int) list_YD03.size() * m + "元");
                textView_jieusuan03.setTextSize(20);
            }
        });

        textView_tijiao03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int tNumber = Integer.valueOf(textView_leixing03.getText().toString().split("\\(")[1].split("\\)")[0]);
                if (list_YD03 == null || list_YD03.size() == 0){
                    Toast.makeText(YuDing03.this, "请添加乘车人！", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (tNumber < list_YD03.size()){
                    Toast.makeText(YuDing03.this, "剩余票数不足！", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            if (!NetworkUtils.checkNet(YuDing03.this)) {
                                Toast.makeText(YuDing03.this, "当前网络不可用", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            passengers = new Passenger[list_YD03.size()];
                            for (int i = 0; i < list_YD03.size(); i++) {
                                passengers[i] = (Passenger) list_YD03.get(i).get("passenger");
                            }
//                        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
//                        String sessionid = sharedPreferences.getString("Cookie", "");
//                        Message message = handler.obtainMessage();
//                        OkHttpClient client = new OkHttpClient();
//                        RequestBody requestBody = new FormBody.Builder()
//                                .add("trainNo", textView_liechehao03.getText().toString())
//                                .add("startTrainDate", textView_date03.getText().toString())
//                                .add("seatName", textView_leixing03.getText().toString())
//                                .add("id", passengers[0].getId())
//                                .add("idType", passengers[0].getIdType())
//                                .build();
//                        Request request = new Request.Builder()
//                                .url(Constant.Host + "/otn/Order")
//                                .addHeader("Cookie", sessionid)
//                                .post(requestBody)
//                                .build();
//                        try {
//                            Response response = client.newCall(request).execute();
//                            Log.d(TAG, "response:" + response);
//                            String responsedata = response.body().string();
//                            Log.d(TAG, "获取服务器数据:" + responsedata);
//                            //解析成功接收到的数据
//                            if (response.isSuccessful()) {
//                                //解析Json
//                                Gson gson = new GsonBuilder().create();
//                                Order orders = gson.fromJson(responsedata,Order.class);
//                                message.what = 1;
//                                message.obj = orders;
//                            } else {
//                                message.what = 2;
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            message.what = 2;
//                        }
//                        handler.sendMessage(message);
                            Message message = handler.obtainMessage();
                            try {
                                URL url = new URL(Constant.Host + "/otn/Order");
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setConnectTimeout(Constant.REQUEST_TIMEOUT);
                                httpURLConnection.setReadTimeout(Constant.SO_TIMEOUT);//读取超时 单位毫秒
                                //发送POST方法必须设置容下两行
                                httpURLConnection.setDoOutput(true);
                                httpURLConnection.setDoInput(true);

                                //不使用缓存
                                httpURLConnection.setUseCaches(false);
                                SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                                String value = sp.getString("Cookie", "");

                                //设置请求属性
                                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                                httpURLConnection.setRequestProperty("Charset", "UTF-8");
                                httpURLConnection.setRequestProperty("Cookie", value);

                                PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
                                //发送请求数据
//                            Log.d("zzNo",tvTicketPassengerStep3TrainNo.getText().toString());
//                            Log.d("zzDate",tvTicketPassengerStep3Date.getText().toString().split("\\(")[0]);
//                            Log.d("zzName",tvTicketPassengerStep3SeatName.getText().toString().split("\\(")[0]);
                                String params = "trainNo=" + textView_liechehao03.getText().toString() +
                                        "&startTrainDate=" + textView_date03.getText().toString().split("\\(")[0] +
                                        "&seatName=" + textView_leixing03.getText().toString().split("\\(")[0];
                                for (int i = 0; i < passengers.length; i++) {
                                    params += "&id=" + passengers[i].getId() +
                                            "&idType=" + passengers[i].getIdType();
                                }
                                Log.d("params", params);
                                Log.d("xx", "这里4");
                                printWriter.write(params);
                                printWriter.flush();
                                printWriter.close();

                                int resultCode = httpURLConnection.getResponseCode();
                                if (resultCode == HttpURLConnection.HTTP_OK) {
                                    InputStream in = httpURLConnection.getInputStream();
                                    StringBuffer sb = new StringBuffer();
                                    String readLine = new String();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                                    while ((readLine = reader.readLine()) != null) {
                                        sb.append(readLine).append("\n");
                                    }
                                    String result = sb.toString();
                                    Log.d("result", result);
                                    Log.d("xx", "这里5");

                                    //解析JSON
                                    Gson gson = new GsonBuilder()
                                            .create();
                                    Order orders = gson.fromJson(result, Order.class);
                                    message.what = 1;
                                    message.obj = orders;
                                    Log.d("xx,这里6", String.valueOf(orders));
                                } else {
                                    message.what = 2;
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                message.what = 2;
                            } catch (IOException e) {
                                e.printStackTrace();
                                message.what = 2;
                            }
                            handler.sendMessage(message);
                        }
                    }.start();
                }
            }
        });

        textView_jieusuan03.setText("订单总额:￥" + (int) 0.00 + "元");
        textView_jieusuan03.setTextSize(20);
    }



    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent datalist){
        super.onActivityResult(requestCode, resultCode, datalist);
        switch (resultCode) {
            case RESULT_OK:
//                list_YD03 = new ArrayList<Map<String, Object>>();
                list_YD03 = (List<Map<String, Object>>) datalist.getSerializableExtra("contactdata");
                passengers = new Passenger[list_YD03.size()];
                for (int i = 0; i < list_YD03.size(); i++) {
                    passengers[i] = (Passenger) list_YD03.get(i).get("passenger");
                }
                Log.d("YuDing03.this","size"+list_YD03.size());


                //TODO 计算价钱
//                int PriceSum = Integer.parseInt(getIntent().getStringExtra("SeatPrice"));
//                Log.d("ss2", String.valueOf(PriceSum));
                String money = textView_price03.getText().toString().split("￥")[1].split("\\.")[0];
                int m = Integer.valueOf(money);
                textView_jieusuan03.setText("订单总额:￥" + (int) list_YD03.size() * m + "元");
                textView_jieusuan03.setTextSize(20);

//                adapter = new SimpleAdapter(this,
//                        list_YD03,
//                        R.layout.item_yuding03,
//                        new String[]{"name", "idcard", "tel"},
//                        new int[]{R.id.textView_name_YD03, R.id.textView_ID_YD03, R.id.textView_phone_YD03});
                yd03Adapter = new YD03Adapter(this, list_YD03);
                listView_YD03.setAdapter(yd03Adapter);
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}