package com.example.a12306f.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.a12306f.R;
import com.example.a12306f.a.Order;
import com.example.a12306f.utils.DialogClose;
import com.example.a12306f.utils.ZxingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketOrderPayedActivity extends AppCompatActivity {

    private TextView tvOrder,tvView;
    private ListView lvTicket;
    private Order order;
    private List<Map<String,Object>> data;
    private SimpleAdapter simpleAdapter;
//    private String[] names = {"冬不拉","陈飞"};
//    private String[] lieche = {"D5","D5"};
//    private String[] date = {"2020-6-1","2020-6-1"};
//    private String[] liechehao = {"6车51号","6车52号"};

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
            map.put("liechehao","2车"+(i+1)+"号");
//            map.put("liechehao",order.getPassengerList()[i].getSeat().getSeatNO());
            map.put("t",R.drawable.forward_25);
            data.add(map);
        }
        simpleAdapter = new SimpleAdapter(TicketOrderPayedActivity.this,
                data,
                R.layout.item_yuding04,
                new String[]{"names","lieche","date","liechehao"},
                new int[]{R.id.textView_name_YD04,R.id.textView_lieche_YD04,R.id.textView_date_YD04,R.id.textView_liechehao_YD04});
        lvTicket.setAdapter(simpleAdapter);
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
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

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
