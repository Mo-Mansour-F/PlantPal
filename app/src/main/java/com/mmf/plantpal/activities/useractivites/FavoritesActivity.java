package com.mmf.plantpal.activities.useractivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mmf.plantpal.R;
import com.mmf.plantpal.adapters.FavoriteAdapter;
import com.mmf.plantpal.databinding.ActivityFavoritesBinding;
import com.mmf.plantpal.models.Item;
import com.mmf.plantpal.models.User;
import com.mmf.plantpal.utilteis.Constants;
import com.mmf.plantpal.utilteis.MyFireBaseReferences;
import com.mmf.plantpal.utilteis.MySharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    ActivityFavoritesBinding binding;

    FavoriteAdapter favoriteAdapter;
    User user;


    List<Item> favoriteItemList = new ArrayList<>();


    private DatabaseReference favoriteReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this activity using data binding.
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the user details from shared preferences.
        user = MySharedPreferencesManager.getUserLoginDetails(this);

        // Set up the action bar.
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getString(R.string.str_favorite));
        }

        // Get the Firebase reference for the user's favorite items.
        favoriteReference = MyFireBaseReferences
                .getFavoriteReference()
                .child(user.getReferenceId());

        // Initialize the RecyclerView to display favorite items.
        initRecyclerView();
        // Fetch and display the user's favorite items.
        getFavorites();
    }


    private void initRecyclerView() {
        // Create an adapter for displaying favorite items and set it to the RecyclerView.
        favoriteAdapter = new FavoriteAdapter(this, favoriteReference);
        binding.favoriteRecyclerView.setHasFixedSize(true);
        binding.favoriteRecyclerView.setAdapter(favoriteAdapter);
    }

    private void getFavorites() {
        favoriteReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        favoriteItemList.clear();

                        // Iterate through the user's favorite items in the database and add it to the list
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Item item = child.getValue(Item.class);
                            favoriteItemList.add(item);
                        }

                        // Check if there are no favorite items to display.
                        if (favoriteItemList.size() == 0) {
                            binding.progressHorizontal.setVisibility(View.GONE);
                            binding.state.setVisibility(View.VISIBLE);
                            binding.state.setText(getString(R.string.str_no_favorite));
                            binding.progressHorizontalLayout.setVisibility(View.VISIBLE);
                        } else {
                            binding.progressHorizontalLayout.setVisibility(View.GONE);
                        }

                        // Update the RecyclerView with the list of favorite items.
                        favoriteAdapter.setItemList(favoriteItemList);


//
//                        if (snapshot.getChildrenCount() == 0) {
//
//                            for (DataSnapshot child : snapshot.getChildren()) {
//                                Item item = child.getValue(Item.class);
//
//                                favoriteItemList.add(item);
//                            }
//
//
//
//                            binding.progressHorizontal.setVisibility(View.GONE);
//                            binding.state.setVisibility(View.VISIBLE);
//                            binding.state.setText(getString(R.string.str_no_favorite));
//                            binding.progressHorizontalLayout.setVisibility(View.VISIBLE);
//
//                            favoriteAdapter.setItemList(favoriteItemList);
//
//
//                        } else {
//                            binding.progressHorizontalLayout.setVisibility(View.GONE);
//
//                            for (DataSnapshot child : snapshot.getChildren()) {
//                                getFavoritePlant(child.getKey());
//                           }
//                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


//    private void getFavoritePlant(String key) {
//        MyFireBaseReferences
//                .getPlantsReference()
//                .child(key)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            Item item = snapshot.getValue(Item.class);
//                            favoriteItemList.add(item);
//                            favoriteAdapter.setItemList(favoriteItemList);
//
//
//                        }else {
//                            getFavoriteAccessory(key);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
//
//
//
//    private void getFavoriteAccessory(String key) {
//        MyFireBaseReferences
//                .getAccessoriesReference()
//                .child(key)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            Item item = snapshot.getValue(Item.class);
//                            favoriteItemList.add(item);
//                        }
//                        favoriteAdapter.setItemList(favoriteItemList);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}