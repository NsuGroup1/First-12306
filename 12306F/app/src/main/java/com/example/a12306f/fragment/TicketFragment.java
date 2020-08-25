package com.example.a12306f.fragment;

import com.example.a12306f.LoginActivity;
import com.example.a12306f.a.Passenger;
import com.example.a12306f.a.Seat;
import com.example.a12306f.a.Train;
import com.example.a12306f.stationlist.Station;
import com.example.a12306f.utils.Constant;
import com.example.a12306f.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tbruyelle.rxpermissions2.RxPermissions;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a12306f.R;
import com.example.a12306f.stationlist.StationListActivity;
import com.example.a12306f.ticket.TicketQuery;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TicketFragment extends Fragment {

    private TextView tv_start_city,tv_arrive_city,tv_time;
    Button btn_query;
    private ImageView img_station_exchange;
    final private String TAG = "TicketFragment";
    private String stationFrom,stationTo;

    private List<Map<String,Object>> data;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  TicketFragment(){
        //需要空的构造方法
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_ticket_fragment,container,false);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Train[] trains = (Train[]) msg.obj;
                    for (Train trains1:trains){
                        Map<String,Object> map = new HashMap<>();
                        map.put("trainNo",trains1.getTrainNo());
                        if (trains1.getStartStationName().equals(trains1.getFromStationName())){
                            map.put("img1",R.drawable.flg_shi);
                        }else {
                            map.put("img1",R.drawable.flg_guo);
                        }
                        if (trains1.getEndStationName().equals(trains1.getToStationName())){
                            map.put("img2",R.drawable.flg_zhong);
                        }else {
                            map.put("img2",R.drawable.flg_guo);
                        }
//                        map.put("startStationName",trains1.getStartStationName());
//                        map.put("endStationName",trains1.getEndStationName());
//                        map.put("fromStationName",trains1.getFromStationName());
//                        map.put("toStationName",trains1.getToStationName());
                        map.put("startTime",trains1.getStartTime());
                        map.put("durationTime",trains1.getDurationTime());
                        map.put("arriveTime",trains1.getArriveTime()+"("+trains1.getDayDifference()+")");
                        String[] seatKey = {"seat1","seat2","seat3","seat4"};
                        int i = 0;
                        Map<String, Seat> seats = trains1.getSeats();
                        for (String key :seats.keySet()){
                            Seat seat = seats.get(key);
                            map.put(seatKey[i++],seat.getSeatName()+ ":" + seat.getSeatNum());
                        }
//                        map.put("dayDifference",trains1.getDayDifference());
//                        map.put("durationTime",trains1.getDurationTime());
//                        map.put("startTrainDate",trains1.getStartTrainDate());
//                        map.put("seats",trains1.getSeats());
                        data.add(map);
                    }
                    break;
                case 2:
                    Toast.makeText(getActivity(),"服务器错误，请重新登录！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_start_city = view.findViewById(R.id.tv_start_city);
        tv_arrive_city = view.findViewById(R.id.tv_arrive_city);
        tv_time = view.findViewById(R.id.tv_time);
        btn_query = view.findViewById(R.id.btn_query);
        img_station_exchange = view.findViewById(R.id.img_station_exchange);

        //联网查询
        if (!NetworkUtils.checkNet(getActivity())){
            Toast.makeText(getActivity(),"网络异常！",Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog progressDialog = ProgressDialog.show(
                getActivity(),
                null,
                "正在加载中...",
                false, true);
        new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = handler.obtainMessage();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                String sessionid = sharedPreferences.getString("Cookie", "");

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("fromStationName",tv_start_city.getText().toString())
                        .add("toStationName",tv_arrive_city.getText().toString())
                        .add("startTrainDate",tv_time.getText().toString())
                        .build();
                Request request = new Request.Builder()
                        .url(Constant.Host+"/otn/TrainList")
                        .post(requestBody)
                        .addHeader("Cookie", sessionid)
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d(TAG, "获取的服务器数据： " + responseData);
                    if (response.isSuccessful()){
                        Gson gson = new GsonBuilder().create();
                        Train[] trains =  gson.fromJson(responseData, Train[].class);
                        message.what = 1;
                        message.obj = trains;
                    }
                    else {
                        message.what = 2;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    message.what = 2;
                }
                handler.sendMessage(message);

            }
        }.start();

//出发城市
        tv_start_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), StationListActivity.class);
                startActivityForResult(intent,100);
            }
        });
// 到达城市
        tv_arrive_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),StationListActivity.class);
                startActivityForResult(intent,200);
            }
        });
        //
        //交换
        img_station_exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String leftT = tv_arrive_city.getText().toString();
                final String rightT = tv_start_city.getText().toString();
                TranslateAnimation anileft = new TranslateAnimation(0,500,0,0);
                anileft.setInterpolator(new LinearInterpolator());
                anileft.setDuration(500);
                anileft.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        tv_start_city.clearAnimation();
                        tv_start_city.setText(leftT);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                tv_start_city.startAnimation(anileft);
                //若XDelta>0，则说明控件向右侧发生移动
                TranslateAnimation aniright = new TranslateAnimation(0,-500,0,0);
                aniright.setInterpolator(new LinearInterpolator());
                aniright.setDuration(500);
                //三个方法分别是Animation开始的时候调用，完成的时候调用，重复的时候调用。
                aniright.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tv_arrive_city.clearAnimation();
                        tv_arrive_city.setText(rightT);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                tv_arrive_city.startAnimation(aniright);
            }
        });


        final Calendar oldCalendar = Calendar.getInstance();
        final int Year = oldCalendar.get(Calendar.YEAR);
        final int Month = oldCalendar.get(Calendar.MONTH);
        final int Day = oldCalendar.get(Calendar.DATE);
        String Week = DateUtils.formatDateTime(getActivity(),oldCalendar.getTimeInMillis(),DateUtils.FORMAT_SHOW_WEEKDAY);
        Log.d("getActivity.this", "Week: "+Week);

        tv_time.setText(Year+"-"+(Month+1)+"-"+Day+" "+Week);
        //TODO 日期选择对话框
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        Calendar newCalendar = Calendar.getInstance();
                        newCalendar.set(year,month,day);
                        String weekDay = DateUtils.formatDateTime(getActivity(),newCalendar.getTimeInMillis(),DateUtils.FORMAT_SHOW_WEEKDAY);
                        tv_time.setText(year+"-"+(month+1)+"-"+day+" "+weekDay);
                        Log.d("getActivity.this",weekDay);
                    }
                },Year,Month,Day).show();
            }
        });

        //查询按钮
        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("searchData", (Serializable) data);
                intent.putExtra("stationFrom",tv_start_city.getText().toString());
                intent.putExtra("stationTo",tv_arrive_city.getText().toString());
                intent.putExtra("startTicketDate",tv_time.getText().toString());
                intent.putExtras(bundle);
                intent.setClass(getActivity(), TicketQuery.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String stationName = data.getStringExtra("name");
        if(!TextUtils.isEmpty(stationName)){
            switch (requestCode){
                case 100:
                    tv_start_city.setText(stationName);
                    break;
                case 200:
                    tv_arrive_city.setText(stationName);
                    break;
            }
        }
    }
}