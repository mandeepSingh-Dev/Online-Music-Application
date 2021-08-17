package com.example.myapp;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorSingleton
{
    private static  Executor instance;
    private static Object lock="";

    private ExecutorSingleton()
    {}

    public static Executor getInstance()
    {
        if(instance==null)
        {
            synchronized (lock)
            {
                if(instance==null)
                {
                    instance= Executors.newSingleThreadExecutor();
                }
            }
        }
        return instance;
    }

}
