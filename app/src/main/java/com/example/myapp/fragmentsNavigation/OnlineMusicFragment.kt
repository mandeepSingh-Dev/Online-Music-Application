package com.example.myapp.fragmentsNavigation

import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapp.MusicRecylerView.MyAdapter
import com.example.myapp.MusicRecylerView.Songs
import com.example.myapp.MusicService
import com.example.myapp.R
import com.example.myapp.databinding.FolderFeaturedArtistLayoutBinding
import com.example.myapp.databinding.FragmentOnlineMusicBinding
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class OnlineMusicFragment : Fragment() {

    //view binding
    private var _binding: FragmentOnlineMusicBinding? = null

    var storage: FirebaseStorage? = null
    var mRefernce: StorageReference? = null

    //trending Punjabi Arraylist
    var trendingPunjabiArraylist: ArrayList<Songs>? = null
    var trendingEnglishArraylist: ArrayList<Songs>? = null
    var trendingHindiArraylist: ArrayList<Songs>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_online_music, container, false)
        _binding = FragmentOnlineMusicBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        //getting storage INstance and refernce from storage..
        storage = FirebaseStorage.getInstance()
        mRefernce = storage?.getReference()

        //set up of artists images and etc..
        setUp_FeaturedArtists(view)
        //set color of status bar..
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity?.window?.statusBarColor = resources.getColor(R.color.Dark_Red, null)
        } else {
            activity?.window?.statusBarColor = resources.getColor(R.color.Dark_Red)

        }
        //getting arraylist of local(device) songs from Music Service..
        var arrlist: ArrayList<Songs>? = MusicService.songsList


        if (arrlist != null) {
            //   CoroutineScope(IO).launch {
            Log.d("YOTHREAD", Thread.currentThread().toString())
            setDataToRecentPlayedList(arrlist, view)
            setDataToNewReleasePlayedList(arrlist, view)
            // }
        }


        //for setting Trending layout & its data
        if (arrlist != null) {
            setUp_TrendingFolder(arrlist)
        }

        //for setting Top Charts Folders(include layouts)
        setUp_TopChartsFolder(view)
        //for setting Top Charts Folders(include layouts)

        // CtentResolver resolver=getContext().getContentResolver();
        setUp_DiscoverFolders(view)
        setUp_MoodFolders(view)

        //coroutine scope to launch suspend function in it

          CoroutineScope(Dispatchers.Main).launch {

        set_Arraylist_ToTrendingLayouts(mRefernce!!, "Trending_Playlist", "Trending_Punjabi", view)
         }
        /* includeEnglishTrendingLayout.background =
            resources.getDrawable(R.drawable.gradient_includetrending_eng, null)
        includePunjabiTrendingLayout.background =
            resources.getDrawable(R.drawable.gradient_includetrending_punjabi)
        includeHindiTrendingLayout.background =
            resources.getDrawable(R.drawable.gradient_includetrending_hindi, null)*/

    }  //onViewClosed


    //function to set data to Recent Played
    /*suspend fun*/ fun setDataToRecentPlayedList(
        arraylist: ArrayList<Songs>,
        view: View
    )/*= withContext(Dispatchers.IO)*/ {
        val myAdapter: MyAdapter = MyAdapter(context, arraylist)

        // withContext(Dispatchers.Main){
        val recycelrView = view.findViewById<RecyclerView>(R.id.Recently_RecylerView)
        recycelrView.layoutManager =
            GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        recycelrView.adapter = myAdapter
        // }
    }

    //function to set data to Trending include layouts
    /*suspend*/ fun setDataToNewReleasePlayedList(
        arraylist: ArrayList<Songs>,
        view: View
    )/*= withContext(Dispatchers.IO)*/ {
        val myAdapter: MyAdapter = MyAdapter(context, arraylist)
        // withContext(Dispatchers.Main){
        val recycelrView = view.findViewById<RecyclerView>(R.id.newRelease_RecylerView)
        recycelrView.layoutManager =
            GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        recycelrView.adapter = myAdapter
        // }
    }


    //function to setUp Trending folders(include layouts)
    fun setUp_TrendingFolder(arrlist: ArrayList<Songs>) {


        if (arrlist != null) {
            _binding?.includeTrendingEnglish?.InLanguageTextView?.setText("In English (15)")
            _binding?.includeTrendingEnglish?.songName1?.setText(arrlist.get(0).songName)
            _binding?.includeTrendingEnglish?.songName2?.setText(arrlist.get(1).songName)
            _binding?.includeTrendingEnglish?.songName3?.setText(arrlist.get(2).songName)
            _binding?.includeTrendingEnglish?.songName4?.setText(arrlist.get(3).songName)
            _binding?.includeTrendingEnglish?.songName5?.setText(arrlist.get(4).songName)
        }
        if (arrlist != null) {
            _binding?.includeTrendingHindi?.InLanguageTextView?.setText("In Hindi (15)")
            _binding?.includeTrendingHindi?.songName1?.setText(arrlist.get(0).songName)
            _binding?.includeTrendingHindi?.songName2?.setText(arrlist.get(1).songName)
            _binding?.includeTrendingHindi?.songName3?.setText(arrlist.get(2).songName)
            _binding?.includeTrendingHindi?.songName4?.setText(arrlist.get(3).songName)
            _binding?.includeTrendingHindi?.songName5?.setText(arrlist.get(4).songName)
        }
        if (arrlist != null) {
            _binding?.includeTrendingPunjabi?.InLanguageTextView?.setText("In Punjabi (15)")
            _binding?.includeTrendingPunjabi?.songName1?.setText(arrlist.get(0).songName)
            _binding?.includeTrendingPunjabi?.songName2?.setText(arrlist.get(1).songName)
            _binding?.includeTrendingPunjabi?.songName3?.setText(arrlist.get(2).songName)
            _binding?.includeTrendingPunjabi?.songName4?.setText(arrlist.get(3).songName)
            _binding?.includeTrendingPunjabi?.songName5?.setText(arrlist.get(4).songName)
        }
    }

    //function to setUp Top Charts folders(include layouts)
    fun setUp_TopChartsFolder(view: View) {
        _binding?.includeEnglishTop10?.languageTextviewFolder?.setText("Punjabi")
        _binding?.includePunjabiTop10?.languageTextviewFolder?.setText("International")
        _binding?.includeHindiTop10?.languageTextviewFolder?.setText("Hindi")
        _binding?.includeUSTop10?.languageTextviewFolder?.setText("US")
        _binding?.includeDanceTop10?.languageTextviewFolder?.setText("Dance")
        _binding?.includeHindi90sTop10?.languageTextviewFolder?.setText("Hindi 90s")

    }

    //function to setUp Top Charts folders(include layouts)
    fun setUp_DiscoverFolders(view: View) {
        _binding?.includeRetro?.languageTextviewFolder?.setText("RETRO")
        _binding?.includeIndie?.languageTextviewFolder?.setText("INDIE")
        _binding?.includeEDM?.languageTextviewFolder?.setText("EDM")
        _binding?.includeWedding?.languageTextviewFolder?.setText("WEDDING")
        _binding?.includeDance?.languageTextviewFolder?.setText("DANCE")
        _binding?.includeRock?.languageTextviewFolder?.setText("ROCK")
        _binding?.includeKids?.languageTextviewFolder?.setText("KIDS")
        _binding?.includeGhazals?.languageTextviewFolder?.setText("GHAZAL")
    }

    //function to setUp Moods&Collection folders(include layouts)
    fun setUp_MoodFolders(view: View) {
        _binding?.includeRomance?.languageTextviewFolder?.setText("Romance")
        _binding?.include90sAndEarly?.languageTextviewFolder?.setText("90s & Early 2000s")
        _binding?.includeParty?.languageTextviewFolder?.setText("Party")
        _binding?.includeHipHop?.languageTextviewFolder?.setText("Hip-Hop")
        _binding?.includeBhakti?.languageTextviewFolder?.setText("Bhakti")
        _binding?.includeMusic?.languageTextviewFolder?.setText("Music")
        _binding?.includeHeals?.languageTextviewFolder?.setText("Heals")
        _binding?.includePunjabiHits?.languageTextviewFolder?.setText("Punjabi Top Hits")
        _binding?.includeWorkout?.languageTextviewFolder?.setText("Workout")
    }

    fun setUp_FeaturedArtists(view: View) {
        _binding?.includeTheWeeknd?.singerName?.setText("The Weekend")
        _binding?.includeDiljitDosanjh?.singerName?.setText("Diljit Dosanjh")
        _binding?.includeJustinBeiber?.singerName?.setText("The Justin Beiber")
        _binding?.includeAmmyVirk?.singerName?.setText("Ammy virk")
        _binding?.includeKygo?.singerName?.setText("Kygo")
        _binding?.includeApdhillon?.singerName?.setText("Ap Dhillon")
        _binding?.includeCamilacabillo?.singerName?.setText("Camila Cabillo")
        _binding?.includeDilpreetdhillon?.singerName?.setText("Dilpreet Dhillon")
        _binding?.includeJasonDerulo?.singerName?.setText("Jason Derulo")
        _binding?.includeShivjot?.singerName?.setText("Shivjot")
        _binding?.includeTonesAndI?.singerName?.setText("Tones and I")
        _binding?.includeGurnaamBhullar?.singerName?.setText("Gurnaam Bhullar")
        _binding?.includeDuaLipa?.singerName?.setText("Dua lipa")
        _binding?.includeManinderButtar?.singerName?.setText("Maninder buttar")
        _binding?.includeAnnieMarie?.singerName?.setText("Annie Marie")
        _binding?.includeAmritMaan?.singerName?.setText("Amrit Maan")
        _binding?.includePostMalone?.singerName?.setText("Post Malone")
        _binding?.includeNehaKakkar?.singerName?.setText("Neha kakkar")
        _binding?.includeKishoreKumar?.singerName?.setText("Kishore kumar")
        _binding?.includeArijitSingh?.singerName?.setText("Arijit Singh")

        //functions to upload images on artist images..
        val featured_Artist = "Featured Artists"
        set_ImageToFeaturedArtist(
            featured_Artist,
            "The Weeknd",
            "the weekend.jpg",
            _binding?.includeTheWeeknd!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Ammy virk",
            "ammyvirkimage.jpg",
            _binding?.includeAmmyVirk!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Diljit Dosanjh",
            "Diljit-Dosanjh13.jpg",
            _binding?.includeDiljitDosanjh!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Amrit Maan",
            "amrit_maan-6.jpg",
            _binding?.includeAmritMaan!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Annie Marie",
            "annie marie.jpg",
            _binding?.includeAnnieMarie!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Ap dhillon",
            "AP-dhillon-wallpapers-7.jpg",
            _binding?.includeApdhillon!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Arijit singh",
            "arijit singh.jpg",
            _binding?.includeArijitSingh!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Camila Cabillo",
            "camilla cabillo.jpg",
            _binding?.includeCamilacabillo!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Dilpreet Dhillon",
            "dilpreet dhilon.jpg",
            _binding?.includeDilpreetdhillon!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Dua lipa",
            "dua lipa.jpg",
            _binding?.includeDuaLipa!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Gurnaam Bhullar",
            "gurnaam bhullar.jpg",
            _binding?.includeGurnaamBhullar!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Jason Derulo",
            "jason derulo.jpg",
            _binding?.includeJasonDerulo!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Justin Beiber",
            "justin beiber.jpg",
            _binding?.includeJustinBeiber!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Kishore kumar",
            "kishorekumar.jpg",
            _binding?.includeKishoreKumar!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Kygo",
            "kygo.jpg",
            _binding?.includeKygo!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Maninder buttar",
            "maninder.jpg",
            _binding?.includeManinderButtar!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Neha kakkar",
            "neha kakkr.jpg",
            _binding?.includeNehaKakkar!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Post malone",
            "post malone.jpg",
            _binding?.includePostMalone!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Shivjot",
            "shivjot.jpg",
            _binding?.includeShivjot!!,
            view
        )
        set_ImageToFeaturedArtist(
            featured_Artist,
            "Tones and I",
            "tones and i.jpg",
            _binding?.includeTonesAndI!!,
            view
        )


    }

    fun set_ImageToFeaturedArtist(
        playlistName: String,
        artistName: String,
        imageName: String,
        layout: FolderFeaturedArtistLayoutBinding,
        view: View
    ) {
        mRefernce?.child(playlistName)?.child(artistName)
            ?.child(imageName)?.downloadUrl?.addOnSuccessListener {
                Glide.with(view).asBitmap().load(it).into(layout.singerImage)
                Log.d("FUNDD", "fnction run secuucfdfully")
            }
    }
    suspend fun set_Arraylist_ToTrendingLayouts(refernce: StorageReference, playlistName: String, folderName: String, view: View) = withContext(Dispatchers.Default)
    {

        refernce?.child(playlistName)?.child(folderName)?.listAll()?.addOnSuccessListener {

            var list = it.items
            for (i in 0..list.size - 1) {

                list.get(i).downloadUrl?.addOnSuccessListener {
                    Log.d("UUU",it.toString())

                    var resolver=activity?.contentResolver
                    val cursor: Cursor?=resolver?.query(it,null,null,null,null)

/*
                            Log.d("HCUROEHELLO", cursor!!.getStriursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)))
*/


                    }
                }
             }//for loop closed

                }









    } //class finished here




