package com.example.a12306f.order;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a12306f.R;

import com.example.a12306f.ViewPagerActivity;
import com.example.a12306f.a.Order;
import com.example.a12306f.utils.ZxingUtils;

import java.util.HashMap;
import java.util.Map;
import com.example.a12306f.utils.ZxingUtils;

public class TicketPayedSuccessActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button back;
    private Order order;
    private TextView tv_ticket_success;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_payed_success);

        ActionBar actionBar = getSupportActionBar();
//        actionBar.setLogo(R.mipmap.ic_launcher);
//        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        imageView = findViewById(R.id.ewm);
        back = findViewById(R.id.ticket_success_back);
        tv_ticket_success = findViewById(R.id.ticket_success_order);
        order = (Order) getIntent().getSerializableExtra("order");
        tv_ticket_success.setText(order.getId());

        //创建二维码
//        Map<String,Object> contact = (HashMap<String, Object>) getIntent().getSerializableExtra("row");
        Order order = (Order) getIntent().getSerializableExtra("order");
        ZxingUtils.createQRImage(
                "订单号："+order.getId() + "," +"列车号："+ order.getTrain().getTrainNo() + "," +
                        "出发日期："+order.getTrain().getStartTrainDate() + "," +
                        "乘客人数："+order.getPassengerList().length + "  价钱："+ order.getOrderPrice(), imageView,700,700
//                2020 + "," + "D5" + "," +
//                2020 + "," +
//                        123456, imageView,700,700
        );
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(TicketPayedSuccessActivity.this, ViewPagerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
