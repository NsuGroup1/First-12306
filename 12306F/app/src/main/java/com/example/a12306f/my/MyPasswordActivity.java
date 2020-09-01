package com.example.a12306f.my;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a12306f.LoginActivity;
import com.example.a12306f.R;
import com.example.a12306f.a.Account;
import com.example.a12306f.utils.Constant;
import com.example.a12306f.utils.Md5Utils;
import com.example.a12306f.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MyPasswordActivity extends AppCompatActivity {
    private static final String TAG = "MyPasswordActivity";
    private String action = "";
    private ProgressDialog progressDialog;
    private Button btn_queding;
    private EditText et_new_password,et_new_password1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (progressDialog != null){
                progressDialog.dismiss();
            }
            switch (msg.what){
                case 1:
                    String result = msg.obj.toString();
                    if ("1".equals(result)){
                        Toast.makeText(MyPasswordActivity.this,"修改成功！",Toast.LENGTH_SHORT).show();
                        MyPasswordActivity.this.finish();

                    }else if ("-1".equals(result)){
                        Toast.makeText(MyPasswordActivity.this,"修改失败！",Toast.LENGTH_SHORT).show();
                        MyPasswordActivity.this.finish();
                    }
                    break;
                case 2:
                    Toast.makeText(MyPasswordActivity.this,"服务器错误，请重试！",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MyPasswordActivity.this,"请重新登录！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_password);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        et_new_password=findViewById(R.id.newpassword);
        et_new_password1=findViewById(R.id.newpassword1);

        btn_queding = findViewById(R.id.password_save);

        btn_queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.checkNet(MyPasswordActivity.this)){
                    Toast.makeText(MyPasswordActivity.this,"当前网络不可用",Toast.LENGTH_SHORT).show();
                    return;
                }
                String p = et_new_password.getText().toString();
                final String p1 = et_new_password1.getText().toString();
                if (!p.equals(p1)){
                    Toast.makeText(MyPasswordActivity.this, "密码不一致，请重新确认！", Toast.LENGTH_SHORT).show();
                    return;
                }else if ((p.equals("dong"))){
                    Toast.makeText(MyPasswordActivity.this, "新密码不能与旧密码一致！", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    progressDialog = ProgressDialog.show(
                            MyPasswordActivity.this,
                            null,
                            "正在加载中....",
                            false, true);

                    //修改密码
                    new Thread() {
                        @Override
                        public void run() {
                            Message message = handler.obtainMessage();

                            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                            String sessionid = sharedPreferences.getString("Cookie", "");

                            OkHttpClient client = new OkHttpClient();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("newPassword",p1)
                                    .add("action","update")
                                    .build();
                            Request request = new Request.Builder()
                                    .url(Constant.Host + "/otn/AccountPassword")
                                    .addHeader("Cookie", sessionid)
                                    .post(requestBody)
                                    .build();

                            try {
                                Response response = client.newCall(request).execute();
                                String responseData = response.body().string();
                                Log.d(TAG, "获取的服务器数据： " + responseData);
                                if (response.isSuccessful()) {
                                    Gson gson = new GsonBuilder().create();
                                    String resultString = gson.fromJson(responseData, String.class);
                                    Log.d(TAG, "accounts： " + resultString);
                                    message.what = 1;
                                    message.obj = resultString;
                                    message.what = 1;
                                }else {
                                    message.what=2;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                message.what=2;
                            }catch (JsonSyntaxException e){
                                message.what = 3;
                            }
                            handler.sendMessage(message);

                        }
                    }.start();
                }
            }
        });
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
