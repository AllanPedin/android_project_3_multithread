package com.example.android_project_3_multithread;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String NO_NETWORK_TEXT_LARGE = "Issues!";
    private static final String NO_NETWORK_TEXT_SMALL = "Network is unreachable";

    private static final String JSON_PET_INDEX = "pets.json";

    private JSONArray pets;
    private ArrayList<String> petNames  = new ArrayList<String>();
    private ArrayList<String> petImageNames = new ArrayList<String>();
    private String selectedImageName;
    Bitmap bitmapOfImage;
    //preferences
    private static String urlString;

    ConnectivityCheck connectivityChecker;
    ImageView petImage;
    TextView largeTextView;
    TextView smallTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferencesSetup();

        setFields();

        toolBarSetup();

        getPets();

        menuSetup();

        loadImage();

    }

    private void getPets() {
        if(!connectivityChecker.isNetworkReachable()){
            spinner.setVisibility(View.GONE);
            return;
        }

        DownloadJSONTask downloadJSONTask =  new DownloadJSONTask();
        downloadJSONTask.execute();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getPrefValues(SharedPreferences settings) {
        this.urlString = settings.getString("listPref", null);
    }

    private void setFields(){
        connectivityChecker =  new ConnectivityCheck(this);
        petImage = findViewById(R.id.image_view);
        largeTextView = findViewById(R.id.text_view_large);
        smallTextView = findViewById(R.id.text_view_small);
    }
    Toolbar toolbar;
    private void toolBarSetup(){
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    Spinner spinner;
    private void menuSetup() {
        System.out.println("MEAINFASOFINAEWSFONAGOLAWENBGASDOLNFGASDFNASDF________________!!!!!!!!!!!!!!!!!!!!!!!!!!___________++++++++++++++++++++++++");
        spinner = findViewById(R.id.pet_spinner);
        for(int i =0; i< pets.length();i++){
            try {
                petNames.add(pets.getJSONObject(i).getString("name"));
                petImageNames.add(pets.getJSONObject(i).getString("file"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String[] array = new String[petNames.size()];
        for(int j =0;j<petNames.size();j++){
            array[j] = petNames.get(j);
        }
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        SpinnerActivity onSelect = new SpinnerActivity();
        spinner.setOnItemSelectedListener(onSelect);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(this, SettingsActivity.class);
        startActivity(myIntent);
        return true;
    }


    SharedPreferences preferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    private void preferencesSetup(){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("listPref")){
                    urlString = sharedPreferences.getString("listPref", null);
                    menuSetup();
                    loadImage();
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);

        getPrefValues(preferences);
    }

    private void loadImage() {
        if(!connectivityChecker.isNetworkReachable()){ //no network
            petImage.setImageResource(R.drawable.dinosaur);
            largeTextView.setText(NO_NETWORK_TEXT_LARGE);
            smallTextView.setText(NO_NETWORK_TEXT_SMALL);
            return;
        }else{
            petImage.setImageBitmap(bitmapOfImage);
        }
    }
    private class DownloadJSONTask extends AsyncTask<String, Void, JSONArray>{

        @Override
        protected JSONArray doInBackground(String... strings) {
            try {
                URL url = new URL(urlString + JSON_PET_INDEX);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if(connection.getResponseCode() != 200){
                    System.out.println("Broke------------------------------------------");
                    return null;
                }
                StringBuilder response = new StringBuilder();
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
                JSONObject json = (JSONObject) new JSONObject(response.toString());
                JSONArray jsonArray = json.getJSONArray("pets");
                setPets(jsonArray);
                return jsonArray;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
    private void setPets(JSONArray _pets){
        this.pets = _pets;
    }
    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            System.out.println("---------------------------------------------------");
            try {
                selectedImageName = pets.getJSONObject(pos).getString("file");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DownloadImageTask downloadImage = new DownloadImageTask();
            downloadImage.execute();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loadImage();
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }
    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try{
                URL url = new URL(urlString + selectedImageName);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                connection.connect();

                int statusCode = connection.getResponseCode();
                if (statusCode / 100 != 2) {
                    return null;
                }

                InputStream is = connection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                // the following buffer will grow as needed
                ByteArrayOutputStream baf = new ByteArrayOutputStream(50);
                //ByteArrayBuffer baf = new ByteArrayBuffer(DEFAULTBUFFERSIZE);
                int current = 0;

                while ((current = bis.read()) != -1) {
                    baf.write((byte) current);

                    //probably want to check for canceled here
                }

                // convert to a bitmap
                byte[] imageData = baf.toByteArray();
                bitmapOfImage = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                return bitmapOfImage;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }


}