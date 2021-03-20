package com.example.app_epoka;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    private TextView tv;
    private String urlServiceWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tvResultat);
    }

    public void authentification(View view) {
        EditText no = (EditText) findViewById(R.id.et_No);
        EditText mdp = (EditText) findViewById(R.id.et_mdp);

        if (no.getText().toString().matches("")) {
            Toast.makeText(this, "Veuillez renseigner votre numéro", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mdp.getText().toString().matches("")) {
            Toast.makeText(this, "Veuillez renseigner votre mot de passe", Toast.LENGTH_SHORT).show();
            return;
        }

        urlServiceWeb = "http://192.168.56.1/epoka-app-web/services/serv_authentification-mobile.php?user=" + no.getText() + "&mdp=" + mdp.getText();
        String resultat = getServerDataTexteBrut(urlServiceWeb);

        if(resultat.substring(0, 1).equals("#")){
            Toast.makeText(this, "Numéro ou mot de passe incorect", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("result", resultat);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private String getServerDataTexteBrut(String urlString){
        InputStream is = null;
        String result = "";
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //échange http avec le serveur
            URL url = new URL(urlString);
            HttpURLConnection connexion = (HttpURLConnection) url.openConnection();
            connexion.connect();
            is = connexion.getInputStream();

            //exploitation de la réponse
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String ligne;
            while ((ligne = br.readLine()) != null){
                result = result + ligne + "\n";
            }
        } catch (Exception expt) {
            Log.e("log-tag", "Erreur pendant la récupération des données : " + expt.toString());
        }

        return result;
    }
}