package com.example.a12306f;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.a12306f.utils.Constant;
import com.example.a12306f.utils.Md5Utils;
import com.example.a12306f.utils.NetworkUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText etName, etPassword;
    private Button btnLogin;
    private CheckBox cbLogin;
    private TextView tvForgetPassword;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

//    // Storage Permissions
//    private static final int REQUEST_EXTERNAL_STORAGE = 1;
//    private static String[] PERMISSIONS_STORAGE = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE };
//
//    /**
//     * Checks if the app has permission to write to device storage
//     *
//     * If the app does not has permission then the user will be prompted to
//     * grant permissions
//     *
//     * @param activity
//     */
//    public static void verifyStoragePermissions(Activity activity) {
//        // Check if we have write permission
//        int permission = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE);
//        }
//    }

        @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            if (progressDialog != null){
                progressDialog.dismiss();
            }
            switch (message.what){
                case 1:
                    String sessionid = message.obj.toString();
                    int result = message.arg1;
                    if (result == 0){
                        etName.setError("用户名或密码错误");
                        etName.requestFocus();
                    }else if (1 == result){
                        sharedPreferences = getSharedPreferences("user",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        // 记录用户名或密码
                        if(cbLogin.isChecked()){
                            editor.putString("username",etName.getText().toString());
                            editor.putString("password",Md5Utils.MD5(etPassword.getText().toString()));
                        }
//                        else {
//
//                            //清空以前的登陆信息
//                            editor.remove("username");
//                            editor.remove("password");
//                        }
                        //保存
                        editor.putString("Cookie",sessionid);

                        //执行修改
                        editor.commit();

                        //跳转页面
                        Intent intent = new Intent(LoginActivity.this,ViewPagerActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                    break;
                case 2:
                    Toast.makeText(LoginActivity.this,"服务器错误，请重试！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //获得实例对象
//        sharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
//        button_login_lo = findViewById(R.id.button_login_lo);
//        textView_forget = findViewById(R.id.textView_forget_login);
//        editText_name = findViewById(R.id.editText_name_login);
//        editText_code = findViewById(R.id.editText_code_login);
//        checkBox_login = findViewById(R.id.checkBox_login);

        etName = findViewById(R.id.name);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.bt_login);
        cbLogin = findViewById(R.id.cb_remember);
        tvForgetPassword = findViewById(R.id.forget_pw);
        //设置超链接
        tvForgetPassword.setText(Html.fromHtml("<a href=\"https://kyfw.12306.cn/otn/forgetPassword/initforgetMyPassword\">忘记密码？</a>"));
        tvForgetPassword.setMovementMethod(LinkMovementMethod.getInstance());

//        //判断checkBox状态
//        if (sharedPreferences.getBoolean("ISCHECK", false)) {
//            cbLogin.setChecked(true);
//            etName.setText(sharedPreferences.getString("username", ""));
//            etPassword.setText(sharedPreferences.getString("password", ""));
//            Intent intent1 = new Intent(LoginActivity.this, ViewPagerActivity.class);
//            startActivity(intent1);
//        }
//        cbLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (cbLogin.isChecked()) {
//                    sharedPreferences.edit().putBoolean("ISCHECK", true).commit();
//                }
//            }
//        });

        //用户名，密码验证
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String account = etName.getText().toString();
//                String password = etPassword.getText().toString();
                if (TextUtils.isEmpty(etName.getText().toString())) {
                    etName.setError("请输入帐号");
                    etName.requestFocus();
                } else if (TextUtils.isEmpty(etPassword.getText().toString())) {
                    etPassword.setError("请输入密码");
                    etPassword.requestFocus();
                } else if (!etName.getText().toString().equals("dong")) {
                   Toast.makeText(LoginActivity.this,"帐号错误！",Toast.LENGTH_SHORT).show();
                    etName.requestFocus();
                    etName.setError("账号错误！");
                } else if (!etPassword.getText().toString().equals("dong")) {
                   Toast.makeText(LoginActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                    etPassword.requestFocus();
                    etPassword.setError("密码错误！");
                } else {
                        if (!NetworkUtils.checkNet(LoginActivity.this)){
                            Toast.makeText(LoginActivity.this,"网络异常！",Toast.LENGTH_SHORT).show();
                            return;//停止
                        }
                        // 进度对话框
                        progressDialog = ProgressDialog.show(
                                LoginActivity.this,
                                null,
                                "正在加载中...",
                                false,true);


                        new Thread(){
                            @Override
                            public void run() {
                                String result = "";
                                Message message = handler.obtainMessage();

                                OkHttpClient client = new OkHttpClient();
                                RequestBody requestBody = new FormBody.Builder()
                                        .add("username", etName.getText().toString())
                                        .add("password", Md5Utils.MD5(etPassword.getText().toString()))
                                        .build();
                                Request request = new Request.Builder()
                                        .url(Constant.Host+"/Login")
                                        .post(requestBody)
                                        .build();

                                try {
                                    Response response = client.newCall(request).execute();
                                    Log.d(TAG,"response:");
                                    String responsedata = response.body().string();
                                    Log.d(TAG,"获取服务器数据:"+responsedata);

                                    //解析成功接收到的数据
                                    if (response.isSuccessful()) {
                                        //pull解析
                                        //生成解析器
                                        XmlPullParser parser = Xml.newPullParser();
                                        //引入要解析的流
                                        parser.setInput(new StringReader(responsedata));
                                        //事件类型解析
                                        int type = parser.getEventType();
                                        while (type!=XmlPullParser.END_DOCUMENT){
                                            switch (type){
                                                case XmlPullParser.START_TAG:
                                                    if ("result".equals(parser.getName())){
                                                        result = parser.nextText();
                                                        Log.d(TAG,"result"+result);
                                                    }
                                                    break;
                                            }
                                            type = parser.next();
                                        }
                                        //读取sessionid
                                        Headers headers = response.headers();
                                        Log.d(TAG,"headers:"+headers);
                                        List<String> cookies = headers.values("Set-Cookie");
                                        Log.d(TAG,"Set-Cookie:"+cookies);
                                        String session = cookies.get(0);
                                        Log.d(TAG,"onResponse-size:"+session);
                                        String sessionid = session.substring(0,session.indexOf(";"));
                                        Log.d(TAG,"session is:"+sessionid);
                                        //发送消息
                                        message.what = 1;
                                        message.arg1 = Integer.parseInt(result);
                                        message.obj = sessionid;

                                    } else {
                                        message.what = 2;
                                        Log.d(TAG,"what2:");
                                    }
                                } catch (XmlPullParserException | IOException e) {
                                    e.printStackTrace();
                                    message.what = 2;
                                    Log.d(TAG,"what2:");
                                }
                                handler.sendMessage(message);
                            }
                        }.start();
                    }
                }
        });

    }
}