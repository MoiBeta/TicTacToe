package com.triqui.tictactoe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.triqui.tictactoe.app.Constants;
import com.triqui.tictactoe.databinding.ActivityFindGameBinding;

public class FindGameActivity extends AppCompatActivity {
    private ActivityFindGameBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private String jugadaId = "";
    private ListenerRegistration listenerRegistration = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intProgressBar();
        initFirebase();
        eventos();
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();
        assert user != null;
        uid = user.getUid();
    }

    private void eventos() {
        binding.buttonJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMenuVisibility(false);
                buscarPartida();
            }
        });

        binding.buttonRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMenuVisibility(false);
                Intent i = new Intent(FindGameActivity.this, RankingActivity.class);
                startActivity(i);
            }
        });

        binding.btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent i = new Intent(FindGameActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void buscarPartida() {
        binding.textViewLoading.setText("Buscando partida libre");
        binding.animationView.setAnimation("game_loading.json");
        binding.animationView.playAnimation();

        db.collection("jugadas")
                .whereEqualTo("jugadorDosId", "")
                .get()
                .addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().size() == 0){
                            crearNuevaPartida();
                        } else {
                            boolean encontrado = false;
                            for(DocumentSnapshot docJugada : task.getResult().getDocuments()){
                                if(!docJugada.get("jugadorUnoId").equals(uid)) {
                                    encontrado = true;
                                    jugadaId = docJugada.getId();
                                    Jugada jugada = docJugada.toObject(Jugada.class);
                                    assert jugada != null;
                                    jugada.setJugadorDosId(uid);

                                    db.collection("jugadas")
                                            .document(jugadaId)
                                            .set(jugada)
                                            .addOnSuccessListener(FindGameActivity.this, new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    binding.textViewLoading.setText("Partida libre encontrada! Comenzando la partida...");
                                                    binding.animationView.setRepeatCount(0);
                                                    binding.animationView.setAnimation("checked_animation.json");
                                                    binding.animationView.playAnimation();
                                                    final Handler handler = new Handler();
                                                    final Runnable r = new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            startGame();
                                                        }
                                                    };
                                                    handler.postDelayed(r, 3000);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            changeMenuVisibility(true);
                                            Toast.makeText(FindGameActivity.this, "Hubo un error al entrar en la partida", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(FindGameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                }
                                if(!encontrado) crearNuevaPartida();
                            }
                        }
                    }
                });
    }

    private void crearNuevaPartida() {
        binding.textViewLoading.setText("Creando nueva partida");
        Jugada nuevaJugada = new Jugada(uid);

        db.collection("jugadas")
                .add(nuevaJugada)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        jugadaId = documentReference.getId();
                        esperarJugador();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                changeMenuVisibility(true);
                Toast.makeText(FindGameActivity.this, "Error creando nueva partida", Toast.LENGTH_SHORT).show();
                Toast.makeText(FindGameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void esperarJugador() {
        binding.textViewLoading.setText("Esperando a otro jugador");
        listenerRegistration = db.collection("jugadas")
                .document(jugadaId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        if(value.get("jugadorDosId") != "") {
                            binding.animationView.setRepeatCount(0);
                            binding.animationView.setAnimation("checked_animation.json");
                            binding.animationView.playAnimation();
                            binding.textViewLoading.setText("Un jugador nuevo a llegado!! La partida está por iniciar.");
                            final Handler handler = new Handler();
                            final Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    startGame();
                                }
                            };
                            handler.postDelayed(r, 3000);
                        }
                    }
                });
    }

    private void startGame() {
        if(listenerRegistration != null){
            listenerRegistration.remove();
        }
        Intent i = new Intent(FindGameActivity.this, GameActivity.class);
        i.putExtra(Constants.EXTRAS_JUGADA_ID, jugadaId);
        startActivity(i);
        jugadaId = "";
    }

    private void intProgressBar() {
        binding.progressBar.setIndeterminate(true);
        binding.textViewLoading.setText("Cargando...");

        changeMenuVisibility(true);
    }

    private void changeMenuVisibility(boolean showMenu) {
        binding.layoutProgressbar.setVisibility(showMenu ? View.GONE : View.VISIBLE);
        binding.menuJuego.setVisibility(showMenu ? View.VISIBLE : View.GONE);
        binding.btnExit.setVisibility(showMenu ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(jugadaId != ""){
            changeMenuVisibility(false);
            esperarJugador();
        } else{
            changeMenuVisibility(true);
        }
    }

    @Override
    protected void onStop() {
        if(listenerRegistration != null){
            listenerRegistration.remove();
        }
        if(!jugadaId.equals("")){
            db.collection("jugadas")
                    .document(jugadaId)
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            jugadaId = "";
                        }
                    });
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }
}