package com.example.a12306f;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etName, etPassword;
    private Button btnLogin;
    private CheckBox cbLogin;
    private TextView tvForgetPassword;
    private SharedPreferences sharedPreferences;

    //    Button button_login_lo;
//    TextView textView_forget;
//    EditText editText_name,editText_code;
//    CheckBox checkBox_login;
    String username, usercode;
//    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //获得实例对象
        sharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
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
        tvForgetPassword.setText(Html.fromHtml("<a href=\"http://www.163.com\">忘记密码？</a>"));
        tvForgetPassword.setMovementMethod(LinkMovementMethod.getInstance());

        //判断checkBox状态
        if (sharedPreferences.getBoolean("ISCHECK", false)) {
            cbLogin.setChecked(true);
            etName.setText(sharedPreferences.getString("username", ""));
            etPassword.setText(sharedPreferences.getString("usercode", ""));
            Intent intent1 = new Intent(LoginActivity.this, ViewPagerActivity.class);
            startActivity(intent1);
        }
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
                String account = etName.getText().toString();
                String password = etPassword.getText().toString();
                if (TextUtils.isEmpty(etName.getText().toString())) {
                    etName.setError("请输入帐号");
                    etName.requestFocus();
                } else if (TextUtils.isEmpty(etPassword.getText().toString())) {
                    etPassword.setError("请输入密码");
                    etPassword.requestFocus();
                } else if (!etName.getText().toString().equals("dong")) {
//                   Toast.makeText(LoginActivity.this,"帐号错误！",Toast.LENGTH_SHORT).show();
                    etName.requestFocus();
                    etName.setError("账号错误！");
                } else if (!etPassword.getText().toString().equals("dong")) {
//                   Toast.makeText(LoginActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                    etPassword.requestFocus();
                    etPassword.setError("密码错误！");
                } else {
                    if (cbLogin.isChecked()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user", account);
                        editor.putString("password", password);
                        editor.commit();
                    }
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, ViewPagerActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}