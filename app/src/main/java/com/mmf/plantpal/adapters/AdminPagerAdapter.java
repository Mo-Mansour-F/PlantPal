package com.mmf.plantpal.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mmf.plantpal.fragments.AccessoryFragment;
import com.mmf.plantpal.fragments.PlantFragment;


public class AdminPagerAdapter extends FragmentStateAdapter {

    public AdminPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PlantFragment();
            case 1:
                return new AccessoryFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}


