package com.iot.bumblebee.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iot.bumblebee.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityConfiguracao extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    FirebaseFirestore firestore;
    Button btnLogout;
    Dialog loadingDialog;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        loadingDialog = new Dialog(ActivityConfiguracao.this);
        loadingDialog.setContentView(R.layout.loading_progress_bar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        userData();

    }

    public void userData() {
        CircleImageView userImage = findViewById(R.id.image_profile);
        TextView userName = findViewById(R.id.text_nome);
        TextView userEmail = findViewById(R.id.text_email);

        userName.setText(currentUser.getDisplayName());
        userEmail.setText(currentUser.getEmail());

        if (currentUser.getPhotoUrl() != null){
            Glide.with(this).load(currentUser.getPhotoUrl()).into(userImage);
        }
        else {
            Glide.with(this).load(R.drawable.avatar).into(userImage);
        }

        loadingDialog.cancel();
    }


    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
        startActivity(intent);
        finish();
    }
}