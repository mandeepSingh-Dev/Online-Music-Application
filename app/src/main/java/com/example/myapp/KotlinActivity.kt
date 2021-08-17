package com.example.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.transition.Transition
import android.util.Log
import androidx.constraintlayout.motion.widget.MotionLayout

class KotlinActivity : AppCompatActivity()
{
     var handler:Handler.Callback?=null
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

     handler =object : Handler.Callback{
           override fun handleMessage(msg: Message): Boolean {
return true
           }
       }



    }
}
class MyHandler(): Handler(Looper.getMainLooper())
{
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)

    }

}


