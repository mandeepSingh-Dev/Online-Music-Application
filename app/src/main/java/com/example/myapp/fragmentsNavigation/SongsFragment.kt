package com.example.myapp.fragmentsNavigation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapp.MusicRecylerView.MyAdapter
import com.example.myapp.MusicRecylerView.MyAdapter2
import com.example.myapp.MusicRecylerView.Songs
import com.example.myapp.MusicRecylerView.Songs_FireBase
import com.example.myapp.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch


class SongsFragment : Fragment() {

    var con: Boolean? = null
    private var songsList: ArrayList<Songs>? = null
    var recylrView: RecyclerView? = null
    var adpter:MyAdapter?=null

    var addpter:MyAdapter2?=null

    var songsFireList:ArrayList<Songs_FireBase>?=null

    var toolbarimageview:ImageView?=null
    var toolbarimageview2:ImageView?=null
    var toolbarimageview3:ImageView?=null
    var toolbarimageview4:ImageView?=null





    var receiver=object: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent?.action.equals("SENDING_BITMAPSTR"))
        {
                var bitmapstrr=intent?.getStringExtra("BITMAPSTR")
            var bitmap=convertToBitmap(bitmapstrr)
            toolbarimageview?.setImageBitmap(bitmap)
            toolbarimageview2?.setImageBitmap(bitmap)
            toolbarimageview3?.setImageBitmap(bitmap)
            toolbarimageview4?.setImageBitmap(bitmap)


        }
        else if(intent?.action.equals("SENDING_FIRENAME"))
        {
           /* Log.d("kfgjkfjkjfk","OOOPS"+intent?.getStringExtra("metasongName"))
            var str=intent?.getStringExtra("metasongName")
            songsList?.add(Songs(str))

            //Log.d("LIISTDFDFD",songsList?.get(0)?.songName!!)

            var sognfirelist:ArrayList<Songs_FireBase> = intent?.getSerializableExtra("songFirebaseList") as ArrayList<Songs_FireBase>
                Log.d("sizeRECIERFIRELIST",songsFireList?.size.toString()+"f;blhf")*/
        }

    }

}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        songsList = ArrayList<Songs>()
        songsFireList= ArrayList()
        storage = FirebaseStorage.getInstance()
         LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, IntentFilter("SENDING_BITMAPSTR"))
        return inflater.inflate(R.layout.fragment_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recylrView=view.findViewById<RecyclerView>(R.id.songsRecyclerView)
        toolbarimageview=view.findViewById(R.id.songImageOnToolbar1)
        toolbarimageview2=view.findViewById(R.id.songImageOnToolbar2)
        toolbarimageview3=view.findViewById(R.id.songImageOnToolbar3)
        toolbarimageview4=view.findViewById(R.id.songImageOnToolbar4)




        /*  var bitmapppa = BitmapFactory.decodeResource(resources, R.drawable.album_icon)
          var imageview = view.findViewById<ImageView>(R.id.songImageOnToolbar)
          imageview.setImageResource(R.drawable.album_icon)
  */
        /*  con=true
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent("BOOLEAN").putExtra("CON",con))
*/
       // recylrView = view.findViewById(R.id.songsRecyclerView)
        Log.d("JHJDF","LKH")
        gettingSongsListfromFireBase("Trending_Playlist", "English")
        /* if(songsList!=null) {
            var adapter = MyAdapter(requireContext(), songsList)
            recylrView.layoutManager =
                GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            recylrView.adapter = adapter
        }
        Log.d("jopo",songsList?.get(0)?.songName!!)*/


    }

    var storage: FirebaseStorage? = null
    var mReference: StorageReference? = null
      var songName:String?=null
     var artistName:String?=null
     var  bitmapStr:String?=null
     var songSizeStr:String?=null
     var durationStr:String?=null
     var songUriStr:String?=null
     var creationDate:Long?=null
     var duration:Long?=null
     var size:Long?=null
     var uri: Uri?=null
     var bitmap:Bitmap?=null
    fun gettingSongsListfromFireBase(playlist: String, folder: String)
    {
        //initStorageReference(storage!!) //initialization of mReference
         mReference=storage?.getReference()
        var songsListtti = ArrayList<Songs>()
        Log.d("JHJDF",mReference.toString()+storage.toString())

        var intent=Intent("SENDING_FIRENAME")
        var manager=LocalBroadcastManager.getInstance(requireContext())

        var metadatalist=ArrayList<Songs>()
        mReference?.child(playlist)?.child(folder)?.listAll()?.addOnSuccessListener(object:OnSuccessListener<ListResult> {
            override fun onSuccess(listResult: ListResult?) {
                listResult?.items?.forEach{
                    songsFireList?.add(Songs_FireBase(it.metadata, it.downloadUrl))
                  //  Log.d("SIFIRE", songsFireList?.size.toString())
                   // intent.putExtra("songFirebaseList", songsFireList)
                   // manager.sendBroadcast(intent)
                    }
                addpter=MyAdapter2(context,songsFireList)
                recylrView?.layoutManager =
                    GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
                recylrView?.adapter = addpter

            }
        })

    } //gettingSon.....function closed

    fun convertToBitmap(bitmapStr: String?): Bitmap? {
        var bitmap1:Bitmap?=null
        try {
           // Log.d("bitMAPSTR",bitmapStr!!)
            val byteArray = Base64.decode(bitmapStr!!, Base64.DEFAULT)
             bitmap1= BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            Log.d("BITMMAP",bitmap1.toString())
        }catch (e:Exception){}
        Log.d("HHELO",bitmap1.toString())
    return bitmap1
    }


}