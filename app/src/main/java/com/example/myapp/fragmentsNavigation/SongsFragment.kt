package com.example.myapp.fragmentsNavigation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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



var receiver=object: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent?.action.equals("SENDINGFIRESONGDATA"))
        {

        }
        else if(intent?.action.equals("SENDING_FIRENAME"))
        {
            Log.d("JOJOJO","OOOPS"+intent?.getStringExtra("songName"))
         var arrlist:ArrayList<Songs> = intent?.getSerializableExtra("SERIALLIST") as ArrayList<Songs>
            Log.d("KFHG",arrlist.get(0).songName)
            songsList=intent?.getSerializableExtra("SERIALLIST") as ArrayList<Songs>

           val adaapter: MyAdapter2 = MyAdapter2(context,songsList)
            Log.d("JGHJ",songsList?.get(3)?.songName!!)
            recylrView?.layoutManager =
                GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
            recylrView?.adapter = adaapter
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
        storage = FirebaseStorage.getInstance()
         LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, IntentFilter("SENDING_FIRENAME"))
        return inflater.inflate(R.layout.fragment_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recylrView=view.findViewById<RecyclerView>(R.id.songsRecyclerView)

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
    fun gettingSongsListfromFireBase(playlist: String, folder: String) {

        //initStorageReference(storage!!) //initialization of mReference
         mReference=storage?.getReference()
        var songsListtti = ArrayList<Songs>()
        Log.d("JHJDF",mReference.toString()+storage.toString())

        var intent=Intent("SENDING_FIRENAME")
        var manager=LocalBroadcastManager.getInstance(requireContext())


        /* songsList.add(Songs("mandeep"))
        songsList.add(Songs("amardeep"))*/

        //val taskarr= arrayOf<Task<StorageMetadata>>()
        //val arrlist=ArrayList<Task<ListResult>>()

           // var dfhd = mReference?.child(playlist!!)?.child(folder)?.listAll()

     /*   mReference?.child(playlist)?.child(folder)?.listAll()
            ?.addOnSuccessListener(object :  OnSuccessListener<ListResult> {
                override fun onSuccess(p0: ListResult?) {
                   Log.d("JE BAT",p0?.items?.get(0)?.downloadUrl.toString())
                }
            })*/
        var metadatalist=ArrayList<Songs>()
        mReference?.child(playlist)?.child(folder)?.listAll()
            ?.addOnSuccessListener(object : OnSuccessListener<ListResult>{
                override fun onSuccess(p0: ListResult?) {
                    p0?.items?.forEach {

                        Log.d("HJFGIK", it.downloadUrl.toString() + "dfdf")
                        songsListtti.add(Songs(it.downloadUrl.toString()))
                        intent.putExtra("songName",it.downloadUrl.toString())

                        it?.metadata?.addOnSuccessListener(object:OnSuccessListener<StorageMetadata>{
                            override fun onSuccess(md: StorageMetadata?) {
                                Log.d("NNNAEM",md?.getCustomMetadata("SongName").toString())
                            metadatalist.add(Songs(md?.getCustomMetadata("SongName").toString()))
                            }
                        })
                    }

                    Log.d("HOHOH",songsListtti.get(0).songName)
                    intent.putExtra("SERIALLIST",songsListtti)
                    manager.sendBroadcast(intent)
                }
            })
        var listt:ArrayList<Songs> = ArrayList<Songs>()
        /* mReference?.child(playlist)?.child(folder)?.listAll()
            ?.addOnSuccessListener{
                    Log.d("HJFGIK","dfdf")
                    it.items.forEach {

                        Log.d("HJFGIK", it.downloadUrl.toString() + "dfdf")
                        songsListtti.add(Songs(it.downloadUrl.toString()))
                    }
                Log.d("HOHOH",songsListtti.get(0).songName)
                activity?.runOnUiThread(object : Runnable{
                    override fun run() {
                        var myApter=MyAdapter(requireContext(),songsListtti)
                        recylrView?.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
                        recylrView?.adapter = myApter
                    }
                })
            }*/



               /* if(songsListtti.size<14)
                {
                    Log.d("HJFghgGIK",songsListtti.get(1).songName+"dfdf")

                }*/

                /*for (i in 0..it.items.size-1) {

                    it.items[i].metadata.addOnSuccessListener { storageMetadata ->
                         songName = storageMetadata.getCustomMetadata("SongName").toString()
                         artistName = storageMetadata.getCustomMetadata("Artist").toString()
                         bitmapStr = storageMetadata.getCustomMetadata("Bitmap").toString()
                         songSizeStr = storageMetadata.getCustomMetadata("Size").toString()
                         durationStr = storageMetadata.getCustomMetadata("Duration").toString()
                         songUriStr = storageMetadata.getCustomMetadata("Uri").toString()
                         creationDate = storageMetadata.creationTimeMillis
                         duration = durationStr?.toLong()
                         size = songSizeStr?.toLong()


                         bitmap = convertToBitmap(bitmapStr)
                        Log.d("BMAP", bitmap.toString())

                        Log.d("HEJKKGO", storageMetadata.getCustomMetadata("SongName").toString() + folder)

                        it.items[i].downloadUrl.addOnSuccessListener {
                            //now getting downloadedUri of song 1 by 1
                            var uri = it
                        }

                        //if (bitmap != null) {
                        songsList?.add(Songs(songName, bitmap))

                        // } else {
                        //  songsList?.add(Songs(songName, bitmap))
                        // }
                        Log.d("HEJJJUIGUY", songName!!)
                        Log.d("HEuyuyuJJJUIGUY", songsList?.get(i)?.songName!!)


                    }
                    //getting metadata of song 1 by 1


                } //for loop finishes here
                //Log.d("SONGLISTT",songsList?.get(0)?.songName!!)
                var adapter1 = MyAdapter(context, songsList)

                 recylrView?.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
                recylrView?.adapter = adapter1
*/

    }


    // Log.d("HDFH",listResult.get(0).name)

        //Log.d("SONGLISTT",songsList.get(0).songName)

     //gettingSon.....function closed


    fun initStorageReference(storage1: FirebaseStorage) {
        mReference = storage1.reference
    }

    fun convertToBitmap(bitmapStr: String?): Bitmap? {
        var bitmap1:Bitmap?=null
        try {
            Log.d("bitMAPSTR",bitmapStr!!)
            val byteArray = Base64.decode(bitmapStr!!, Base64.NO_WRAP)
             bitmap1= BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            Log.d("BITMMAP",bitmap1.toString())
        }catch (e:Exception){}
        Log.d("HHELO",bitmap1.toString())
    return bitmap1
    }


}