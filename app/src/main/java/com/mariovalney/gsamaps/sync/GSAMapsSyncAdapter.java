package com.mariovalney.gsamaps.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.mariovalney.gsamaps.R;
import com.mariovalney.gsamaps.data.DataContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Mário Valney on 08/12/14.
 * mail: mariovalney@gmail.com
 */
public class GSAMapsSyncAdapter extends AbstractThreadedSyncAdapter {

    private final String WEBSERVICE_URL = "http://api.jangal.com.br/gsamaps/embaixadores.json";

    // Every day has 86400 seconds
    public static final int SYNC_INTERVAL = 30 * 86400;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;


    public GSAMapsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String webServiceJSON = null;

        try {
            URL url = new URL(WEBSERVICE_URL);

            // Criando o request da conexão
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Lendo e jogando numa String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                Log.d("Content Provider", "WebService fora do ar?");
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Sendo legal com o cara da manutenção: linhas para debbugar :D
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return;
            }

            webServiceJSON = buffer.toString();

        } catch (IOException e) {
            Log.e("Content Provider", "Error ", e);
            return;

        } finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Content Provider", "Error closing stream", e);
                }
            }
        }

        try {
            getDataFromJson(webServiceJSON);

        } catch (JSONException e) {
            Log.e("Content Provider", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Log.d("configurePeriodicSync", "OK");

        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        GSAMapsSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount,
                context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));

        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private String getDataFromJson(String json) throws JSONException {

        final String API_BASE = "embaixadores";
        final String API_NOME = "nome";
        final String API_INSTITUICAO = "instituicao";
        final String API_PAIS = "pais";
        final String API_LATITUDE = "latitude";
        final String API_LONGITUDE = "longitude";
        final String API_ENDERECO_UM = "end_linha_um";
        final String API_ENDERECO_DOIS = "end_linha_dois";

        JSONObject webserviceJson = new JSONObject(json);
        JSONArray embaixadoresArray = webserviceJson.getJSONArray(API_BASE);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(embaixadoresArray.length());

        for(int i = 0; i < embaixadoresArray.length(); i++) {
            // These are the values that will be collected.

            String nome;
            String instituicao;
            String pais;
            String lat;
            String lng;
            String endUm;
            String endDois;

            // Pegar cada item da Array
            JSONObject embaixador = embaixadoresArray.getJSONObject(i);

            nome = embaixador.getString(API_NOME);
            instituicao = embaixador.getString(API_INSTITUICAO);
            pais = embaixador.getString(API_PAIS);
            lat = embaixador.getString(API_LATITUDE);
            lng = embaixador.getString(API_LONGITUDE);
            endUm = embaixador.getString(API_ENDERECO_UM);
            endDois = embaixador.getString(API_ENDERECO_DOIS);


            ContentValues embaixadorValues = new ContentValues();

            embaixadorValues.put(DataContract.AmbassadorEntry.COLUMN_NAME_NOME, nome);
            embaixadorValues.put(DataContract.AmbassadorEntry.COLUMN_NAME_INSTITUICAO, instituicao);
            embaixadorValues.put(DataContract.AmbassadorEntry.COLUMN_NAME_PAIS, pais);
            embaixadorValues.put(DataContract.AmbassadorEntry.COLUMN_NAME_LATITUDE, lat);
            embaixadorValues.put(DataContract.AmbassadorEntry.COLUMN_NAME_LONGITUDE, lng);
            embaixadorValues.put(DataContract.AmbassadorEntry.COLUMN_NAME_ENDERECO_LINHA_UM, endUm);
            embaixadorValues.put(DataContract.AmbassadorEntry.COLUMN_NAME_ENDERECO_LINHA_DOIS, endDois);

            cVVector.add(embaixadorValues);
        }

        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int rowsUpdated = getContext().getContentResolver()
                    .bulkInsert(DataContract.AmbassadorEntry.buildAmbassadorListUri(), cvArray);

            return Integer.toString(rowsUpdated);
        }

        return null;
    }
}
