package com.mmf.plantpal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.UserLayoutBinding;
import com.mmf.plantpal.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserVewHolder> {
    private Context context;
    private List<User> usersList;


    public UsersAdapter(Context context) {
        this.context = context;
        this.usersList = new ArrayList<>();
    }


    public static class UserVewHolder extends RecyclerView.ViewHolder {
        UserLayoutBinding binding;

        public UserVewHolder(View view) {
            super(view);
            binding = UserLayoutBinding.bind(view);
        }
    }


    @NonNull
    @Override
    public UserVewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false);
        return new UserVewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UserVewHolder holder, final int position) {

        // Get the User object at the current position
        User user = usersList.get(position);

        holder.binding.userName.setText(user.getName());
        holder.binding.userEmail.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    // Set the list of users and update the adapter.
    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }
}



