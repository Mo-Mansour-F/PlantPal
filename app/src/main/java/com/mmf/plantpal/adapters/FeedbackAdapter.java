package com.mmf.plantpal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mmf.plantpal.R;
import com.mmf.plantpal.databinding.FeedbackLayoutBinding;
import com.mmf.plantpal.databinding.UserLayoutBinding;
import com.mmf.plantpal.models.FeedbackMessage;
import com.mmf.plantpal.models.User;

import java.util.ArrayList;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackVewHolder> {
    private Context context;
    private List<FeedbackMessage> feedbackMessageList;

    public FeedbackAdapter(Context context) {
        this.context = context;
        this.feedbackMessageList = new ArrayList<>();
    }


    public static class FeedbackVewHolder extends RecyclerView.ViewHolder {
        FeedbackLayoutBinding binding;

        public FeedbackVewHolder(View view) {
            super(view);
            binding = FeedbackLayoutBinding.bind(view);
        }
    }


    @NonNull
    @Override
    public FeedbackVewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_layout, parent, false);
        return new FeedbackVewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FeedbackVewHolder holder, final int position) {

        FeedbackMessage feedbackMessage = feedbackMessageList.get(position);

        holder.binding.userName.setText(feedbackMessage.getFromUserName());
        holder.binding.userEmail.setText(feedbackMessage.getFromUserEmail());
        holder.binding.userFeedback.setText(feedbackMessage.getFeedbackMessage());
        holder.binding.rating.setRating(feedbackMessage.getRate());
    }

    @Override
    public int getItemCount() {
        return feedbackMessageList.size();
    }


    public void setFeedbackMessageList(List<FeedbackMessage> feedbackMessageList) {
        this.feedbackMessageList = feedbackMessageList;
        notifyDataSetChanged();
    }
}



