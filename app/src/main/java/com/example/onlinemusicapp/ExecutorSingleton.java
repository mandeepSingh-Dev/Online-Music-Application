package com.example.onlinemusicapp;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorSingleton
{
    private static  Executor instance;
    private static Object lock="";

    //Deafult Constructor (empty) so that no one can make its instances
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
