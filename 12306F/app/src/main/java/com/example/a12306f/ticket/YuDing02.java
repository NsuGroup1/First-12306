package com.example.a12306f.ticket;

import androidx.annotation.NonNull;
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
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a12306f.R;
import com.example.a12306f.a.Seat;
import com.example.a12306f.a.Train;
import com.example.a12306f.utils.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
//import com.example.a12306f.adapter.SeatAdapter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class YuDing02 extends AppCompatActivity {

    final String TAG = "YuDing02.this";
    private TextView beforeDay,afterDay,DateTitle,StationTitle;
    private ListView yuding02_listview;
    private TextView LieCheHao,FromToTime,LiShi;
    private SeatAdapter seatAdapter;
    private List<Map<String,Object>> list02;
    private Train train;
    private ProgressDialog progressDialog;
//    private Train train;
//    private Seat seat;
    private int day;
//    private String[] leixing = {"商务座","硬座","一等座","软座","无座"};
//    private String[] nums={"39张","38张","17张","48张","50张"};
//    private String[] price={"￥154.00","￥152.00","￥269.00","￥215.00","￥250.00"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yu_ding02);

        beforeDay = findViewById(R.id.tv_previousDay2);
        afterDay = findViewById(R.id.tv_afterDay2);
        DateTitle = findViewById(R.id.TicketDateTitle2);
        StationTitle = findViewById(R.id.TicketStationTitle2);
        yuding02_listview = findViewById(R.id.yuding02_listview);
         LieCheHao= findViewById(R.id.LieCheHao);
        FromToTime = findViewById(R.id.FromToTime2);
        LiShi = findViewById(R.id.LiShi);

        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String date02 = bundle.getString("date");

        String route02 = bundle.getString("route");
        String lieche02 = bundle.getString("lieche");
        String fachetime02 = bundle.getString("timeout");
        String arrivaltime02 = bundle.getString("timein");
        String num0102 = bundle.getString("num01");
//判断时间差
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date now = null;
        try {
            now = df.parse(fachetime02);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date= null;
        try {
            date = df.parse(arrivaltime02);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l=date.getTime()-now.getTime();
        long day=l/(24*60*60*1000);
        long hour=(l/(60*60*1000)-day*24);
        long min=((l/(60*1000))-day*24*60-hour*60);


        DateTitle.setText(date02);
        FromToTime.setText(fachetime02+"-"+arrivaltime02);
        StationTitle.setText(route02);
        LieCheHao.setText(lieche02+"");
        LiShi.setText(""+day+"天"+hour+"小时"+min+"分");
//        day = getIntent().getIntExtra("day",0);

        beforeDay.setOnClickListener(new MyTicketListener());
        afterDay.setOnClickListener(new MyTicketListener());


//        list02 = getList02();
        list02 = new ArrayList<>();
        seatAdapter = new SeatAdapter();
        yuding02_listview.setAdapter(seatAdapter);
        new TicketTask1().execute();
    }
    private class MyTicketListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Calendar calendar = Calendar.getInstance();
            String DateFrom = DateTitle.getText().toString();
            int Year = Integer.parseInt(DateFrom.split("-")[0]);
            int Month = Integer.parseInt(DateFrom.split("-")[1])-1;
            int Day = Integer.parseInt(DateFrom.split("-")[2].split(" ")[0]);
            calendar.set(Year,Month,Day);
            switch (view.getId()){
                case R.id.tv_previousDay2:
                    calendar.add(Calendar.DAY_OF_MONTH,-1);
                    break;
                case R.id.tv_afterDay2:
                    calendar.add(Calendar.DAY_OF_MONTH,1);
                    break;
            }
            //更新TextView
            String weekDay = DateUtils.formatDateTime(YuDing02.this,calendar.getTimeInMillis(),DateUtils.FORMAT_SHOW_WEEKDAY);
            DateTitle.setText(calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)+" "+weekDay);
            new TicketTask1().execute();
        }
    }
    //异步任务获取
    class TicketTask1 extends AsyncTask<String,String,Object> {

        @Override
        protected Object doInBackground(String[] strings) {
            String resultObject = "";
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String sessionid = sharedPreferences.getString("Cookie", "");
            Log.d(TAG, "sessionid： " + sessionid);
            OkHttpClient okHttpClient = new OkHttpClient();
             @SuppressLint("WrongThread") RequestBody requestBody = new FormBody.Builder()
                    .add("fromStationName", StationTitle.getText().toString().split("-")[0])
                    .add("toStationName", StationTitle.getText().toString().split("-")[1])
                    .add("startTrainDate", DateTitle.getText().toString().split(" ")[0])
                    .add("trainNo",LieCheHao.getText().toString())
                    .add("action", "query")
                    .build();
            Request request = new Request.Builder()
                    .url(Constant.Host + "/otn/Train")
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
                    Train trains = gson.fromJson(responseData, Train.class);
                    return trains;
                } else {
                    resultObject = "2";
                }
            } catch (IOException e) {
                e.printStackTrace();
                resultObject = "2";
            }
            return resultObject;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            list02.clear();
            if (o instanceof Train){
                train = (Train) o;
                Map<String,Seat> seats = train.getSeats();
                for (String key:seats.keySet()){
                    Map<String,Object> map = new HashMap<>();
                    Seat seat = seats.get(key);
                    Log.d("seat", String.valueOf(seat));
                    map.put("ticketType",seat.getSeatName());
                    map.put("ticketNum",seat.getSeatNum());
                    map.put("ticketPrice",seat.getSeatPrice());
                    list02.add(map);
                }
                seatAdapter.notifyDataSetChanged();
            }else if (o instanceof String){

            }
        }
    }
//    private List<Map<String,Object>> getList02(){
//        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
//        for (int i = 0 ;i < leixing.length ; i++){
//            Map<String, Object> map = new HashMap<String,Object>();
//            map.put("leixing",leixing[i]);
//            map.put("nums",nums[i]);
//            map.put("price",price[i]);
//            list.add(map);
//        }
//        return list;
//    }

    //
    class SeatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list02.size();
        }

        @Override
        public Object getItem(int i) {
            return list02.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (holder == null){
                holder = new ViewHolder();
                view = LayoutInflater.from(YuDing02.this).inflate((R.layout.item_yuding02), null);
                holder.tvSeatName =  view.findViewById(R.id.textView_leixing02);
                holder.tvSeatNum = view.findViewById(R.id.textView_num0201);
                holder.tvSeatPrice = view.findViewById(R.id.textView_price02);
                holder.btn_order = view.findViewById(R.id.button_yuding);
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }
            holder.tvSeatName.setText(list02.get(i).get("ticketType").toString());
            holder.tvSeatNum.setText(list02.get(i).get("ticketNum").toString());
            holder.tvSeatPrice.setText(list02.get(i).get("ticketPrice").toString());
            final ViewHolder finalHolder = holder;
            holder.btn_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(YuDing02.this, YuDing03.class);
                    intent.putExtra("StationName",StationTitle.getText().toString());
                    intent.putExtra("LieCheHao",LieCheHao.getText().toString());
                    intent.putExtra("TicketDate",DateTitle.getText().toString());
                    intent.putExtra("day",day);
                    intent.putExtra("FromToTime",FromToTime.getText().toString());
                    intent.putExtra("Seat", finalHolder.tvSeatName.getText().toString()+"("+finalHolder.tvSeatNum.getText().toString()+")");
                    intent.putExtra("SeatPrice",finalHolder.tvSeatPrice.getText().toString());
                    startActivity(intent);
                }
            });
            return view;
        }
    }
    class ViewHolder{
        TextView tvSeatName;
        TextView tvSeatNum;
        TextView tvSeatPrice;
        Button btn_order;
    }
}