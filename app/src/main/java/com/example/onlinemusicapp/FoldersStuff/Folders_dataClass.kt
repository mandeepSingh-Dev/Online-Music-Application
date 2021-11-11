package com.example.onlinemusicapp.FoldersStuff

import com.example.onlinemusicapp.MusicRecylerView.Songs

class Folders_dataClass()
{
    var folderNamee:String?=null
    var arraylist:ArrayList<Songs>?=null
    //this constructor for Trending layout folders
    constructor(folderName: String,  arraylist:ArrayList<Songs>):this()
    {
        folderNamee=folderName
        this.arraylist=arraylist


    }
    //:this(name1 = name)

    //this constructor for Top Charts and Moods&Collection layouts folders
    constructor(folderName: String,subname:String):this()

    //this constructor for Featured Artists and Discover layout folders
    constructor(folderName: String):this()  //here for Featured ARtist folderName is Artists name.



}