package com.example.android.sunshine;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {


    private ArrayAdapter<String> mForecastAdapter;

    final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    public ForecastFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            updateWeather();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Unknown unit type", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            try {
                updateWeather();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Unknown unit type", e);
                e.printStackTrace();
                return false;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() throws Exception {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = getString(R.string.pref_location_key);
        String defaultLocation = getString(R.string.pref_location_default);
        String location = prefs.getString(key, defaultLocation);

        WeatherDataParser.TemperatureUnit unit = GetTemperatureUnit();
        WeatherDataParser parser = new WeatherDataParser(unit);
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(mForecastAdapter, parser);
        fetchWeatherTask.execute(location);
    }

    private WeatherDataParser.TemperatureUnit GetTemperatureUnit() throws Exception {
        final String unit_metric = getString(R.string.pref_temperature_unit_metric);
        final String unit_imperial = getString(R.string.pref_temperature_unit_imperial);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unit = preferences.getString(getString(R.string.pref_temperature_key), unit_metric);

        if (unit.equals(unit_metric))
            return WeatherDataParser.TemperatureUnit.METRIC;

        if (unit.equals(unit_imperial))
            return WeatherDataParser.TemperatureUnit.IMPERIAL;

        throw new Exception("Unknown unit: " + unit);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mForecastAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.list_view_forecast_textview, new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                String forecast = mForecastAdapter.getItem(position);
                intent.putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });
        return rootView;
    }
}

