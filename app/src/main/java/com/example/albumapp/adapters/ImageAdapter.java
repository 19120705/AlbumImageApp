package com.example.albumapp.adapters;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.albumapp.R;
import com.example.albumapp.activities.PhotoActivity;
import com.example.albumapp.models.MenuItem;
import com.example.albumapp.models.MyCategory;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context context;
    private List<MyCategory> listCategories;
    private List<MyImage> listImages;
    private int spanCount;

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
    @SuppressLint("NotifyDataSetChanged")
    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
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
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Tạo một Dialog mới
                final Dialog dialog = new Dialog(v.getContext(), R.style.TransparentDialog);
                // Đặt nội dung cho Dialog từ một layout XML
                dialog.setContentView(R.layout.dialog_layout_photo);
                dialog.setCanceledOnTouchOutside(true);
                LinearLayout linearLayout = dialog.findViewById(R.id.dialog_layout_photo);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                // Tìm ImageView trong Dialog và đặt hình ảnh của nó
                ImageView dialogImageView = (ImageView) dialog.findViewById(R.id.dialog_imageView_photo);
                Glide.with(context)
                        .load(listImages.get(position).getThumb())
                        .into(dialogImageView);


                RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
                LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
                recyclerView.setLayoutManager(layoutManager);
                List<MenuItem> menuItems = new ArrayList<>();
                menuItems.add(new MenuItem(R.drawable.ic_ios_share,v.getResources().getString(R.string.share) , Color.BLACK));
                menuItems.add(new MenuItem(R.drawable.ic_favorite, v.getResources().getString(R.string.favorite), Color.BLACK));
                menuItems.add(new MenuItem(R.drawable.ic_add_photos, v.getResources().getString(R.string.addAlbum), Color.BLACK));
                menuItems.add(new MenuItem(R.drawable.ic_priavte_off, v.getResources().getString(R.string.addprivate), Color.BLACK));
                menuItems.add(new MenuItem(R.drawable.ic_trash_red, v.getResources().getString(R.string.delete), Color.RED));
                MenuAdapter adapter = new MenuAdapter(menuItems);
                recyclerView.setAdapter(adapter);

                // Thêm DividerItemDecoration vào RecyclerView
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(v.getContext(), R.drawable.divider);
                recyclerView.addItemDecoration(dividerItemDecoration);
                recyclerView.setBackgroundResource(R.drawable.background_recyclerview_menu);
                // Hiển thị Dialog
                dialog.show();
                return true;
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
            ViewGroup.LayoutParams layoutParam = imageView.getLayoutParams();
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

            imageView.setLayoutParams(layoutParam);
        }
    }


    public class MyAsyncTask implements Runnable {
        public int pos;
        public ArrayList<MyImage> dataImages;

        public void setPos(int pos) {
            this.pos = pos;
        }

        @Override
        public void run() {
            //Lay image cua tat ca category
            dataImages = new ArrayList<>();
            for(int i = 0;i<listCategories.size();i++) {
                dataImages.addAll(listCategories.get(i).getListImages());
            }
            for(int i = 0;i<dataImages.size();i++) {
                if (listImages.get(pos).getPath() == dataImages.get(i).getPath()) {
                    pos = i;
                    break;
                }
            }
            // Update UI on the main thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    intent.putParcelableArrayListExtra("dataImages", new ArrayList<>(dataImages));
                    intent.putExtra("pos", pos);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }



}