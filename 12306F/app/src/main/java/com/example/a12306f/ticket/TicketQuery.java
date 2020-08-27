package com.example.a12306f.ticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a12306f.R;
import com.example.a12306f.ViewPagerActivity;
import com.example.a12306f.a.Seat;
import com.example.a12306f.a.Train;
import com.example.a12306f.fragment.TicketFragment;
import com.example.a12306f.my.MyAccountActivity;
import com.example.a12306f.my.MyContactAdd;
import com.example.a12306f.utils.Constant;
import com.example.a12306f.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TicketQuery extends AppCompatActivity {
    final String TAG = "TicketQuery.this";
    private TextView tv_beforeDay;
    private TextView tv_afterDay;
    private TextView tv_dateTitle;
    private TextView tv_stationTitle;
    private ListView lv_TicketInformationList;
    private List<Map<String, Object>> data,orderData;
    private SimpleAdapter queryAdapter;
    private ProgressDialog progressDialog;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (progressDialog != null){
                progressDialog.dismiss();
            }
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    data.clear();
                    Train[] trains = (Train[]) msg.obj;
                    if (trains.length == 0){
                        Toast.makeText(TicketQuery.this,"没有对应的车次",Toast.LENGTH_SHORT).show();
                    }else {
                        for (Train trains1 : trains) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("trainNo", trains1.getTrainNo());
                            if (trains1.getStartStationName().equals(trains1.getFromStationName())) {
                                map.put("img1", R.drawable.flg_shi);
                            } else {
                                map.put("img1", R.drawable.flg_guo);
                            }
                            if (trains1.getEndStationName().equals(trains1.getToStationName())) {
                                map.put("img2", R.drawable.flg_zhong);
                            } else {
                                map.put("img2", R.drawable.flg_guo);
                            }
//                        map.put("startStationName",trains1.getStartStationName());
//                        map.put("endStationName",trains1.getEndStationName());
//                        map.put("fromStationName",trains1.getFromStationName());
//                        map.put("toStationName",trains1.getToStationName());
                            map.put("startTime", trains1.getStartTime());
                            map.put("durationTime", trains1.getDurationTime());
                            map.put("arriveTime", trains1.getArriveTime() + "(" + trains1.getDayDifference() + ")");
                            String[] seatKey = {"seat1", "seat2", "seat3", "seat4"};
                            int i = 0;
                            Map<String, Seat> seats = trains1.getSeats();
                            for (String key : seats.keySet()) {
                                Seat seat = seats.get(key);
                                map.put(seatKey[i++], seat.getSeatName() + ":" + seat.getSeatNum());
                            }
//                        map.put("dayDifference",trains1.getDayDifference());
//                        map.put("durationTime",trains1.getDurationTime());
//                        map.put("startTrainDate",trains1.getStartTrainDate());
//                        map.put("seats",trains1.getSeats());
                            data.add(map);
                        }
                    }
                    queryAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    Toast.makeText(TicketQuery.this,"服务器错误，请重新登录！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
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

        ActionBar actionBar = getSupportActionBar();
//        actionBar.setLogo(R.mipmap.ic_launcher);
//        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

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
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        data = (List<Map<String, Object>>) bundle.getSerializable("searchData");

        data = new ArrayList<>();
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

                orderData = new ArrayList<>();//将data里面seat有关数据放进orderData里面

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
        new TicketTask().execute();
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
            new TicketTask().execute();
        }
    }

    //异步任务获取
    class TicketTask extends AsyncTask<String,String,Object>{

        @Override
        protected Object doInBackground(String[] strings) {
            String resultObject = "";
            Message message = handler.obtainMessage();
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String sessionid = sharedPreferences.getString("Cookie", "");
            Log.d(TAG, "sessionid： " + sessionid);
            OkHttpClient okHttpClient = new OkHttpClient();
            @SuppressLint("WrongThread") RequestBody requestBody = new FormBody.Builder()
                    .add("fromStationName", getIntent().getStringExtra("stationFrom"))
                    .add("toStationName", getIntent().getStringExtra("stationTo"))
                    .add("startTrainDate", tv_dateTitle.getText().toString().split(" ")[0])
                    .add("action", "query")
                    .build();
            Request request = new Request.Builder()
                    .url(Constant.Host + "/otn/TrainList")
                    .addHeader("Cookie", sessionid)
                    .post(requestBody)
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                Log.d(TAG, "response： " + response);
                String responseData = response.body().string();
                Log.d(TAG, "获取的服务器数据： " + responseData);
                if (response.isSuccessful()) {
                    Gson gson = new GsonBuilder().create();
                    Train[] trains = gson.fromJson(responseData, Train[].class);
                    message.what = 1;
                    message.obj = trains;
                } else {
                    message.what = 2;
                }
            } catch (IOException e) {
                e.printStackTrace();
                message.what = 2;
            }
            handler.sendMessage(message);
            return resultObject;
        }
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