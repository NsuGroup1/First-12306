package com.example.a12306f.ticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a12306f.R;
import com.example.a12306f.ViewPagerActivity;
import com.example.a12306f.a.Order;
import com.example.a12306f.a.Passenger;
import com.example.a12306f.order.TicketPayedSuccessActivity;
import com.example.a12306f.utils.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class YuDing04 extends AppCompatActivity {

    final private String TAG = "YuDing04";
    private ListView listView;
    private List<HashMap<String,Object>> data;
    private SimpleAdapter simpleAdapter;
    private TextView TV_zanbu,TV_queren,TV_ticketId;
    private Order order;
    private ProgressDialog progressDialog;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null){
                progressDialog.dismiss();
            }
            switch (msg.what){
                case 1:
                    String result1 = msg.obj.toString();
                    if ("1".equals(result1)){
                        Toast.makeText(YuDing04.this,"支付成功！",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(YuDing04.this,TicketPayedSuccessActivity.class);
                        intent.putExtra("order",order);
                        for (int i = 0;i < data.size();i++)
                            intent.putExtra("zw",data.get(i).get("liechehao").toString());
                        Log.d("order-----", String.valueOf(order));
                        startActivity(intent);
                    }else if ("0".equals(result1)){
                        Toast.makeText(YuDing04.this,"支付失败！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    Toast.makeText(YuDing04.this,"服务器错误，请重试！",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(YuDing04.this,"请重新登录！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
//    private String[] names = {"冬不拉","陈飞"};
//    private String[] lieche = {"D5","D5"};
//    private String[] date = {"2020-6-1","2020-6-1"};
//    private String[] liechehao = {"6车51号","6车52号"};



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_yu_ding04);

            ActionBar actionBar = getSupportActionBar();
//        actionBar.setLogo(R.mipmap.ic_launcher);
//        actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);


            listView = findViewById(R.id.listview_YD04);
            TV_zanbu = findViewById(R.id.TV_zanbupay_YD04);
            TV_queren = findViewById(R.id.TV_querenpay_YD04);
            TV_ticketId = findViewById(R.id.ticket_id);
            order = (Order) getIntent().getSerializableExtra("order");
            Log.d(TAG, "yuding4order: "+order);
            TV_ticketId.setText(order.getId());

            data = new ArrayList<>();
            for (int i = 0; i< order.getPassengerList().length; i++){
                HashMap<String,Object> map= new HashMap<>();
                map.put("names",order.getPassengerList()[i].getName());
                map.put("lieche",order.getTrain().getTrainNo());
                map.put("date",order.getTrain().getStartTrainDate());
                map.put("liechehao","5车"+(i+1)+"号");
                data.add(map);
            }
            simpleAdapter = new SimpleAdapter(this,data,R.layout.item_yuding04,
                    new String[]{"names","lieche","date","liechehao"},
                    new int[]{R.id.textView_name_YD04,R.id.textView_lieche_YD04,R.id.textView_date_YD04,
                            R.id.textView_liechehao_YD04});
            listView.setAdapter(simpleAdapter);

            TV_zanbu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(YuDing04.this, ViewPagerActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            TV_queren.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(){
                        @Override
                        public void run() {
                            Message message = new Message();
                            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                            String sessionid = sharedPreferences.getString("Cookie", "");
                            Log.d(TAG, "sessionid： " + sessionid);

                            try {
                                OkHttpClient okHttpClient = new OkHttpClient();
                                RequestBody requestBody = new  FormBody.Builder()
                                        .add("orderId",TV_ticketId.getText().toString())
                                        .build();
                                Request request = new Request.Builder()
                                        .url(Constant.Host +"/otn/Pay")
                                        .addHeader("Cookie", sessionid)
                                        .post(requestBody)
                                        .build();
                                Response response = okHttpClient.newCall(request).execute();
                                String responseData = response.body().string();
                                Log.d(TAG, "获取的服务器数据： " + responseData);
                                if (response.isSuccessful()) {
                                    //解析JSON
                                    Gson gson = new GsonBuilder().create();
                                    //Order orders = gson.fromJson(result,Order.class);
                                    String result = gson.fromJson(responseData,String.class);
                                    message.what = 1;
                                    message.obj = result;
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
//                    Intent intent = new Intent();
//                    intent.setClass(YuDing04.this, TicketPayedSuccessActivity.class);
//                    startActivity(intent);
                }
            });
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