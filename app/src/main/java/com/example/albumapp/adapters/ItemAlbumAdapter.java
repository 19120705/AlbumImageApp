package com.example.albumapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.albumapp.R;
import com.example.albumapp.activities.PhotoActivity;
import com.example.albumapp.models.MyImage;

import java.util.ArrayList;
import java.util.List;

public class ItemAlbumAdapter extends RecyclerView.Adapter<ItemAlbumAdapter.ItemAlbumViewHolder> {
    private List<MyImage> listImages;
    private ImageView imgPhoto;
    private boolean isItemClickable;
    private int spanCount;

    public ItemAlbumAdapter(List<MyImage> list, int spanCount) {
        this.listImages = list;
        this.spanCount = spanCount;
        isItemClickable = true;
    }

    public void turnOffClickable() {
        isItemClickable = false;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<MyImage> list) {
        this.listImages = list;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false);

        return new ItemAlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAlbumViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.onBind(listImages.get(position), position);
    }

    @Override
    public int getItemCount() {
        return listImages.size();
    }

    public class ItemAlbumViewHolder extends RecyclerView.ViewHolder {
        private Context context;

        public ItemAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            ViewGroup.LayoutParams layoutParam = imgPhoto.getLayoutParams();
            if (spanCount == 1) {
                layoutParam.width = 1300;
                layoutParam.height = 1300;
            }
            if (spanCount == 2) {
                layoutParam.width = 650;
                layoutParam.height = 650;
            }

            if (spanCount == 3) {
                layoutParam.width = 455;
                layoutParam.height = 455;
            }
            if (spanCount == 4) {
                layoutParam.width = 350;
                layoutParam.height = 350;
            }

            imgPhoto.setLayoutParams(layoutParam);

        }

        public void onBind(MyImage image, int pos) {
            // set ảnh cho imgPhoto bằng thư viện Glide
            Glide.with(context).load(image.getThumb()).into(imgPhoto);

            imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isItemClickable) {
                        return;
                    }
                    Intent intent = new Intent(context, PhotoActivity.class);
                    intent.putParcelableArrayListExtra("dataImages", new ArrayList<>(listImages));
                    intent.putExtra("pos", pos);

                    ((Activity) context).startActivityForResult(intent, 10);
                }
            });
        }
    }
}
