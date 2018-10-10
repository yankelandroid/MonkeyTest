package com.monkey.monkeytest.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadGif extends AsyncTask<String, Void, byte[]> {

    private Callbacks callbacks;
    private int httpStatusCode;
    private String errorMessage;

    public DownloadGif(Callbacks callbacks, Context context) {
        this.callbacks = callbacks;
    }


    @Override
    protected void onPreExecute() {
        callbacks.onAboutToBegin();
    }

    @Override
    protected byte[] doInBackground(String... params) {

        try {


            URL imageUrl = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();

            httpStatusCode = connection.getResponseCode(); // Get the HTTP Status Code.
            if (httpStatusCode != HttpURLConnection.HTTP_OK) { // If there is an error - return null
                errorMessage = connection.getResponseMessage();
                return null;
            }


            InputStream is = connection.getInputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read;

            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }

            baos.flush();

            return baos.toByteArray();

        } catch (Exception e) {
            errorMessage = e.getMessage();
            Log.d("ImageManager", "Error: " + e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        if (errorMessage == null) {
            callbacks.onSuccess(bytes);
        } else {
            callbacks.onError(httpStatusCode, errorMessage);
        }

    }

    public interface Callbacks {
        void onAboutToBegin();

        void onSuccess(byte[] result);

        void onError(int httpStatusCode, String errorMessage);
    }
}

