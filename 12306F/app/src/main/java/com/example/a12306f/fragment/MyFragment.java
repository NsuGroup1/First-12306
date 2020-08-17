package com.example.a12306f.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.a12306f.LoginActivity;
import com.example.a12306f.R;
import com.example.a12306f.my.MyAccountActivity;
import com.example.a12306f.my.MyContactActivity;
import com.example.a12306f.my.MyPasswordActivity;
import com.example.a12306f.utils.DialogClose;

import java.util.ArrayList;
import java.util.List;

public class MyFragment extends Fragment {
    private ListView listView_info_my;
    private List<String> list;
    private ArrayAdapter<String> arrayAdapter;

    private Button button_out_my;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_my_fragment,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Intent intent = new Intent();
        listView_info_my = getView().findViewById(R.id.lv_my);
        button_out_my = getView().findViewById(R.id.btn_logout);

        button_out_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
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
                        final EditText edit = new EditText(getActivity());
                        new AlertDialog.Builder(getActivity())
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle("请输入原密码")
                                .setView(edit)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int witch) {
                                        String newTel = edit.getText().toString();
                                        if(TextUtils.isEmpty(newTel)){
                                            DialogClose.setClosable(dialog,false);
                                            edit.setError("密码不能为空，请输入原密码");
                                            edit.requestFocus();
                                        }else if("dong".equals(newTel)){
                                            DialogClose.setClosable(dialog,true);
                                            startActivity(new Intent(getActivity(), MyPasswordActivity.class));
                                        }else {
                                            DialogClose.setClosable(dialog,false);
                                            edit.setError("密码与原密码不一致，请重新输入原密码");
                                            edit.requestFocus();
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