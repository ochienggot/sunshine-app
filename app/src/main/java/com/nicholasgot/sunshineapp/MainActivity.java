package com.nicholasgot.sunshineapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private String mLocation;

    private static final String FORECASTFRAGMENT_TAG = "forecast_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLocation = Utility.getPreferredLocation(this);

        // Add forecast fragment to this Activity's state
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public void onResume() { // TODO: grok Activity lifecyle
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        boolean locationChanged = !location.equals(mLocation);
        if (locationChanged) {
            ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager()
                    .findFragmentByTag(FORECASTFRAGMENT_TAG);
            if (forecastFragment != null) {
                forecastFragment.onLocationChanged();
            }
            mLocation = Utility.getPreferredLocation(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.preferred_location) {
            openPreferredLocationInmap();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInmap() {

        String location = Utility.getPreferredLocation(this);
        Uri uri = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
