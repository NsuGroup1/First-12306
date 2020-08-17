package com.example.a12306f;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去除标题行
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // AppCompatActivity默认会有ActionBar，所以此处要隐藏ActionBar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        //自动登录
        SharedPreferences settings = getSharedPreferences("user", 0);
        String username = settings.getString("username", "");
        String password = settings.getString("password", "");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}