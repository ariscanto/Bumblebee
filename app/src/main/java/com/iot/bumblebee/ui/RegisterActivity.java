package com.iot.bumblebee.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iot.bumblebee.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    ImageView userPhoto;
    static int PreReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;
    private String TAG;
    private EditText userEmail, userPassword, userName;
    private CardView btnSalvar;
    private TextView sigin;
    FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    String userID;
    CircularReveal circularReveal;
    ConstraintLayout constraintLayout;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_and_password_register);

        //constraintLayout = findViewById(R.id.constraintCadastro);
        View view = findViewById(R.id.constraintCadastro);
        circularReveal = new CircularReveal(view);
        circularReveal.onActivityCreate(getIntent());

        userName = (EditText) findViewById(R.id.editTextName);
        userEmail = (EditText) findViewById(R.id.editTextTextEmail);
        userPassword = (EditText) findViewById(R.id.editTextTextPassword);
        btnSalvar = (CardView) findViewById(R.id.btnSalvar);
        userPhoto = (CircleImageView) findViewById(R.id.regUserPhoto);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = userName.getText().toString();
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String image = userPhoto.getDrawable().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || image.isEmpty()) {
                    showMessage("Please Verify all fields");
                    btnSalvar.setVisibility(View.VISIBLE);

                } else {
                    loadingDialog = new Dialog(RegisterActivity.this);
                    loadingDialog.setContentView(R.layout.loading_progress_bar);
                    loadingDialog.setCancelable(false);
                    loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    loadingDialog.show();
                    CreateUserAccount(email, name, password);
                }
            }
        });

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 27) {
                    checkAndReqForPermission();
                } else {
                    openGalery();
                }
            }
        });
    }

    //criar funcoes

    private void CreateUserAccount(String email, String name, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("bumblebee_usuarios").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("Name", name);
                            user.put("Email", email);
                            user.put("Password", password);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {

                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingDialog.cancel();
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });

                            if (pickedImgUri != null){
                                updateUserInfo(name, pickedImgUri, mAuth.getCurrentUser());

                            }
                            else {
                                updateUserInfoWithoutPhoto(name, Objects.requireNonNull(mAuth.getCurrentUser()));
                            }

                        } else {
                            loadingDialog.cancel();
                            showToastRegisterFail();
                            btnSalvar.setVisibility(View.VISIBLE);

                        }
                    }
                });
    }

    private void updateUserInfo(String name, Uri pickedImgUri, FirebaseUser currentUser) {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("bumblebee_perfil_usuarios");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(profleUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            showToastRegisterSuccess();
                                            loadingDialog.cancel();
                                            Intent intent = new Intent(RegisterActivity.this, ActivityLogin.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    private void updateUserInfoWithoutPhoto(String name, FirebaseUser currentUser) {

        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        currentUser.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showToastRegisterSuccess();
                            loadingDialog.cancel();
                            Intent intent = new Intent(RegisterActivity.this, ActivityLogin.class);
                            startActivity(intent);
                        }else{
                            loadingDialog.cancel();
                        }
                    }
                });
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private  void showToastRegisterSuccess(){
        final Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        View customToast = getLayoutInflater().inflate(R.layout.show_toast_sucesso_registro, null);
        toast.setView(customToast);
        toast.show();
    }

    private  void showToastRegisterFail(){
        final Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        View customToast = getLayoutInflater().inflate(R.layout.show_toast_falha_registro, null);
        toast.setView(customToast);
        toast.show();
    }


    private void openGalery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);

    }

    private void checkAndReqForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PreReqCode);
            }
        } else {
            openGalery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
            pickedImgUri = data.getData();
            userPhoto.setImageURI(pickedImgUri);

        }

    }
}