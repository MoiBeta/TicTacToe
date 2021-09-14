package com.triqui.tictactoe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.triqui.tictactoe.databinding.ActivityRegistroBinding;
import com.triqui.tictactoe.model.User;

public class RegistroActivity extends AppCompatActivity {
    private ActivityRegistroBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    String name, email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        changeVisibility(true);
        eventos();
    }

    private void changeVisibility(boolean showForm) {
        binding.progressBarRegistro.setVisibility(showForm ? View.GONE : View.VISIBLE);
        binding.formRegistro.setVisibility(showForm ? View.VISIBLE : View.GONE);
    }

    private void eventos() {
        binding.buttonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = binding.editTextName.getText().toString().trim();
                email = binding.editTextEmail.getText().toString().trim();
                password = binding.editTextPassword.getText().toString().trim();
                if (name.isEmpty()) {
                    binding.editTextName.setError("El nombre es necesario para continuar");
                } else if (name.length() > 6) {
                    binding.editTextName.setError("El nickname no puede ser mayor a 6 carateres");
                } else if (email.isEmpty()) {
                    binding.editTextEmail.setError("El email es necesario para continuar");
                } else if (password.isEmpty()) {
                    binding.editTextPassword.setError("La contraseña es necesaria para continuar");
                } else {
                    changeVisibility(false);
                    createUser();
                }
            }
        });
    }

    private void createUser() {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUserInterface(user);
                        } else {
                            Toast.makeText(RegistroActivity.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                            updateUserInterface(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistroActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInterface(FirebaseUser user) {
        if(user != null){
            //Almacenar la informacion del usuario en Firestore
            User newUser = new User (name, 0 ,0);
            db.collection("users")
                    .document(user.getUid())
                    .set(newUser)
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Navegar hacia la siguiente pantalla de navegación
                            Intent i = new Intent(RegistroActivity.this, FindGameActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });


        } else{
            changeVisibility(true);
            binding.editTextPassword.setError("Email, Nombre y/o Contraseña incorrecto");
            binding.editTextPassword.requestFocus();
        }
    }
}