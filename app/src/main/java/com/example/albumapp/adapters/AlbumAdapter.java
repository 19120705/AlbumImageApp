package com.example.albumapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.albumapp.R;
import com.example.albumapp.activities.ItemAlbumActivity;
import com.example.albumapp.models.MyAlbum;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private List<MyAlbum> mListAlbums;
    private Context context;
    public AlbumAdapter(List<MyAlbum> mListAlbums, Context context) {
        this.mListAlbums = mListAlbums;
        this.context = context;
    }

    public void setData(List<MyAlbum> mListAlbums) {
        this.mListAlbums = mListAlbums;
        notifyDataSetChanged();
    }

    @Override
    public AlbumAdapter.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);


        return new AlbumAdapter.AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.onBind(mListAlbums.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mListAlbums.size();
    }
    class AlbumViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img_album;
        private final TextView txtName_album;
        private final TextView txtCount_item_album;
        private Context context;
        private LinearLayout layout_bottom_delete;
        private LinearLayout layout_bottom_slide_show;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            img_album = itemView.findViewById(R.id.img_album);
            txtName_album = itemView.findViewById(R.id.txtName_album);
            txtCount_item_album = itemView.findViewById(R.id.txtCount_item_album);
            context = itemView.getContext();
        }

        public void onBind(MyAlbum ref, int pos) {
            bindData(ref);

            img_album.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ItemAlbumActivity.class);
                    ArrayList<String> list = new ArrayList<>();
                    for(int i=0;i<ref.getList().size();i++) {
                        list.add(ref.getList().get(i).getThumb());
                    }

                    intent.putStringArrayListExtra("data", list);
                    intent.putExtra("name", ref.getName());
                    intent.putExtra("ok", 1);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

//            img_album.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    txtPath.setText(ref.getPathFolder());
//                    txtPath.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Toast.makeText(context, ref.getPathFolder(), Toast.LENGTH_SHORT).show();
//                        }
//                    });

//                    layout_bottom_slide_show.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            slideShowEvents(ref);
//                        }
//                    });
//                    layout_bottom_delete.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            deleteEvents(ref, pos);
//                        }
//                    });
//                    return true;
//                }
//            });

        }

        private void bindData(MyAlbum ref) {
            txtName_album.setText(ref.getName());
            txtCount_item_album.setText(Integer.toString(ref.getList().size()) + " items");
            if (ref.getImg()!=null) {
                Glide.with(context).load(ref.getImg().getPath()).into(img_album);
            }

//
//        private void slideShowEvents(@NonNull MyAlbum ref) {
//            Intent intent = new Intent(context, SlideShowActivity.class);
//            ArrayList<String> list = new ArrayList<>();
//            for(int i=0;i<ref.getList().size();i++) {
//                list.add(ref.getList().get(i).getThumb());
//            }
//            intent.putStringArrayListExtra("data_slide", list);
//            intent.putExtra("name", ref.getName());
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        }
//        private void deleteEvents(MyAlbum ref, int pos) {
//            for(int i=0;i<ref.getList().size();i++) {
//                Uri targetUri = Uri.parse("file://" + ref.getList().get(i).getPath());
//                File file = new File(targetUri.getPath());
//                if (file.exists()){
//                    file.delete();
//                }
//            }
//            mListAlbums.remove(pos);
//            notifyDataSetChanged();
        }
    }
}
