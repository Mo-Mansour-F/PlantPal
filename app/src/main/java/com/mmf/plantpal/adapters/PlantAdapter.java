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
import com.mmf.plantpal.databinding.PlantLayoutBinding;
import com.mmf.plantpal.models.Accessory;
import com.mmf.plantpal.models.Plant;
import com.mmf.plantpal.models.User;
import com.mmf.plantpal.utilteis.Constants;
import com.mmf.plantpal.utilteis.MoneyFormatter;
import com.mmf.plantpal.utilteis.MyFireBaseReferences;
import com.mmf.plantpal.utilteis.MySharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {


    private Context context;
    private List<Plant> plantList;
    private OnPlantClickListener onPlantClickListener;
    private DatabaseReference favoriteReference;


    private User user;

    public PlantAdapter(Context context) {
        this.context = context;
        this.plantList = new ArrayList<>();
        user = MySharedPreferencesManager.getUserLoginDetails(context);

        if (user.getRole() == Constants.ROLE_USER) {
            favoriteReference = MyFireBaseReferences
                    .getFavoriteReference()
//                    .child(Constants.KEY_PLANTS)
                    .child(user.getReferenceId());

        }


    }

    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        PlantLayoutBinding binding;

        public PlantViewHolder(View view) {
            super(view);
            binding = PlantLayoutBinding.bind(view);
        }
    }


    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_layout, parent, false);
        return new PlantViewHolder(itemView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull final PlantViewHolder holder, final int position) {
        final Plant plant = plantList.get(position);


        holder.binding.plantName.setText(plant.getName());
        holder.binding.plantPrice.setText(MoneyFormatter.fromNumberToMoneyFormat(plant.getPrice()));
        holder.binding.plantSpecies.setText(plant.getSpecies());
        holder.binding.plantDescription.setText(plant.getDescription());


        if (plant.getImagePath() == null || plant.getImagePath().isEmpty()) {
            holder.binding.plantImage.setImageResource(R.drawable.plantimage);
        } else {
            Glide.with(context)
                    .load(plant.getImagePath())
                    .error(R.drawable.plantimage)
                    .placeholder(R.drawable.plantimage)
                    .into(holder.binding.plantImage);
        }


        if (user.getRole() == Constants.ROLE_ADMIN) {
            bindForAdmin(holder, plant);
        } else {
            bindForUser(holder, plant);
        }


    }


    private void bindForAdmin(PlantViewHolder holder, Plant plant) {
        holder.binding.btnAddCartOrDelete.setIcon(context.getDrawable(R.drawable.ic_delete));
        holder.binding.btnAddCartOrDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlantClickListener == null) return;
                onPlantClickListener.onDeleteButtonClick(plant);
            }
        });


        holder.binding.btnFavoriteOrUpdate.setIcon(context.getDrawable(R.drawable.ic_update));
        holder.binding.btnFavoriteOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlantClickListener == null) return;
                onPlantClickListener.onUpdateButtonClick(plant);
            }
        });
    }


    private void checkIsFavorite(PlantViewHolder holder, Plant plant) {

        if (plant.isFavorite()) {
            holder.binding.btnFavoriteOrUpdate.setIconTintResource(R.color.md_theme_light_error);
            holder.binding.btnFavoriteOrUpdate.setIcon(context.getDrawable(R.drawable.ic_filled_favorite));
        } else {
            holder.binding.btnFavoriteOrUpdate.setIconTintResource(R.color.seed);
            holder.binding.btnFavoriteOrUpdate.setIcon(context.getDrawable(R.drawable.ic_favorite));
        }


    }

    private void bindForUser(PlantViewHolder holder, Plant plant) {

        checkIsFavorite(holder, plant);

        holder.binding.btnAddCartOrDelete.setEnabled(plant.getStock() > 0);

        holder.binding.btnAddCartOrDelete.setIcon(context.getDrawable(R.drawable.ic_add_cart));
        holder.binding.btnAddCartOrDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlantClickListener == null) return;
                onPlantClickListener.onAddToCartButtonClick(plant);
            }
        });



        holder.binding.btnFavoriteOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (plant.isFavorite()){
                    favoriteReference
                            .child(plant.getReferenceId())
                            .removeValue();
                }else {
                    favoriteReference
                            .child(plant.getReferenceId())
                            .setValue(true);
                }


                plant.setFavorite(!plant.isFavorite());
                checkIsFavorite(holder, plant);
            }
        });


        holder.binding.plantRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlantClickListener == null) return;
                onPlantClickListener.onClick(holder.binding.plantImage, plant);
            }
        });


        favoriteReference
                .child(plant.getReferenceId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() ) {
                            plant.setFavorite(true);
                        }else {
                            plant.setFavorite(false);
                        }

                        checkIsFavorite(holder, plant);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    public void setPlantList(List<Plant> plantList) {
        this.plantList = plantList;
        notifyDataSetChanged();
    }

    public void setOnPlantClickListener(OnPlantClickListener onPlantClickListener) {
        this.onPlantClickListener = onPlantClickListener;
    }


    public interface OnPlantClickListener {
        void onClick(View view, Plant plant);
        void onDeleteButtonClick(Plant plant);
        void onUpdateButtonClick(Plant plant);
        void onAddToCartButtonClick(Plant plant);
    }


}

