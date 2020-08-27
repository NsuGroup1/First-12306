package com.example.a12306f.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.a12306f.R;
import com.example.a12306f.a.Seat;
import com.example.a12306f.a.Train;
import com.example.a12306f.adapter.JAdapter;
import com.example.a12306f.stationlist.StationListActivity;
import com.example.a12306f.ticket.TicketQuery;
import com.example.a12306f.utils.History;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TicketFragment extends Fragment {

    private TextView tv_start_city,tv_arrive_city,tv_time;
    Button btn_query;
    private ImageView img_station_exchange;
    private ListView lv_history;
    final private String TAG = "TicketFragment";
    private History history;
    private List<Map<String,Object>> data;
    private ArrayList arrayList;
    private JAdapter Adapter;


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
        lv_history = view.findViewById(R.id.history);
        tv_start_city = view.findViewById(R.id.tv_start_city);
        tv_arrive_city = view.findViewById(R.id.tv_arrive_city);
        tv_time = view.findViewById(R.id.tv_time);
        btn_query = view.findViewById(R.id.btn_query);
        img_station_exchange = view.findViewById(R.id.img_station_exchange);


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
//历史记录
        history = new History(getActivity());
        arrayList = history.query();
        lv_history = view.findViewById(R.id.history);
        Adapter= new JAdapter(getActivity(),arrayList,R.layout.history_item);
        lv_history.setAdapter(Adapter);



         final Calendar oldCalendar = Calendar.getInstance();
        final int Year = oldCalendar.get(Calendar.YEAR);
        final int Month = oldCalendar.get(Calendar.MONTH);
        final int Day = oldCalendar.get(Calendar.DATE);
        String Week = DateUtils.formatDateTime(getActivity(),oldCalendar.getTimeInMillis(),DateUtils.FORMAT_SHOW_WEEKDAY);
        Log.d("getActivity.this", "Week: "+Week);

        tv_time.setText(Year +"-"+(Month +1)+"-"+ Day +" "+Week);

        //TODO 日期选择对话框
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//
//                        Calendar newCalendar = Calendar.getInstance();
//                        newCalendar.set(year,month,day);
//                        String weekDay = DateUtils.formatDateTime(getActivity(),newCalendar.getTimeInMillis(),DateUtils.FORMAT_SHOW_WEEKDAY);
//                        tv_time.setText(year+"-"+(month+1)+"-"+day+" "+weekDay);
//                        Log.d("getActivity.this",weekDay);
//                    }
//                },Year,Month,Day).show();
//                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener(){
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        Calendar newCalendar = Calendar.getInstance();
//                        newCalendar.set(year,month,dayOfMonth);
//                        String weekDay = DateUtils.formatDateTime(getActivity(),newCalendar.getTimeInMillis(),DateUtils.FORMAT_SHOW_WEEKDAY);
//                        tv_time.setText(year+"-"+(month+1)+"-"+dayOfMonth+" "+weekDay);
//                        Log.d("getActivity.this",weekDay);
//                    }
//                };
//                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),listener,
//                        Year,Month,Day){
//                    @Override
//                    public void onDateChanged(@NonNull DatePicker view, int year, int month, int dayOfMonth) {
//                        super.onDateChanged(view, year, month, dayOfMonth);
//                        if (year < Year)
//                            view.updateDate(Year, Month, Day);
//                        if (month < Month && year == Year)
//                            view.updateDate(Year, Month, Day);
//                        if (dayOfMonth < Day && year == Year && month == Month)
//                            view.updateDate(Year, Month, Day);
//
//                    }
//                };
//                datePickerDialog.show();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newCalender = Calendar.getInstance();
                        newCalender.set(year,month,dayOfMonth);
                        String weekDay = DateUtils.formatDateTime(getActivity(),newCalender.getTimeInMillis(),DateUtils.FORMAT_SHOW_WEEKDAY);
                        tv_time.setText(year+"-"+(month+1)+"-"+dayOfMonth+" "+weekDay);
                    }
                },Year,Month,Day);
                datePickerDialog.setTitle("请选择日期");
                datePickerDialog.getDatePicker().setMinDate(oldCalendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        //查询按钮
        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //历史记录
                String SW=tv_start_city.getText().toString();
                String EW=tv_arrive_city.getText().toString();
                history.insert(SW,EW);
                SharedPreferences sharedPreferences= getActivity().getSharedPreferences("user",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("SW",SW);
                editor.putString("EW",EW);
                editor.commit();

                arrayList = history.query();
                Adapter= new JAdapter(getActivity(),arrayList,R.layout.history_item);
                Adapter.notifyDataSetChanged();

//跳转查询
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