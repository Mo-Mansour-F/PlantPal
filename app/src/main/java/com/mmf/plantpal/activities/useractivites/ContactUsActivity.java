package com.mmf.plantpal.activities.useractivites;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.ActivityContactUsBinding;
import com.mmf.plantpal.models.FeedbackMessage;
import com.mmf.plantpal.models.User;
import com.mmf.plantpal.utilteis.MsgAlert;
import com.mmf.plantpal.utilteis.MyFireBaseReferences;
import com.mmf.plantpal.utilteis.MySharedPreferencesManager;

public class ContactUsActivity extends AppCompatActivity {
    ActivityContactUsBinding binding;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = MySharedPreferencesManager.getUserLoginDetails(this);

        //to add the title and the back btn to the toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getString(R.string.str_contact_us));
        }

        initView();
    }


    private void initView() {
        binding.btnOpenMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactMail();
            }
        });
    }


    private void openContactMail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        String[] to = {getString(R.string.str_contact_mail)};
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Test Subject");
        intent.putExtra(Intent.EXTRA_TEXT, "Test Message");

        try {
            startActivity(Intent.createChooser(intent, "Send Email"));
        } catch (Exception e) {
            MsgAlert.showErrorToast(this, getString(R.string.error_no_app));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}