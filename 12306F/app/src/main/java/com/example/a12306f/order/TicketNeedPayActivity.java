package com.example.a12306f.order;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
;
import com.example.a12306f.R;
import com.example.a12306f.ViewPagerActivity;
import com.example.a12306f.a.Order;
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

public class TicketNeedPayActivity extends AppCompatActivity {

    final private String TAG = "TicketNeedPayActivity";
    private Order order;
    private List<Map<String,Object>> data;
    private SimpleAdapter simpleAdapter;
    private ProgressDialog progressDialog;

    private TextView tvNeedPayId, tvCancelOrder, tvConfirmOrder;
    private ListView lv_order_list_need_pay;
    private Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null){
                progressDialog.dismiss();
            }
            switch (msg.what){
                case 1:
                    String result = msg.obj.toString();
                    if ("1".equals(result)){
                        Toast.makeText(TicketNeedPayActivity.this,"取消订单成功！",Toast.LENGTH_SHORT).show();
                        TicketNeedPayActivity.this.finish();
                    }
                    break;
            }
        }
    };
    private Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null){
                progressDialog.dismiss();
            }
            switch (msg.what){
                case 1:
                    String result = msg.obj.toString();
                    if ("1".equals(result)){
                        Toast.makeText(TicketNeedPayActivity.this,"确认支付成功！",Toast.LENGTH_SHORT).show();
                        TicketNeedPayActivity.this.finish();
                    }
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
        setContentView(R.layout.activity_ticket_need_pay);

        tvNeedPayId = findViewById(R.id.ticket_order_id_wait);
        tvCancelOrder = findViewById(R.id.cancel_order);
        tvConfirmOrder = findViewById(R.id.confirm_order);
        lv_order_list_need_pay = findViewById(R.id.lv_ticket_order_need_pay);

        order = (Order) getIntent().getSerializableExtra("order");
        tvNeedPayId.setText(order.getId());

        data = new ArrayList<>();
        for (int i=0 ; i < order.getPassengerList().length; i++){
            Map<String,Object> map = new HashMap<>();
            map.put("names",order.getPassengerList()[i].getName());
            map.put("lieche",order.getTrain().getTrainNo());
            map.put("date",order.getTrain().getStartTrainDate());
            map.put("liechehao","5车"+(i+1)+"号");
            data.add(map);
        }
        simpleAdapter = new SimpleAdapter(TicketNeedPayActivity.this,
                data,
                R.layout.item_yuding04,
                new String[]{"names","lieche","date","liechehao"},
                new int[]{R.id.textView_name_YD04,R.id.textView_lieche_YD04,R.id.textView_date_YD04,R.id.textView_liechehao_YD04});
        lv_order_list_need_pay.setAdapter(simpleAdapter);


        //点击取消订单
        tvCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.checkNet(TicketNeedPayActivity.this)){
                    Toast.makeText(TicketNeedPayActivity.this,"当前网络不可用",Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = ProgressDialog.show(TicketNeedPayActivity.this,
                        null,
                        "正在加载中....",
                        false,true);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Message message = handler1.obtainMessage();
                        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                        String sessionid = sharedPreferences.getString("Cookie", "");
                        Log.d(TAG, "sessionid： " + sessionid);

                        try {
                            OkHttpClient okHttpClient = new OkHttpClient();
                            RequestBody requestBody = new  FormBody.Builder()
                                    .add("orderId",order.getId())
                                    .build();
                            Request request = new Request.Builder()
                                    .url(Constant.Host +"/otn/Cancel")
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
                        handler1.sendMessage(message);
                    }
                }.start();

            }
        });
        //确认支付
        tvConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.checkNet(TicketNeedPayActivity.this)){
                    Toast.makeText(TicketNeedPayActivity.this,"当前网络不可用",Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = ProgressDialog.show(TicketNeedPayActivity.this,
                        null,
                        "正在加载中....",
                        false,true);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Message message = handler2.obtainMessage();
                        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                        String sessionid = sharedPreferences.getString("Cookie", "");
                        Log.d(TAG, "sessionid： " + sessionid);

                        try {
                            OkHttpClient okHttpClient = new OkHttpClient();
                            RequestBody requestBody = new  FormBody.Builder()
                                    .add("orderId",order.getId())
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
                                String result1 = gson.fromJson(responseData,String.class);
                                message.what = 1;
                                message.obj = result1;
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
                        handler2.sendMessage(message);
                    }
                }.start();
            }
        });
    }
}
