package com.example.mealmate.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Import ImageView
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealmate.DataClass;
import com.example.mealmate.Fragments.RecipeDetailFragment;
import com.example.mealmate.R;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList != null ? dataList : new ArrayList<>(); // Initialize to empty list if null
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataClass dataItem = dataList.get(position); // Get the data item

        // Load the image URL with Glide
        String imageUrl = dataItem.getDataImage();
        if (imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image) // Placeholder while loading
                    .error(R.drawable.error_image) // Error placeholder
                    .into(holder.recImage);
        } else {
            holder.recImage.setImageResource(R.drawable.placeholder_image); // Use placeholder if URL is null
        }

        // Set title and description
        holder.recTitle.setText(dataItem.getDataTitle());
        holder.recDesc.setText(dataItem.getDataDesc()); // Set description text

        // Handle card click to navigate to the RecipeDetailFragment
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Image", imageUrl);
                bundle.putString("Description", dataItem.getDataDesc());
                bundle.putString("Title", dataItem.getDataTitle());
                bundle.putString("Key", dataItem.getKey());
                recipeDetailFragment.setArguments(bundle);

                // Replace the current fragment
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.navigation, recipeDetailFragment)
                        .addToBackStack(null) // Optional: adds to the back stack
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList) {
        dataList = searchList != null ? searchList : new ArrayList<>(); // Prevent null assignment
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView recImage; // Changed to ImageView
        TextView recTitle, recDesc; // Make sure recDesc is included
        CardView recCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recImage = itemView.findViewById(R.id.recImage); // Bind the ImageView
            recCard = itemView.findViewById(R.id.recCard);
            recTitle = itemView.findViewById(R.id.recTitle);
            recDesc = itemView.findViewById(R.id.recDesc); // Bind description TextView
        }
    }
}
