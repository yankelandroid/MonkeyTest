package com.monkey.monkeytest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.monkey.monkeytest.network.DownloadGif;
import com.monkey.monkeytest.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class PreviewActivity extends AppCompatActivity implements DownloadGif.Callbacks, View.OnClickListener {

    private GifImageView gifImageView;
    ImageView imageSave, imageShare;
    private Gif gif;
    private Bitmap bitmap;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        supportPostponeEnterTransition();
        gif = (Gif) getIntent().getSerializableExtra(MainActivity.EXTRA_GIF);
        gifImageView = findViewById(R.id.gif);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String imageTransitionName = getIntent().getStringExtra(MainActivity.EXTRA_IMAGE_TRANSITION_NAME);
            gifImageView.setTransitionName(imageTransitionName);
        }

        DownloadGif downloadGif = new DownloadGif(this, this);
        downloadGif.execute(gif.getUrl_original());

        imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(this);

        imageShare = findViewById(R.id.imageShare);
        imageShare.setOnClickListener(this);
    }

    @Override
    public void onAboutToBegin() {

    }

    @Override
    public void onSuccess(byte[] result) {
        GifDrawable gifDrawable = null;

        try {
            gifDrawable = new GifDrawable(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifImageView.setImageDrawable(gifDrawable);

    }

    @Override
    public void onError(int httpStatusCode, String errorMessage) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imageSave:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(PreviewActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                }

                break;

            case R.id.imageShare:
                if (gif.getBytes() != null)
                    shareGif();
                break;
        }
    }

    private void shareGif() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "sharingGif.gif";

        File sharingGifFile = new File(baseDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(sharingGifFile);
            fos.write(gif.getBytes());

            fos.close();
        } catch (IOException io) {
        }
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/gif");
        Uri uri = Uri.fromFile(sharingGifFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share Emoji"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Util.saveGIF(gif.getUrl_original(), this);

                } else {
                    Toast.makeText(PreviewActivity.this, "Permission denied to save ... ", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
