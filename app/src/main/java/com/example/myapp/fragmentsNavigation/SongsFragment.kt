package com.example.myapp.fragmentsNavigation

import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Base64
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.PaletteAsyncListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.myapp.MusicRecylerView.MyAdapter
import com.example.myapp.MusicRecylerView.MyAdapter2
import com.example.myapp.MusicRecylerView.Songs
import com.example.myapp.MusicRecylerView.Songs_FireBase
import com.example.myapp.MusicServices.MusicService.songsList
import com.example.myapp.MyViewModel.MyViewModel
import com.example.myapp.MyViewModel.MyViewModelFactory
import com.example.myapp.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import com.example.myapp.StorageReferenceSingleton
import com.example.myapp.MusicServices.MusicServiceOnline
import com.example.myapp.databinding.FragmentSongsBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.StorageMetadata
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.text.DecimalFormat


class SongsFragment : Fragment() {

    var con: Boolean? = null
    private var songsList: ArrayList<Songs>? = null
    var recylrView: RecyclerView? = null
    var adpter: MyAdapter? = null

    var addpter: MyAdapter2? = null
    var window: Window? = null

    var songsFireList: ArrayList<Songs_FireBase>? = null

    var toolbarimageview: ImageView? = null
    var toolbarimageview2: ImageView? = null
    var toolbarimageview3: ImageView? = null
    var toolbarimageview4: ImageView? = null
    var toolbar: Toolbar? = null
    var lottie: LottieAnimationView? = null
    var lottie2: LottieAnimationView? = null

    var storage: FirebaseStorage? = null
    var mReference: StorageReference? = null
    var songName: String? = null
    var artistName: String? = null
    var bitmapStr: String? = null
    var songSizeStr: String? = null
    var durationStr: String? = null
    var songUriStr: String? = null
    var creationDate: Long? = null
    var duration: Long? = null
    var size: Long? = null
    var uri: Uri? = null
    var bitmap: Bitmap? = null
    var binding: FragmentSongsBinding? = null

    var playlist: String? = null
    var folder: String? = null

    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var localBroadcastManager: LocalBroadcastManager? = null
    var songsfirelist: ArrayList<Songs>? = null

    //Motion Layout Variables
    var motionSongName: TextView? = null
    var motionImagevIew: ImageView? = null
    var motionCurrentDuration: TextView? = null
    var motionTotalDuration: TextView? = null
    var motionartistName: TextView? = null
    var motionCardView: CardView? = null
    var lastSpace: View? = null
    var dotsButton: ImageButton? = null
    var playPauseButton: CircleImageView? = null
    var prev_Button: ImageView? = null
    var next_Button: ImageView? = null
    var positionTextview: TextView? = null
    var seekBar: SeekBar? = null
    var bottomNavigationView: BottomNavigationView? = null

    //bottom sheet dialog views..
    var bottomSheetDialog: BottomSheetDialog? = null
    var bottomsheetLayout: View? = null
    var artistSheetText: TextView? = null
    var albumSheetText: TextView? = null
    var durationSheetText: TextView? = null
    var songSizeSheet: TextView? = null
    var shareSong: TextView? = null
    var bottomSHEET: View? = null

    var recievedBitmap: Bitmap? = null
    var positionnn: Int? = null

    companion object {
        var staticSonglistFirebase: ArrayList<Songs_FireBase>? = null
    }

    lateinit var musicServiceOnline: MusicServiceOnline

    //getting MusicServiceOnline instance by connecting this class to
    // MusicServiceOnline class using ServiceConnection class
    var serviceConnectionOnline = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("KPKPKP", "onServiceConnected 101")
            val myBinder2 = service as MusicServiceOnline.MyBinder2
            musicServiceOnline = myBinder2.service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("KPKPKP", "onServiceDisconnected 110")
        }
    }

    //here we getting bitmap from OnlineSongsFragment..
    var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent?.action.equals("SENDING_BITMAPSTR")) {
                var bitmapstrr = intent?.getStringExtra("BITMAPSTR")

                CoroutineScope(Dispatchers.Main).launch {
                    var bitmap = convertToBitmap(bitmapstrr)
                    binding?.songImageOnToolbar1?.setImageBitmap(bitmap)
                    binding?.songImageOnToolbar2?.setImageBitmap(bitmap)
                    binding?.songImageOnToolbar3?.setImageBitmap(bitmap)
                    binding?.songImageOnToolbar4?.setImageBitmap(bitmap)
                }
            } else if (intent?.action.equals("SENDING_FIRENAME")) {
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
        staticSonglistFirebase = ArrayList()

        localBroadcastManager = LocalBroadcastManager.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // songsList = ArrayList<Songs>()
        songsFireList = ArrayList()
        songsList = ArrayList()
        storage = FirebaseStorage.getInstance()
        binding = FragmentSongsBinding.inflate(inflater, container, false)

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(receiver, IntentFilter("SENDING_BITMAPSTR"))
        // return inflater.inflate(R.layout.fragment_songs, container, false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SongsFragmentggggg", "songsFRagment");

        //this block is for Fullscreen,color the status bar.

        //this block is for Fullscreen,color the status bar.
        if (Build.VERSION.SDK_INT >= 21) {
            window = this.requireActivity().window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.setStatusBarColor(resources.getColor(R.color.Green))
        }

        //TO START MusicServiceOnline Service.
        var intent = Intent(activity, MusicServiceOnline::class.java)
        intent.setAction("START_FROM_SONGSFRAGMENT")
        activity?.startService(intent)


        //sending  "STOP KAR" string to stop offline song.
        binding?.lottieSongsFragment?.setOnClickListener {
            Log.d("HELLOIGHFR", "HFSDSDJSSTARTSSSlottie")

            var intentttt = Intent("STOP KAR")
            localBroadcastManager?.sendBroadcast(intentttt)
        }

        //getting motion layout from <include/> navigationActivity.
        motionLayoutViews()

        //getting bottomSheetLayout views
        bottomSheetViews()

        //In this Receiver we r getting song position, currentduration position aftr evry 1 second

        //In this Receiver we r getting song position, currentduration position aftr evry 1 second
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "ACTION_SEND_ONLINE") {
                    // motionSongName?.text=intent.getStringExtra("SONGNAAAAM")
                    try {
                        //  Log.d("PPOOK", intent.getIntExtra("receivedPosition_ONLINE", 1).toString())
                        // val receivedPosition = intent.getIntExtra("receivedPosition_ONLINE", 1)

                        //To send Song Uri to anothher app..
                        shareSongUri(positionnn!!)
                        val receivedCurrentPosition =
                            intent.getIntExtra("CURRENTDURATION_ONLINE", 1000)
                        //WE CAN USE CLICKED POSITION INSEAD OF receivedPosition in below line
                        positionTextview!!.text =
                            (positionnn!! + 1).toString() + "/" + songsList?.size
                        //setting current position to textview and seekbar
                        seekBar?.setProgress(receivedCurrentPosition)
                        val currentTimeLabel: String? = createTimeLabel(receivedCurrentPosition)
                        motionCurrentDuration!!.text = currentTimeLabel

                        //SETTING Total duration to seekbar and TextView
                        seekBar?.setMax(musicServiceOnline.getDuration())
                        motionTotalDuration?.setText(createTimeLabel(musicServiceOnline.getDuration()))
                        motionSongName!!.text = songsList?.get(positionnn!!)?.songName
                        motionartistName!!.text = songsList?.get(positionnn!!)?.artist
                        artistSheetText!!.text = songsList?.get(positionnn!!)?.artist
                        albumSheetText!!.text = songsList?.get(positionnn!!)?.songName
                        durationSheetText?.setText(createTimeLabel(musicServiceOnline.getDuration()))

                        val songSize = songsList?.get(positionnn!!)?.songSize
                        Log.d("soongssize", songSize.toString())
                        songSizeSheet?.setText(byteToMB(songSize!!) + "mb")

                        //here we set imagebitmap to motionImagevIew and use bitmap for Palette colors
                        recievedBitmap = songsList?.get(positionnn!!)?.bitmap
                        if (recievedBitmap != null) {
                            motionImagevIew!!.setImageBitmap(recievedBitmap)
                            Log.d("revicerdBitmap", recievedBitmap.toString())
                            //setting pallette color to motionlayout
                            try {
                                Palette.from(recievedBitmap!!)
                                    .generate(object : PaletteAsyncListener {
                                        override fun onGenerated(palette: Palette?) {
                                            try {
                                                setPaletteColor(palette!!)
                                                motionCardView!!.setBackgroundColor(
                                                    palette!!.getMutedColor(
                                                        activity!!.resources.getColor(R.color.paletteDEFAULT)
                                                    )
                                                )
                                                lastSpace!!.setBackgroundColor(
                                                    palette!!.getMutedColor(
                                                        activity!!.resources.getColor(R.color.paletteDEFAULT)
                                                    )
                                                )

                                            } catch (e: java.lang.Exception) {
                                            }
                                        }
                                    })
                            } catch (e: java.lang.Exception) {
                            }
                        } else {
                            motionImagevIew!!.setImageDrawable(resources.getDrawable(R.drawable.music_two_tonne))
                        }
                    } catch (e: java.lang.Exception) {
                    }
                }

            }
        }
        //register LocalBroadcastManager to receive data from MusicService.
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(receiver, IntentFilter("ACTION_SEND_ONLINE"))

        var animation = AnimationUtils.loadAnimation(context, R.anim.opening_anim)
        binding?.songFragmentRootLay?.animation = animation

        lottie = binding?.lottieSongsFragment
        //initialise storageRefernce here..
        mReference = StorageReferenceSingleton().getStroageReference()


        toolbar = view.findViewById<Toolbar>(R.id.toolbarSongsFragment)

        if (arguments != null) {
            playlist = arguments?.get("Playlist").toString()
            folder = arguments?.getString("Folder").toString()
            binding?.toolbarSongsFragment?.setTitle(folder)

            CoroutineScope(Dispatchers.Main).launch {
                // gettingSongsListfromFireBase(playlist, folder)
                Log.d("fklgjkflgflgkflgkf", folder!!)
                gettingSongsListfromFireBase(playlist!!, folder!!)
            }

        } else {
            binding?.toolbarSongsFragment?.setTitle("Music")
        }
    } //onViewCreated Finished


    suspend fun gettingSongsListfromFireBase(
        playlist: String = "Trending_Playlist",
        folder: String = "English"
    ) = withContext(Dispatchers.Main)
    {
        Log.d("hhhhhhh", "flfghgfhfghfgj");
        //here this intent is to send 'it'  (SongsList)
        var i = Intent("Send_SongsList")
        //initStorageReference(storage!!) //initialization of mReference
        var factory: MyViewModelFactory = MyViewModelFactory(playlist!!, folder!!/*,mReference!!*/)
        var myviewmodel =
            ViewModelProvider(this@SongsFragment, factory).get(MyViewModel::class.java)


        myviewmodel.getLiveList()?.observe(viewLifecycleOwner, Observer {

            lottie?.visibility = View.GONE
            lottie2?.visibility = View.GONE
            staticSonglistFirebase = it

            //putting arraylist and sending to Music Service
            i.putParcelableArrayListExtra("songslIst", it)
            localBroadcastManager?.sendBroadcast(i)


            addpter = MyAdapter2(context, it)
            binding?.songsRecyclerView?.layoutManager =
                GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
            binding?.songsRecyclerView?.adapter = addpter


            addpter?.setOnClickListener(object : MyAdapter2.CustomItemClickListener2 {
                override fun customOnItemClick(position: Int) {
                    //i2 intnt to send position to MusicServiceOnline Service
                    positionnn = position
                    var i2 = Intent("FIREPOSITION")
                    i2.putExtra("positionfire", position)
                    localBroadcastManager?.sendBroadcast(i2)

                    //intenttttt is to sending "STOP KAR" String to MusicService
                    //to stop Local/offline song so that online song can play without disturbance
                    var intentttt = Intent("STOP KAR_OFFLINE")
                    localBroadcastManager?.sendBroadcast(intentttt)
                }
            })

            it.forEach {
                it.storageMetadataa.addOnSuccessListener(object :
                    OnSuccessListener<StorageMetadata> {
                    override fun onSuccess(list: StorageMetadata?) {
                        val songName: String? = list?.getCustomMetadata("SongName")
                        val bitmapstr: String? = list?.getCustomMetadata("Bitmap")
                        val dateModifiedd: String? = list?.getCreationTimeMillis().toString()
                        val artist: String? = list?.getCustomMetadata("Artist")
                        val durationn: Long? = list?.getCustomMetadata("Duration")?.toLong()
                        val songSize: Long? = list?.getCustomMetadata("Size")?.toLong()
                        //converting bitmapstr to set in arraylist as a bitmap
                        //converting bitmapstr to set in arraylist as a bitmap
                        val bitmap = convertToBitmap(bitmapstr)

                        it.downloadedUri.addOnSuccessListener(OnSuccessListener<Uri?> { uri ->
                            songsList!!.add(
                                Songs(
                                    uri!!,
                                    songName!!,
                                    artist!!,
                                    durationn!!,
                                    bitmap!!,
                                    dateModifiedd!!,
                                    songSize!!
                                )
                            )
                        })
                        //checking elements in songFireList is added or not
                        //checking elements in songFireList is added or not
                        Log.d("HELEffffJ", songsList!!.size.toString() + "fjk")

                    }

                })
            }

        }
        )

    } //gettingSon.....function closed

    fun byteToMB(bytes: Long): String? {
        Log.d("SONGSIZE", bytes.toString())
        val bytesfloat = java.lang.Float.valueOf(bytes.toString())
        val mb = bytesfloat / 1000000
        //val mbStr = mb.toString()

        return DecimalFormat("###.#").format(mb.toDouble()).toString()
    }

    /*suspend*/ fun convertToBitmap(bitmapStr: String?): Bitmap?/*= withContext(Dispatchers.Default)*/ {
        var bitmap1: Bitmap? = null
        try {
            // Log.d("bitMAPSTR",bitmapStr!!)
            val byteArray = Base64.decode(bitmapStr!!, Base64.DEFAULT)
            bitmap1 = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            Log.d("BITMMAP", bitmap1.toString())
        } catch (e: Exception) {
        }
        Log.d("HHELO", bitmap1.toString())
        return/*@withContext*/ bitmap1
    }

    fun motionLayoutViews() {
        //getting motion layout from <include/> navigationActivity.
        val motionLayoutt: MotionLayout = requireActivity().findViewById(R.id.inccluddeMotion)
        motionCardView = motionLayoutt.findViewById(R.id.cardview)
        motionSongName = motionLayoutt.findViewById<TextView>(R.id.song_name)
        motionartistName = motionLayoutt.findViewById<TextView>(R.id.artist_name_text_view)
        motionImagevIew = motionLayoutt.findViewById<ImageView>(R.id.album_art_image_view)
        playPauseButton = motionLayoutt.findViewById(R.id.play_pause_button)
        motionCurrentDuration = motionLayoutt.findViewById<TextView>(R.id.currentDurationText)
        motionTotalDuration = motionLayoutt.findViewById<TextView>(R.id.totalDurationText)
        prev_Button = motionLayoutt.findViewById<ImageView>(R.id.prev_image_view)
        next_Button = motionLayoutt.findViewById<ImageView>(R.id.next_image_view)
        motionLayoutt.findViewById<View>(R.id.collapse_image_view)
        positionTextview = motionLayoutt.findViewById<TextView>(R.id.position)
        lastSpace = motionLayoutt.findViewById<View>(R.id.lastspace)
        dotsButton = motionLayoutt.findViewById<ImageButton>(R.id.DotsButton)
        bottomNavigationView = motionLayoutt.findViewById(R.id.bottomNavigation)
        seekBar = motionLayoutt.findViewById(R.id.seekbar)

    }


    fun bottomSheetViews() {
        bottomSHEET = LayoutInflater.from(context).inflate(
            R.layout.detailssong_bottom_sheetlayout,
            activity?.findViewById(R.id.sheetConstraintLayout)
        )
        artistSheetText = bottomSHEET?.findViewById<TextView>(R.id.artist_textview)
        albumSheetText = bottomSHEET?.findViewById<TextView>(R.id.album_textview)
        durationSheetText = bottomSHEET?.findViewById<TextView>(R.id.Duration_textview)
        songSizeSheet = bottomSHEET?.findViewById<TextView>(R.id.songSize_textview)
        shareSong = bottomSHEET?.findViewById<TextView>(R.id.share_textview)
    }

    fun createTimeLabel(duration: Int): String? {
        var timeLabel: String? = ""
        val min = duration / 1000 / 60
        val sec = duration / 1000 % 60
        timeLabel += "$min:"
        if (sec < 10) {
            timeLabel += "0"
        }
        timeLabel += sec
        return timeLabel
    }

    fun shareSongUri(receivedPosition: Int) {
        shareSong!!.setOnClickListener {
            val uri = songsList?.get(receivedPosition)?.songuri
            val file = File(uri.toString())
            val intentShare = Intent(Intent.ACTION_SEND)
            intentShare.type = "audio/*"
            // if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
            //this if block also work for android 7 api 24 but not in 27 ..
            Log.d("POPLUPEHLE", file.toString())
            intentShare.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(intentShare, "Share Sound File"))

            /* }else{
                     // Uri urifromProvider=FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".fileprovider",file);
                     try {
                         Uri urifromProvider = FileProvider.getUriForFile(requireContext(),
                                 "com.example.myapp.provider", file);
                         intentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                         intentShare.setDataAndType(urifromProvider,getActivity().getContentResolver().getType(urifromProvider));

                         startActivity(intentShare);
                     }catch(Exception e){e.printStackTrace();
                         Log.d("exceeption",e.getMessage());
                     }
                  }*/
        }
    }

    fun setPaletteColor(p: Palette) {
        try {
            motionCardView!!.setBackgroundColor(
                p.getDarkMutedColor(
                    requireActivity().resources.getColor(
                        R.color.Green
                    )
                )
            )
            lastSpace!!.setBackgroundColor(
                p.getDarkMutedColor(
                    requireActivity().resources.getColor(
                        R.color.Green
                    )
                )
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window?.setStatusBarColor(p.getDarkMutedColor(requireActivity().resources.getColor(R.color.Green)))
                window?.setNavigationBarColor(
                    p.getDarkMutedColor(
                        requireActivity().resources.getColor(
                            R.color.Green
                        )
                    )
                )
            }
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        //binding MusicServiceOnline to this class
        var intent = Intent(context, MusicServiceOnline::class.java)
        activity?.bindService(intent, serviceConnectionOnline, Context.BIND_AUTO_CREATE)
    }
}