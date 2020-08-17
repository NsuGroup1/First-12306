package com.example.a12306f;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a12306f.adapter.MyFragmentPagerAdapter;
import com.example.a12306f.fragment.MyFragment;
import com.example.a12306f.fragment.OrderFragment;
import com.example.a12306f.fragment.TicketFragment;

import java.util.ArrayList;

public class ViewPagerActivity extends FragmentActivity {

    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;
    private ImageView image;
    private TextView view1, view2, view3;
    private int currIndex;//当前页卡编号
    private int bmpW;//横线图片宽度
    private int offset;//图片移动偏移量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        InitTextView();
        InitImage();
        InitViewPager();
    }

    //初始化
    private void InitViewPager() {
        viewPager = findViewById(R.id.viewpager);
        //数据源
        fragmentList = new ArrayList<Fragment>();
        TicketFragment ticketFragment = new TicketFragment();
        OrderFragment orderFragment = new OrderFragment();
        MyFragment myFragment = new MyFragment();
        fragmentList.add(ticketFragment);
        fragmentList.add(orderFragment);
        fragmentList.add(myFragment);

        //给ViewPager设置适配器
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentList));
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new MyOnPageChageListener());
    }

    /**
     * 初始化图片唯一像素
     */
    private void InitImage() {
        image = findViewById(R.id.imageView_vp);
        bmpW = BitmapFactory.decodeResource(getResources(),R.drawable.cursor).getWidth();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenW = displayMetrics.widthPixels;
        offset = (screenW/3-bmpW)/2;

        //imageview设置平移，使下划线平移到初始位置
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset,0);
        image.setImageMatrix(matrix);
    }

    private void InitTextView() {
        view1 = findViewById(R.id.textView_vp_guid1);
        view2 = findViewById(R.id.textView_vp_guid2);
        view3 = findViewById(R.id.textView_vp_guid3);

        view1.setOnClickListener(new txListener(0));
        view2.setOnClickListener(new txListener(1));
        view3.setOnClickListener(new txListener(2));

    }

    /**
     * 接收Text View对应的编号，通知View Pager切换Fragment
     */
    private class txListener implements View.OnClickListener {
        private int index = 0;
        public txListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }


    private class MyOnPageChageListener implements ViewPager.OnPageChangeListener {
        private int one = offset*2+bmpW;//两个相邻页面的偏移量
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //页卡切换时的动画效果，ViewAnimation
            Animation animation = new TranslateAnimation(currIndex*one,position*one,0,0);//平移动画
            currIndex = position;
            animation.setFillAfter(true);//动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);//动画持续时间0.2秒
            image.startAnimation(animation);//是用ImageView来显示动画的
            int i = currIndex + 1;
            Toast.makeText(ViewPagerActivity.this,"您选择了第"+i+"个页卡", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}