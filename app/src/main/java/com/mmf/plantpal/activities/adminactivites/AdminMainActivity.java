package com.mmf.plantpal.activities.adminactivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.mmf.plantpal.R;
import com.mmf.plantpal.adapters.AdminPagerAdapter;
import com.mmf.plantpal.databinding.ActivityAdminMainBinding;
import com.mmf.plantpal.utilteis.MySharedPreferencesManager;

public class AdminMainActivity extends AppCompatActivity {

    ActivityAdminMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        setSupportActionBar(binding.toolbar);


        setupDrawerContent(binding.nvView);
        initViewPager();
        initView();



    }

    private void initViewPager(){
        AdminPagerAdapter pagerAdapter = new AdminPagerAdapter(AdminMainActivity.this);

        binding.viewPager.setAdapter(pagerAdapter);

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_fragment_plant:
                        binding.viewPager.setCurrentItem(0);
                        return true;

                    case R.id.navigation_fragment_accessory:
                        binding.viewPager.setCurrentItem(1);
                        return true;
                }
                return false;
            }
        });


        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.bottomNavigation.getMenu().getItem(position).setChecked(true);
            }
        });
    }


    private void initView(){
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.open_drawer) {
                    binding.drawerLayout.openDrawer(GravityCompat.END);
                    return true;
                }
                return true;
            }
        });
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.drawer_menu_users_user_details:
                                startActivity(new Intent(AdminMainActivity.this, UsersActivity.class));
                                selectDrawerItem(menuItem);
                                break;



                            case R.id.drawer_menu_users_feedback:
                                startActivity(new Intent(AdminMainActivity.this, UsersFeedbackActivity.class));
                                selectDrawerItem(menuItem);
                                break;


                            case R.id.drawer_menu_logout:
                                logout();
                                break;

                        }
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }




    public void selectDrawerItem(MenuItem menuItem) {
        binding.drawerLayout.closeDrawers();
    }



    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawer(GravityCompat.END);
        } else if (binding.viewPager.getCurrentItem() > 0) {
            binding.viewPager.setCurrentItem(0);
        } else {
            super.onBackPressed();
        }
    }



    private void logout() {
        MySharedPreferencesManager.logout(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }




}