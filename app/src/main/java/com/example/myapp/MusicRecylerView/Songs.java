package com.example.myapp.MusicRecylerView;

import android.graphics.Bitmap;
import android.net.Uri;

public class Songs
{
    Uri songuri;
    String songName;
    String artist;
    Long duration;
    Bitmap bitmap;
    String dateModified;
    long songSize;

    public Songs(String dateModified,String songName){
        this.dateModified=dateModified;
        this.songName=songName;
    }
    public Songs(String songName, String artist,Uri songuri){
        this.songName=songName;
        this.artist=artist;
        this.songuri=songuri;
    }

    public Songs(Uri songuri, String songName, String artist, Long duration, Bitmap bitmap,String dateModified,long songSize)
    {
        this.songuri=songuri;
        this.songName=songName;
        this.artist=artist;
        this.duration=duration;
        this.bitmap=bitmap;
        this.dateModified=dateModified;
        this.songSize=songSize;
    }

    public Songs(Uri songuri, String songName, String artist, Long duration,String dateModified,long songSize)
    {
        this.songuri=songuri;
        this.songName=songName;
        this.artist=artist;
        this.duration=duration;
        this.dateModified=dateModified;
        this.songSize=songSize;
    }






    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setSonguri(Uri songuri) {
        this.songuri = songuri;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri getSonguri() {
        return songuri;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtist() {
        return artist;
    }

    public Long getDuration() {
        return duration;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getDateModified() {
        return dateModified;
    }

    public long getSongSize() {
        return songSize;
    }
}
