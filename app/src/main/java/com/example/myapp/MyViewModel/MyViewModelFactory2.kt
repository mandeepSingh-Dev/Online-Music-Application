package com.example.myapp.MyViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.storage.StorageReference

class MyViewModelFactory2 :ViewModelProvider.NewInstanceFactory
{
    private var playlist:String?=null
    private var folder:String?=null
   private var artistImageName:String?=null


    constructor(playlist:String,folder:String,artistImageName:String/*,mReference:StorageReference*/)
    {
        this.playlist=playlist
        this.folder=folder
        this.artistImageName=artistImageName


    }



    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        var myViewModel2=MyViewModel2(playlist!!,folder!!,artistImageName!!/*,mReference!!*/) as T

        return myViewModel2
    }






}