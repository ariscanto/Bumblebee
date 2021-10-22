package com.iot.bumblebee.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.iot.bumblebee.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    String TAG;
    TextInputEditText userEmail, userPassword, userPAssword2, userName;
    ImageView regBtn;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_and_password_register);

        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userPAssword2 = findViewById(R.id.regPassword2);
        userName = findViewById(R.id.regName);
        regBtn = findViewById(R.id.regBtn);


        /*regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Cadastro Realizado Com Sucesso", Toast.LENGTH_LONG).show();
            }
        });*/
    }
}