package com.example.myapp.MusicRecylerView;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Songs implements Parcelable
{
    Uri songuri;
    String songName;
    String artist;
    Long duration;
    Bitmap bitmap;

    public Songs(){}
    public Songs(Uri songuri, String songName, String artist, Long duration, Bitmap bitmap)
    {
        this.songuri=songuri;
        this.songName=songName;
        this.artist=artist;
        this.duration=duration;
        this.bitmap=bitmap;
    }

    public Songs(Uri songuri, String songName, String artist, Long duration)
    {
        this.songuri=songuri;
        this.songName=songName;
        this.artist=artist;
        this.duration=duration;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
