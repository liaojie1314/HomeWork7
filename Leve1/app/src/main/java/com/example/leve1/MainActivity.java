package com.example.leve1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private int count=30;

    private Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    textView.setText(String.valueOf((int) msg.obj));
                    break;
                default:
                    break;
            }
        }
    };
    Timer timer=new Timer();
    TimerTask task=new TimerTask() {
        @Override
        public void run() {
            if (count>0){
                count--;
            }else {
                destoryTimer();
            }
            Message message=new Message();
            message.what=1;
            message.obj=count;
            handler.sendMessage(message);
        }
    };

    private void destoryTimer(){
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.tv_am);
        timer.scheduleAtFixedRate(task,1000,1000);
    }
}