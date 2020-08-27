package com.example.a12306f.order;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a12306f.R;

import com.example.a12306f.ViewPagerActivity;
import com.example.a12306f.a.Order;
import com.example.a12306f.utils.ZxingUtils;

import java.util.HashMap;
import java.util.Map;
//import com.example.a12306f.utils.ZxingUtils;

public class TicketPayedSuccessActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_payed_success);

        imageView = findViewById(R.id.ewm);
        back = findViewById(R.id.ticket_success_back);


        //创建二维码
        Map<String,Object> contact = (HashMap<String, Object>) getIntent().getSerializableExtra("row");
        Order order = (Order) getIntent().getSerializableExtra("order");
        ZxingUtils.createQRImage(
//                order.getId() + "," + order.getTrain().getTrainNo() + "," +
//                        order.getTrain().getStartTrainDate() + "," +
//                        order.getPassengerList(), imageView,700,700
                2020 + "," + "D5" + "," +
                2020 + "," +
                        123456, imageView,700,700
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
}
