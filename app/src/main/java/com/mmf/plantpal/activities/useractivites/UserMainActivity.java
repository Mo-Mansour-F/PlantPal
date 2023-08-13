package com.mmf.plantpal.activities.useractivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.mmf.plantpal.R;
import com.mmf.plantpal.adapters.UserPagerAdapter;
import com.mmf.plantpal.databinding.ActivityUserMainBinding;
import com.mmf.plantpal.listerners.OnCartItemChangedListener;
import com.mmf.plantpal.models.User;
import com.mmf.plantpal.repository.DataRepository;
import com.mmf.plantpal.utilteis.MySharedPreferencesManager;

import java.util.Locale;

public class UserMainActivity extends AppCompatActivity implements OnCartItemChangedListener {
    ActivityUserMainBinding binding;


    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupDrawerContent(binding.nvView);
        initViewPager();
        initView();


        this.onOnCartItemChanged();

//        binding.nvView.addHeaderView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        initUserInfo();
    }

    private void initUserInfo() {
        user = MySharedPreferencesManager.getUserLoginDetails(this);

        if (user.getReferenceId() == null) {
            finish();
        }
        binding.userNameTv.setText(user.getName());
        binding.userEmailTv.setText(user.getEmail());


        binding.userInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserProfile();
            }
        });
    }


    private void initViewPager() {


        UserPagerAdapter userPagerAdapter = new UserPagerAdapter(UserMainActivity.this);

        binding.viewPager.setAdapter(userPagerAdapter);

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


                    case R.id.navigation_fragment_cart_item:
                        binding.viewPager.setCurrentItem(2);
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


    private void initView() {
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

                            case R.id.drawer_menu_profile:
                                showUserProfile();
                                break;

                            case R.id.drawer_menu_favorites:
                                startActivity(new Intent(UserMainActivity.this, FavoritesActivity.class));
                                break;


                            case R.id.drawer_menu_contact_us:
                                startActivity(new Intent(UserMainActivity.this, ContactUsActivity.class));
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

    private void showUserProfile() {
        Intent intent = new Intent(UserMainActivity.this, ProfileActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(UserMainActivity.this,
                binding.userImage, ViewCompat.getTransitionName(binding.userImage));
        startActivity(intent, options.toBundle());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    @Override
    public void onOnCartItemChanged() {
        int count = DataRepository.getItemList().size();
        BadgeDrawable badgeDrawable = binding.bottomNavigation.getOrCreateBadge(R.id.navigation_fragment_cart_item);
        badgeDrawable.setBadgeNumberLocale(Locale.US);
        badgeDrawable.setMaxCharacterCount(3);

        if (count == 0) {
            badgeDrawable.setVisible(false);
        } else {
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(count);
        }
    }
}