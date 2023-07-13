package com.example.notesapp.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesapp.MainActivity;
import com.example.notesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button loginBtn;
    TextView loginRegistar, forgotPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginRegistar = findViewById(R.id.loginRegistar);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        forgotPassword = findViewById(R.id.forgotPassword);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("accounts");



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                if(email.isEmpty()) {
                    loginEmail.setError("Este campo é obrigatório!");
                    loginEmail.requestFocus();
                    return;
                }
                if(password.isEmpty()) {
                    loginPassword.setError("Este campo é obrigatório!");
                    loginPassword.requestFocus();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    checkEmailVerification();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutLogin), "Email ou palavra-passe incorretos!", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutLogin), "Email ou palavra-passe incorretos!", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                    } catch (FirebaseNetworkException e) {
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutLogin), "Verifique a conexão com a internet", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        loginRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }

    private void checkEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user.isEmailVerified() == true ) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutLogin), "Verique o seu email primeiro", Snackbar.LENGTH_SHORT);
            snackbar.show();
            mAuth.signOut();
        }
    }

}