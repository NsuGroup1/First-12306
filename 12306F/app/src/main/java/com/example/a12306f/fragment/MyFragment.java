package com.example.a12306f.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a12306f.LoginActivity;
import com.example.a12306f.R;
import com.example.a12306f.a.Account;
import com.example.a12306f.my.MyAccountActivity;
import com.example.a12306f.my.MyContactActivity;
import com.example.a12306f.my.MyPasswordActivity;
import com.example.a12306f.utils.Constant;
import com.example.a12306f.utils.DialogClose;
import com.example.a12306f.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyFragment extends Fragment {
    private ListView listView_info_my;
    private List<String> list;
    private ArrayAdapter<String> arrayAdapter;

    private Button button_out_my;
    private static final String TAG = "MyFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_my_fragment,container,false);

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Toast.makeText(getActivity(),"成功退出！",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getActivity(),"退出失败！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Intent intent = new Intent();
        listView_info_my = getView().findViewById(R.id.lv_my);
        button_out_my = getView().findViewById(R.id.btn_logout);

        button_out_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出登录实现
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Message msg = handler.obtainMessage();
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                        String sessionid = sharedPreferences.getString("Cookie", "");
                        OkHttpClient okHttpClient = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(Constant.Host+"/otn/Logout ")
                                .addHeader("Cookie",sessionid)
                                .build();
                        try {
                            Response response = okHttpClient.newCall(request).execute();
                            if (response.isSuccessful()){
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);//返回登录界面，同时清空之前的栈并开启新栈（必须同时使用）
//                                getActivity().finish();//退出程序
                                msg.what = 1;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            msg.what = 2;
                        }
                        handler.sendMessage(msg);
                    }
                }.start();

            }
        });

        list = new ArrayList<>();
        list.add("我的联系人");
        list.add("我的账户");
        list.add("我的密码");
        arrayAdapter = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,list);
        listView_info_my.setAdapter(arrayAdapter);

        listView_info_my.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        intent.setClass(getActivity(), MyContactActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent.setClass(getActivity(), MyAccountActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        //连接服务器修改密码
                        final EditText edit = new EditText(getActivity());
                        new AlertDialog.Builder(getActivity())
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle("请输入原密码")
                                .setView(edit)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int witch) {
                                        if (!NetworkUtils.checkNet(getActivity())){
                                            Toast.makeText(getActivity(),"当前网络不可用",Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        final String OldPassword = edit.getText().toString();
                                        if(TextUtils.isEmpty(OldPassword)){
                                            DialogClose.setClosable(dialog,false);
                                            edit.setError("密码不能为空，请输入原密码");
                                            edit.requestFocus();
                                        }else {
                                            final Handler handler1 = new Handler(){
                                                @Override
                                                public void handleMessage(@NonNull Message msg) {
                                                    super.handleMessage(msg);
                                                    switch (msg.what){
                                                        case 1:
                                                            String account = (String) msg.obj;
//

                                                            if(account.equals("1")){
                                                                DialogClose.setClosable(dialog,true);
                                                                startActivity(new Intent(getActivity(), MyPasswordActivity.class));
                                                            }
                                                            if (account.equals("0")){
                                                                DialogClose.setClosable(dialog,false);
                                                                edit.setError("密码与原密码不一致，请重新输入原密码");
                                                                edit.requestFocus();
                                                            }
//                                                            DialogClose.setClosable(dialog,true);
//                                                            startActivity(new Intent(getActivity(), MyPasswordActivity.class));
                                                            break;
                                                        case 2:
//                                                            Toast.makeText(getActivity(),"网络错误!",Toast.LENGTH_SHORT).show();
                                                            DialogClose.setClosable(dialog,true);
                                                            edit.setError("密码与原密码不一致，请重新输入原密码");
                                                            edit.requestFocus();
                                                            break;
                                                        case 3:
                                                            Toast.makeText(getActivity(),"网络错误",Toast.LENGTH_SHORT).show();
                                                            break;
                                                    }
                                                }
                                            };
                                            new Thread(){
                                                @Override
                                                public void run() {
                                                    super.run();

                                                    Message message = handler1.obtainMessage();
                                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                                                    String sessionid = sharedPreferences.getString("Cookie", "");
                                                    Log.d(TAG, "sessionid： " + sessionid);

                                                    OkHttpClient okHttpClient = new OkHttpClient();
                                                    RequestBody requestBody = new FormBody.Builder()
                                                            .add("oldPassword",edit.getText().toString())
                                                            .add("action","query")
                                                            .build();
                                                    Request request = new Request.Builder()
                                                            .addHeader("Cookie", sessionid)
                                                            .url(Constant.Host +"/otn/AccountPassword")
                                                            .post(requestBody)
                                                            .build();
                                                    try {
                                                        Response response = okHttpClient.newCall(request).execute();
                                                        String responseData = response.body().string();
                                                        Log.d(TAG, "获取的服务器数据： " + responseData);
                                                        Log.d(TAG, "response.isSuccessful()" + response.isSuccessful());
                                                        if (response.isSuccessful()){
                                                            Gson gson = new GsonBuilder().create();
//                                                            Account account = gson.fromJson(responseData,Account.class);
                                                            String account = gson.fromJson(responseData,String.class);
                                                            Log.d(TAG, "account" + account);
                                                            message.what = 1;
                                                            message.obj = account;
                                                        }
                                                        else {
                                                            message.what = 2;
                                                        }
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                        message.what = 3;
                                                    }
                                                    handler1.sendMessage(message);
                                                }
                                            }.start();
                                        }
//                                        if(TextUtils.isEmpty(newTel)){
//                                            DialogClose.setClosable(dialog,false);
//                                            edit.setError("密码不能为空，请输入原密码");
//                                            edit.requestFocus();
//                                        }else if(requestBody.equals(newTel)){
//                                            DialogClose.setClosable(dialog,true);
//                                            startActivity(new Intent(getActivity(), MyPasswordActivity.class));
//                                        }else {
//                                            DialogClose.setClosable(dialog,false);
//                                            edit.setError("密码与原密码不一致，请重新输入原密码");
//                                            edit.requestFocus();
//                                        }
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        DialogClose.setClosable(dialog,true);
                                    }
                                })
                                .create()
                                .show();
                        break;
                }

            }
        });

    }
}