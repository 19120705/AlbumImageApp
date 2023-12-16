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
    private OnImageSelectChangeListener onImageSelectChangeListener;
    boolean isMultiSelect;
    private Context context;
    public ImageSelectAdapter(Context context, boolean isMultiSelect) {
        this.context = context;
        this.isMultiSelect = isMultiSelect;
    }

    public void setOnImageSelectChangeListener(OnImageSelectChangeListener listener) {
        this.onImageSelectChangeListener = listener;
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

    public int getSelectedItemCount() {
        if(listSelectedImages !=null)
            return listSelectedImages.size();
        return 0;
    }
    public List<MyImage> getListSelectedImage() {
        return listSelectedImages;
    }


    private void onImageSelectChanged() {
        if (onImageSelectChangeListener != null) {
            onImageSelectChangeListener.onImageSelectChanged(getSelectedItemCount());
        }
    }
    public void addList(MyImage image) {
        listSelectedImages.add(image);
        onImageSelectChanged();
    }

    public void removeList(MyImage image) {
        listSelectedImages.remove(image);
        onImageSelectChanged();
    }

    public class ImageSelectHolder extends RecyclerView.ViewHolder {
        private ImageView imgPhoto;
        private ImageView iconTick;

        public ImageSelectHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            iconTick = itemView.findViewById(R.id.iconTick);

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
                    if (isMultiSelect)
                        return false;
                    isMultiSelect = true;

                    return true;
                }
            });
            imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isMultiSelect) {
                        if (iconTick.getVisibility() == View.VISIBLE) {
                            imgPhoto.setImageAlpha(255);
                            iconTick.setVisibility(View.INVISIBLE);
                            removeList(image);
                        } else if (iconTick.getVisibility() == View.INVISIBLE) {
                            imgPhoto.setImageAlpha(100);
                            iconTick.setVisibility(View.VISIBLE);
                            addList(image);
                        }
                    }
                    else {
                        Intent intent = new Intent(context, PhotoActivity.class);
                        intent.putParcelableArrayListExtra("dataImages", new ArrayList<>(listImages));
                        intent.putExtra("pos", position);

                        ((Activity) context).startActivityForResult(intent, 10);
                    }
                }
            });
        }
    }

    public interface OnImageSelectChangeListener {
        void onImageSelectChanged(int selectedCount);
    }
}