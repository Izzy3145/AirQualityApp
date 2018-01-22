package com.example.android.airqualityapp;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String LOG_TAG = "AirQualityApp";
    private static final String SERVICE_URL = "https://api.openaq.org/v1/measurements";
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }

    private void setUpMap(){
        new Thread(new Runnable(){
            public void run(){
                try {
                    retrieveAndAddCities();
                }catch (IOException e)
                {
                    Log.e(LOG_TAG, "Cannot retrieve cities", e);
                    return;
                }
            }
        }).start();
    }

    protected void retrieveAndAddCities() throws IOException{
        HttpURLConnection conn = null;
        final StringBuilder json = new StringBuilder();
        try {
            URL url = new URL(SERVICE_URL);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            //read the JSON data into the stringBuidler
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1){
                json.append(buff, 0, read);
            }
        } catch (IOException e){
            Log.e(LOG_TAG, "Error connecting to service", e);
        } finally {
            if (conn != null){
                conn.disconnect();
            }
        }

        //create markers using json
        runOnUiThread(new Runnable(){
            public void run(){
                try{
                    createMarkersFromJson(json.toString());
                } catch (JSONException e){
                    Log.e(LOG_TAG, "Error processing JSON"+json,e);
                }
            }
        });
    }

    void createMarkersFromJson(String json) throws JSONException{
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("results");

        for (int i = 0; i <jsonArray.length(); i++){
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            //create marker for each city in json response
            mMap.addMarker(new MarkerOptions()
                    .title(jsonObj.getString("location"))
                    .snippet(Integer.toString(jsonObj.getInt("value")))
                    .position(new LatLng(
                                    jsonObj.getJSONObject("coordinates").getDouble("latitude"),
                                    jsonObj.getJSONObject("coordinates").getDouble("longitude")
                            )
                    ));
        }
    }
}
