package com.mmf.plantpal.activities.useractivites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.ActivityShowPlantBinding;
import com.mmf.plantpal.models.Plant;
import com.mmf.plantpal.utilteis.Constants;
import com.mmf.plantpal.utilteis.MoneyFormatter;

public class ShowPlantActivity extends AppCompatActivity {

    ActivityShowPlantBinding binding;

    Plant plant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowPlantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve the Plant object passed as an extra from the previous activity.
        plant = (Plant) getIntent().getSerializableExtra(Constants.KEY_PLANTS_PARAM);

        // Initialize back btn elements
        initView();
        // Fill UI with plant information
        initPlantInfo();
    }


    private void initView(){
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void initPlantInfo(){
        // Fill UI elements with plant information.
        binding.plantNameHeader.setText(plant.getName());
        binding.plantName.setText(plant.getName());
        binding.plantPrice.setText(MoneyFormatter.fromNumberToMoneyFormat(plant.getPrice()));
        binding.stock.setText(String.valueOf(plant.getStock()));
        binding.plantSpecies.setText(plant.getSpecies());
        binding.plantCareInstructions.setText(plant.getCareInstructions());
        binding.plantGrowHabit.setText(plant.getGrowHabit());
        binding.plantDescription.setText(plant.getDescription());

        // Load and display the plant image using Glide

        // If there is no image path provided, display a default image
        if (plant.getImagePath() == null || plant.getImagePath().isEmpty()) {
            binding.plantImage.setImageResource(R.drawable.plantimage);
        }
        else {
            // Load the plant image from the firebase using Glide
            Glide.with(this)
                    .load(plant.getImagePath())
                    .error(R.drawable.plantimage)
                    .placeholder(R.drawable.plantimage)
                    .into(binding.plantImage);

            // Load a blurred version of the plant image in another ImageView using Glide
            Glide.with(this)
                    .load(plant.getImagePath())
                    .error(R.drawable.plantimage)
                    .placeholder(R.drawable.plantimage)
                    .into(binding.plantImageBlur);
        }
    }
}