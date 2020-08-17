package com.example.a12306f.my;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a12306f.R;
import com.example.a12306f.utils.DialogClose;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyContactEdit extends AppCompatActivity {

    private ListView myContactEdit;
    private Button btn_Save;
    private SimpleAdapter adapter;
    private List<Map<String,Object>> data;
    private ProgressDialog progressDialog;

    String[] k1 = {"姓名","证件类型","证件号码","乘客类型","手机号"};
    String[] k2 = {""};
    Integer[] k3 = {R.drawable.forward_25};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact_edit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        myContactEdit = findViewById(R.id.lv_mycontact_edit_list);
        btn_Save = findViewById(R.id.contact_edit_save);

        //TODO 获取上个页面传来的数据
        Intent intent = getIntent();
        Map<String,Object> contact = (HashMap<String, Object>) getIntent().getSerializableExtra("row");

        data = new ArrayList<Map<String, Object>>();

        Map<String,Object> map1 = new HashMap<>();
        String name = (String) contact.get("name");
        map1.put("k1","姓名");
        //以左括号进行分割，取第一段
        map1.put("k2",name.split("\\(")[0]);
        map1.put("k3",R.drawable.forward_25);
        data.add(map1);

        Map<String,Object> map2 = new HashMap<>();
        String idType = "身份证";
        map2.put("k1","证件类型");
        //以冒号进行分割，取第一段
        map2.put("k2",idType.split("\\:")[0]);
        map2.put("k3",R.drawable.flg_null);
        data.add(map2);

        Map<String,Object> map3 = new HashMap<>();
        String idCard = (String) contact.get("idCard");
        map3.put("k1","证件号码");
        //以冒号进行分割，取第一段
        map3.put("k2",idCard);
        map3.put("k3",R.drawable.flg_null);
        data.add(map3);

        Map<String,Object> map4 = new HashMap<>();
        String age = (String) contact.get("name");
        map4.put("k1","乘客类型");
        map4.put("k2",age.split("\\(")[1].split("\\)")[0]);
        map4.put("k3",R.drawable.forward_25);
        data.add(map4);

        Map<String,Object> map5 = new HashMap<>();
        String tel = (String) contact.get("tel");
        map5.put("k1","手机号");
        map5.put("k2",tel);
        map5.put("k3",R.drawable.forward_25);
        data.add(map5);

        adapter = new SimpleAdapter(
                this,
                data,
                R.layout.item_my_contact_edit,
                new String[]{"k1","k2","k3"},
                new int[]{R.id.tv_my_contact_edit_key,R.id.tv_my_contact_edit_value,R.id.img_my_contact_edit_flag});

        myContactEdit.setAdapter(adapter);

        myContactEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                switch(position){
                    case 0:
                        final EditText editName = new EditText(MyContactEdit.this);
                        editName.setText((String) data.get(position).get("k2"));
                        new AlertDialog.Builder(MyContactEdit.this)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle("请输入姓名")
                                .setView(editName)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int witch) {
                                        String newName = editName.getText().toString();
                                        if(TextUtils.isEmpty(newName)){
                                            DialogClose.setClosable(dialog,false);
                                            editName.setError("请输入姓名");
                                            editName.requestFocus();
                                        }else{
                                            DialogClose.setClosable(dialog,true);
                                            data.get(position).put("k2",newName);
                                            adapter.notifyDataSetChanged();
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
                    case 3:
                        final String[] data1 = {"成人", "学生", "儿童", "其他"};
                        new AlertDialog.Builder(MyContactEdit.this)
                                .setTitle("请选择乘客类型")
                                .setSingleChoiceItems(data1, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int is) {
                                        String type = data1[is];
                                        data.get(position).put("k2",type);
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .create()
                                .show();
                        break;
                    case 4:
                        final EditText editTel = new EditText(MyContactEdit.this);
                        editTel.setText((String) data.get(position).get("k2"));
                        new AlertDialog.Builder(MyContactEdit.this)
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
                                            data.get(position).put("k2",newTel);
                                            adapter.notifyDataSetChanged();
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

        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyContactEdit.this, MyContactActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_contact_edit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()){
            case R.id.my_contact_delete:
                startActivity(new Intent(MyContactEdit.this,MyContactActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}