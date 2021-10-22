package com.iot.bumblebee.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.iot.bumblebee.R;


public class LoginActivity extends AppCompatActivity {

    private EditText userMail,userPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView logToRegBtn = findViewById(R.id.loginToReg);
        logToRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        userMail = findViewById(R.id.login_mail);
        userPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.loginnBtn);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty()) {
                    showMessage("Campos Vazios");
                } else {
                    signIn(mail,password);
                }




            }
        });
    }

    private void signIn(String mail, String password) {
        if(mail.equals("root") || password.equals("root")){
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            showToastLoginSuccess();
        }else{
           showToastLoginFailure();
        }
    }


    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),text, Toast.LENGTH_LONG).show();
    }
    private  void showToastLoginSuccess(){
        final Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        View customToast = getLayoutInflater().inflate(R.layout.custom_toast, null);
        toast.setView(customToast);
        toast.show();
    }
    private  void showToastLoginFailure(){
        final Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        View customToast = getLayoutInflater().inflate(R.layout.show_login_failure, null);
        toast.setView(customToast);
        toast.show();
    }
}