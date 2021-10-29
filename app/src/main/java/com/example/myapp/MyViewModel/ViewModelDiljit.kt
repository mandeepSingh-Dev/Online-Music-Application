package com.example.myapp.MyViewModel

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.myapp.R
import com.example.myapp.StorageReferenceSingleton
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.StorageReference
import java.io.File

class ViewModelDiljit(application: Application):AndroidViewModel(application)
{
    private var liveBitmap:MutableLiveData<Bitmap>?=null
    private var mrefernceSingleton:StorageReference?=null
    private var liveArtistName:MutableLiveData<String>?=null


    init {

        liveArtistName=MutableLiveData()
        liveBitmap=MutableLiveData()
        mrefernceSingleton = StorageReferenceSingleton().getStroageReference()
        mrefernceSingleton?.child("Featured_Artists")?.child("Diljit Dosanjh")?.child("Diljit-Dosanjh13.jpg")?.downloadUrl?.addOnSuccessListener {
            liveArtistName?.value="Diljit Dosanjh"

            Glide.with(application)?.asBitmap().load(it).into(object: CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    liveBitmap?.value=resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    TODO("Not yet implemented")
                }
            })



        }

    }
    fun getLiveBitmap():LiveData<Bitmap>?
    {
        return liveBitmap
    }
    fun getLiveArtistName():LiveData<String>?
    {
        return liveArtistName
    }
}
