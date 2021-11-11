package com.example.onlinemusicapp.MusicRecylerView;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Songs implements Serializable, Parcelable {
    Uri songuri;
    String songName;
    String artist;
    Long duration;
    Bitmap bitmap;
    String dateModified;
    long songSize;

    public Songs() {
    }

    public Songs(String songName) {
        this.songName = songName;
    }


    public Songs(String dateModified, String songName) {
        this.dateModified = dateModified;
        this.songName = songName;
    }

    public Songs(String songName, String artist, Uri songuri) {
        this.songName = songName;
        this.artist = artist;
        this.songuri = songuri;
    }

    public Songs(Uri songuri, String songName, String artist, Long duration, Bitmap bitmap, String dateModified, long songSize) {
        this.songuri = songuri;
        this.songName = songName;
        this.artist = artist;
        this.duration = duration;
        this.bitmap = bitmap;
        this.dateModified = dateModified;
        this.songSize = songSize;
    }

    public Songs(Uri songuri, String songName, String artist, Long duration, String dateModified, long songSize) {
        this.songuri = songuri;
        this.songName = songName;
        this.artist = artist;
        this.duration = duration;
        this.dateModified = dateModified;
        this.songSize = songSize;
    }

    //for pracise only
    public Songs(String songName, Bitmap bitmap) {
        this.songName = songName;
        this.bitmap = bitmap;
    }


    protected Songs(Parcel in) {
        songuri = in.readParcelable(Uri.class.getClassLoader());
        songName = in.readString();
        artist = in.readString();
        if (in.readByte() == 0) {
            duration = null;
        } else {
            duration = in.readLong();
        }
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        dateModified = in.readString();
        songSize = in.readLong();
    }

    public static final Creator<Songs> CREATOR = new Creator<Songs>() {
        @Override
        public Songs createFromParcel(Parcel in) {
            return new Songs(in);
        }

        @Override
        public Songs[] newArray(int size) {
            return new Songs[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(songuri, flags);
        dest.writeString(songName);
        dest.writeString(artist);
        if (duration == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(duration);
        }
        dest.writeParcelable(bitmap, flags);
        dest.writeString(dateModified);
        dest.writeLong(songSize);
    }
}
