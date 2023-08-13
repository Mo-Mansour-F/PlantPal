package com.mmf.plantpal.activities.loginactivites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.window.SplashScreen;

import com.mmf.plantpal.BuildConfig;
import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.ActivitySplashScreenBinding;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {
    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());



        getVersionNumber();


        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.textfrombottom);
        Animation titleAnimation = AnimationUtils.loadAnimation(this, R.anim.fromtopanime);

        binding.appLogo.startAnimation(logoAnimation);
        binding.appTitle.startAnimation(titleAnimation);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                finish();
            }
        }, 3500);

    }


    private void getVersionNumber(){

        String currentVersionName = BuildConfig.VERSION_NAME;
        String version = String.format(Locale.US, "%s  %s", getString(R.string.str_version), currentVersionName);
        binding.appVersion.setText(version);
    }
}