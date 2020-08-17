package com.example.a12306f.my;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.a12306f.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ACA03AddActivity extends AppCompatActivity {

    private Button button_ACA03;
    private ListView listView_ACA03;
    private List<Map<String,Object>> data_ACA;

    private String[] navigation_ACA = {"姓名","证件类型","证件号码","乘客类型","电话"};
    private String[] content_ACA;
    private int[] image_ACA = {R.drawable.forward_25,0,0,R.drawable.forward_25,R.drawable.forward_25};
    private TextView textView_content_ACA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_c_a03_add);

        button_ACA03 = findViewById(R.id.button_ACA03);
        listView_ACA03 = findViewById(R.id.listview_ACA03);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String name_ACA = bundle.getString("name_ACA");
        String ID = "身份证";
        String ID_ACA = bundle.getString("ID_ACA");
        String People = "成人";
        final String phone_ACA = bundle.getString("phone_ACA");
        content_ACA = new String[]{name_ACA,ID,ID_ACA,People,phone_ACA};
        data_ACA = new ArrayList<>();
        HashMap<String,Object> map;
        for (int i = 0 ; i < navigation_ACA.length; i++){
            map = new HashMap<>();
            map.put("navigation",navigation_ACA[i]);
            map.put("content",content_ACA[i]);
            map.put("image",image_ACA[i]);
            data_ACA.add(map);
        }

        final SimpleAdapter simpleAdapter_ACA = new SimpleAdapter(ACA03AddActivity.this
                ,data_ACA,R.layout.item_add_my_contact,
                new String[]{"navigation","content","image"}
                ,new int[]{R.id.textView_navigation_addmycantacts,R.id.textView_content_addmycontacts
                ,R.id.imageView_addmycontacts});
        listView_ACA03.setAdapter(simpleAdapter_ACA);

        listView_ACA03.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                switch (position) {
                    case 0:
                        final View name_dialog = getLayoutInflater().inflate(R.layout.dialog_amc, null);
                        new AlertDialog.Builder(ACA03AddActivity.this)
                                .setTitle("请输入姓名")
                                .setView(name_dialog)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        EditText editText_dialog_amc = findViewById(R.id.editText_dialog_AMC);
//                                        editText_dialog_amc.setText(textView_content_addmycontacts.getText().toString());

                                        EditText editText_dialog_amc = name_dialog.findViewById(R.id.editText_dialog_AMC);
                                        editText_dialog_amc.setText(name_ACA);

                                        Map map1 = data_ACA.get(position);
                                        map1.put("name", name_ACA);
                                        data_ACA.set(position, (HashMap<String, Object>) map1);
                                        simpleAdapter_ACA.notifyDataSetChanged();
                                    }
                                }).setNegativeButton("取消", null).create().show();
                        break;
                    case 3:
                        Dialog builder = new androidx.appcompat.app.AlertDialog.Builder(ACA03AddActivity.this)
                                .setTitle("请选择乘客类型")
                                .setSingleChoiceItems(new String[]{"成人", "儿童", "学生", "其他"}, 0,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                simpleAdapter_ACA.notifyDataSetChanged();
                                                dialog.dismiss();
                                            }
                                        })
                                .setNegativeButton("取消", null)
                                .show();
                        break;
                    case 4:
                        View phone_dialog = getLayoutInflater().inflate(R.layout.dialog_amc4, null);
                        new androidx.appcompat.app.AlertDialog.Builder(ACA03AddActivity.this)
                                .setTitle("请输入电话号码")
                                .setView(phone_dialog)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        EditText editText_phone_dialog = findViewById(R.id.edTextPhone_dialog_amc);
                                        editText_phone_dialog.setText(data_ACA.get(position).get("content").toString());
                                        Map map2 = data_ACA.get(position);
                                        map2.put("content", editText_phone_dialog.getText().toString());
                                        data_ACA.set(position, (HashMap<String, Object>) map2);
                                        simpleAdapter_ACA.notifyDataSetChanged();
                                    }
                                }).setNegativeButton("取消", null).create().show();
                        break;
                }
            }
        });
    }
}