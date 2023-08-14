package com.mmf.plantpal.activities.useractivites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.mmf.plantpal.R;
import com.mmf.plantpal.adapters.HelpViewPagerAdapter;
import com.mmf.plantpal.databinding.ActivityHelpBinding;
import com.mmf.plantpal.models.HelpItem;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {
    ActivityHelpBinding binding;
    HelpViewPagerAdapter introViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getString(R.string.str_help));
        }


        final List<HelpItem> mList = new ArrayList<>();
        mList.add(new HelpItem("App SiteMap", R.drawable.help_0));
        mList.add(new HelpItem("Add Plant Or Accessory To Favorite", R.drawable.help_1));
        mList.add(new HelpItem("Add Plant Or Accessory To Cart", R.drawable.help_2));
        mList.add(new HelpItem("Search Plant Or Accessory", R.drawable.help_3));
        mList.add(new HelpItem("Sort Item", R.drawable.help_4));
        mList.add(new HelpItem("Filter Item By Species Or type", R.drawable.help_5));
        mList.add(new HelpItem("Edit Or Delete Account", R.drawable.help_6));
        mList.add(new HelpItem("Contact Us", R.drawable.help_7));
        mList.add(new HelpItem("Show Item Details", R.drawable.help_8));


        introViewPagerAdapter = new HelpViewPagerAdapter(this, mList);
        binding.screenViewpager.setAdapter(introViewPagerAdapter);

        binding.tabIndicator.setupWithViewPager(binding.screenViewpager);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}