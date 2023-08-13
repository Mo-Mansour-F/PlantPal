package com.mmf.plantpal.activities.adminactivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mmf.plantpal.R;
import com.mmf.plantpal.adapters.UsersAdapter;
import com.mmf.plantpal.databinding.ActivityUsersBinding;
import com.mmf.plantpal.models.User;
import com.mmf.plantpal.utilteis.Constants;
import com.mmf.plantpal.utilteis.MyFireBaseReferences;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
    ActivityUsersBinding binding;

    UsersAdapter usersAdapter;
    List<User> userList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getString(R.string.str_users));
        }



        initView();
        initRecyclerView();
        getUsers();
    }


    private void initView(){

    }


    private void initRecyclerView(){
        usersAdapter = new UsersAdapter(this);
        binding.usersRecyclerView.setHasFixedSize(true);

        binding.usersRecyclerView.setAdapter(usersAdapter);
    }


    private void getUsers(){
        MyFireBaseReferences
                .getUsersReference()
                .orderByChild("role")
                .equalTo(Constants.ROLE_USER)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();

                        for (DataSnapshot child : snapshot.getChildren()) {
                            User user = child.getValue(User.class);
                            userList.add(user);
                        }


                        usersAdapter.setUsersList(userList);


                        if (userList.size() == 0){
                            binding.progressHorizontal.setVisibility(View.GONE);
                            binding.state.setVisibility(View.VISIBLE);
                            binding.state.setText(getString(R.string.str_no_users));
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