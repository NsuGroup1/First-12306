package com.example.a12306f.order;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.a12306f.R;
import com.example.a12306f.a.Order;
import com.example.a12306f.my.MyContactAdd;
import com.example.a12306f.utils.Constant;
import com.example.a12306f.utils.DialogClose;
import com.example.a12306f.utils.ZxingUtils;
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

public class TicketOrderPayedActivity extends AppCompatActivity {
    private TextView tvOrder,tvView;
    private ListView lvTicket;
    private Order order;
    private List<Map<String,Object>> data;
    private SimpleAdapter simpleAdapter;
    final String TAG = "TicketOrderPayedActivity";

//    private String[] names = {"冬不拉","陈飞"};
//    private String[] lieche = {"D5","D5"};
//    private String[] date = {"2020-6-1","2020-6-1"};
//    private String[] liechehao = {"6车51号","6车52号"};

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_order_payed);

        tvOrder = findViewById(R.id.ticket_number);
        tvView = findViewById(R.id.view_ewm);
        lvTicket = findViewById(R.id.lv_ticket);

        order = (Order) getIntent().getSerializableExtra("order");
        tvOrder.setText(order.getId());

        data = new ArrayList<Map<String,Object>>();
//        for (int i=0 ; i<order.getPassengerList().length ; i++){
//            Map<String,Object> map = new HashMap<>();
//            map.put("ticket4Name",order.getPassengerList()[i].getName());
//            map.put("ticket4No",order.getTrain().getTrainNo());
//            map.put("ticket4Date",order.getTrain().getStartTrainDate());
//            map.put("ticket4SeatName","6车51号");
//            data.add(map);
//        }
        for (int i=0 ; i<order.getPassengerList().length ; i++){
            Map<String,Object> map = new HashMap<>();
            map.put("names",order.getPassengerList()[i].getName());
            map.put("lieche",order.getTrain().getTrainNo());
            map.put("date",order.getTrain().getStartTrainDate());
            map.put("id_TOP",order.getPassengerList()[i].getId());
            map.put("type_TOP",order.getPassengerList()[i].getType());
            map.put("liechehao","2车"+(i+1)+"号");
            map.put("t",R.drawable.forward_25);
            data.add(map);
        }
        simpleAdapter = new SimpleAdapter(TicketOrderPayedActivity.this,
                data,
                R.layout.item_yuding04,
                new String[]{"names","lieche","date","liechehao"},
                new int[]{R.id.textView_name_YD04,R.id.textView_lieche_YD04,R.id.textView_date_YD04,R.id.textView_liechehao_YD04});
        lvTicket.setAdapter(simpleAdapter);
        Log.d(TAG,"order:"+order);
        lvTicket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String[] idtype = {"退票", "改签"};
                new AlertDialog.Builder(TicketOrderPayedActivity.this)
                        .setTitle("请选择操作")
                        .setSingleChoiceItems(idtype, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int is) {
                                String type = idtype[is];
                                data.get(position).put("value",type);
                                simpleAdapter.notifyDataSetChanged();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final Handler handler= new Handler(){
                                    @Override
                                    public void handleMessage(@NonNull Message msg) {
                                        super.handleMessage(msg);
                                        switch (msg.what){
                                            case 1:
                                                String result = msg.obj.toString();
                                                if ("-1".equals(result)) {
                                                    simpleAdapter.notifyDataSetChanged();
                                                    Toast.makeText(TicketOrderPayedActivity.this, "退票成功!", Toast.LENGTH_LONG).show();
                                                }else {
                                                    Toast.makeText(TicketOrderPayedActivity.this,"退票失败！",Toast.LENGTH_SHORT).show();
                                                }
                                                break;
                                            case 2:
                                                Toast.makeText(TicketOrderPayedActivity.this, "网络问题！", Toast.LENGTH_LONG).show();
                                                break;
                                        }
                                    }
                                };
                                Log.d(TAG,"i:"+i);
                                if (i==-1){
                                    new Thread(){
                                        @SuppressLint("LongLogTag")
                                        @Override
                                        public void run() {
                                            super.run();
                                            Message msg = handler.obtainMessage();
                                            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                                            String sessionid = sharedPreferences.getString("Cookie", "");
                                            Log.d(TAG, "sessionid：" + sessionid);
                                            OkHttpClient okHttpClient = new OkHttpClient();
                                            RequestBody requestBody = new FormBody.Builder()
                                                    .add("orderId",tvOrder.getText().toString())
                                                    .add("id",data.get(position).get("id_TOP").toString())
                                                    .add("idType",data.get(position).get("type_TOP").toString())
                                                    .build();
                                            Request request = new Request.Builder()
                                                    .url(Constant.Host + "/otn/Refund")
                                                    .addHeader("Cookie", sessionid)
                                                    .post(requestBody)
                                                    .build();
                                            Response response = null;
                                            try {
                                                response = okHttpClient.newCall(request).execute();
                                                Log.d(TAG, "response： " + response);
                                                String responseData = response.body().string();
                                                Log.d(TAG, "获取的服务器数据： " + responseData);
                                                if (response.isSuccessful()){
                                                    Gson gson = new GsonBuilder().create();
                                                    String result = gson.fromJson(responseData,String.class);
                                                    Log.d(TAG, "result： " + result);
                                                    Log.d(TAG,"order:"+order);
                                                    msg.what = 1;
                                                    msg.obj = result;
                                                }else {
                                                    msg.what = 2;
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            handler.sendMessage(msg);
                                        }
                                    }.start();
                                }
//                                else {
//                                    Toast.makeText()
//                                }
                            }
                        })
                        .create()
                        .show();
            }
        });
        tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_ewm,null);
                ImageView img =  dialogView.findViewById(R.id.ewm);
                Order order = (Order) getIntent().getSerializableExtra("order");
                ZxingUtils.createQRImage(
                        "订单号："+order.getId() + "," +
                                "   列车号："+ order.getTrain().getTrainNo() + "," +
                                "   出发日期："+order.getTrain().getStartTrainDate() + "," +
                                "   乘客人数："+order.getPassengerList().length +
                                "   总价："+ order.getOrderPrice(), img,700,700);
                new AlertDialog.Builder(TicketOrderPayedActivity.this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("二维码")
                        .setView(dialogView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DialogClose.setClosable(dialog,true);
                            }
                        })
                        .create()
                        .show();
            }
        });
    }
}
