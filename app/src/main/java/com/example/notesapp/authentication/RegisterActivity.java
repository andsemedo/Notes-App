package com.example.notesapp.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesapp.MainActivity;
import com.example.notesapp.R;
import com.example.notesapp.SplashScreen;
import com.example.notesapp.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText regFirst, regLast, regEmail, regPassword;
    Button regBtn;
    TextView regEntrar;

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    String nome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regFirst = findViewById(R.id.regFirst);
        regLast = findViewById(R.id.regLast);
        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        regBtn = findViewById(R.id.regBtn);
        regEntrar = findViewById(R.id.regEntrar);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Write a message to the database




        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName = regFirst.getText().toString();
                String lastName = regLast.getText().toString();
                String email = regEmail.getText().toString();
                String password = regPassword.getText().toString();

                if(firstName.isEmpty()) {
                    regFirst.setError("Este campo é obrigatório!");
                    regFirst.requestFocus();
                    return;
                }
                if(lastName.isEmpty()) {
                    regLast.setError("Este campo é obrigatório!");
                    regLast.requestFocus();
                    return;
                }
                if(email.isEmpty()) {
                    regEmail.setError("Este campo é obrigatório!");
                    regEmail.requestFocus();
                    return;
                }
                if(password.isEmpty()) {
                    regPassword.setError("Este campo é obrigatório!");
                    regPassword.requestFocus();
                    return;
                }
                if(password.length() < 6) {
                    regPassword.setError("Minímo 6 caracteres!");
                    regPassword.requestFocus();
                    return;
                }

                nome = firstName + " " + lastName;

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success
                                    User user = new User(nome, "");
                                    myRef = database.getReference("accounts");
                                    myRef.child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "sucesso", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    Toast.makeText(RegisterActivity.this,"Registo feito com sucesso!", Toast.LENGTH_SHORT).show();
                                    sendEmailVerification();
                                    //Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                    //startActivity(i);
                                    //finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    //Toast.makeText(RegisterActivity.this, "Falha ao fazer o registo!",Toast.LENGTH_LONG).show();
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        regEmail.setError("Este email não é válido!");
                                        regEmail.requestFocus();
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutRegister), "Uma conta já existe com este email", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        });

            }
        });

        regEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

    }

    private void sendEmailVerification() {

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutRegister), "Foi-lhe enviado um email de verificação, verique e faça o login!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    mAuth.signOut();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }
                    }, 5000);

                }
            });
        } else {
            Toast.makeText(RegisterActivity.this,"Falha ao enviar o email de verificação!", Toast.LENGTH_SHORT).show();

        }

    }

}