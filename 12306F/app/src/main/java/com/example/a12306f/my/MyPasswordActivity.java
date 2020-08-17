package com.example.a12306f.my;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a12306f.R;


public class MyPasswordActivity extends AppCompatActivity {

    private Button btn_queding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_password);

        btn_queding = findViewById(R.id.bt_save);
        btn_queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
