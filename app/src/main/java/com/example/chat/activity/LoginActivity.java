package com.example.chat.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chat.R;
import com.example.chat.service.ChatService;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    private CheckBox rememberPass;

    private ChatService.ChatBinder chatBinder;

    private Intent intent = null;

    private LocationReceiver locationReceiver = new LocationReceiver();
    private LocationReceiver locationReceiver2 = new LocationReceiver();
    //内部类，实现BroadcastReceiver
    private class LocationReceiver extends BroadcastReceiver {
        //必须要重载的方法，用来监听是否有广播发送
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            String account = intent.getStringExtra("name");
            List<String> friendIdList = intent.getStringArrayListExtra("friendIdList");
            List<String> friendNameList = intent.getStringArrayListExtra("friendNameList");
            if ("success".equals(intentAction)) {
                Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(LoginActivity.this,MainActivity.class);
                intent1.putExtra("name",account);
                intent1.putStringArrayListExtra("friendIdList", (ArrayList<String>) friendIdList);
                intent1.putStringArrayListExtra("friendNameList", (ArrayList<String>) friendNameList);
                startActivity(intent1);
            }
            if ("error".equals(intentAction)) {
                Toast.makeText(LoginActivity.this,"账号或密码错误",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            chatBinder = (ChatService.ChatBinder)iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LitePal.getDatabase();

        intent = new Intent(LoginActivity.this,ChatService.class);
        startService(intent);

        bindService(intent,connection,BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction("success");
        registerReceiver(locationReceiver, filter);

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("error");
        registerReceiver(locationReceiver2, filter1);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        accountEdit = (EditText)findViewById(R.id.account);
        passwordEdit = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);
        rememberPass = (CheckBox)findViewById(R.id.remember_pass);
        boolean isRemember = pref.getBoolean("remember_password",false);
        if(isRemember){
            String account = pref.getString("account","");
            String password = pref.getString("password","");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberPass.setChecked(true);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                //if(account.equals("88888")&&password.equals("123456")) {
                if(password.equals("123456")) {
                    editor = pref.edit();
                    if(rememberPass.isChecked()){
                        editor.putBoolean("remember_password",true);
                        editor.putString("account",account);
                        editor.putString("password",password);
                    }else {
                        editor.clear();
                    }
                    editor.apply();

                    chatBinder.starClient(account,password);//连接服务器


                }else{



                    //Toast.makeText(LoginActivity.this,"密码不正确",Toast.LENGTH_SHORT).show();

                    //Toast.makeText(LoginActivity.this,"用户名不存在",Toast.LENGTH_SHORT).show();
                    }


                }


        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(locationReceiver);
        unregisterReceiver(locationReceiver2);
        unbindService(connection);
        chatBinder.closeClient();
        stopService(intent);
    }
}
