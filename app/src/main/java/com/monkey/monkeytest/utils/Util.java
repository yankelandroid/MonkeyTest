package com.monkey.monkeytest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.monkey.monkeytest.PreviewActivity;
import com.monkey.monkeytest.network.DownloadGif;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class Util {

    private static final String TAG = "SAVEGIF";

    public static void saveGIF(String url, final Context context) {

        DownloadGif downloadGif = new DownloadGif(new DownloadGif.Callbacks() {
            @Override
            public void onAboutToBegin() {

            }

            @Override
            public void onSuccess(byte[] result) {
                try {
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "shared_gif_shai" + System.currentTimeMillis() + ".gif");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(result);
                    Log.d(TAG, "on do in background, write to fos");
                    fos.flush();
                    fos.close();
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());

                }
            }

            @Override
            public void onError(int httpStatusCode, String errorMessage) {

            }
        }, context);
        downloadGif.execute(url);


    }
}
