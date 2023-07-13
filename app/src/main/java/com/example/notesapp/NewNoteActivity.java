package com.example.notesapp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.notesapp.classes.BuyedThemes;
import com.example.notesapp.classes.Notes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NewNoteActivity extends AppCompatActivity {

    ImageView addNoteBack, addNoteDone, noteImage, deleteNoteImage;
    EditText noteTitle, inputNote;
    CoordinatorLayout newNoteLayout;
    Boolean isEdit;
    String nKey, nTitle, nContent, author, imageUrl, audioUrl;

    LinearLayout layoutAssetsThemes;

    private String themeUrl, themeColor;

    private FirebaseAuth mAuth;
    private Drawable selectedTheme;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    ArrayList<String> buyedThemesArrayList;
    ArrayList<String> themeColorArrayList;

    BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    Uri selectedImageUri;
    ActivityResultLauncher<String> launcher;

    StorageReference storage = FirebaseStorage.getInstance().getReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);


        addNoteBack = findViewById(R.id.addNoteBack);
        addNoteDone = findViewById(R.id.addNoteDone);
        noteTitle = findViewById(R.id.noteTitle);
        inputNote = findViewById(R.id.inputNote);
        newNoteLayout = findViewById(R.id.newNoteLayout);
        noteImage = findViewById(R.id.noteImage);
        deleteNoteImage = findViewById(R.id.deleteNoteImage);

        // receiving data for update
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        nKey = getIntent().getStringExtra("noteKey");
        nTitle = getIntent().getStringExtra("noteTitle");
        nContent = getIntent().getStringExtra("noteContent");
        imageUrl = getIntent().getStringExtra("imageUrl");

        mAuth = FirebaseAuth.getInstance();


        if(isEdit) {
            noteTitle.setText(nTitle);
            inputNote.setText(nContent);

            themeUrl = getIntent().getStringExtra("theme");
            themeColor = getIntent().getStringExtra("themeColor");
            System.out.println("theme: "+themeUrl);

            if(imageUrl != null) {
                Glide.with(getApplicationContext()).load(imageUrl).into(noteImage);
                //Picasso.get().load(imageUrl).into(noteImage);
                noteImage.setVisibility(View.VISIBLE);
                deleteNoteImage.setVisibility(View.VISIBLE);
            } else {
                noteImage.setVisibility(View.GONE);
            }

        }

        addNoteBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewNoteActivity.this, MainActivity.class));
                finish();
            }
        });

        inputNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    addNoteDone.setVisibility(View.VISIBLE);
                } else {
                    if(noteTitle.getText().length() == 0) addNoteDone.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    addNoteDone.setVisibility(View.VISIBLE);
                } else {
                    if(inputNote.getText().length() == 0) addNoteDone.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {

                        noteImage.setImageURI(uri);
                        noteImage.setVisibility(View.VISIBLE);
                        deleteNoteImage.setVisibility(View.VISIBLE);
                        addNoteDone.setVisibility(View.VISIBLE);

                        final StorageReference storageRef = storage.child(System.currentTimeMillis() + "." + getFileExtension(uri));

                        storageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageUrl = uri.toString();
                                        System.out.println("Image url 1: "+imageUrl);
                                    }
                                });
                            }
                        });

                        selectedImageUri = uri;
                    }
                }
        );

        addNoteDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEdit) {
                    updateNote();
                } else {
                    saveNote();
                }

            }
        });

        deleteNoteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteImage.setImageURI(null);
                noteImage.setVisibility(View.GONE);
                deleteNoteImage.setVisibility(View.GONE);
                imageUrl = "";
                addNoteDone.setVisibility(View.VISIBLE);
            }
        });

        loadThemesAssets();
        loadBuyedThemes();
        initAssets();
        setNoteTheme();

    }

    private void saveNote() {
        if(!noteTitle.getText().toString().trim().isEmpty() || !inputNote.getText().toString().trim().isEmpty()) {


            // Write a message to the database


            author = mAuth.getCurrentUser().getUid();
            DatabaseReference myRef = database.getReference("notes").child(author);

            myRef.keepSynced(true);

            String date = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

            String key = myRef.push().getKey();

            System.out.println("Image url: "+imageUrl);
            System.out.println("Theme Color: "+themeColor);

            Notes note = new Notes(key, noteTitle.getText().toString(), inputNote.getText().toString(), date, themeUrl, themeColor, imageUrl);

            myRef.child(key).setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(NewNoteActivity.this, "Note salvo com sucesso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(NewNoteActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(NewNoteActivity.this, "Falha ao salvar!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "Titulo ou conteúdo não podem ser vazios", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNote() {
        if(!noteTitle.getText().toString().trim().isEmpty() || !inputNote.getText().toString().trim().isEmpty()) {
            mAuth = FirebaseAuth.getInstance();

            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();


            author = mAuth.getCurrentUser().getUid();

            String key = nKey;

            DatabaseReference myRef = database.getReference("notes").child(author).child(key);

            myRef.keepSynced(true);

            String date = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.getDefault()).format(new Date());


            Notes note = new Notes(key, noteTitle.getText().toString(), inputNote.getText().toString(), date, themeUrl, themeColor, imageUrl);


            myRef.setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(NewNoteActivity.this, "Note atualizado com sucesso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(NewNoteActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(NewNoteActivity.this, "Falha ao salvar!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    private void loadThemesAssets() {

        final ImageView viewTheme1 = newNoteLayout.findViewById(R.id.viewTheme1);
        final ImageView viewTheme5 = newNoteLayout.findViewById(R.id.viewTheme6);
        final ImageView viewTheme8 = newNoteLayout.findViewById(R.id.viewTheme9);


        Glide.with(getApplicationContext())
                .load("https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_2.png?alt=media&token=608cd3fc-34f6-4e61-b020-43223690ce51")
                        .centerCrop().into(viewTheme1);
        Glide.with(getApplicationContext())
                .load("https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_8.png?alt=media&token=5a8186ab-ccde-43db-9c09-5ee4c74ca041")
                        .centerCrop().into(viewTheme5);
        Glide.with(getApplicationContext())
                .load("https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_14.jpeg?alt=media&token=46ba915a-fc83-4c3e-a818-0be93fc8bc32")
                        .centerCrop().into(viewTheme8);

        /*

        Picasso.get().load(
                "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_2.png?alt=media&token=608cd3fc-34f6-4e61-b020-43223690ce51"
                )
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        viewTheme1.setBackground(new BitmapDrawable(getResources(), bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        Picasso.get().load(
                "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_8.png?alt=media&token=5a8186ab-ccde-43db-9c09-5ee4c74ca041"
                )
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        viewTheme5.setBackground(new BitmapDrawable(getResources(), bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });



        Picasso.get().load(
                "https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_14.jpeg?alt=media&token=46ba915a-fc83-4c3e-a818-0be93fc8bc32"
                )
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        viewTheme8.setBackground(new BitmapDrawable(getResources(), bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
        */


    }

    private void loadBuyedThemes() {

        author = mAuth.getCurrentUser().getUid();

        DatabaseReference myRef = database.getReference("myThemes").child(author);

        layoutAssetsThemes = newNoteLayout.findViewById(R.id.layoutAssetsThemes);

        //add free themes to array list
        buyedThemesArrayList = new ArrayList<>();
        buyedThemesArrayList.add(null);
        buyedThemesArrayList.add("https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_2.png?alt=media&token=608cd3fc-34f6-4e61-b020-43223690ce51");
        buyedThemesArrayList.add("https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_8.png?alt=media&token=5a8186ab-ccde-43db-9c09-5ee4c74ca041");
        buyedThemesArrayList.add("https://firebasestorage.googleapis.com/v0/b/notes-app-fb2dd.appspot.com/o/themes%2Fbg_theme_14.jpeg?alt=media&token=46ba915a-fc83-4c3e-a818-0be93fc8bc32");

        themeColorArrayList = new ArrayList<>();
        themeColorArrayList.add("light");
        themeColorArrayList.add("dark");
        themeColorArrayList.add("light");
        themeColorArrayList.add("dark");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    BuyedThemes buyedThemes = itemSnapshot.getValue(BuyedThemes.class);
                    buyedThemesArrayList.add(buyedThemes.getThemeUrl());
                    themeColorArrayList.add(buyedThemes.getThemeColor());

                    // Create the FrameLayout
                    FrameLayout frameLayout = new FrameLayout(getApplicationContext());
                    frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                    ));
                    frameLayout.setPadding(0, 0, 10, 0);

                    // Create the ImageView
                    ImageView view = new ImageView(getApplicationContext());
                    FrameLayout.LayoutParams viewParams = new FrameLayout.LayoutParams(
                            dpToPx(getApplicationContext(), 80),
                            dpToPx(getApplicationContext(), 120)
                    );
                    view.setLayoutParams(viewParams);

                    Glide.with(getApplicationContext())
                                    .load(buyedThemes.getThemeUrl())
                                            .centerCrop()
                                                    .into(view);

                    //view.setId(R.id.viewTheme1);

                    // Create the ImageView
                    ImageView imageView = new ImageView(getApplicationContext());
                    FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                            dpToPx(getApplicationContext(), 35),
                            dpToPx(getApplicationContext(), 35)
                    );
                    imageView.setLayoutParams(imageParams);
                    imageView.setPadding(dpToPx(getApplicationContext(), 10), dpToPx(getApplicationContext(), 10), dpToPx(getApplicationContext(), 10), dpToPx(getApplicationContext(), 10));
                    imageView.setColorFilter(getResources().getColor(R.color.white));
                    imageView.setImageResource(0);
                    //imageView.setImageResource(R.drawable.ic_done);
                    //imageView.setId(R.id.imageTheme1);

                    // Add the View and ImageView to the FrameLayout
                    frameLayout.addView(view);
                    frameLayout.addView(imageView);


                    // Add the FrameLayout to the HorizontalScrollView
                    layoutAssetsThemes.addView(frameLayout);

                }

                for (int i=0; i<layoutAssetsThemes.getChildCount(); i++) {
                    FrameLayout frameLayout = (FrameLayout) layoutAssetsThemes.getChildAt(i);
                    ImageView view = (ImageView) frameLayout.getChildAt(0);
                    ImageView imageView = (ImageView) frameLayout.getChildAt(1);

                    final int indice = i;

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            for (int j=0; j<layoutAssetsThemes.getChildCount(); j++) {
                                FrameLayout frameLayout1 = (FrameLayout) layoutAssetsThemes.getChildAt(j);
                                ImageView imageView1 = (ImageView) frameLayout1.getChildAt(1);

                                if (j == indice) {
                                    imageView1.setImageResource(R.drawable.ic_done);
                                } else {
                                    imageView1.setImageResource(0);
                                }

                                themeUrl = buyedThemesArrayList.get(indice);
                                themeColor = themeColorArrayList.get(indice);

                                setNoteTheme();

                                if(!noteTitle.getText().toString().trim().isEmpty() || !inputNote.getText().toString().trim().isEmpty()) {
                                    addNoteDone.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private int dpToPx(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    private void initAssets() {
        final LinearLayout layoutAssets = findViewById(R.id.layoutAssets);
        bottomSheetBehavior = BottomSheetBehavior.from(layoutAssets);
        layoutAssets.findViewById(R.id.textAssets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    //loadThemesAssets();
                    //loadBuyedThemes();
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        newNoteLayout.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            NewNoteActivity.this,
                            new String[]{READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION);

                } else {
                    launcher.launch("image/*");
                }

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if(grantResults.length > 0) {
                boolean permissionToRead = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(permissionToRead) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    launcher.launch("image/*");
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    private void setNoteTheme() {

        System.out.println("selected theme: "+selectedTheme);

        if(themeUrl != null) {
                System.out.println("back: "+selectedTheme);
                System.out.println("ThemeURL: "+themeUrl);
                Glide.with(getApplicationContext())
                                .load(themeUrl)
                                        .centerCrop()
                                                .into(new SimpleTarget<Drawable>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                        newNoteLayout.setBackground(resource);
                                                    }
                                                });
              /*
        Picasso.get().load(themeUrl).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        newNoteLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
                //newNoteLayout.setBackground(selectedTheme);
*/

            if(themeColor != null) {
                if (themeColor.equals("dark")) {
                    System.out.println("Não é igual");
                    noteTitle.setTextColor(getResources().getColor(R.color.white));
                    inputNote.setTextColor(getResources().getColor(R.color.white));
                    addNoteBack.setColorFilter(R.color.white);
                    addNoteDone.setColorFilter(R.color.white);
                } else {
                    System.out.println("Não é igual");
                    noteTitle.setTextColor(getResources().getColor(R.color.dark));
                    inputNote.setTextColor(getResources().getColor(R.color.dark));
                    addNoteBack.setColorFilter(R.color.dark);
                    addNoteDone.setColorFilter(R.color.dark);
                }
            }

        } else {
            newNoteLayout.setBackgroundResource(R.drawable.background_theme_default);
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}