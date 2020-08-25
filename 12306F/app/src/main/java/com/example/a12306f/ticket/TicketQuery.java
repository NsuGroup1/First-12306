package com.example.a12306f.ticket;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.a12306f.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketQuery extends AppCompatActivity {

    private TextView tv_beforeDay;
    private TextView tv_afterDay;
    private TextView tv_dateTitle;
    private TextView tv_stationTitle;
    private ListView lv_TicketInformationList;
    private List<Map<String, Object>> data,orderData;
    private SimpleAdapter queryAdapter;
    private ProgressDialog progressDialog;

//    private String[] TicketNum = {"G108", "D29", "D5", "T297", "K291"};
//    private Integer[] imgs1 = {R.drawable.flg_shi};
//    private Integer[] imgs2 = {R.drawable.flg_zhong, R.drawable.flg_guo, R.drawable.flg_guo, R.drawable.flg_zhong, R.drawable.flg_zhong};
//    private String[] StartTime = {"04:47", "07:00", "07:05", "12:00", "16:55"};
//    private String[] ArriveTime = {"14:46(0日)", "11:48(0日)", "11:55(0日)", "20:45(0日)", "01:30(1日)"};
//    private String[] Seat1 = {"高级软卧:42", "特等座:4", "一等座:17", "商务座:20", "高级软卧:2"};
//    private String[] Seat2 = {"硬座:45", "硬座:20", "软卧:48", "一等座:5", "特等座:18"};
//    private String[] Seat3 = {"一等座:8", "软座:7", "硬座:38", "硬卧:50", "硬座:33"};
//    private String[] Seat4 = {"无座:100", "硬卧:19", "无座:39", "无座:49", "无座:26"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_query);

        tv_beforeDay = findViewById(R.id.tv_previousDay);
        tv_afterDay = findViewById(R.id.tv_afterDay);
        tv_dateTitle = findViewById(R.id.TicketDateTitle);
        tv_stationTitle = findViewById(R.id.TicketStationTitle);
        lv_TicketInformationList = findViewById(R.id.TicketInformation);

        tv_stationTitle.setText(getIntent().getStringExtra("stationFrom") + "-" + getIntent().getStringExtra("stationTo"));
        tv_dateTitle.setText(getIntent().getStringExtra("startTicketDate"));

        tv_beforeDay.setOnClickListener(new MyTicketListener());
        tv_afterDay.setOnClickListener(new MyTicketListener());

//        data = new ArrayList<>();
//        for (int i = 0; i < TicketNum.length; i++) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("TicketNum", TicketNum[i]);
//            map.put("img1", imgs1[0]);
//            map.put("img2", imgs2[i]);
//            map.put("fromTime", StartTime[i]);
//            map.put("toTime", ArriveTime[i]);
//            map.put("seat1", Seat1[i]);
//            map.put("seat2", Seat2[i]);
//            map.put("seat3", Seat3[i]);
//            map.put("seat4", Seat4[i]);
//            data.add(map);
//        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        data = (List<Map<String, Object>>) bundle.getSerializable("searchData");

        queryAdapter = new SimpleAdapter(
                this,
                data,
                R.layout.item_yuding01,
                new String[]{"trainNo", "img1", "img2", "startTime", "arriveTime", "seat1", "seat2", "seat3", "seat4"},
                new int[]{R.id.TicketNumber, R.id.startImg, R.id.endImg, R.id.startTime, R.id.arriveTime, R.id.Seat1, R.id.Seat2, R.id.Seat3, R.id.Seat4}
        );
        lv_TicketInformationList.setAdapter(queryAdapter);
        lv_TicketInformationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ListView listView = (ListView) parent;
//                HashMap<String,String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
//                String liechehao = map.get("TicketNum");
//                String fachetime = map.get("fromTime");
//                String arrivaltime = map.get("toTime");
//                String seat1 = map.get("seat1");

                orderData = new ArrayList<>();//将data里面seat有关书籍放进orderData里面

                Bundle bundle = new Bundle();
//                bundle.putSerializable("orderData",orderData.get(position).get());
                bundle.putString("date",tv_dateTitle.getText().toString());
                bundle.putString("route",tv_stationTitle.getText().toString());
                bundle.putString("lieche",data.get(position).get("trainNo").toString());
                bundle.putString("timeout",data.get(position).get("startTime").toString());
                bundle.putString("timein",data.get(position).get("arriveTime").toString());
                bundle.putString("durationTime",data.get(position).get("durationTime").toString());

                Intent intent = new Intent(TicketQuery.this,YuDing02.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //前一天后一天的实现
    private class MyTicketListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Calendar calendar = Calendar.getInstance();
            String oldDateFrom = tv_dateTitle.getText().toString();
            int oldYear = Integer.parseInt(oldDateFrom.split("-")[0]);
            int oldMonth = Integer.parseInt(oldDateFrom.split("-")[1]) - 1;
            int oldDay = Integer.parseInt(oldDateFrom.split("-")[2].split(" ")[0]);
            calendar.set(oldYear, oldMonth, oldDay);
            switch (view.getId()) {
                case R.id.tv_previousDay:
                    calendar.add(Calendar.DAY_OF_MONTH, -1);

                    break;
                case R.id.tv_afterDay:
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    break;
            }
            //更新TextView
            String weekDay = DateUtils.formatDateTime(TicketQuery.this, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_WEEKDAY);
            tv_dateTitle.setText(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + weekDay);
        }
    }
}