package com.example.a12306f.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a12306f.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyContactActivity extends AppCompatActivity {
    private ListView lv_contact;
    private List<Map<String, Object>> data;
//    private ActionBar actionBar = this.getActionBar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);
        //actionbar
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setLogo(R.mipmap.ic_launcher);
//        actionBar.setDisplayUseLogoEnabled(true);

        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        //显示联系人
        lv_contact = findViewById(R.id.lv_contact);

        data = new ArrayList<Map<String, Object>>();
        // row 1
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("name", "冬不拉");
        row.put("age", "(成人)");
        row.put("idCard", "110110199009091111");
        row.put("tel", "12345678910");
        data.add(row);

        Map<String, Object> row2 = new HashMap<String, Object>();
        row2.put("name", "陈文飞");
        row2.put("age", "(学生)");
        row2.put("idCard", "110110199009091112");
        row2.put("tel", "12345678911");
        data.add(row2);

        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item_contact,
                new String[]{"name", "age", "idCard", "tel"}, new int[]{R.id.contact_name, R.id.contact_age,
                R.id.id_card_number, R.id.phone_number});
        // 绑定
        lv_contact.setAdapter(adapter);

        //编辑内容
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putString("name", "xxxxxx");
//                intent.putExtra("name", "");
                intent.putExtra("row", (Serializable) data.get(position));
                intent.setClass(MyContactActivity.this, MyContactEdit.class);

                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_contact_add,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.my_contact_add:
                Intent intent = new Intent();
                intent.setClass(MyContactActivity.this,MyContactAdd.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.my_contact_add,menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //返回菜单的处理
    public boolean MenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }return super.onMenuItemSelected(featureId, item);
    }
}
