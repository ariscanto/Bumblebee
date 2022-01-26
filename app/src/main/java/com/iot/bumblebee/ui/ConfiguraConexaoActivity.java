package com.iot.bumblebee.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iot.bumblebee.MainActivity;
import com.iot.bumblebee.R;
import com.iot.bumblebee.util.ClientScanResult;
import com.iot.bumblebee.util.FinishScanListener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class ConfiguraConexaoActivity extends AppCompatActivity {
    ListView lvDevices;
    TextView tvConectado, tvInfo;
    Button btConfigurar;
    private static final String TAG = ConfiguraConexaoActivity.class.getName();
    public ConnectivityManager mConnectivityManager;
    public String url = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configura_conexao);

        lvDevices =  (ListView)  findViewById(R.id.listDevices);
        tvConectado = (TextView) findViewById(R.id.tvConexao);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        btConfigurar = (Button) findViewById(R.id.btConfigura);
        mConnectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(ConnectivityManager.class);
        actionBarSetup();
        tvInfo.setText("Para permitir que a placa se conecte ao App,\nas configurações do roteador portátil devem ser:\n\nSSID:\tbambobi\nPASSWORD:\tas1DF2gh3JK4");
        if (isTetherActive()){
            tvConectado.setText("Ponto de acesso ativo");
            btConfigurar.setVisibility(View.INVISIBLE);
            lvDevices.setVisibility(View.VISIBLE);
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            lvDevices.setAdapter(itemsAdapter);
            ArrayList<ClientScanResult> clientesConectados = new ArrayList<ClientScanResult>();
            Runnable runnable = getClientList( this,new FinishScanListener() {
                @Override
                public void onFinishScan(ArrayList<ClientScanResult> clients) {
                    for (ClientScanResult clientScanResult : clients) {
                        itemsAdapter.add(clientScanResult.toString());
                        clientesConectados.add(clientScanResult);
                    }

                }
            });
            Thread mythread = new Thread(runnable);
            mythread.start();

            lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapterView, View view, int position, long l) {

                    Log.d(TAG, "IpAddr: " +  clientesConectados.get(position).getIpAddr());
                    url = new String("http://"+ clientesConectados.get(position).getIpAddr());
                    Log.d(TAG, "url:"+url);

                    Intent intent = new Intent(getBaseContext(), ActivityBumblebee.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            });
        }
        else{
            lvDevices.setVisibility(View.INVISIBLE);
            btConfigurar.setVisibility(View.VISIBLE);
        }
    }


    private void actionBarSetup() {
        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("Configurar Conexão");
        //ab.setSubtitle("\nconectar-se a um device de monitoramento\n");
    }

    public void configuraTether(View v){
        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
}