package com.example.albumapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.albumapp.R;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.PhotoInterface;

import java.util.ArrayList;

public class SlideImageAdapter extends PagerAdapter {
    private ArrayList<MyImage> listImages;
    private PhotoInterface photoInterface;
    private Context context;
    private ImageView imgPicture;
    private boolean flag = false;

    public void setPictureInterface(PhotoInterface photoInterface) {
        this.photoInterface = photoInterface;
    }

    public void setData(ArrayList<MyImage> listImages) {
        this.listImages = listImages;
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return listImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slide_picture, container, false);
        imgPicture = view.findViewById(R.id.imgPicture);
        Glide.with(context).load(listImages.get(position).getThumb()).into(imgPicture);
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        imgPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoInterface.actionShow(flag);
                if(flag)
                    flag = false;
                else
                    flag = true;
            }
        });
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}