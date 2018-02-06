package com.example.android.airqualityapp;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.android.airqualityapp.MainActivity.POLLUTANT_NAME;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String LOG_TAG = "MAP_ACTIVITY_LOG_TAG";
    private static final String SERVICE_URL = "https://api.openaq.org/v1/measurements";
    private GoogleMap mMap;
    private float mMarkerColor;
    private String mIntentExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //enable back button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //get data from Intent
        Intent intent = getIntent();
        mIntentExtra = intent.getStringExtra(POLLUTANT_NAME);
        //load map with markers
        setUpMapIfNeeded();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            //respond to the up button click
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpMapIfNeeded() {
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
        mMap.setInfoWindowAdapter(new CustomInfoWindowGoogleMap(MapsActivity.this));

        boolean success = mMap.setMapStyle(new MapStyleOptions(getResources().
                getString(R.string.style_json)));

        if(!success){
            Log.e(LOG_TAG, "Style parsing failed");
        }

        setUpMap();
    }

    private void setUpMap() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    retrieveAndAddCities();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Cannot retrieve cities", e);
                    return;
                }
            }
        }).start();
    }

    protected void retrieveAndAddCities() throws IOException {
        HttpURLConnection conn = null;
        final StringBuilder json = new StringBuilder();
        try {
            URL url = UrlUtils.buildUrl(mIntentExtra);
            Log.e(LOG_TAG, "URL: "+url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            //read the JSON data into the stringBuidler
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                json.append(buff, 0, read);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to service", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        //create markers using json
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    createMarkersFromJson(json.toString());
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error processing JSON" + json, e);
                }
            }
        });
    }

    public float markerColorGradient(Integer value){
        if (value <= 75){
            mMarkerColor = BitmapDescriptorFactory.HUE_GREEN;
        } else if (value <= 150 && value >=75) {
            mMarkerColor = BitmapDescriptorFactory.HUE_ORANGE;
        } else if (value <= 300 && value >= 150){
            mMarkerColor = BitmapDescriptorFactory.HUE_RED;
        } else if (value <= 600 && value >= 300) {
            mMarkerColor = BitmapDescriptorFactory.HUE_ROSE;
        }else if (value > 600){
            mMarkerColor = BitmapDescriptorFactory.HUE_VIOLET;
        }
        return mMarkerColor;
    }

    void createMarkersFromJson(String json) throws JSONException {

        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("results");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            Integer pollutantCount = jsonObj.getInt("value");

            //test for presence of latitude and longitude
            if (jsonObj.has("coordinates")) {
                //create marker for each city in json response
                mMap.addMarker(new MarkerOptions()
                        .title(jsonObj.getString("city") + ", " +
                                jsonObj.getString("country"))
                        .snippet(jsonObj.getString("parameter") + ": " +
                                Integer.toString( jsonObj.getInt("value")))
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColorGradient(pollutantCount)))
                        .position(new LatLng(jsonObj.getJSONObject("coordinates").getDouble("latitude"),
                                jsonObj.getJSONObject("coordinates").getDouble("longitude")))
                );
            } else {
                i++;
            }
        }
    }
}
