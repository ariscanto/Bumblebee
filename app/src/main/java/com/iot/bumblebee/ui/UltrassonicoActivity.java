package com.iot.bumblebee.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.iot.bumblebee.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class UltrassonicoActivity extends AppCompatActivity {

    TextView tvDistancia, title;
    Chip tvEstado;
    CardView cardStatus;
    private static final String TAG = UltrassonicoActivity.class.getName();


    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    // Tem que ser alterado para pegar o IP da placa automaticamente
    public String url;



    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();


        setContentView(R.layout.activity_ultrassonico);
        tvDistancia = findViewById(R.id.tvDistancia);
        tvEstado = findViewById(R.id.chipAlerta);
        cardStatus = findViewById(R.id.statusCard);
        title = findViewById(R.id.textView2);

        try {
            url = getIntent().getStringExtra("url");
            Log.d("Tela Ultrassonico", "url:"+url);
        }catch (Exception e){
            e.printStackTrace();
        }

        rotinaUltrassonico(this);

    }

    public void rotinaUltrassonico(Context context) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // work
                Runnable runnable = sendAndRequestResponse(url,context);
                Thread mythread = new Thread(runnable);
                mythread.start();

            }
        }, 0, 2*1000); // 0 - time before first execution, 10*1000 - repeating of all subsequent executions


    }
    public Runnable sendAndRequestResponse(String url,Context context) {
        Runnable runnable = new Runnable() {
            public void run() {
                //RequestQueue initialized
                mRequestQueue = Volley.newRequestQueue(context);

                //String Request initialized
                mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Requisição");
                        Toast.makeText(getApplicationContext(), "Response :" + response.toString(), Toast.LENGTH_SHORT).show();
                        //display the response on screen
                        try {
                            JSONObject obj = new JSONObject(response);
                            tvDistancia.setText("Há " + obj.getString("distance") + "cm do objeto!");
                            tvEstado.setText(obj.getString("state"));
                            if (tvEstado.getText().equals("PERIGO")){
                                tvEstado.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#D83634")));
                                cardStatus.setCardBackgroundColor(Color.parseColor("#D83634"));
                            }
                            else if (tvEstado.getText().equals("ALERTA")){
                                tvEstado.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFA000")));
                                cardStatus.setCardBackgroundColor(Color.parseColor("#FFA000"));
                            }
                            else {
                                tvEstado.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#1a1a1a")));
                                cardStatus.setCardBackgroundColor(Color.parseColor("#1a1a1a"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.i(TAG, "Error :" + error.toString());
                    }
                });

                mRequestQueue.add(mStringRequest);
            }};
        return runnable;
    }
}