package com.example.tictactoe.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tictactoe.databinding.ActivityRankingBinding;
import com.example.tictactoe.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity {
    private ActivityRankingBinding binding;
    private List<User> usersList;
    private FirebaseFirestore db;
    private RankingAdapter rankingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRankingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        rankingAdapter = new RankingAdapter();
        usersList = new ArrayList<>();

        db.collection("users")
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User currentUser = new User(document.get("name").toString(), Integer.parseInt(document.get("score").toString()), Integer.parseInt(document.get("partidasJugadas").toString()));
                                usersList.add(currentUser);
                            }
                            updateAdapter();
                        } else {
                            Toast.makeText(RankingActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(rankingAdapter);
    }

    private void updateAdapter() {
        rankingAdapter.setData(usersList);
    }
}