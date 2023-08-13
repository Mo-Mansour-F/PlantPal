package com.mmf.plantpal.activities.useractivites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.ActivityShowAccessoryBinding;
import com.mmf.plantpal.models.Accessory;
import com.mmf.plantpal.models.Plant;
import com.mmf.plantpal.utilteis.Constants;
import com.mmf.plantpal.utilteis.MoneyFormatter;

public class ShowAccessoryActivity extends AppCompatActivity {
    ActivityShowAccessoryBinding binding;

    Accessory accessory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowAccessoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        accessory = (Accessory) getIntent().getSerializableExtra(Constants.KEY_ACCESSORY_PARAM);

        initView();
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
        binding.accessoryNameHeader.setText(accessory.getName());
        binding.accessoryName.setText(accessory.getName());
        binding.accessoryPrice.setText(MoneyFormatter.fromNumberToMoneyFormat(accessory.getPrice()));
        binding.stock.setText(String.valueOf(accessory.getStock()));
        binding.accessoryType.setText(accessory.getType());
        binding.accessoryDescription.setText(accessory.getDescription());
        binding.accessoryUsageInstructions.setText(accessory.getUsageInstructions());



        if (accessory.getImagePath() == null || accessory.getImagePath().isEmpty()) {
            binding.accessoryImage.setImageResource(R.drawable.plantimage);
        }
        else {
            Glide.with(this)
                    .load(accessory.getImagePath())
                    .error(R.drawable.plantimage)
                    .placeholder(R.drawable.plantimage)
                    .into(binding.accessoryImage);



            Glide.with(this)
                    .load(accessory.getImagePath())
                    .error(R.drawable.plantimage)
                    .placeholder(R.drawable.plantimage)
                    .into(binding.accessoryImageBlur);
        }
    }
}