package com.example.chat.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.example.chat.Client;
import com.example.chat.fragment.OneFragment;
import com.example.chat.R;
import com.example.chat.fragment.ThreeFragment;
import com.example.chat.fragment.TwoFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private View mOneLin;
    private View mTwoLin;
    private View mThreeLin;
    private View mOneImg;
    private View mTwoImg;
    private View mThreeImg;

    private View mFrameLayout;
    private OneFragment mOneFragment;//三个碎片
    private TwoFragment mTwoFragment;
    private ThreeFragment mThreeFragment;
    private FragmentManager manager;
    private FragmentTransaction transaction;

    private ArrayList<String> data = new ArrayList<>();

    private List<String> friendIdList;

    private List<String> friendNameList;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//获取系统时间格式

    private String hostname = null;//账号

    public LocationReceiver locationReceiver = new LocationReceiver();
    //内部类，实现BroadcastReceiver
    private class LocationReceiver extends BroadcastReceiver {
        //必须要重载的方法，用来监听是否有广播发送
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            String toName = intent.getStringExtra("name");//发送者账号
            List<String> list = intent.getStringArrayListExtra("msg");
            if (intentAction.equals("location.reportsucc")) {//接收到消息开启通知
                Date date = new Date(System.currentTimeMillis());

                String currentDate = simpleDateFormat.format(date);//获取系统时间
                Intent intent1 = new Intent(MainActivity.this,ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("toName",toName);//发送者账号
                bundle.putString("name",friendNameList.get(friendIdList.indexOf(toName)));//发送者姓名
                bundle.putStringArrayList("list",(ArrayList<String>) list);
                bundle.putString("time",currentDate);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("one",bundle);
                PendingIntent pi = PendingIntent.getActivity(MainActivity.this,0,intent1,FLAG_UPDATE_CURRENT);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                Notification notification = new NotificationCompat.Builder(MainActivity.this)
                        .setContentTitle(friendNameList.get(friendIdList.indexOf(toName)))
                        .setContentText(Client.msg.get(Client.msg.size()-1))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentIntent(pi)
                        .build();
                notificationManager.notify(1, notification); // 通过通知管理器发送通知

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFrameLayout = findViewById(R.id.frame);
        mOneLin = findViewById(R.id.one_lin);
        mTwoLin = findViewById(R.id.two_lin);
        mThreeLin = findViewById(R.id.three_lin);
        mOneImg=findViewById(R.id.one_img);
        mTwoImg=findViewById(R.id.two_img);
        mThreeImg=findViewById(R.id.three_img);
        mOneLin.setOnClickListener(this);
        mTwoLin.setOnClickListener(this);
        mThreeLin.setOnClickListener(this);
        //获取FragmentManager对象
        manager = getSupportFragmentManager();
        //获取FragmentTransaction对象
        transaction = manager.beginTransaction();
        setSwPage(0);

        Intent intent = getIntent();
        hostname = intent.getStringExtra("name");//账号
        friendIdList = intent.getStringArrayListExtra("friendIdList");
        friendNameList = intent.getStringArrayListExtra("friendNameList");
        data = (ArrayList<String>) friendNameList;
        Bundle bundle = new Bundle();

        bundle.putString("name",hostname);
        bundle.putStringArrayList("friendIdList", (ArrayList<String>) friendIdList);
        bundle.putStringArrayList("friendNameList", (ArrayList<String>) friendNameList);
        Bundle bundle1 = new Bundle();
        bundle1.putString("name",hostname);
        bundle1.putStringArrayList("friendIdList", (ArrayList<String>) friendIdList);
        bundle1.putStringArrayList("friendNameList", (ArrayList<String>) friendNameList);
        mOneFragment.setArguments(bundle);//给碎片1发送数据
        mThreeFragment = new ThreeFragment();
        mThreeFragment.setArguments(bundle1);//给碎片3发送数据
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.one_lin:
                setSwPage(0);
                break;
            case R.id.two_lin:
                setSwPage(1);
                break;
            case R.id.three_lin:
                setSwPage(2);
                break;
        }
    }
    public void setSwPage(int i) {
        //获取FragmentManager对象
        manager = getSupportFragmentManager();
        //获取FragmentTransaction对象
        transaction = manager.beginTransaction();
        //先隐藏所有的Fragment
        hideFragments(transaction);
        switch (i) {
            case 0:
                reImgSelect();
                mOneImg.setSelected(true);
                if (mOneFragment == null) {
                    mOneFragment = new OneFragment();
                    transaction.replace(R.id.frame, mOneFragment);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    transaction.replace(R.id.frame, mOneFragment);
                    transaction.show(mOneFragment);
                }
                break;
            case 1:
                reImgSelect();
                mTwoImg.setSelected(true);
                if (mTwoFragment == null) {
                    mTwoFragment = new TwoFragment();
                    transaction.replace(R.id.frame, mTwoFragment);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    transaction.replace(R.id.frame, mTwoFragment);
                    transaction.show(mTwoFragment);
                }
                break;
            case 2:
                reImgSelect();
                mThreeImg.setSelected(true);
                if (mThreeFragment == null) {
                    mThreeFragment = new ThreeFragment();
                    transaction.replace(R.id.frame, mThreeFragment);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    transaction.replace(R.id.frame, mThreeFragment);
                    transaction.show(mThreeFragment);
                }
                break;
        }
        transaction.commit();
    }

    //将四个的Fragment隐藏
    private void hideFragments(FragmentTransaction transaction) {
        if (mOneFragment != null) {
            transaction.hide(mOneFragment);
        }
        if (mTwoFragment != null) {
            transaction.hide(mTwoFragment);
        }
        if (mThreeFragment != null) {
            transaction.hide(mThreeFragment);
        }
    }

    //初始化底部菜单选择状态
    private void reImgSelect(){
        mOneImg.setSelected(false);
        mTwoImg.setSelected(false);
        mThreeImg.setSelected(false);
    }

    @Override
    protected void onPostResume() {
        //创建广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction("location.reportsucc");
        registerReceiver(locationReceiver, filter);
        super.onPostResume();
    }

    @Override
    protected void onPause() {
//        unregisterReceiver(locationReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(locationReceiver);//关闭广播接收器
        super.onDestroy();
    }
}



//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//
//    private LinearLayout mOneLin, mTwoLin, mThreeLin;
//    private ImageView mOneImg,mTwoImg,mThreeImg;
//
//    private FrameLayout mFrameLayout;
//    private OneFragment mOneFragment;
//    private TwoFragment mTwoFragment;
//    private ThreeFragment mThreeFragment;
//    private FragmentManager manager;
//    private FragmentTransaction transaction;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mFrameLayout = findViewById(R.id.frame);
//        mOneLin = findViewById(R.id.one_lin);
//        mTwoLin = findViewById(R.id.two_lin);
//        mThreeLin = findViewById(R.id.three_lin);
//        mOneImg=findViewById(R.id.one_img);
//        mTwoImg=findViewById(R.id.two_img);
//        mThreeImg=findViewById(R.id.three_img);
//        mOneLin.setOnClickListener(this);
//        mTwoLin.setOnClickListener(this);
//        mThreeLin.setOnClickListener(this);
//        //获取FragmentManager对象
//        manager = getSupportFragmentManager();
//        //获取FragmentTransaction对象
//        transaction = manager.beginTransaction();
//        setSwPage(0);
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.one_lin:
//                setSwPage(0);
//                break;
//            case R.id.two_lin:
//                setSwPage(1);
//                break;
//            case R.id.three_lin:
//                setSwPage(2);
//                break;
//        }
//    }
//
//    public void setSwPage(int i) {
//        //获取FragmentManager对象
//        manager = getSupportFragmentManager();
//        //获取FragmentTransaction对象
//        transaction = manager.beginTransaction();
//        //先隐藏所有的Fragment
//        hideFragments(transaction);
//        switch (i) {
//            case 0:
//                reImgSelect();
//                mOneImg.setSelected(true);
//                if (mOneFragment == null) {
//                    mOneFragment = new OneFragment();
//                    transaction.add(R.id.frame, mOneFragment);
//                } else {
//                    //如果微信对应的Fragment已经实例化，则直接显示出来
//                    transaction.show(mOneFragment);
//                }
//                break;
//            case 1:
//                reImgSelect();
//                mTwoImg.setSelected(true);
//                if (mTwoFragment == null) {
//                    mTwoFragment = new TwoFragment();
//                    transaction.add(R.id.frame, mTwoFragment);
//                } else {
//                    //如果微信对应的Fragment已经实例化，则直接显示出来
//                    transaction.show(mTwoFragment);
//                }
//                break;
//            case 2:
//                reImgSelect();
//                mThreeImg.setSelected(true);
//                if (mThreeFragment == null) {
//                    mThreeFragment = new ThreeFragment();
//                    transaction.add(R.id.frame, mThreeFragment);
//                } else {
//                    //如果微信对应的Fragment已经实例化，则直接显示出来
//                    transaction.show(mThreeFragment);
//                }
//                break;
//        }
//        transaction.commit();
//    }
//
//    //将四个的Fragment隐藏
//    private void hideFragments(FragmentTransaction transaction) {
//        if (mOneFragment != null) {
//            transaction.hide(mOneFragment);
//        }
//        if (mTwoFragment != null) {
//            transaction.hide(mTwoFragment);
//        }
//        if (mThreeFragment != null) {
//            transaction.hide(mThreeFragment);
//        }
//    }
//
//    //初始化底部菜单选择状态
//    private void reImgSelect(){
//        mOneImg.setSelected(false);
//        mTwoImg.setSelected(false);
//        mThreeImg.setSelected(false);
//    }
//}
