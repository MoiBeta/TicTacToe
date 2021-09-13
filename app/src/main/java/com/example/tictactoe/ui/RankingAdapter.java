package com.example.tictactoe.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tictactoe.databinding.ItemRankingBinding;
import com.example.tictactoe.model.User;

import java.util.ArrayList;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {
    List<User> userList = new ArrayList<>();

    public RankingAdapter() {

    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RankingViewHolder viewHolder = new RankingViewHolder(ItemRankingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        User currentUser = userList.get(position);
        holder.tvName.setText(currentUser.getName());
        holder.tvScore.setText(String.valueOf(currentUser.getScore()));
        holder.tvGames.setText(String.valueOf(currentUser.getPartidasJugadas()));
    }

    @Override
    public int getItemCount() {
        if (userList.isEmpty()) {
            return 0;
        } else {
            return userList.size();
        }
    }

    public void setData(List<User> newList) {
        userList = newList;
        notifyDataSetChanged();
    }

    public class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvScore, tvGames;

        public RankingViewHolder(ItemRankingBinding binding) {
            super(binding.getRoot());
            tvName = binding.textViewName;
            tvScore = binding.textViewScore;
            tvGames = binding.textViewGamesPlayed;
        }
    }
}
