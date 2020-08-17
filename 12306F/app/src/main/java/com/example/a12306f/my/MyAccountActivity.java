package com.example.a12306f.my;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a12306f.R;
import com.example.a12306f.utils.DialogClose;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAccountActivity extends AppCompatActivity {
    private ListView myAccountlist;
    private Button btn_myAccountSave;
    private SimpleAdapter simpleAdapter;
    private List<Map<String,Object>> data;
    private ProgressDialog progressDialog;
    private String action = "";


//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            if (progressDialog != null){
//                progressDialog.dismiss();
//            }
//            switch (msg.what){
//                case 1:
//                    data.clear();
//                    Account account = (Account) msg.obj;
//                    Map<String,Object> map1 = new HashMap<>();
//                    map1.put("key","用户名");
//                    map1.put("value",account.getUsername());
//                    map1.put("img",R.drawable.flg_null);
//                    data.add(map1);
//                    Map<String,Object> map2 = new HashMap<>();
//                    map2.put("key","姓名");
//                    map2.put("value",account.getName());
//                    map2.put("img",R.drawable.flg_null);
//                    data.add(map2);
//                    Map<String,Object> map3 = new HashMap<>();
//                    map3.put("key","证件类型");
//                    map3.put("value",account.getIdType());
//                    map3.put("img",R.drawable.flg_null);
//                    data.add(map3);
//                    Map<String,Object> map4 = new HashMap<>();
//                    map4.put("key","证件号码");
//                    map4.put("value",account.getId());
//                    map4.put("img",R.drawable.flg_null);
//                    data.add(map4);
//                    Map<String,Object> map5 = new HashMap<>();
//                    map5.put("key","乘客类型");
//                    map5.put("value",account.getType());
//                    map5.put("img",R.drawable.forward_25);
//                    data.add(map5);
//                    Map<String,Object> map6 = new HashMap<>();
//                    map6.put("key","电话");
//                    map6.put("value",account.getTel());
//                    map6.put("img",R.drawable.forward_25);
//                    data.add(map6);
//                    simpleAdapter.notifyDataSetChanged();
//                    break;
//                case 2:
//                    Toast.makeText(MyAccountActivity.this,"服务器错误，请重试！",Toast.LENGTH_SHORT).show();
//                    break;
//                case 3:
//                    Toast.makeText(MyAccountActivity.this,"请重新登录！",Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    };
    private Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null){
                progressDialog.dismiss();
            }
            switch (msg.what){
                case 1:
                    String result = msg.obj.toString();
                    Log.d("result",result);
                    if (result != null){
                        Toast.makeText(MyAccountActivity.this,"修改成功！",Toast.LENGTH_SHORT).show();
                        MyAccountActivity.this.finish();
                    }else {
                        Toast.makeText(MyAccountActivity.this,"修改失败！",Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        myAccountlist = findViewById(R.id.lv_account);
        btn_myAccountSave = findViewById(R.id.my_account_save);

        data = new ArrayList<Map<String, Object>>();
        String[] key1 = {"用户名","姓名","证件类型","证件号码","乘客类型","电话"};
        String[] key2 = {"dong","冬不拉","身份证","11010119910511947X","成人","13812345678"};
        Integer[] key3 = {R.drawable.flg_null,R.drawable.flg_null,R.drawable.flg_null,R.drawable.flg_null,R.drawable.forward_25,R.drawable.forward_25};
        for (int i=0;i<key1.length;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("key",key1[i]);
            map.put("value",key2[i]);
            map.put("img",key3[i]);
            data.add(map);
        }

        simpleAdapter = new SimpleAdapter(this,
                data,
                R.layout.item_my_contact_edit,
                new String[]{"key","value","img"},
                new int[]{R.id.tv_my_contact_edit_key,R.id.tv_my_contact_edit_value,R.id.img_my_contact_edit_flag});

        myAccountlist.setAdapter(simpleAdapter);

        myAccountlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                switch (position){
                    case 4:
                        final String[] data1 = {"成人", "学生", "儿童", "其他"};
                        new AlertDialog.Builder(MyAccountActivity.this)
                                .setTitle("请选择乘客类型")
                                .setSingleChoiceItems(data1, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int is) {
                                        String type = data1[is];
                                        data.get(position).put("value",type);
                                        simpleAdapter.notifyDataSetChanged();
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .create()
                                .show();
                        break;
                    case 5:
                        final EditText editTel = new EditText(MyAccountActivity.this);
                        editTel.setText((String) data.get(position).get("value"));
                        new AlertDialog.Builder(MyAccountActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle("请输入电话号码")
                                .setView(editTel)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int witch) {
                                        String newTel = editTel.getText().toString();
                                        if(TextUtils.isEmpty(newTel)){
                                            DialogClose.setClosable(dialog,false);
                                            editTel.setError("请输入电话号码");
                                            editTel.requestFocus();
                                        }else{
                                            DialogClose.setClosable(dialog,true);
                                            data.get(position).put("value",newTel);
                                            simpleAdapter.notifyDataSetChanged();
                                        }
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
