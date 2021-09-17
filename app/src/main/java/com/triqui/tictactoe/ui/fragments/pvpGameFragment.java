package com.triqui.tictactoe.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.triqui.tictactoe.R;
import com.triqui.tictactoe.app.Constants;
import com.triqui.tictactoe.databinding.FragmentPvpGameBinding;
import com.triqui.tictactoe.model.User;
import com.triqui.tictactoe.ui.Jugada;

import java.util.ArrayList;
import java.util.List;

public class pvpGameFragment extends Fragment {

    private FragmentPvpGameBinding binding;
    private List<ImageView> casillas;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    String uid, jugadaId, playerOneName = "", playerTwoName = "", ganadorId = "";
    Jugada jugada;
    ListenerRegistration listenerJugadas = null;
    View.OnClickListener listener;
    String jugadorName;
    User userPlayer1, userPlayer2;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentPvpGameBinding.inflate(inflater, container, false);
        initViews();
        initGame();
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                casillaSeleccionada(view);
            }
        };
        setListeners();
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initGame() {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        Bundle extras = getActivity().getIntent().getExtras();
        jugadaId = extras.getString(Constants.EXTRAS_JUGADA_ID);
    }

    private void initViews() {
        casillas = new ArrayList<>();
        casillas.add(binding.imageView0);
        casillas.add(binding.imageView1);
        casillas.add(binding.imageView2);
        casillas.add(binding.imageView3);
        casillas.add(binding.imageView4);
        casillas.add(binding.imageView5);
        casillas.add(binding.imageView6);
        casillas.add(binding.imageView7);
        casillas.add(binding.imageView8);

    }

    @Override
    public void onStart() {
        super.onStart();
        jugadaListener();
    }

    private void jugadaListener() {
        listenerJugadas = db.collection("jugadas")
                .document(jugadaId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Toast.makeText(getActivity(), "Error al obtener los datos de la partida", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String source = value != null
                                && value.getMetadata().hasPendingWrites() ? "Local" : "Server";

                        if(value.exists() && source.equals("Server")){
                            jugada = value.toObject(Jugada.class);
                            if (playerOneName.isEmpty() || playerTwoName.isEmpty()){
                                //obtener los nombre de usuario y setearlos en estos textos
                                getPlayerNames();
                            }
                            updateUI();
                        }
                        updatePlayersUI();
                    }

                });
    }

    private void updatePlayersUI(){
        if (jugada.isTurnoJugadorUno()) {
            binding.textviewPlayerOne.setTextColor(getResources().getColor(R.color.dark_green));
            binding.textviewPlayerTwo.setTextColor(getResources().getColor(R.color.gray));
        } else {
            binding.textviewPlayerOne.setTextColor(getResources().getColor(R.color.gray));
            binding.textviewPlayerTwo.setTextColor(getResources().getColor(R.color.dark_green));
        }

        if(!jugada.getGanadorId().isEmpty()){
            ganadorId = jugada.getGanadorId();
            showDialogGameOver();
        }
    }

    private void updateUI() {
        for(int i = 0; i<9; i++){
            int casilla = jugada.getCeldasSeleccionadas().get(i);
            if(casilla == 0){
                casillas.get(i).setImageResource(R.drawable.ic_empty_square);
            } else if(casilla == 1){
                casillas.get(i).setImageResource(R.drawable.ic_player_one);
            } else{
                casillas.get(i).setImageResource(R.drawable.ic_player_two);
            }
        }
    }

    private void getPlayerNames() {
        //Obtener el nombre del player 1
        db.collection("users")
                .document(jugada.getJugadorUnoId())
                .get()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userPlayer1 = documentSnapshot.toObject(User.class);
                        playerOneName = documentSnapshot.get("name").toString();
                        binding.textviewPlayerOne.setText(playerOneName);
                        if(jugada.getJugadorUnoId().equals(uid)){
                            jugadorName = playerOneName;
                        }
                    }
                });
        //Obtener el nombre del player 2
        db.collection("users")
                .document(jugada.getJugadorDosId())
                .get()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userPlayer2 = documentSnapshot.toObject(User.class);
                        playerTwoName = documentSnapshot.get("name").toString();
                        binding.textviewPlayerTwo.setText(playerTwoName);
                        if(jugada.getJugadorDosId().equals(uid)){
                            jugadorName = playerTwoName;
                        }
                    }
                });


    }

    public void casillaSeleccionada(View view){
        if(!jugada.getGanadorId().isEmpty()){
            Toast.makeText(getActivity(), "La partida ha terminado", Toast.LENGTH_SHORT).show();
        } else{
            if (jugada.isTurnoJugadorUno() && jugada.getJugadorUnoId().equals(uid)) {
                //Está jugando el jugador 1
                actualizarJugada(view.getTag().toString());
            } else if (!jugada.isTurnoJugadorUno() && jugada.getJugadorDosId().equals(uid)) {
                //Está jugando el jugador 2
                actualizarJugada(view.getTag().toString());
            } else {
                Toast.makeText(getActivity(), "No es tu turno aún", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void actualizarJugada(String numeroCasilla) {
        int posicionCasilla = Integer.parseInt(numeroCasilla);

        if(jugada.getCeldasSeleccionadas().get(posicionCasilla) != 0){
            Toast.makeText(getActivity(), "Seleccion una casilla libre", Toast.LENGTH_SHORT).show();
        } else {
            if (jugada.isTurnoJugadorUno()) {
                casillas.get(posicionCasilla).setImageResource(R.drawable.ic_player_one);
                jugada.getCeldasSeleccionadas().set(posicionCasilla, 1);
            } else {
                casillas.get(posicionCasilla).setImageResource(R.drawable.ic_player_two);
                jugada.getCeldasSeleccionadas().set(posicionCasilla, 2);
            }
            if (existeEmpate()) {
                jugada.setGanadorId("EMPATE");
                Toast.makeText(getActivity(), "Empate!", Toast.LENGTH_SHORT).show();
            } else if (existeSolución()) {
                jugada.setGanadorId(uid);
            } else{
                cambioTurno();
            }

            //Actualizar en firestore los datos de la jugada
            db.collection("jugadas")
                    .document(jugadaId)
                    .set(jugada)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void cambioTurno() {
        //Cambio de turno
        jugada.setTurnoJugadorUno(!jugada.isTurnoJugadorUno());
    }

    private boolean existeSolución(){
        boolean existe = false;

            List<Integer> selectedCells = jugada.getCeldasSeleccionadas();
            if(selectedCells.get(0) == selectedCells.get(1)
                    && selectedCells.get(1) == selectedCells.get(2)
                    && selectedCells.get(2) != 0){
                existe = true;
            } else if(selectedCells.get(3) == selectedCells.get(4)
                    && selectedCells.get(4) == selectedCells.get(5)
                    && selectedCells.get(5) != 0){
                existe = true;
            } else if(selectedCells.get(6) == selectedCells.get(7)
                    && selectedCells.get(7) == selectedCells.get(8)
                    && selectedCells.get(8) != 0){
                existe = true;
            } else if(selectedCells.get(0) == selectedCells.get(3)
                    && selectedCells.get(3) == selectedCells.get(6)
                    && selectedCells.get(6) != 0){
                existe = true;
            } else if(selectedCells.get(1) == selectedCells.get(4)
                    && selectedCells.get(4) == selectedCells.get(7)
                    && selectedCells.get(7) != 0){
                existe = true;
            } else if(selectedCells.get(3) == selectedCells.get(5)
                    && selectedCells.get(5) == selectedCells.get(8)
                    && selectedCells.get(8) != 0){
                existe = true;
            } else if(selectedCells.get(0) == selectedCells.get(4)
                    && selectedCells.get(4) == selectedCells.get(8)
                    && selectedCells.get(8) != 0){
                existe = true;
            } else if(selectedCells.get(2) == selectedCells.get(4)
                    && selectedCells.get(4) == selectedCells.get(6)
                    && selectedCells.get(6) != 0){
                existe = true;
            }
        return existe;
    }

    public boolean existeEmpate(){
    boolean existe = false;
    //Empate
    boolean hayCasillaLibre = false;
        for (int i = 0; i<9; i++){
            if(jugada.getCeldasSeleccionadas().get(i) == 0){
                hayCasillaLibre = true;
                break;
            }
        }
        if(!hayCasillaLibre){
            //Empate
            existe = true;
        }
        return existe;
    }

    private void setListeners() {
        binding.imageView0.setOnClickListener(listener);
        binding.imageView1.setOnClickListener(listener);
        binding.imageView2.setOnClickListener(listener);
        binding.imageView3.setOnClickListener(listener);
        binding.imageView4.setOnClickListener(listener);
        binding.imageView5.setOnClickListener(listener);
        binding.imageView6.setOnClickListener(listener);
        binding.imageView7.setOnClickListener(listener);
        binding.imageView8.setOnClickListener(listener);
    }

    private void showDialogGameOver(){
        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_game_over, null);
        TextView tvPuntos = v.findViewById(R.id.textViewPuntos);
        TextView tvInfo = v.findViewById(R.id.textViewInformación);
        LottieAnimationView gameOverAnimation = v.findViewById(R.id.animation_view);
        builder.setView(v)
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                });

        if (ganadorId.equals("EMPATE")){
            actualizarPuntos(1);
            tvInfo.setText(jugadorName + " has empatado!");
            tvPuntos.setText("+1 punto");
        } else if (ganadorId.equals(uid)){
            actualizarPuntos(3);
            tvInfo.setText(jugadorName + " has ganado!");
            tvPuntos.setText("+3 puntos");
        }else{
            actualizarPuntos(0);
            tvInfo.setText(jugadorName + " has perdido!");
            tvPuntos.setText("0 puntos");
            gameOverAnimation.setAnimation("thumbs_down_animation.json");
        }

        gameOverAnimation.playAnimation();
// 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle("Game Over");
        builder.setCancelable(false);

// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void actualizarPuntos(int puntosObtenidos) {
        User jugadorActualizar = null;
        if(uid.equals(jugada.getJugadorUnoId())){
            userPlayer1.setScore(userPlayer1.getScore() + puntosObtenidos);
            userPlayer1.setPartidasJugadas(userPlayer1.getPartidasJugadas() + 1);
            jugadorActualizar = userPlayer1;
        }else if(uid.equals(jugada.getJugadorDosId())){
            userPlayer2.setScore(userPlayer2.getScore() + puntosObtenidos);
            userPlayer2.setPartidasJugadas(userPlayer2.getPartidasJugadas() + 1);
            jugadorActualizar = userPlayer2;
        }

        db.collection("users")
                .document(uid)
                .set(jugadorActualizar)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onStop() {
        if(listenerJugadas != null){
            listenerJugadas.remove();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}