package com.example.albumapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.R;
import com.example.albumapp.models.MyCategory;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{
    private Context context;
    private List<MyCategory> listCategories;

    public CategoryAdapter(Context context) {
        this.context = context;
    }

    public void setListCategories(List<MyCategory> listCategories){
        this.listCategories = listCategories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        MyCategory category = listCategories.get(position);
        if (category == null)
            return;

        holder.textViewCategoryName.setText(category.getNameCategory());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        holder.recyclerViewCategories.setLayoutManager(gridLayoutManager);

        ImageAdapter imageAdapter = new ImageAdapter(context.getApplicationContext());
        imageAdapter.setListImages(category.getListImages());
        imageAdapter.setListCategory(listCategories);
        holder.recyclerViewCategories.setAdapter(imageAdapter);


    }

    @Override
    public int getItemCount() {
        if (listCategories != null){
            return listCategories.size();
        }
        return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewCategoryName;
        private RecyclerView recyclerViewCategories;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            recyclerViewCategories = itemView.findViewById(R.id.recyclerViewCategory);
        }
    }
}
