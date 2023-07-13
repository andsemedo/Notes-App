package com.example.notesapp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.notesapp.authentication.LoginActivity;
import com.example.notesapp.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilePhoto;
    private EditText updateName, updateEmail, updatePassword;
    private Button btnUpdateProfile, btnDeleteProfile;

    private ActivityResultLauncher<String> launcher;

    private StorageReference storage = FirebaseStorage.getInstance().getReference();
    private String imageUrl="", email, nome, editEmail, editName, password, photo;
    private Uri selectedImageUri;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference;
    private View view;

    AlertDialog dialogChangeEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePhoto = findViewById(R.id.profilePhoto);
        updateName = findViewById(R.id.updateName);
        updateEmail = findViewById(R.id.updateEmail);
        btnDeleteProfile = findViewById(R.id.btnDeleteProfile);

        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        loadUserData();

        uploadProfilePhoto();

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            ProfileActivity.this,
                            new String[]{READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION);

                } else {

                    launcher.launch("image/*");
                }
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
            }
        });

        btnDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

    }

    private void deleteAccount() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        currentUser.delete();

        Snackbar snackbar = Snackbar.make(findViewById(R.id.profile), "Conta deletada com sucesso", Snackbar.LENGTH_SHORT);
        snackbar.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        }, 1500);
    }

    private void loadUserData() {


        firebaseAuth = FirebaseAuth.getInstance();

        String userId = firebaseAuth.getCurrentUser().getUid();

        reference = database.getReference("accounts").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userData = snapshot.getValue(User.class);
                if (userData != null) {
                    nome = userData.getNome();
                    updateName.setText(nome);

                    if (!userData.getPhoto().isEmpty()) {
                        photo = userData.getPhoto();
                        Glide.with(getApplicationContext())
                                .load(userData.getPhoto())
                                .centerCrop()
                                .into(profilePhoto);

                    }

                    email = firebaseAuth.getCurrentUser().getEmail();
                    updateEmail.setText(email);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void uploadProfilePhoto() {

        launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {



                        profilePhoto.setImageURI(uri);

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


    }

    private void updateUserData() {

        // verify if user changed email, password and name
        editEmail = updateEmail.getText().toString();
        editName = updateName.getText().toString();
        password = updatePassword.getText().toString();


        User user;
        if(!imageUrl.equals("")) {
            user = new User(editName, imageUrl);

        } else {
            user = new User(editName, photo);
        }

        reference = database.getReference("accounts");
        reference.child(firebaseAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.profile), "Dados atualizado com sucesso", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });




        //Toast.makeText(this, nome +" "+email, Toast.LENGTH_SHORT).show();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}