package com.mariovalney.gsamaps.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mário Valney on 20/11/14.
 * mail: mariovalney@gmail.com
 */
public final class DataContract {

    public DataContract() {}

    // CONSTANTS FOR CONTENT PROVIDER
    public static final String CONTENT_AUTHORITY = "com.mariovalney.gsamaps.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_DATA = AmbassadorEntry.TABLE_NAME;
    public static final String PATH_LOCATIONS = "coordenadas";

    public static abstract class AmbassadorEntry implements BaseColumns {

        // CONSTANTS FOR DB
        public static final String TABLE_NAME = "embaixadores";
        public static final String COLUMN_NAME_NOME = "nome";
        public static final String COLUMN_NAME_INSTITUICAO = "instituicao";
        public static final String COLUMN_NAME_PAIS = "pais";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_ENDERECO_LINHA_UM = "endereco_linha_um";
        public static final String COLUMN_NAME_ENDERECO_LINHA_DOIS = "endereco_linha_dois";

        // CONSTANTS FOR CP
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_DATA;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_DATA;
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DATA).build();

        // URI FOR ONE AMBASSADOR
        public static Uri buildAmbassadorDetailsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // URI FOR ALL LOCATIONS
        public static Uri buildAmbassadorsLocationsUri() {
            return CONTENT_URI.buildUpon().appendPath(PATH_LOCATIONS).build();
        }

        // URI FOR THE LIST OF AMBASSADORS
        public static Uri buildAmbassadorListUri() {
            return CONTENT_URI;
        }

        // URI FOR THE LIST OF AMBASSADORS
        public static Uri buildAmbassadorUri() {
            return CONTENT_URI;
        }
    }
}
