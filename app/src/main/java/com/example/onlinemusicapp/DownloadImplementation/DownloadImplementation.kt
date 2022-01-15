package com.example.onlinemusicapp.DownloadImplementation

import android.app.DownloadManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.example.onlinemusicapp.MusicServices.MusicService.songsList
import java.time.Duration

class DownloadImplementation() {

    companion object {
        @JvmStatic   //JvmStatic notation for using this downloadSong method in java without companion keyword
        fun downloadSong(context:Context,songUri:Uri)
    {
        var downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        var request = DownloadManager.Request(songUri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        //var jhsd=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_MUSIC,
            "/Beatz/NEWSONNGY.mp3"
        )
        downloadManager.enqueue(request)
    }
}
}