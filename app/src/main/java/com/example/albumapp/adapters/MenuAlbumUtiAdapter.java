package com.example.albumapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.R;
import com.example.albumapp.models.MenuItem;

import java.util.List;

public class MenuAlbumUtiAdapter extends RecyclerView.Adapter<MenuAlbumUtiAdapter.ViewHolder> {
    private List<MenuItem> menuItems;

    public MenuAlbumUtiAdapter(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_utilities, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);
        holder.menuIcon.setImageResource(menuItem.getIconResource());
        holder.menuTitle.setText(menuItem.getTitle());
        holder.menuTitle.setTextColor(menuItem.getColor());
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView menuIcon;
        TextView menuTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            menuIcon = itemView.findViewById(R.id.menu_icon);
            menuTitle = itemView.findViewById(R.id.menu_title);
        }
    }
}
