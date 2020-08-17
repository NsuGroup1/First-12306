package com.example.a12306f.ticket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.a12306f.R;
import com.example.a12306f.a.Order;
import com.example.a12306f.a.Passenger;
import com.example.a12306f.adapter.YD03Adapter;
import com.example.a12306f.my.AddContactsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YuDing03 extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yu_ding03);


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
        start_city_03.setText(getIntent().getStringExtra("StationName").split("-")[0]);
        arrive_city_03.setText(getIntent().getStringExtra("StationName").split("-")[1]);
        textView_fachetime03.setText(getIntent().getStringExtra("FromToTime").split("-")[0]);
        textView_arrivaltime03.setText(getIntent().getStringExtra("FromToTime").split("-")[1]);
        textView_date03.setText(getIntent().getStringExtra("TicketDate").split(" ")[0] + "(" + getIntent().getIntExtra("day", 0) + "日)");
        textView_leixing03.setText(getIntent().getStringExtra("Seat"));
        textView_price03.setText(getIntent().getStringExtra("SeatPrice"));

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

        textView_tijiao03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(YuDing03.this, YuDing04.class);
                startActivity(intent2);
            }
        });

    }
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent datalist){
        super.onActivityResult(requestCode, resultCode, datalist);
        switch (resultCode) {
            case RESULT_OK:
                list_YD03 = new ArrayList<Map<String, Object>>();
                list_YD03 = (List<Map<String, Object>>) datalist.getSerializableExtra("contactdata");
                passengers = new Passenger[list_YD03.size()];
                for (int i = 0; i < list_YD03.size(); i++) {
                    passengers[i] = (Passenger) list_YD03.get(i).get("passenger");
                }

                //TODO 计算价钱
//                int PriceSum = Integer.parseInt(getIntent().getStringExtra("SeatPrice"));
//                Log.d("ss2", String.valueOf(PriceSum));
                String money = textView_price03.getText().toString().split("￥")[1].split("\\.")[0];
                int m = Integer.valueOf(money);
                textView_jieusuan03.setText("订单总额:￥" + (int) list_YD03.size() * m + "元");

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
}