package com.example.homework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StreamTokenizer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private DBOpenHelper mDBOpenHelper;
    private TextView mTvLoginactivityRegister;
    private RelativeLayout mRILoginactivityTop;
    private EditText mEtLoginactivityUsername;
    private EditText mEtLoginactivityPassword;
    private LinearLayout mLILoginactivityTwo;
    private Button mBtLoginactivityLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        mDBOpenHelper=new DBOpenHelper(this);
        HashMap<String,String>map=new HashMap<>();
        map.put("username","你的用户名");
        map.put("password","用户的密码");
        sendPostNetRequest("https://www.wanandroid.com/user/register",map);
    }
    private void initView(){
        mBtLoginactivityLogin=findViewById(R.id.bt_loginactivity_login);
        mTvLoginactivityRegister=findViewById(R.id.tv_loginactivity_register);
        mRILoginactivityTop=findViewById(R.id.rl_loginactivity_top);
        mEtLoginactivityUsername=findViewById(R.id.et_loginactivity_username);
        mEtLoginactivityPassword=findViewById(R.id.et_loginactivity_password);
        mLILoginactivityTwo=findViewById(R.id.ll_loginactivity_two);
        mBtLoginactivityLogin.setOnClickListener(this);
        mTvLoginactivityRegister.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_loginactivity_register:
                startActivity(new Intent(this,RegisterActivity.class));
                finish();
                break;
            case R.id.bt_loginactivity_login:
                String name=mEtLoginactivityUsername.getText().toString().trim();
                String password=mEtLoginactivityPassword.getText().toString().trim();
                if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(password)){
                    ArrayList<User>data=mDBOpenHelper.getAllData();
                    boolean match=false;
                    for (int i=0;i<data.size();i++){
                        User user=data.get(i);
                        if (name.equals(user.getName())&&password.equals(user.getPassword())){
                            match=true;
                            break;
                        }else {
                            match=false;
                        }
                    }
                    if (match){
                        Toast.makeText(this,"登录成功",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(this,"用户名或密码不正确，请重新输入",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this,"请输入您的用户名或密码",Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }
    private void sendPostNetRequest(String mUrl,HashMap<String,String>params){
        new Thread(
                () -> {
                    try {
                        URL url=new URL(mUrl);
                        HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        StringBuilder dataToWrite=new StringBuilder();
                        for (String key: params.keySet()){
                            dataToWrite.append("=").append(params.get(key)).append("&");
                        }
                        connection.connect();
                        OutputStream outputStream=connection.getOutputStream();
                        outputStream.write(dataToWrite.substring(0,dataToWrite.length()-1).getBytes());
                        InputStream in=connection.getInputStream();
                        String responseData=StreamToString(in);
                        Message message=new Message();
                        message.obj=responseData;
                        MHandler.sendMessage(message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
        ).start();
    }

    private String StreamToString(InputStream in) {
        StringBuilder sb=new StringBuilder();
        String oneLine;
        BufferedReader reader=new BufferedReader(new InputStreamReader(in));
        try {
            while ((oneLine=reader.readLine())!=null){
                sb.append(oneLine).append('\n');
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                in.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    private class MHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg){
            super.handleMessage(msg);
            String responseData=msg.obj.toString();
        }
    }
}