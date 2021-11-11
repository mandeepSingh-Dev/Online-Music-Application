package com.example.onlinemusicapp.MyViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.storage.StorageReference

class MyViewModelFactory :ViewModelProvider.NewInstanceFactory
{
    private var playlist:String?=null
    private var folder:String?=null
    private var mReference:StorageReference?=null


    constructor(playlist:String,folder:String/*,mReference:StorageReference*/)
    {
        this.playlist=playlist
        this.folder=folder
        this.mReference=mReference

    }



    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        var myViewModel=MyViewModel(playlist!!,folder!!/*,mReference!!*/) as T

        return myViewModel
    }






}