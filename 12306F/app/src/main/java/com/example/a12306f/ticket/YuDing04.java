package com.example.a12306f.ticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.a12306f.R;
import com.example.a12306f.ViewPagerActivity;
import com.example.a12306f.order.TicketPayedSuccessActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YuDing04 extends AppCompatActivity {

    private ListView listView;
    private List<HashMap<String,Object>> data;
    private SimpleAdapter simpleAdapter;
    private TextView TV_zanbu,TV_queren;
    private String[] names = {"冬不拉","陈飞"};
    private String[] lieche = {"D5","D5"};
    private String[] date = {"2020-6-1","2020-6-1"};
    private String[] liechehao = {"6车51号","6车52号"};



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

            data = new ArrayList<>();
            for (int i = 0;i<names.length;i++){
                HashMap<String,Object> map= new HashMap<>();
                map.put("names",names[i]);
                map.put("lieche",lieche[i]);
                map.put("date",date[i]);
                map.put("liechehao",liechehao[i]);
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
                    Intent intent = new Intent();
                    intent.setClass(YuDing04.this, TicketPayedSuccessActivity.class);
                    startActivity(intent);
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