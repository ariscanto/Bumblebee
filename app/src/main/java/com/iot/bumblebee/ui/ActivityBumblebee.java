package com.iot.bumblebee.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iot.bumblebee.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityBumblebee extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ImageView btnConfig;
    CircularReveal circularReveal;
    CircleImageView circleImageView;
    public String url = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        url = getIntent().getStringExtra("url");

        btnConfig = findViewById(R.id.user_config);
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //signOut();
            }
        });
        userData();
        initViews();
        mapView();

    }

    public void mapView(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
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
        url = getIntent().getStringExtra("url");
        btnConfig = findViewById(R.id.user_config);
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentConfig = new Intent(ActivityBumblebee.this, ActivityConfiguracao.class);
                intentConfig.putExtra("url", url);
                startActivity(intentConfig);
            }
        });
        @SuppressLint("WrongViewCast") FloatingActionButton fabCorridas = findViewById(R.id.fabCorridas);
        fabCorridas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCorrida = new Intent(ActivityBumblebee.this, LocationActivity.class);
                startActivity(intentCorrida);

            }
        });
        CardView cardUltrassonico = findViewById(R.id.card_ultrassonico);
        cardUltrassonico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentRelatorio = new Intent(ActivityBumblebee.this, UltrassonicoActivity.class);
                intentRelatorio.putExtra("url", url);
                startActivity(intentRelatorio);
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng position = new LatLng(-3.0692875, -59.9225004);
        googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title("Posição Atual")
                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_marker))
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 25));
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }
}