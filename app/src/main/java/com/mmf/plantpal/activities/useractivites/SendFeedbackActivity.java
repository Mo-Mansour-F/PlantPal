package com.mmf.plantpal.activities.useractivites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.ActivitySendFeedbackBinding;
import com.mmf.plantpal.models.FeedbackMessage;
import com.mmf.plantpal.models.User;
import com.mmf.plantpal.utilteis.MsgAlert;
import com.mmf.plantpal.utilteis.MyFireBaseReferences;
import com.mmf.plantpal.utilteis.MySharedPreferencesManager;

public class SendFeedbackActivity extends AppCompatActivity {
    ActivitySendFeedbackBinding binding;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = MySharedPreferencesManager.getUserLoginDetails(this);



        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getString(R.string.str_feedback));
        }



        initView();
    }



    private void initView() {
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmptyFields()) return;

                sendFeedback();
            }
        });
    }


    private boolean checkEmptyFields() {
        String feedbackMsg = binding.feedbackEt.getText().toString().trim();
        float ratingFloat = binding.rating.getRating();

        if (feedbackMsg.length() == 0) {
            MsgAlert.showFieldError(getApplicationContext(), getString(R.string.error_required), binding.feedbackEt);
            return true;
        }

        if (ratingFloat == 0) {
            MsgAlert.showErrorToast(this, getString(R.string.error_rating_required));
            return true;
        }


        return false;
    }



    private void sendFeedback() {
        MsgAlert.showProgress(this);

        float ratingFloat = binding.rating.getRating();

        String feedbackMsg = binding.feedbackEt.getText().toString().trim();

        DatabaseReference reference = MyFireBaseReferences
                .getFeedbackReference()
                .push();


        FeedbackMessage feedbackMessage = new FeedbackMessage();
        feedbackMessage.setReferenceId(reference.getKey());
        feedbackMessage.setFromUserName(user.getName());
        feedbackMessage.setFromUserEmail(user.getEmail());
        feedbackMessage.setFeedbackMessage(feedbackMsg);
        feedbackMessage.setRate(ratingFloat);



        reference
                .setValue(feedbackMessage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MsgAlert.hideProgressDialog();
                        MsgAlert.showSuccessToast(SendFeedbackActivity.this, getString(R.string.message_send_successfully));
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