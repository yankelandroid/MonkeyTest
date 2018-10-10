package com.monkey.monkeytest;

import android.graphics.drawable.Drawable;
import android.os.Parcelable;

import java.io.Serializable;

import pl.droidsonroids.gif.GifDrawable;

public class Gif implements Serializable{

    String id;
    String url_compressed;
    private String url_original;
    private byte[] bytes;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl_compressed() {
        return url_compressed;
    }

    public void setUrl_compressed(String url_compressed) {
        this.url_compressed = url_compressed;
    }

    public String getUrl_original() {
        return url_original;
    }

    public void setUrl_original(String url_original) {
        this.url_original = url_original;
    }
}
