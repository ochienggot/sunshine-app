package com.nicholasgot.sunshineapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nicholasgot.sunshineapp.data.WeatherContract;

public class MainActivity extends AppCompatActivity
        implements ForecastFragment.OnItemSelectedListener {

    private String mLocation;

    private static final String DETAIL_FRAGMENT_TAG = "DFTAG";
    public static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLocation = Utility.getPreferredLocation(this);

        if (findViewById(R.id.weather_detail_container) != null) {
            mTwoPane = true;
            // show the detail view in this activity by adding or replacing detail fragment
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailActivityFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            } else {
                mTwoPane = false;
            }
        }

        // Add forecast fragment to this Activity's state
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
//                    .commit();
//        }
    }

    @Override
    public void onResume() { // TODO: grok Activity lifecyle
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        boolean locationChanged = !location.equals(mLocation);
        if (locationChanged) {
            ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment); // TODO: fix
            if (forecastFragment != null) {
                forecastFragment.onLocationChanged();
            }

            DetailActivityFragment df = (DetailActivityFragment)
                    getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if (df != null) {
                df.onLocationChanged(location);
            }
            mLocation = location;
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
        // as you specify a parent activity in AndroidManifest.values-sw600dp.
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

    public void onItemSelected(Uri dateUri) {
        // The fragments shouldn't communicate directly

        if (mTwoPane) { // 2-pane: On tablet, launch DetailActivity Fragment
            DetailActivityFragment newFrag = DetailActivityFragment.newInstance(dateUri);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.weather_detail_container, newFrag, DETAIL_FRAGMENT_TAG);

            ft.commit();
        } else { // On phone: launch Detail Activity
            // TODO: URI, intent passing data READ
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(dateUri);
            startActivity(intent);
        }
    }
}
