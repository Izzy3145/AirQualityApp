package com.example.android.airqualityapp;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by izzystannett on 24/01/2018.
 */

public class UrlUtils {
    //TODO learn about app architecture
    private static String LOG_TAG = UrlUtils.class.getSimpleName();

    public static final String BASE_URL = "https://api.openaq.org/v1/measurements";
    private static final String QUERY_PARAM = "?";

    public static final int PARAMETER_PM25_ID = 1304;
    public static final int PARAMETER_PM10_ID = 1308;
    public static final int PARAMETER_SO2_ID = 1312;
    public static final int PARAMETER_NO2_ID = 1316;
    public static final int PARAMETER_O3_ID = 1320;
    public static final int PARAMETER_CO_ID = 1324;

    public static URL buildUrl(String pollutantName){
        String queriedPollutant = "parameter=" + pollutantName;

        Uri chosenPollutantUri = Uri.parse(BASE_URL).buildUpon()
                .encodedQuery(queriedPollutant)
                .build();

        try {
                URL chosenPollutantURL = new URL(chosenPollutantUri.toString());
                Log.v(LOG_TAG, "URL: " + chosenPollutantURL);
            return chosenPollutantURL;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
