package com.example.android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "!!!!!!!!! onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_view_location:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String location = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
                showMap(location);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showMap(String location) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("geo")
                .encodedAuthority("0,0")
                .appendQueryParameter("q", location.replace(' ', '+'));
        Uri geoLocation = builder.build();
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        Log.v(LOG_TAG, "!!!!!!!!! onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.v(LOG_TAG, "!!!!!!!!! onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.v(LOG_TAG, "!!!!!!!!! onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.v(LOG_TAG, "!!!!!!!!! onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, "!!!!!!!!! onDestroy");
        super.onDestroy();
    }
}
