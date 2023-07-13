package com.example.notesapp.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.notesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailForgot;
    private TextView btnBackTOLogin;
    private Button btnRecoverPassword;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailForgot = findViewById(R.id.emailForgot);
        btnBackTOLogin = findViewById(R.id.btnBackTOLogin);
        btnRecoverPassword = findViewById(R.id.btnRecoverPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        btnRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = emailForgot.getText().toString();

                if(email.isEmpty()) {
                    emailForgot.setError("Preencha este campo");
                    emailForgot.requestFocus();
                } else {

                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutForgot), "Email de recuperação enviado com sucesso", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidCredentialsException e){
                                    emailForgot.setError("Este email não é válido");
                                    emailForgot.requestFocus();
                                }catch (FirebaseAuthInvalidUserException e){
                                    Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutForgot), "Email de recuperação enviado com sucesso", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                } catch (Exception e) {

                                }
                            }
                        }
                    });

                }

            }
        });

        btnBackTOLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

    }
}