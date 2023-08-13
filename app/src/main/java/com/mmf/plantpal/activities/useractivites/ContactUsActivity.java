package com.mmf.plantpal.activities.useractivites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.ActivityContactUsBinding;
import com.mmf.plantpal.models.ContactMessage;
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


        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getString(R.string.str_contact_us));
        }


        initFromUserInfo();
        initView();
    }


    private void initFromUserInfo() {

        String fromName = getString(R.string.str_name) +
                " : " +
                user.getName();

        String fromEmail = getString(R.string.hint_email) +
                " : " +
                user.getEmail();


        binding.fromName.setText(fromName);
        binding.fromEmail.setText(fromEmail);
    }


    private void initView() {
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmptyFields()) return;

                sendMessage();
            }
        });
    }


    private boolean checkEmptyFields() {
        String message = binding.messageEt.getText().toString().trim();
        if (message.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.messageEt);
            return true;
        }

        return false;
    }


    private void sendMessage() {
        MsgAlert.showProgress(this);
        String message = binding.messageEt.getText().toString().trim();

        DatabaseReference reference = MyFireBaseReferences
                .getContactMessageReference()
                .push();


        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setReferenceId(reference.getKey());
        contactMessage.setFromUserName(user.getName());
        contactMessage.setFromUserEmail(user.getEmail());
        contactMessage.setMessage(message);


        reference
                .setValue(contactMessage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MsgAlert.hideProgressDialog();
                        MsgAlert.showSuccessToast(ContactUsActivity.this, getString(R.string.message_send_successfully));
                        finish();
                    }
                });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}