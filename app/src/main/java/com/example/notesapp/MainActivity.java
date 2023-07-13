package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesapp.authentication.LoginActivity;
import com.example.notesapp.classes.Notes;
import com.example.notesapp.interfaces.RecyclerViewInterface;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {

    ImageView addNoteBtn, btnDeleteNote;
    TextView teste;
    private FirebaseAuth mAuth;

    RecyclerView allNotesRecyclerView;
    ValueEventListener eventListener;
    NoteAdapter noteAdapter;
    DatabaseReference noteRef;
    String userID;
    List<Notes> notesList;
    ConstraintLayout layoutNoteOptions, constraintDeleteNote;

    AlertDialog dialogDeleteNote;

    DatabaseReference myRef;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    Toolbar menuToolbar;

/*
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

 */

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();



        if(mAuth.getCurrentUser() != null) {

            userID = mAuth.getCurrentUser().getUid();

            System.out.println(userID);

            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            myRef = database.getReference("notes").child(userID);
            myRef.keepSynced(true);

            System.out.println(myRef);

            addNoteBtn = findViewById(R.id.addNoteBtn);
            allNotesRecyclerView = findViewById(R.id.allNotesRecyclerView);
            layoutNoteOptions = findViewById(R.id.layoutNoteOptions);
            btnDeleteNote = findViewById(R.id.btnDeleteNote);
            constraintDeleteNote = findViewById(R.id.constraintDeleteNote);
            menuToolbar = findViewById(R.id.menuToolbar);
            menuToolbar.setTitle("Minhas notas");
            setSupportActionBar(menuToolbar);
            /*
            drawerLayout = findViewById(R.id.drawerMenu);
            navigationView = findViewById(R.id.nav_view);

            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if(item.getItemId() == R.id.wallet) {
                        Toast.makeText(MainActivity.this, "Wallet clicked", Toast.LENGTH_SHORT).show();
                    } else if(item.getItemId() == R.id.themes) {
                        Toast.makeText(MainActivity.this, "Themes clicked", Toast.LENGTH_SHORT).show();
                    } else if(item.getItemId() == R.id.profile) {
                        Toast.makeText(MainActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show();
                    } else if(item.getItemId() == R.id.logout) {
                        Toast.makeText(MainActivity.this, "Logout clicked", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });

             */

            notesList = new ArrayList<>();

            allNotesRecyclerView.setLayoutManager(
                    new LinearLayoutManager(this)
            );

            noteAdapter = new NoteAdapter(this, notesList, this);
            allNotesRecyclerView.setAdapter(noteAdapter);

            Query query = myRef.orderByChild("date");

            query.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    notesList.clear();
                    for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                        Notes notes = itemSnapshot.getValue(Notes.class);
                        notesList.add(0, notes);
                    }
                    noteAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            addNoteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, NewNoteActivity.class));
                    //finish();
                }
            });

            EditText noteSearch = findViewById(R.id.noteSearch);

            noteSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(notesList.size() != 0) {
                        noteAdapter.searchNotes(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });


        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.layout_menu, menu);
        for(int i=0; i<menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if(menu.getItem(i).getTitle().toString().equals("Sair")) {
                SpannableString spannableString = new SpannableString(
                        menu.getItem(i).getTitle().toString()
                );
                spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableString.length(), 0);
                menuItem.setTitle(spannableString);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.wallet) {
            startActivity(new Intent(getApplicationContext(), WalletActivity.class));
            return true;
        } else if (id == R.id.themes) {
            startActivity(new Intent(getApplicationContext(), ThemeActivity.class));
            return true;
        } else if (id == R.id.profile) {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            return true;
        } else if (id == R.id.logout) {
            Toast.makeText(getApplicationContext(), "Logout selected", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNoteClick(int position) {
        Intent i = new Intent(MainActivity.this, NewNoteActivity.class);

        i.putExtra("noteTitle", notesList.get(position).getTitle());
        i.putExtra("noteKey", notesList.get(position).getId());
        i.putExtra("noteContent", notesList.get(position).getContent());
        i.putExtra("theme", notesList.get(position).getTheme());
        i.putExtra("themeColor", notesList.get(position).getThemeColor());
        i.putExtra("imageUrl", notesList.get(position).getImageURL());
        i.putExtra("isEdit", true);

        startActivity(i);
    }



    @Override
    public void onNoteLongClick(int position) {
        Toast.makeText(getApplicationContext(), "Long Press", Toast.LENGTH_SHORT).show();

        //Toast.makeText(getApplicationContext(), dialogDeleteNote.toString(), Toast.LENGTH_SHORT).show();

        if(dialogDeleteNote == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutNoteContainer)
            );

            builder.setView(view);
            dialogDeleteNote = builder.create();
            if(dialogDeleteNote.getWindow() != null) {
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteNote(position);
                    dialogDeleteNote.dismiss();
                    dialogDeleteNote = null;
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteNote.dismiss();
                    dialogDeleteNote = null;
                }
            });

            dialogDeleteNote.show();
        }

    }

    private void deleteNote(int pos) {
        Notes note = notesList.get(pos);
        String noteId = note.getId();

        myRef.child(noteId).removeValue();

        notesList.remove(pos);
        noteAdapter.notifyItemRemoved(pos);
        noteAdapter.notifyItemChanged(pos, notesList.size());
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }
}