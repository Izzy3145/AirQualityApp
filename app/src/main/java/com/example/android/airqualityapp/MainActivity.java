package com.example.android.airqualityapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String POLLUTANT_NAME = "POLLUTANT_NAME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickPM25(View view){
        Intent intent = new Intent(this, MapsActivity.class)
                .putExtra(POLLUTANT_NAME,"pm25");
        startActivity(intent);
    }

    public void onClickPM10(View view){
        Intent intent = new Intent(this, MapsActivity.class)
                .putExtra(POLLUTANT_NAME,"pm10");
        startActivity(intent);
    }

    public void onClickSO2(View view){
        Intent intent = new Intent(this, MapsActivity.class)
                .putExtra(POLLUTANT_NAME,"so2");
        startActivity(intent);
    }

    public void onClickNO2(View view){
        Intent intent = new Intent(this, MapsActivity.class)
                .putExtra(POLLUTANT_NAME, "no2");
        startActivity(intent);
    }

    public void onClickO3(View view){
        Intent intent = new Intent(this, MapsActivity.class)
                .putExtra(POLLUTANT_NAME, "o3");
        startActivity(intent);
    }

    public void onClickCO(View view){
        Intent intent = new Intent(this, MapsActivity.class)
                .putExtra(POLLUTANT_NAME, "co");
        startActivity(intent);
    }
}
