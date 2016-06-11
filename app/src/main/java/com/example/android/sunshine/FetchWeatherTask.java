package com.example.android.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

    final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
    private final ArrayAdapter<String> mAdapter;
    private final WeatherDataParser mParser;

    public FetchWeatherTask(ArrayAdapter<String> adapter, WeatherDataParser parser) {
        mAdapter = adapter;
        mParser = parser;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);
        if (strings != null) {
            mAdapter.clear();
            for (String item : strings) {
                mAdapter.add(item);
            }
        }
    }

    @Override
    protected String[] doInBackground(String... params) {

        int numDays = 7;
        String zipcode = params[0];
        String urlString = buildUrl(zipcode, numDays);

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL(urlString);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
            String[] result = mParser.getWeatherDataFromJson(forecastJsonStr);

            return result;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private String buildUrl(String zipcode, int numDays) {
        // http://api.openweathermap.org/data/2.5/forecast/daily?q=London&mode=xml&units=metric&cnt=7&appid=b1b15e88fa797225412429c1c50c122a
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                .appendPath("daily")
                .appendQueryParameter("q", zipcode)
                .appendQueryParameter("units", "metric")
                .appendQueryParameter("mode", "json")
                .appendQueryParameter("cnt", Integer.toString(numDays))
                .appendQueryParameter("appid", "11baddacd66974a123ee631413f257ce");

        return builder.build().toString();
    }
}
