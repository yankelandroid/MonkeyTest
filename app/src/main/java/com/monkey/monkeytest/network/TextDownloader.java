package com.monkey.monkeytest.network;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TextDownloader extends AsyncTask<String, Void, String> {

    private Callbacks callbacks;
    private int httpStatusCode;
    private String errorMessage;
    private int type;

    public TextDownloader(Callbacks callbacks, int type) {
        this.callbacks = callbacks;
        this.type = type;
    }


    @Override
    protected String doInBackground(String... params) {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;

        try {
            String link = params[0];
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            httpStatusCode = connection.getResponseCode();
            if (httpStatusCode != HttpURLConnection.HTTP_OK) {
                errorMessage = connection.getResponseMessage();
                return null;
            }
            inputStream = connection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            String downLoadedText = "";
            String onLine = reader.readLine();
            while (onLine != null) {
                downLoadedText += onLine + "\n";
                onLine = reader.readLine();
            }
            return downLoadedText;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    protected void onPreExecute() {
        callbacks.onAboutToBegin();
    }


    protected void onPostExecute(String downloadedText) {
        if (errorMessage == null) {
            try {
                callbacks.onSuccess(downloadedText, type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            callbacks.onError(httpStatusCode, errorMessage);
        }
    }

    public interface Callbacks {

        void onAboutToBegin();

        void onSuccess(String downLoadedText, int type) throws JSONException;

        void onError(int httpStatusCode, String errorMessage);

    }
}

