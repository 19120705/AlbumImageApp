package com.example.albumapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.example.albumapp.models.MyImage;

import java.util.ArrayList;
import java.util.List;

public class ImageSelectAdapter extends RecyclerView.Adapter<ImageSelectAdapter.ImageSelectHolder> {
    private List<MyImage> listImages;
    private List<MyImage> listSelectedImages;
    boolean isMultiSelect;
    private Context context;
    public ImageSelectAdapter(Context context, boolean isMultiSelect) {
        this.context = context;
        this.isMultiSelect = isMultiSelect;
    }

    public void setData(List<MyImage> listImages) {
        this.listImages = listImages;
        this.listSelectedImages = new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageSelectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false);

        return new ImageSelectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSelectHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.onBind(listImages.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (listImages != null)
            return listImages.size();
        return 0;
    }

    public List<MyImage> getListSelectedImage() {
        return listSelectedImages;
    }

    public void addList(MyImage image) {
        listSelectedImages.add(image);
    }

    public void removeList(MyImage image) {
        listSelectedImages.remove(image);
    }

    public class ImageSelectHolder extends RecyclerView.ViewHolder {
        private ImageView imgPhoto;

        public ImageSelectHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);

            ViewGroup.LayoutParams layoutParam = imgPhoto.getLayoutParams();
            layoutParam.width = 350;
            layoutParam.height = 350;
            imgPhoto.setLayoutParams(layoutParam);
        }
        public void onBind(MyImage image, int position) {
            if (image == null) {
                return;
            }

            // set ảnh cho imgPhoto bằng thư viện Glide
            Glide.with(context).load(image.getThumb()).into(imgPhoto);
            imgPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    isMultiSelect = true;

                    return true;
                }
            });
            imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (imgPhoto.getImageAlpha() == 100) {
                        imgPhoto.setImageAlpha(255);
                        removeList(image);
                    } else if (imgPhoto.getImageAlpha() == 255) {
                        imgPhoto.setImageAlpha(100);
                        addList(image);
                    }
                }
            });
        }
    }
}