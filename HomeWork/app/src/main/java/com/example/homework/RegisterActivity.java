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
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private String realCode;
    private DBOpenHelper mDBOpenHelper;
    private Button mBtRegisteractivityRegister;
    private RelativeLayout mRIRegisteractivityTop;
    private ImageView mlvRegisteractivityBack;
    private LinearLayout mLIRegisteractivityBody;
    private EditText mEtRegisteractivityUser;
    private EditText mEtRegisteractivityPassword1;
    private EditText mEtRegisteractivityPassword2;
    private EditText mEtRegisteractivityPhonecodes;
    private ImageView mlvRegisteractivityShowcode;
    private RelativeLayout mRIRegisteractivityBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        mDBOpenHelper=new DBOpenHelper(this);
        mlvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode=Code.getInstance().getCode().toLowerCase();
        HashMap<String,String>map=new HashMap<>();
        map.put("username","你的用户名");
        map.put("password","用户的密码");
        map.put("repassword","确认密码");
        sendPostNetRequest("https://www.wanandroid.com/user/register",map);
    }
    private void initView() {
        mBtRegisteractivityRegister = findViewById(R.id.bt_registeractivity_register);
        mRIRegisteractivityTop = findViewById(R.id.rl_registeractivity_top);
        mlvRegisteractivityBack = findViewById(R.id.iv_registeractivity_back);
        mLIRegisteractivityBody = findViewById(R.id.ll_registeractivity_body);
        mEtRegisteractivityUser = findViewById(R.id.et_registeractivity_user);
        mEtRegisteractivityPassword1 = findViewById(R.id.et_registeractivity_password1);
        mEtRegisteractivityPassword2 = findViewById(R.id.et_registeractivity_password2);
        mEtRegisteractivityPhonecodes=findViewById(R.id.et_registeractivity_phoneCodes);
        mlvRegisteractivityShowcode=findViewById(R.id.iv_registeractivity_showCode);
        mRIRegisteractivityBottom=findViewById(R.id.rl_registeractivity_buttom);
        mlvRegisteractivityBack.setOnClickListener(this);
        mlvRegisteractivityShowcode.setOnClickListener(this);
        mBtRegisteractivityRegister.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_registeractivity_back:
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                    break;
                case R.id.iv_registeractivity_showCode:
                    mlvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
                    realCode = Code.getInstance().getCode().toLowerCase();
                    break;
                case R.id.bt_registeractivity_register:
                    String user = mEtRegisteractivityUser.getText().toString().trim();
                    String key = mEtRegisteractivityPassword2.getText().toString().trim();
                    String phoneCode = mEtRegisteractivityPhonecodes.getText().toString().toLowerCase();

                    if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(key) && !TextUtils.isEmpty(phoneCode)) {
                        if (phoneCode.equals(realCode)) {
                            mDBOpenHelper.add(user, key);
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                            Toast.makeText(this, "验证通过，注册成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "验证码错误，注册失败", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(this, "未完善信息，注册失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

        }
        private void sendPostNetRequest(String mUrl, HashMap<String,String>params){
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
                        for (String key : params.keySet()){
                            dataToWrite.append(key).append("=").append(params.get(key)).append("&");
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