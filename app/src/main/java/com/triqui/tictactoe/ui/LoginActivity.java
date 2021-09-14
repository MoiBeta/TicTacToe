package com.triqui.tictactoe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.triqui.tictactoe.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;
    String email, password;
    boolean tryLogin = false;

    @Override
    protected void onStart() {
        super.onStart();
        //Comprobamos si previamente el usuario ya ha iniciado sesión en
        // este dispositivo
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        changeVisibility(true);
        firebaseAuth = FirebaseAuth.getInstance();
        eventos();
    }

    private void eventos() {
        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = binding.editTextEmail.getText().toString();
                password = binding.editTextPassword.getText().toString();

                if(email.isEmpty()){
                    binding.editTextEmail.setError("El email es necesario para continuar");
                } else if(password.isEmpty()){
                    binding.editTextPassword.setError("La contraseña es necesaria para continuar");
                } else{
                    tryLogin = true;
                    changeVisibility(false);
                    loginUser();
                }
            }
        });

        binding.textViewRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(i);
            }
        });
    }

    private void loginUser() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            //Almacenar la informacion del usuario en Firestore
            // TODO

            // Navegar hacia la siguiente pantalla de navegación
            Intent i = new Intent(LoginActivity.this, FindGameActivity.class);
            startActivity(i);
        } else{
            if(tryLogin) {
                changeVisibility(true);
                binding.editTextPassword.setError("Email y/o Contraseña incorrecto");
                binding.editTextPassword.requestFocus();
            }
        }
    }

    private void changeVisibility(boolean showForm) {
        binding.progressBarLogin.setVisibility(showForm ? View.GONE : View.VISIBLE);
        binding.formLogin.setVisibility(showForm ? View.VISIBLE : View.GONE);
    }
}