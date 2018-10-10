package com.monkey.monkeytest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.monkey.monkeytest.network.DownloadGif;

import java.io.IOException;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class GifAdapter extends RecyclerView.Adapter<GifAdapter.GifHolder> {

    private Context context;
    private ArrayList<Gif> gifs;
    private OnGifClickListener listener;

    GifAdapter(Context context, ArrayList<Gif> gifs, OnGifClickListener listener) {
        this.context = context;
        this.gifs = gifs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GifHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gif, parent, false);
        return new GifHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GifHolder holder, @SuppressLint("RecyclerView") final int position) {
        ViewCompat.setTransitionName(holder.gifImageView, gifs.get(position).getId());

        holder.bind(gifs.get(position));

        holder.gifImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.seeOriginalGif(position, gifs.get(position), holder.gifImageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gifs.size();
    }

    class GifHolder extends RecyclerView.ViewHolder implements DownloadGif.Callbacks {

        GifImageView gifImageView;
        ProgressBar progressBar;

        GifHolder(View itemView) {
            super(itemView);

            gifImageView = itemView.findViewById(R.id.gif);
            progressBar = itemView.findViewById(R.id.progress);

        }

        void bind(Gif gif) {

            DownloadGif downloadGif = new DownloadGif(this, context);
            downloadGif.execute(gif.url_compressed);

        }

        @Override
        public void onAboutToBegin() {
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        public void onSuccess(byte[] result) {
            progressBar.setVisibility(View.GONE);
            GifDrawable gifDrawable = null;
            try {
                gifDrawable = new GifDrawable(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (getAdapterPosition() != -1) {
                gifs.get(getAdapterPosition()).setBytes(result);
//                notifyItemChanged(getAdapterPosition());
            }
            gifImageView.setImageDrawable(gifDrawable);


        }

        @Override
        public void onError(int httpStatusCode, String errorMessage) {
            notifyItemRemoved(getAdapterPosition());
        }
    }

    interface OnGifClickListener {
        void seeOriginalGif(int position, Gif gif, GifImageView gifImageView);
    }
}
