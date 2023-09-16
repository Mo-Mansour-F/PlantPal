package com.mmf.plantpal.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.AccessoryLayoutBinding;
import com.mmf.plantpal.models.Accessory;
import com.mmf.plantpal.models.Plant;
import com.mmf.plantpal.models.User;
import com.mmf.plantpal.utilteis.Constants;
import com.mmf.plantpal.utilteis.MoneyFormatter;
import com.mmf.plantpal.utilteis.MyFireBaseReferences;
import com.mmf.plantpal.utilteis.MySharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class AccessoryAdapter extends RecyclerView.Adapter<AccessoryAdapter.AccessoryViewHolder> {

    private Context context;
    private List<Accessory> accessoriesList;
    private OnAccessoryClickListener onAccessoryClickListener;
    private DatabaseReference favoriteReference;


    private User user;

    public AccessoryAdapter(Context context) {
        this.context = context;
        this.accessoriesList = new ArrayList<>();
        user = MySharedPreferencesManager.getUserLoginDetails(context);

        if (user.getRole() == Constants.ROLE_USER) {
            favoriteReference = MyFireBaseReferences
                    .getFavoriteReference()
                    .child(user.getReferenceId());

        }

    }

    public static class AccessoryViewHolder extends RecyclerView.ViewHolder {
        AccessoryLayoutBinding binding;

        public AccessoryViewHolder(View view) {
            super(view);
            binding = AccessoryLayoutBinding.bind(view);
        }
    }


    @NonNull
    @Override
    public AccessoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.accessory_layout, parent, false);
        return new AccessoryViewHolder(itemView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull final AccessoryViewHolder holder, final int position) {
        final Accessory accessory = accessoriesList.get(position);


        holder.binding.accessoryName.setText(accessory.getName());
        holder.binding.accessoryPrice.setText(MoneyFormatter.fromNumberToMoneyFormat(accessory.getPrice()));
        holder.binding.accessoryType.setText(accessory.getType());
        holder.binding.description.setText(accessory.getDescription());



        if (accessory.getImagePath() == null || accessory.getImagePath().isEmpty()) {
            holder.binding.accessoryImage.setImageResource(R.drawable.plantimage);
        }
        else {
            Glide.with(context)
                    .load(accessory.getImagePath())
                    .error(R.drawable.plantimage)
                    .placeholder(R.drawable.plantimage)
                    .into(holder.binding.accessoryImage);
        }


        if (user.getRole() == Constants.ROLE_ADMIN){
            bindForAdmin(holder, accessory);
        }else {
            bindForUser(holder, accessory);
        }
    }


    private void bindForAdmin(AccessoryViewHolder holder, Accessory accessory){
        holder.binding.btnAddCartOrDelete.setIcon(context.getDrawable(R.drawable.ic_delete));
        holder.binding.btnAddCartOrDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAccessoryClickListener == null)return;
                onAccessoryClickListener.onDeleteButtonClick(accessory);
            }
        });


        holder.binding.btnFavoriteOrUpdate.setIcon(context.getDrawable(R.drawable.ic_update));
        holder.binding.btnFavoriteOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAccessoryClickListener == null)return;
                onAccessoryClickListener.onUpdateButtonClick(accessory);
            }
        });
    }



    private void checkIsFavorite(AccessoryViewHolder holder, Accessory accessory){
        if (accessory.isFavorite()) {
            holder.binding.btnFavoriteOrUpdate.setIconTintResource(R.color.md_theme_light_error);
            holder.binding.btnFavoriteOrUpdate.setIcon(context.getDrawable(R.drawable.ic_filled_favorite));
        } else {
            holder.binding.btnFavoriteOrUpdate.setIconTintResource(R.color.seed);
            holder.binding.btnFavoriteOrUpdate.setIcon(context.getDrawable(R.drawable.ic_favorite));
        }
    }


    private void bindForUser(AccessoryViewHolder holder, Accessory accessory){
        checkIsFavorite(holder, accessory);

        holder.binding.btnAddCartOrDelete.setEnabled(accessory.getStock() > 0);
        holder.binding.btnAddCartOrDelete.setIcon(context.getDrawable(R.drawable.ic_add_cart));

        holder.binding.btnAddCartOrDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAccessoryClickListener == null)return;
                onAccessoryClickListener.onAddToCartButtonClick(accessory);
            }
        });

        holder.binding.btnFavoriteOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accessory.isFavorite()){
                    favoriteReference
                            .child(accessory.getReferenceId())
                            .removeValue();
                }else {
                    favoriteReference
                            .child(accessory.getReferenceId())
                            .setValue(true);
                }


                accessory.setFavorite(!accessory.isFavorite());
                checkIsFavorite(holder, accessory);
            }
        });

        holder.binding.accessoryRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAccessoryClickListener == null) return;
                onAccessoryClickListener.onClick(holder.binding.accessoryImage, accessory);
            }
        });




        favoriteReference
                .child(accessory.getReferenceId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() ) {
                            accessory.setFavorite(true);
                        }else {
                            accessory.setFavorite(false);
                        }

                        checkIsFavorite(holder, accessory);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return accessoriesList.size();
    }


    public void setAccessoriesList(List<Accessory> accessoriesList) {
        this.accessoriesList = accessoriesList;
        notifyDataSetChanged();
    }

    public void setOnAccessoryClickListener(OnAccessoryClickListener onAccessoryClickListener) {
        this.onAccessoryClickListener = onAccessoryClickListener;
    }

    public interface OnAccessoryClickListener {
        void onClick(View view, Accessory accessory);
        void onDeleteButtonClick(Accessory accessory);
        void onUpdateButtonClick(Accessory accessory);
        void onAddToCartButtonClick(Accessory accessory);
    }


}

