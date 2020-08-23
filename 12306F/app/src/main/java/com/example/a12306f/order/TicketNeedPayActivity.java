package com.example.a12306f.order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.a12306f.utils.NetworkUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketNeedPayActivity extends AppCompatActivity {
    private Order order;
    private List<Map<String,Object>> data;
    private SimpleAdapter simpleAdapter;
    private ProgressDialog progressDialog;

    private TextView tvNeedPayId, tvCancelOrder, tvConfirmOrder;
    private ListView lv_order_list_need_pay;
    private String[] names = {"冬不拉","陈飞"};
    private String[] lieche = {"D5","D5"};
    private String[] date = {"2020-6-1","2020-6-1"};
    private String[] liechehao = {"6车51号","6车52号"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_need_pay);

        tvNeedPayId = findViewById(R.id.ticket_order_id_wait);
        tvCancelOrder = findViewById(R.id.cancel_order);
        tvConfirmOrder = findViewById(R.id.confirm_order);
        lv_order_list_need_pay = findViewById(R.id.lv_ticket_order_need_pay);

//        order = (Order) getIntent().getSerializableExtra("order");
//        tvNeedPayId.setText(order.getId());

        data = new ArrayList<Map<String,Object>>();
        for (int i=0 ; i<names.length ; i++){
            Map<String,Object> map = new HashMap<>();
            map.put("names",names[i]);
            map.put("lieche",lieche[i]);
            map.put("date",date[i]);
            map.put("liechehao",liechehao[i]);
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
                Intent intent = new Intent(TicketNeedPayActivity.this, ViewPagerActivity.class);
//                startActivity(intent);
                finish();

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
                Intent intent = new Intent();
                intent.setClass(TicketNeedPayActivity.this,TicketPayedSuccessActivity.class);
                startActivity(intent);
            }
        });
    }
}
