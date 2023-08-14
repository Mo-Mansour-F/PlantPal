package com.mmf.plantpal.activities.adminactivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mmf.plantpal.R;
import com.mmf.plantpal.adapters.FeedbackAdapter;
import com.mmf.plantpal.databinding.ActivityUsersFeedbackBinding;
import com.mmf.plantpal.models.FeedbackMessage;
import com.mmf.plantpal.utilteis.MyFireBaseReferences;

import java.util.ArrayList;
import java.util.List;

public class UsersFeedbackActivity extends AppCompatActivity {
    ActivityUsersFeedbackBinding binding;

    FeedbackAdapter feedbackAdapter;
    List<FeedbackMessage> feedbackMessageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getString(R.string.str_feedback));
        }


        initRecyclerView();
        getUsersFeedback();
    }


    private void initRecyclerView(){
        feedbackAdapter = new FeedbackAdapter(this);
        binding.feedbackRecyclerView.setHasFixedSize(true);
        binding.feedbackRecyclerView.setAdapter(feedbackAdapter);
    }



    private void getUsersFeedback(){
        MyFireBaseReferences
                .getFeedbackReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        feedbackMessageList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            FeedbackMessage feedbackMessage = child.getValue(FeedbackMessage.class);
                            feedbackMessageList.add(feedbackMessage);
                        }


                        feedbackAdapter.setFeedbackMessageList(feedbackMessageList);


                        if (feedbackMessageList.size() == 0){
                            binding.progressHorizontal.setVisibility(View.GONE);
                            binding.state.setVisibility(View.VISIBLE);
                            binding.state.setText(getString(R.string.str_no_feedback));
                            binding.progressHorizontalLayout.setVisibility(View.VISIBLE);
                        }else {
                            binding.progressHorizontalLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}