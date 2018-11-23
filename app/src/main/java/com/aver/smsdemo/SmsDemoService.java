package com.aver.smsdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class SmsDemoService extends Service {

    private Handler handler = new Handler();
    private int count = 0;
    private static final String Test_SERVICE_IDENTIFIER = "@string/str_Text";

    private Runnable mTasks = new Runnable() {

        @Override
        public void run() {
            ++count;
            if (count == 5) {
                Intent i = new Intent(Test_SERVICE_IDENTIFIER);
                i.putExtra("SmsDemoService", "Service Message here...");
                sendBroadcast(i);
            }
            handler.postDelayed(mTasks, 1000);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(mTasks, 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(mTasks);
    }
}
