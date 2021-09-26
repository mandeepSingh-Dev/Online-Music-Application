package com.example.myapp.fragmentsNavigation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myapp.R
import com.example.myapp.databinding.FragmentContentUploadBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.io.File


class ContentUpload_Fragment : Fragment()
{
    var binding:FragmentContentUploadBinding?=null

    var songName:String="Null Name"
    var artistName="Null Artist"
    var songUri:Uri?=null
    var duration:Long=44564545
    var songSize:Long=89787887
    var bitmap:Bitmap?=null

    var defaultUri = "content://media/external_primary/images/media/457"

    var storage:FirebaseStorage?=null
    var mRefernce:StorageReference?=null

    //using RegisterForActivityResult
    var contract: ActivityResultContract<Intent,Uri> = object: ActivityResultContract<Intent,Uri>() {
        override fun createIntent(context: Context, input: Intent?): Intent {
            return input!!
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri
        {
            var uri1:Uri?
            if(intent?.data!=null)
            {
                uri1=intent.data
            }
            else{
                uri1=Uri.parse(defaultUri)
            }
            return uri1!!
        }
    } //ActivityForResultContract finish here
    @RequiresApi(Build.VERSION_CODES.Q)
    var luancher:ActivityResultLauncher<Intent> = registerForActivityResult(contract,
        ActivityResultCallback {
            Log.d("HOLOLOPOLO",it.toString())


            var resolver=activity?.contentResolver
            var cursor=resolver?.query(it,null,null,null,null,null)
           if(cursor!!.moveToFirst())
           {
               songName=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                artistName=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
               songUri=it  //uri
               duration=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
               songSize=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))

               try {
                   bitmap = resolver?.loadThumbnail(it, Size(180, 180), null)
                   Log.d("kdfhj",bitmap.toString())
                   binding?.songImageUpload?.setImageBitmap(bitmap)

               }catch (e:Exception){}
               }
            binding?.uploadImage?.visibility=View.GONE
            binding?.clickPlus?.visibility=View.GONE


            binding?.cardViewForHidenView?.visibility=View.VISIBLE
            binding?.smallUploadImage?.visibility=View.VISIBLE



        })

    //.............................

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
           binding=FragmentContentUploadBinding.inflate(inflater,container,false)
        storage= FirebaseStorage.getInstance()
        mRefernce=storage?.getReference()

       // return inflater.inflate(R.layout.fragment_content_upload_, container, false)
    return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.uploadImage?.setOnClickListener {
            var i= Intent()
            i.setType("audio/*")
            i.setAction(Intent.ACTION_PICK )
            luancher.launch(i)

        }
        binding?.smallUploadImage?.setOnClickListener {
            var i= Intent()
            i.setType("audio/*")
            i.setAction(Intent.ACTION_PICK )
            luancher.launch(i)
        }

        binding?.uploadButton?.setOnClickListener {
            var metaDataBuilder=StorageMetadata.Builder()
          var metaDat=metaDataBuilder.setCustomMetadata("Name",songName).build()

            mRefernce?.child("New Playlist")?.child("New Folder")?.putFile(songUri!!,metaDat)?.addOnSuccessListener {
                Log.d("YoPo",it.toString()+"HOGYA")
            }?.addOnFailureListener {
                Log.d("YoPo",it.toString()+"Nop")
            }?.addOnProgressListener {
                var doublee=(100*it.bytesTransferred)/it.totalByteCount

                binding?.uploadButton?.visibility=View.GONE
                binding?.progressBarUpload?.visibility=View.VISIBLE
                binding?.progressTextViewUpload?.visibility=View.VISIBLE

                binding?.progressBarUpload?.setProgress(doublee as Int)





            }?.addOnSuccessListener {
                binding?.uploadButton?.visibility=View.VISIBLE
                binding?.progressBarUpload?.visibility=View.GONE
                binding?.progressTextViewUpload?.visibility=View.GONE


            }
        }


    }
    

}