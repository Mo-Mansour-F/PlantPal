package com.mmf.plantpal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.HelpScreenLayoutBinding;
import com.mmf.plantpal.models.HelpItem;

import java.util.List;

public class HelpViewPagerAdapter extends PagerAdapter {

    Context mContext ;
    List<HelpItem> helpItemList;

    public HelpViewPagerAdapter(Context mContext, List<HelpItem> helpItemList) {
        this.mContext = mContext;
        this.helpItemList = helpItemList;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.help_screen_layout,null);


        HelpScreenLayoutBinding binding = HelpScreenLayoutBinding.bind(layoutScreen);

        HelpItem helpItem = helpItemList.get(position);

        binding.helpTitle.setText(helpItem.getTitle());
        binding.helpImg.setImageResource(helpItem.getHelpImg());

        container.addView(binding.getRoot());

        return binding.getRoot();





    }

    @Override
    public int getCount() {
        return helpItemList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View)object);

    }
}
