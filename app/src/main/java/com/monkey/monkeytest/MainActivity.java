package com.monkey.monkeytest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monkey.monkeytest.network.TextDownloader;
import com.monkey.monkeytest.utils.EndlessScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements TextWatcher, TextDownloader.Callbacks, GifAdapter.OnGifClickListener {

    public static final String EXTRA_IMAGE_TRANSITION_NAME = "transition_name";
    public static final String EXTRA_GIF = "gif";
    private static final String MY_PREFS_NAME = "history";
    AutoCompleteTextView editSearch;
    private static final String API_KEY = "Zz7XnA0RZzJJetQAQv1e2c7ErivA9F5u";
    ProgressBar progress;
    RecyclerView recyclerGifs;
    GifAdapter adapter;
    ArrayList<Gif> gifs = new ArrayList<>();
    private int total_count;
    private TextDownloader downloader_pagination;
    private CharSequence searchText;
    MediaPlayer m = new MediaPlayer();
    RelativeLayout relativeAudio;
    ImageView imageAudio;
    ArrayList<String> history = new ArrayList<>();
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private ArrayAdapter<String> adapter_edit;
    private boolean exist;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);


        editSearch = findViewById(R.id.editSearch);
        adapter_edit = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, history);
        editSearch.setAdapter(adapter_edit);
        editSearch.addTextChangedListener(this);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/neotech_bold.otf");
        editSearch.setTypeface(typeface);

        progress = findViewById(R.id.progress);

        recyclerGifs = findViewById(R.id.recyclerGifs);
        recyclerGifs.getItemAnimator().setChangeDuration(0);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerGifs.setLayoutManager(layoutManager);
        adapter = new GifAdapter(this, gifs, this);
        recyclerGifs.setAdapter(adapter);

        recyclerGifs.addOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                if (total_count > gifs.size()) {
                    downloader_pagination = new TextDownloader(new TextDownloader.Callbacks() {
                        @Override
                        public void onAboutToBegin() {

                        }

                        @Override
                        public void onSuccess(String downLoadedText, int type) throws JSONException {
                            JSONObject object = new JSONObject(downLoadedText);
                            JSONArray array = object.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                Gif gif = new Gif();
                                gif.setId(array.getJSONObject(i).getString("id"));
                                gif.setUrl_compressed(array.getJSONObject(i).getJSONObject("images").getJSONObject("fixed_width_downsampled").getString("url"));
                                gif.setUrl_original(array.getJSONObject(i).getJSONObject("images").getJSONObject("fixed_height").getString("url"));
                                gifs.add(gif);
                            }

                            total_count = object.getJSONObject("pagination").getInt("total_count");
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(int httpStatusCode, String errorMessage) {

                        }
                    }, 0);
                    try {
                        downloader_pagination.execute("http://api.giphy.com/v1/gifs/search?q=" + URLEncoder.encode(String.valueOf(searchText), "utf-8")
                                + "offset=" + gifs.size() + "&api_key=" + API_KEY);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        playMusic();

        relativeAudio = findViewById(R.id.relativeAudio);
        imageAudio = findViewById(R.id.imageAudio);
        relativeAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m != null) {
                    if (m.isPlaying()) {
                        m.pause();
                        imageAudio.setImageResource(R.drawable.ic_play);
                    } else {
                        m.start();
                        imageAudio.setImageResource(R.drawable.ic_pause);
                    }
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        TextDownloader downloader = new TextDownloader(MainActivity.this, 0);
        try {
            searchText = charSequence;
            downloader.execute("http://api.giphy.com/v1/gifs/search?q=" + URLEncoder.encode(String.valueOf(charSequence), "utf-8") + "&api_key=" + API_KEY);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (charSequence.length() > 0)
            progress.setVisibility(View.VISIBLE);

        if (searchText != null) {
            exist = false;
            for (int j = 0; j < history.size(); j++) {
                if (history.get(i).equals(searchText))
                    exist = true;
            }
            if (!exist) {
                history.add(searchText.toString());
                editor.putString("array", new Gson().toJson(history));
                editor.apply();
            }

        }

        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();

        ArrayList<String> arrayList = new Gson().fromJson(prefs.getString("array", ""), listType);
        history.addAll(arrayList);

        adapter_edit.notifyDataSetChanged();

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onAboutToBegin() {

    }

    @Override
    public void onSuccess(String downLoadedText, int type) throws JSONException {
        JSONObject object = new JSONObject(downLoadedText);
        JSONArray array = object.getJSONArray("data");
        progress.setVisibility(View.GONE);
        gifs.clear();
        for (int i = 0; i < array.length(); i++) {
            Gif gif = new Gif();
            gif.setId(array.getJSONObject(i).getString("id"));
            gif.setUrl_compressed(array.getJSONObject(i).getJSONObject("images").getJSONObject("fixed_width_downsampled").getString("url"));
            gif.setUrl_original(array.getJSONObject(i).getJSONObject("images").getJSONObject("fixed_height").getString("url"));
            gifs.add(gif);
        }

        total_count = object.getJSONObject("pagination").getInt("total_count");
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onError(int httpStatusCode, String errorMessage) {
        progress.setVisibility(View.GONE);

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void seeOriginalGif(int position, Gif gif, GifImageView gifImageView) {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra(EXTRA_GIF, gif);
//        intent.putExtra(EXTRA_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(gifImageView));
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                this,
//                gifImageView,
//                ViewCompat.getTransitionName(gifImageView));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            startActivity(intent, options.toBundle());
//        }
        startActivity(intent);


    }

    public void playMusic() {
        try {
            if (m.isPlaying()) {
                m.stop();
                m.release();
                m = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = getAssets().openFd("sound/sound_back.mp3");
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            m.setLooping(true);
            m.start();
        } catch (Exception e) {
            Log.e(MainActivity.class.getSimpleName(), e.toString());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        m.stop();
        m.release();
        m = null;
    }
}
