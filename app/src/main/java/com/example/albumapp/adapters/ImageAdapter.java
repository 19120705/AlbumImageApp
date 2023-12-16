package com.example.albumapp.adapters;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.example.albumapp.utility.DataLocalManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context context;
    private List<MyCategory> listCategories;
    private List<MyImage> listImages;
    private int spanCount;

    private  List<String> imageListFavorite = DataLocalManager.getInstance()
            .getAlbumImages("Favorite");
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


    public Boolean checkImgInFavorite(String  Path){
        for (String img: imageListFavorite) {
            if(img.equals(Path)){
                return true;
            }
        }
        return false;
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

                LinearLayout btnShare = dialog.findViewById(R.id.btnShare);
                LinearLayout btnFavorite = dialog.findViewById(R.id.btnFavorite);
                LinearLayout btnAddToAlbum = dialog.findViewById(R.id.btnAddToAlbum);
                LinearLayout btnAddToPrivate = dialog.findViewById(R.id.btnPrivate);
                LinearLayout btnDelete = dialog.findViewById(R.id.btnDelete);
                LinearLayout linearLayout = dialog.findViewById(R.id.dialog_layout_photo);
                ImageView iconFavorite = dialog.findViewById(R.id.iconFavorite);



                String imgPath = listImages.get(position).getPath();

                if(checkImgInFavorite(imgPath)){
                    iconFavorite.setImageResource(R.drawable.ic_favorite_select);
                }
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

                btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                btnFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.e("1234567", "onClickFavorite: "+imgPath );
                        if(!checkImgInFavorite(imgPath)){
                            imageListFavorite.add(imgPath);
                        }
                        else{
                            imageListFavorite.remove(imgPath);
                        }
                        Set<String> setListImgFavorite = new HashSet<>();
                        for (String i: imageListFavorite) {
                            setListImgFavorite.add(i);
                        }
                        DataLocalManager.getInstance().saveAlbum("Favorite",setListImgFavorite);
                        dialog.dismiss();
                    }
                });

                btnAddToAlbum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                btnAddToPrivate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Set<String> privateAlbum = new HashSet<>();
                        privateAlbum.add(imgPath);
                        DataLocalManager.getInstance().savePrivateAlbum(privateAlbum);
                        dialog.dismiss();
                    }
                });
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(dialog.getContext());

                        builder.setTitle("Confirm");
                        builder.setMessage("Do you want to delete this image?");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                String currentDateString = getCurrentDateFormatted("yyyy-M-dd");
                                DataLocalManager.getInstance().saveTrash(imgPath,convertDateStringToLong(currentDateString, "yyyy-M-dd"));
                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Do nothing
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        dialog.dismiss();
                    }
                });
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

    private static String getCurrentDateFormatted(String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new Date());
    }

    private static long convertDateStringToLong(String dateString, String dateFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            Date date = sdf.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Handle the exception appropriately
        }
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