package com.iot.bumblebee.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.iot.bumblebee.R;
import com.iot.bumblebee.util.ClientScanResult;
import com.iot.bumblebee.util.FinishScanListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class UltrassonicActivity2 extends AppCompatActivity {
    TextView tvDistancia,title;
    Chip tvEstado;
    private static final String TAG = UltrassonicActivity2.class.getName();
    private ConnectivityManager mConnectivityManager;


    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    // Tem que ser alterado para pegar o IP da placa automaticamente


    Timer timer = new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ultrassonico);
        tvDistancia = findViewById(R.id.tvDistancia);
        tvEstado = findViewById(R.id.chipAlerta);
        title = findViewById(R.id.textView2);

        mConnectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(ConnectivityManager.class);


        rotinaUltrassonico(this);

    }
    public Runnable getClientList(Context context, FinishScanListener finishListener) {
        Runnable runnable = new Runnable() {
            public void run() {

                BufferedReader br = null;
                final ArrayList<ClientScanResult> result = new ArrayList<ClientScanResult>();

                try {
                    br = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] splitted = line.split(" +");

                        if ((splitted != null) && (splitted.length >= 4)) {
                            // Basic sanity check
                            String mac = splitted[3];

                            if (mac.matches("..:..:..:..:..:..")) {
                                boolean isReachable = InetAddress.getByName(splitted[0]).isReachable(300);

                                if (isReachable) {
                                    result.add(new ClientScanResult(splitted[0], splitted[3], splitted[5], isReachable));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Wifi:" + e.toString());
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Wifi:" + e.getMessage());
                    }
                }
                // Get a handler that can be used to post to the main thread
                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        finishListener.onFinishScan(result);
                    }
                };
                mainHandler.post(myRunnable);
            }
        };
        return runnable;
    }




    private void sendAndRequestResponse(String url) {
        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Requisição");
                //Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();
                //display the response on screen
                try {
                    JSONObject obj = new JSONObject(response);
                    tvDistancia.setText(obj.getString("distance")+"cm");
                    tvEstado.setText(obj.getString("state"));
                    if(tvEstado.getText().equals("PERIGO"))
                        tvEstado.setChipBackgroundColor(ColorStateList.valueOf(Color.RED));
                    else if(tvEstado.getText().equals("ALERTA"))
                        tvEstado.setChipBackgroundColor(ColorStateList.valueOf(Color.YELLOW));
                    else tvEstado.setChipBackgroundColorResource(R.color.black_500);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(TAG,"Error :" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
    }

    public boolean isTetherActive() {
        try {
            Method method = mConnectivityManager.getClass().getDeclaredMethod("getTetheredIfaces");
            if (method == null) {
                Log.e(TAG, "getTetheredIfaces is null");
            } else {
                String res[] = (String []) method.invoke(mConnectivityManager, null);
                Log.d(TAG, "getTetheredIfaces invoked");
                Log.d(TAG, Arrays.toString(res));
                if (res.length > 0) {
                    Log.d(TAG, res.getClass().toString());
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getTetheredIfaces");
            e.printStackTrace();
        }
        return false;
    }

    public void rotinaUltrassonico(Context context){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // work
                if(isTetherActive()){
                    Runnable runnable = getClientList( context,new FinishScanListener() {
                        @Override
                        public void onFinishScan(final ArrayList<ClientScanResult> clients) {
                            for (ClientScanResult clientScanResult : clients) {
                                Log.d(TAG, "IpAddr: " + clientScanResult.getIpAddr());
                                String url = new String("http://"+clientScanResult.getIpAddr());
                                sendAndRequestResponse(url);
                            }
                        }
                    });
                    Thread mythread = new Thread(runnable);
                    mythread.start();

                }
            }
        }, 0, 3*1000); // 0 - time before first execution, 10*1000 - repeating of all subsequent executions

        //timer.cancel(); //cancels the timer and all schedules executions
    }










}



