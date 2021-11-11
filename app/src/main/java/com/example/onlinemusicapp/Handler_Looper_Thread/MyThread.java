package com.example.onlinemusicapp.Handler_Looper_Thread;

import android.os.Looper;

public class MyThread extends Thread
{
    public MyHandler myHandler;
    @Override
    public void run() {

        Looper.prepare();
        myHandler=new MyHandler();
        Looper.loop();

    }
}
