package com.example.albumapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.albumapp.R;
import com.example.albumapp.models.MyImage;

import java.util.ArrayList;
import java.util.List;

public class ItemAlbumAdapter extends RecyclerView.Adapter<ItemAlbumAdapter.ItemAlbumViewHolder> {
    private List<MyImage> listImages;
    private ImageView imgPhoto;
    private static int REQUEST_CODE_PIC = 10;

    public ItemAlbumAdapter(List<MyImage> list) {
        this.listImages = list;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<MyImage> list) {
        this.listImages = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture_album, parent, false);

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
        }

        public void onBind(MyImage image, int pos) {
            // set ảnh cho imgPhoto bằng thư viện Glide
            Glide.with(context).load(image.getThumb()).into(imgPhoto);
//            imgPhoto.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, PictureActivity.class);
//                    intent.putStringArrayListExtra("data_list_path", album);
//                    intent.putStringArrayListExtra("data_list_thumb", album);
//                    intent.putExtra("pos", pos);
//
//                    ((Activity) context).startActivityForResult(intent, REQUEST_CODE_PIC);
//                }
//            });
        }
    }
}
