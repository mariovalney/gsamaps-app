package com.mariovalney.gsamaps;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mariovalney.gsamaps.data.DataContract.AmbassadorEntry;
import com.mariovalney.gsamaps.sync.GSAMapsSyncAdapter;

public class MainActivity extends ActionBarActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private GoogleMap mMap;

    private static final int MAIN_LOADER = 0;

    private static final String[] AMBASSADORS_LOCATION_COLUMNS = {
            AmbassadorEntry.TABLE_NAME + "." + AmbassadorEntry._ID,
            AmbassadorEntry.COLUMN_NAME_NOME,
            AmbassadorEntry.COLUMN_NAME_LATITUDE,
            AmbassadorEntry.COLUMN_NAME_LONGITUDE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapsInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        getSupportLoaderManager().initLoader(MAIN_LOADER, null, this);
        GSAMapsSyncAdapter.initializeSyncAdapter(this);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_ambassadors_list) {
            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Uri ambassadorsLocation = AmbassadorEntry.buildAmbassadorsLocationsUri();

        return new CursorLoader(
                getApplication(),
                ambassadorsLocation,
                AMBASSADORS_LOCATION_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        // If the DataBase is empty... lets refresh!
        if (cursor.getCount() == 0) {
            Toast.makeText(getApplication(),
                    getString(R.string.trying_connect_message), Toast.LENGTH_SHORT).show();
            GSAMapsSyncAdapter.syncImmediately(this);
        }

        if (!cursor.isClosed() && cursor != null && cursor.moveToFirst()) {

            if (mMap == null) {
                setUpMapIfNeeded();
            }

            // Varrendo o Cursor
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String nome = cursor.getString(cursor.getColumnIndex(
                        AmbassadorEntry.COLUMN_NAME_NOME));
                float lat = cursor.getFloat(cursor.getColumnIndex(
                        AmbassadorEntry.COLUMN_NAME_LATITUDE));
                float lng = cursor.getFloat(cursor.getColumnIndex(
                        AmbassadorEntry.COLUMN_NAME_LONGITUDE));

                // Criando os Markers
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(nome)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

                cursor.moveToNext();
            }
            cursor.close();
        } else if(cursor.isClosed()) {
            getSupportLoaderManager().restartLoader(MAIN_LOADER, null, this);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    private void setUpMapIfNeeded() {
        // Verifica se é "null", ou seja, se não temos mapa ainda.
        if (mMap == null) {
            // Tenta obter um mapa do SupportMapFragment.
            mMap = ((SupportMapFragment)
                    getSupportFragmentManager().findFragmentById(R.id.map_main)).getMap();

            // Verifica se temos um mapa
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        // Configurações do Mapa
        UiSettings mapSettings;
        mapSettings = mMap.getUiSettings();

        mapSettings.setZoomGesturesEnabled(true);
        mapSettings.setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(11.373552, -76.97600300), 2));

    }

}
