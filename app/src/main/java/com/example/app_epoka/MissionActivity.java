package com.example.app_epoka;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MissionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        List<LibelleEtId> list = new ArrayList<LibelleEtId>();
        list.add(new LibelleEtId("Grenoble", 1));
        list.add(new LibelleEtId("Lyon", 2));
        list.add(new LibelleEtId("Paris", 3));
        ArrayAdapter<LibelleEtId> adapter = new ArrayAdapter<LibelleEtId>(this, android.R.layout.simple_spinner_item, list);
        Spinner ville = (Spinner) findViewById(R.id.sVille);
        ville.setAdapter(adapter);
    }

    public class LibelleEtId{
        public String libelle;
        public int id;

        public LibelleEtId(String unLibelle, int unId){
            libelle = unLibelle;
            id = unId;
        }

        @Override
        public String toString(){
            return libelle;
        }
    }

    private void getServerDataJson(String urlString){
        InputStream is = null;
        String result = "";
        ArrayList<NoNom> tableau = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            //échange http avec le serveur
            URL url = new URL(urlString);
            HttpURLConnection connexion = (HttpURLConnection)url.openConnection();
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

        //Parse les données JSON
        try {
            JSONArray jarray = new JSONArray(result);
            for (int i = 0; i < jarray.length(); i++){
                JSONObject jsonData = jarray.getJSONObject(i);
                NoNom film = new NoNom(jsonData.getInt("id"), jsonData.getString("nom"));
                tableau.add(film);
            }
        } catch (JSONException expt) {
            Log.e("log-tag", "Erreur pendant l'analyse des données : " + expt.toString());
        }

        for (int i = 0; i < tableau.size() ; i++) {
            Button b = new Button(this);
            b.setText(tableau.get(i).nom);
            b.setAllCaps(false);
            b.setTag(tableau.get(i).no);
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                    intent.putExtra("id", (int) view.getTag());
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
            });

            ll = (LinearLayout) findViewById(R.id.llLayout);
            ll.addView(b);
        }
}
