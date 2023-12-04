package com.example.albumapp.adapters;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.albumapp.R;
import com.example.albumapp.activities.PhotoActivity;
import com.example.albumapp.models.MyCategory;
import com.example.albumapp.models.MyImage;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context context;
    private List<MyCategory> listCategories;
    private List<MyImage> listImages;
    private Intent intent;
    public ImageAdapter(Context context) {
        this.context = context;
    }

    public void setListCategory(List<MyCategory> listCategory) {
        this.listCategories = listCategory;
    }
    public void setListImages(List<MyImage> listImages) {
        this.listImages = listImages;
        notifyDataSetChanged();
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView")int position) {
        Glide.with(context)
                .load(listImages.get(position).getThumb())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("ImageAdapter", "Load failed", e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, PhotoActivity.class);
                MyAsyncTask myAsyncTask = new MyAsyncTask();
                myAsyncTask.setPos(position);
                new Thread(myAsyncTask).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listImages != null)
            return listImages.size();
        return 0;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item_imageView);
        }
    }


    public class MyAsyncTask implements Runnable {
        public int pos;

        public void setPos(int pos) {
            this.pos = pos;
        }

        @Override
        public void run() {
            // Update UI on the main thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    intent.putExtra("pos", pos);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }



}