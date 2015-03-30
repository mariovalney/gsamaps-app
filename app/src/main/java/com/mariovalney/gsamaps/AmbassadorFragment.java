package com.mariovalney.gsamaps;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mariovalney.gsamaps.data.DataContract.AmbassadorEntry;

/**
 * Created by Mário Valney on 18/11/14.
 * mail: mariovalney@gmail.com
 */
public class AmbassadorFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private GoogleMap mMap;
    private SupportMapFragment mSupportMapFragment;
    private static final int AMBASSADOR_LOADER = 0;

    private static long mEmbaixadorId;

    private static String mEmbaixadorNome;
    private static float mEmbaixadorLatitude;
    private static float mEmbaixadorLongitude;

    public static String SHAREINFO = Integer.toString(R.string.share_message_default);

    private static final String[] AMBASSADOR_COLUMNS = {
            AmbassadorEntry.TABLE_NAME + "." + AmbassadorEntry._ID,
            AmbassadorEntry.COLUMN_NAME_NOME,
            AmbassadorEntry.COLUMN_NAME_INSTITUICAO,
            AmbassadorEntry.COLUMN_NAME_LATITUDE,
            AmbassadorEntry.COLUMN_NAME_LONGITUDE,
            AmbassadorEntry.COLUMN_NAME_ENDERECO_LINHA_UM,
            AmbassadorEntry.COLUMN_NAME_ENDERECO_LINHA_DOIS
    };

    public AmbassadorFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Lê os dados
        Bundle arguments = getArguments();
        if (arguments != null) {
            mEmbaixadorId = arguments.getLong(AmbassadorActivity.INTENT_EMBAIXADOR_ID);
        }

        if (savedInstanceState != null) {
            mEmbaixadorId = savedInstanceState.getLong(AmbassadorActivity.INTENT_EMBAIXADOR_ID);
        }

        // Faz o mapa, se necessário
        setUpMapIfNeeded();

        // Cria a View
        View rootView = inflater.inflate(R.layout.activity_ambassador_container, container, false);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.share, menu);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        Uri ambassadorDetails = AmbassadorEntry.buildAmbassadorDetailsUri(mEmbaixadorId);

        return new CursorLoader(
                getActivity(),
                ambassadorDetails,
                AMBASSADOR_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {

            // Lê as informações do Cursor
            mEmbaixadorNome = cursor.getString(cursor.getColumnIndex(
                    AmbassadorEntry.COLUMN_NAME_NOME));
            mEmbaixadorLatitude = cursor.getFloat(cursor.getColumnIndex(
                    AmbassadorEntry.COLUMN_NAME_LATITUDE));
            mEmbaixadorLongitude = cursor.getFloat(cursor.getColumnIndex(
                    AmbassadorEntry.COLUMN_NAME_LONGITUDE));
            String instituicao = cursor.getString(cursor.getColumnIndex(
                    AmbassadorEntry.COLUMN_NAME_INSTITUICAO));
            String enderecoLinhaUm = cursor.getString(cursor.getColumnIndex(
                    AmbassadorEntry.COLUMN_NAME_ENDERECO_LINHA_UM));
            String enderecoLinhaDois = cursor.getString(cursor.getColumnIndex(
                    AmbassadorEntry.COLUMN_NAME_ENDERECO_LINHA_DOIS));

            AmbassadorFragmentDetails.AMBASSADOR_DETAIL_NOME_VIEW
                    .setText(mEmbaixadorNome);
            AmbassadorFragmentDetails.AMBASSADOR_DETAIL_INSTITUICAO_VIEW
                    .setText(instituicao);
            AmbassadorFragmentDetails.AMBASSADOR_DETAIL_END_UM_VIEW
                    .setText(enderecoLinhaUm);
            AmbassadorFragmentDetails.AMBASSADOR_DETAIL_END_DOIS_VIEW
                    .setText(enderecoLinhaDois);

            // Altera o Título
            getActivity().setTitle(mEmbaixadorNome);

            // Texto de Compartilhamento
            SHAREINFO = String.format(getActivity().getString(R.string.share_message),
                    enderecoLinhaDois, mEmbaixadorNome, instituicao);

            setUpMapIfNeeded();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(AmbassadorActivity.INTENT_EMBAIXADOR_ID)
                && mEmbaixadorId >= 0) {
            getLoaderManager().restartLoader(AMBASSADOR_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey(AmbassadorActivity.INTENT_EMBAIXADOR_ID)) {
            getLoaderManager().initLoader(AMBASSADOR_LOADER, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(AmbassadorActivity.INTENT_EMBAIXADOR_ID, mEmbaixadorId);
        super.onSaveInstanceState(outState);
    }

    private void setUpMapIfNeeded() {
        // Verifica se é "null", ou seja, se não temos mapa ainda.
        mSupportMapFragment = ((SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_ambassador));

        if (mMap == null) {
            // Tenta obter um mapa do SupportMapFragment.
            if (mSupportMapFragment != null) {
                mMap = mSupportMapFragment.getMap();
            }

            // Verifica se temos um mapa
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {


        // Configurações do Mapa

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        UiSettings mapSettings;
        mapSettings = mMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(false);

        LatLng coords = new LatLng(mEmbaixadorLatitude, mEmbaixadorLongitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(coords));

        mMap.addMarker(new MarkerOptions().position(coords).title(mEmbaixadorNome)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

        float maxZoom = mMap.getMaxZoomLevel();
        maxZoom = maxZoom - 1;
        if (maxZoom > 18) { maxZoom = 18; }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, maxZoom));
    }

}
