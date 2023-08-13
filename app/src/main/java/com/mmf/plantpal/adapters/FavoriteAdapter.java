package com.mmf.plantpal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.FavoriteLayoutBinding;
import com.mmf.plantpal.databinding.UserLayoutBinding;
import com.mmf.plantpal.models.Item;
import com.mmf.plantpal.models.User;
import com.mmf.plantpal.utilteis.MoneyFormatter;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteVewHolder> {
    private Context context;
    private List<Item> itemList;
    private DatabaseReference favoriteReference;


    public FavoriteAdapter(Context context, DatabaseReference favoriteReference) {
        this.context = context;
        this.itemList = new ArrayList<>();
        this.favoriteReference = favoriteReference;
    }


    public static class FavoriteVewHolder extends RecyclerView.ViewHolder {
        FavoriteLayoutBinding binding;

        public FavoriteVewHolder(View view) {
            super(view);
            binding = FavoriteLayoutBinding.bind(view);
        }
    }


    @NonNull
    @Override
    public FavoriteVewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_layout, parent, false);
        return new FavoriteVewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FavoriteVewHolder holder, final int position) {

        Item item = itemList.get(position);

        holder.binding.itemName.setText(item.getName());
        holder.binding.itemPrice.setText(MoneyFormatter.fromNumberToMoneyFormat(item.getPrice()));



        if (item.getImagePath() == null || item.getImagePath().isEmpty()) {
            holder.binding.itemImage.setImageResource(R.drawable.plantimage);
        }
        else {
            Glide.with(context)
                    .load(item.getImagePath())
                    .error(R.drawable.plantimage)
                    .placeholder(R.drawable.plantimage)
                    .into(holder.binding.itemImage);
        }



        holder.binding.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteReference
                        .child(item.getReferenceId())
                        .removeValue();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }
}



