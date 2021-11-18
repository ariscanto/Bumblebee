package com.iot.bumblebee.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iot.bumblebee.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityBumblebee extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ImageView btnConfig;
    CircularReveal circularReveal;
    CircleImageView circleImageView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        btnConfig = findViewById(R.id.user_config);
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //signOut();
            }
        });
        userData();
        initViews();

    }


    public void userData() {
        circleImageView = findViewById(R.id.user_image);
        TextView userName = findViewById(R.id.user_name);

        userName.setText(currentUser.getDisplayName());

        if (currentUser.getPhotoUrl() != null){
            Glide.with(this).load(currentUser.getPhotoUrl()).into(circleImageView);
        }
        else {
            Glide.with(this).load(R.drawable.avatar).into(circleImageView);
        }
    }

    public void initViews(){
        btnConfig = findViewById(R.id.user_config);
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentConfig = new Intent(ActivityBumblebee.this, ActivityConfiguracao.class);
                startActivity(intentConfig);
            }
        });
        CardView cardCorrida = findViewById(R.id.cardCorrida);
        cardCorrida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCorrida = new Intent(ActivityBumblebee.this, SobreActivity.class);
                startActivity(intentCorrida);

            }
        });
        CardView cardRelatorio = findViewById(R.id.cardRelatorio);
        cardRelatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRelatorio = new Intent(ActivityBumblebee.this, RelatorioActivity.class);
                startActivity(intentRelatorio);
            }
        });
    }

}