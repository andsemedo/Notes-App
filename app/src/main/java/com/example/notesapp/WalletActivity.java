package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesapp.classes.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class WalletActivity extends AppCompatActivity {

    CardView buyTheme, makeDeposit;

    TextView walletOwner, textViewBalance;
    AlertDialog dialogMakeDeposit;

    private FirebaseAuth mAuth;
    private View view;

    String userId;
    double currentBalance;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);


        buyTheme = findViewById(R.id.buyTheme);
        makeDeposit = findViewById(R.id.makeDeposit);
        walletOwner = findViewById(R.id.walletOwner);
        textViewBalance = findViewById(R.id.textViewBalance);

        mAuth = FirebaseAuth.getInstance();

        String userEmail = mAuth.getCurrentUser().getEmail().toString();
        userId = mAuth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("accounts").child(userId);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userClass = snapshot.getValue(User.class);
                walletOwner.setText(userClass.getNome());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        loadBalance();

        makeDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogMakeDeposit == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(WalletActivity.this);
                    view = LayoutInflater.from(getApplicationContext()).inflate(
                            R.layout.layout_deposit,
                            (ViewGroup) findViewById(R.id.layoutDepositContainer)
                    );

                    builder.setView(view);
                    dialogMakeDeposit = builder.create();
                    if(dialogMakeDeposit.getWindow() != null) {
                        dialogMakeDeposit.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }

                    view.findViewById(R.id.btnMakeDeposit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            makeDeposit();
                            dialogMakeDeposit.dismiss();

                        }
                    });

                    dialogMakeDeposit.show();
                }
            }
        });

        buyTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ThemeActivity.class));
            }
        });
    }

    private void loadBalance() {
        myRef = database.getReference("wallet").child(userId).child("balance");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String balance = snapshot.getValue(String.class);
                if(balance !=null) {
                    currentBalance = Double.parseDouble(balance);
                } else {
                    currentBalance = 0.0;
                }

                textViewBalance.setText(formatCurrency(currentBalance));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeDeposit() {
        final EditText inputValueNumber = view.findViewById(R.id.inputValueNumber);
        final EditText inputCardNumber = view.findViewById(R.id.inputCardNumber);
        final EditText inputExpiryDate = view.findViewById(R.id.inputExpiryDate);
        final EditText inputCvvNumber = view.findViewById(R.id.inputCvvNumber);
        final EditText inputOwnerName = view.findViewById(R.id.inputOwnerName);

        if(inputValueNumber.getText().toString().isEmpty()) {
            inputValueNumber.setError("Este campo é obrigatório!");
            inputValueNumber.requestFocus();
            return;
        }
        if(inputCardNumber.getText().toString().isEmpty()) {
            inputCardNumber.setError("Este campo é obrigatório!");
            inputCardNumber.requestFocus();
            return;
        }
        if(inputExpiryDate.getText().toString().isEmpty()) {
            inputExpiryDate.setError("Este campo é obrigatório!");
            inputExpiryDate.requestFocus();
            return;
        }
        if(inputCvvNumber.getText().toString().isEmpty()) {
            inputCvvNumber.setError("Este campo é obrigatório!");
            inputCvvNumber.requestFocus();
            return;
        }
        if(inputOwnerName.getText().toString().length() < 6) {
            inputOwnerName.setError("Minímo 6 caracteres!");
            inputOwnerName.requestFocus();
            return;
        }

        if(inputCardNumber.getText().length() != 16 ) {
            inputCardNumber.setError("Tem que ser 16 números");
            inputCardNumber.requestFocus();
            return;
        }

        if(inputCvvNumber.getText().length() != 3) {
            inputCvvNumber.setError("Tem que ser 3 números");
            inputCvvNumber.requestFocus();
            return;
        }

        double valueToDeposit = Double.parseDouble(inputValueNumber.getText().toString());

        myRef = database.getReference("wallet").child(userId).child("balance");

        double total = valueToDeposit+currentBalance;

        myRef.setValue(String.valueOf(total)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Deposito feito com sucesso", Toast.LENGTH_SHORT).show();

            }

        });


    }

    private String formatCurrency(double value) {
        Locale localeCV = new Locale("pt", "cv");
        NumberFormat format = NumberFormat.getCurrencyInstance(localeCV);
        return format.format(value);
    }
}