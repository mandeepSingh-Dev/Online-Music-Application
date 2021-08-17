package com.example.myapp

import android.os.Bundle
import android.os.Handler
import android.os.Handler.Callback
import android.os.Message
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class MyHandlerJavaPracitse : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         object:Handler.Callback{
             override fun handleMessage(msg: Message): Boolean {

                 return true
              }
         }
        Thread(Runnable {
             run {
                 Thread.sleep(1000)
                 println("kdnfkd")
             }
        }).start()

    }

}

