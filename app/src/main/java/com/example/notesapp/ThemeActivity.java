package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.notesapp.classes.BuyedThemes;
import com.example.notesapp.classes.Notes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class ThemeActivity extends AppCompatActivity {

    private TextView btnBuyTheme1, btnBuyTheme2, btnBuyTheme3, btnBuyTheme4, btnBuyTheme5, btnBuyTheme6;

    GridLayout themeGriLayout;
    CardView cardViewTheme1;
    private FirebaseAuth mAuth;

    private String userId, themeUrl, themeColor;
    private double currentBalance, price;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private boolean isDispo;
    private int position;
    AlertDialog dialogBuyTheme;
    private final ArrayList<BuyedThemes> buyedThemesList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        mAuth = FirebaseAuth.getInstance();

        String userEmail = mAuth.getCurrentUser().getEmail().toString();
        userId = mAuth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();

        btnBuyTheme1 = findViewById(R.id.btnBuyTheme1);
        btnBuyTheme2 = findViewById(R.id.btnBuyTheme2);
        btnBuyTheme3 = findViewById(R.id.btnBuyTheme3);
        btnBuyTheme4 = findViewById(R.id.btnBuyTheme4);
        btnBuyTheme5 = findViewById(R.id.btnBuyTheme5);
        btnBuyTheme6 = findViewById(R.id.btnBuyTheme6);
        cardViewTheme1 = findViewById(R.id.cardViewTheme1);
        themeGriLayout = findViewById(R.id.themeGriLayout);

        alreadyBuyedThemes();

        loadAvailablesThemes();

        if (btnBuyTheme1.getText().toString().equals("Comprar")) {
            btnBuyTheme1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    price = 120;
                    themeUrl = "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_10.png?alt=media&token=7608c33e-cc3e-48d6-8744-8552b2a01033";
                    position = R.id.btnBuyTheme1;
                    themeColor = "light";

                    // verify if money is available
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

                            if (price > currentBalance) {
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.themeLayout), "Saldo insuficiente", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else {
                                openDialogAndBuyTheme(themeColor);
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }
        if (btnBuyTheme2.getText().toString().equals("Comprar")) {
            btnBuyTheme2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    price = 149;
                    themeUrl = "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_3.png?alt=media&token=e77e3a74-37eb-4f65-b858-4fae26741ff5";
                    position = R.id.btnBuyTheme2;
                    themeColor = "dark";

                    // verify if money is available
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

                            if (price > currentBalance) {
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.themeLayout), "Saldo insuficiente", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else {
                                openDialogAndBuyTheme(themeColor);

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }
        if (btnBuyTheme3.getText().toString().equals("Comprar")) {
            btnBuyTheme3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Click ok");
                    price = 169;
                    themeUrl = "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_4.png?alt=media&token=d41b512c-6a52-4526-b9b9-4a02d9913541";
                    position = R.id.btnBuyTheme3;
                    themeColor = "dark";

                    // verify if money is available
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

                            if (price > currentBalance) {
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.themeLayout), "Saldo insuficiente", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else {
                                openDialogAndBuyTheme(themeColor);

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }
        if (btnBuyTheme4.getText().toString().equals("Comprar")) {
            btnBuyTheme4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    price = 200;
                    themeUrl = "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_7.png?alt=media&token=dfea84e3-e54e-4470-9396-abf1669976d0";
                    position = R.id.btnBuyTheme4;
                    themeColor = "dark";

                    // verify if money is available
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

                            if (price > currentBalance) {
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.themeLayout), "Saldo insuficiente", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else {
                                openDialogAndBuyTheme(themeColor);

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }
        if (btnBuyTheme5.getText().toString().equals("Comprar")) {
            btnBuyTheme5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    price = 110;
                    themeUrl = "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbd_theme_16.jpg?alt=media&token=55df44fb-5ff7-4d21-90a7-368a80f45509";
                    position = R.id.btnBuyTheme5;
                    themeColor = "light";

                    // verify if money is available
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

                            if (price > currentBalance) {
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.themeLayout), "Saldo insuficiente", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else {
                                openDialogAndBuyTheme(themeColor);

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }
        if (btnBuyTheme6.getText().toString().equals("Comprar")) {
            btnBuyTheme6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    price = 149;
                    themeUrl = "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_18.jpg?alt=media&token=9e30ab6a-b51b-404c-8b77-81795b422a27";
                    position = R.id.btnBuyTheme6;
                    themeColor = "dark";

                    // verify if money is available
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

                            if (price > currentBalance) {
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.themeLayout), "Saldo insuficiente", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                //Toast.makeText(getApplicationContext(), "Saldo Indisponivel", Toast.LENGTH_SHORT).show();
                            } else {
                                openDialogAndBuyTheme(themeColor);

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }





    }

    private void loadAvailablesThemes() {
        final ImageView viewTheme1 = findViewById(R.id.view_theme_1);
        final ImageView viewTheme2 = findViewById(R.id.view_theme_2);
        final ImageView viewTheme3 = findViewById(R.id.view_theme_3);
        final ImageView viewTheme4 = findViewById(R.id.view_theme_4);
        final ImageView viewTheme5 = findViewById(R.id.view_theme_5);
        final ImageView viewTheme6 = findViewById(R.id.view_theme_6);

        Glide.with(getApplicationContext())
                        .load("https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_10.png?alt=media&token=7608c33e-cc3e-48d6-8744-8552b2a01033")
                                .centerCrop()
                                        .into(viewTheme1);

        Glide.with(getApplicationContext())
                        .load("https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_3.png?alt=media&token=e77e3a74-37eb-4f65-b858-4fae26741ff5")
                                .centerCrop()
                                        .into(viewTheme2);

        Glide.with(getApplicationContext())
                        .load("https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_4.png?alt=media&token=d41b512c-6a52-4526-b9b9-4a02d9913541")
                                .centerCrop()
                                        .into(viewTheme3);

        Glide.with(getApplicationContext())
                        .load("https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_7.png?alt=media&token=dfea84e3-e54e-4470-9396-abf1669976d0")
                                .centerCrop()
                                        .into(viewTheme4);

        Glide.with(getApplicationContext())
                        .load("https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbd_theme_16.jpg?alt=media&token=55df44fb-5ff7-4d21-90a7-368a80f45509")
                                .centerCrop()
                                        .into(viewTheme5);

        Glide.with(getApplicationContext())
                        .load("https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_18.jpg?alt=media&token=9e30ab6a-b51b-404c-8b77-81795b422a27")
                                .centerCrop()
                                        .into(viewTheme6);

/*
        Picasso.get().load(
                        "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_10.png?alt=media&token=7608c33e-cc3e-48d6-8744-8552b2a01033"
                )
                .into(viewTheme1);

        Picasso.get().load(
                        "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_3.png?alt=media&token=e77e3a74-37eb-4f65-b858-4fae26741ff5"
                )
                .into(viewTheme2);

        Picasso.get().load(
                        "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_4.png?alt=media&token=d41b512c-6a52-4526-b9b9-4a02d9913541"
                )
                .into(viewTheme3);

        Picasso.get().load(
                        "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_7.png?alt=media&token=dfea84e3-e54e-4470-9396-abf1669976d0"
                )
                .into(viewTheme4);

        Picasso.get().load(
                        "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbd_theme_16.jpg?alt=media&token=55df44fb-5ff7-4d21-90a7-368a80f45509"
                )
                .into(viewTheme5);


        Picasso.get().load(
                        "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_18.jpg?alt=media&token=9e30ab6a-b51b-404c-8b77-81795b422a27"
                )
                .into(viewTheme6);

 */
    }

    private void alreadyBuyedThemes() {
        myRef = database.getReference("myThemes").child(userId);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    System.out.println("item "+itemSnapshot);
                    BuyedThemes buyedThemes = itemSnapshot.getValue(BuyedThemes.class);
                    buyedThemesList.add(buyedThemes);
                }

                for (int i=0; i<buyedThemesList.size(); i++) {
                    if(buyedThemesList.get(i).getPos() == R.id.btnBuyTheme1) {
                        btnBuyTheme1.setVisibility(View.GONE);
                        TextView btnBuyedTheme1 = findViewById(R.id.btnBuyedTheme1);
                        btnBuyedTheme1.setVisibility(View.VISIBLE);
                    } else if(buyedThemesList.get(i).getPos() == R.id.btnBuyTheme2) {
                        btnBuyTheme2.setVisibility(View.GONE);
                        TextView btnBuyedTheme2 = findViewById(R.id.btnBuyedTheme2);
                        btnBuyedTheme2.setVisibility(View.VISIBLE);
                    } else if(buyedThemesList.get(i).getPos() == R.id.btnBuyTheme3) {
                        btnBuyTheme3.setVisibility(View.GONE);
                        TextView btnBuyedTheme3 = findViewById(R.id.btnBuyedTheme3);
                        btnBuyedTheme3.setVisibility(View.VISIBLE);
                    } else if(buyedThemesList.get(i).getPos() == R.id.btnBuyTheme4) {
                        btnBuyTheme4.setVisibility(View.GONE);
                        TextView btnBuyedTheme4 = findViewById(R.id.btnBuyedTheme4);
                        btnBuyedTheme4.setVisibility(View.VISIBLE);
                    } else if(buyedThemesList.get(i).getPos() == R.id.btnBuyTheme5) {
                        btnBuyTheme5.setVisibility(View.GONE);
                        TextView btnBuyedTheme5 = findViewById(R.id.btnBuyedTheme5);
                        btnBuyedTheme5.setVisibility(View.VISIBLE);
                    } else if(buyedThemesList.get(i).getPos() == R.id.btnBuyTheme6) {
                        btnBuyTheme6.setVisibility(View.GONE);
                        TextView btnBuyedTheme6 = findViewById(R.id.btnBuyedTheme6);
                        btnBuyedTheme6.setVisibility(View.VISIBLE);
                    }
                }
                System.out.println("List "+buyedThemesList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void openDialogAndBuyTheme(String color) {
        if(dialogBuyTheme == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ThemeActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_buy_theme,
                    (ViewGroup) findViewById(R.id.layoutBuyTheme)
            );

            builder.setView(view);
            dialogBuyTheme = builder.create();
            if(dialogBuyTheme.getWindow() != null) {
                dialogBuyTheme.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            view.findViewById(R.id.textYes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyTheme(color);
                    dialogBuyTheme.dismiss();
                }
            });

            view.findViewById(R.id.textNop).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuyTheme.dismiss();
                }
            });

            dialogBuyTheme.show();
        }
    }

    private void buyTheme(String color) {
        myRef = database.getReference("myThemes").child(userId).push();

        BuyedThemes buThemes = new BuyedThemes(themeUrl, position, color);

        myRef.setValue(buThemes).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    myRef = database.getReference("wallet").child(userId).child("balance");

                    double total = currentBalance-price;

                    myRef.setValue(String.valueOf(total)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(ThemeActivity.this, "Sucessfull", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }


}