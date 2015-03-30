package com.mariovalney.gsamaps.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.mariovalney.gsamaps.data.DataContract.AmbassadorEntry;

/**
 * Created by Mário Valney on 22/11/14.
 * mail: mariovalney@gmail.com
 */
public class DataProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DataDbHelper mDbHelper;

    private static final int ALL_DATA = 1;
    private static final int ALL_LOCATIONS = 2;
    private static final int INDIVIDUAL_DATA = 3;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DataContract.PATH_DATA, ALL_DATA);
        matcher.addURI(authority, DataContract.PATH_DATA + "/coordenadas", ALL_LOCATIONS);
        matcher.addURI(authority, DataContract.PATH_DATA + "/#", INDIVIDUAL_DATA);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DataDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "embaixadores"
            case ALL_DATA: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        AmbassadorEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        AmbassadorEntry.COLUMN_NAME_NOME + " ASC"
                );
                break;
            }

            // "embaixadores/coordenadas"
            case ALL_LOCATIONS: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        AmbassadorEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                break;
            }

            // "embaixadores/*"
            case INDIVIDUAL_DATA: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        AmbassadorEntry.TABLE_NAME,
                        projection,
                        AmbassadorEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        null
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Uri Desconhecida: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        // final int match = sUriMatcher.match(uri);

        switch (sUriMatcher.match(uri)) {
            case ALL_DATA:
                return AmbassadorEntry.CONTENT_TYPE;
            case ALL_LOCATIONS:
                return AmbassadorEntry.CONTENT_TYPE;
            case INDIVIDUAL_DATA:
                return AmbassadorEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Uri Desconhecida: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ALL_DATA: {
                long _id = db.insert(AmbassadorEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = AmbassadorEntry.buildAmbassadorDetailsUri(_id);
                } else {
                    throw new android.database.SQLException("Erro ao inserir dados em " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Uri Desconhecida: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case ALL_DATA:
                rowsDeleted = db.delete(
                        AmbassadorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Uri Desconhecida: " + uri);
        }
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ALL_DATA:
                rowsUpdated = db.update(AmbassadorEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Uri Desconhecida: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ALL_DATA:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    // BulkInsert é usado quando vamos sincronizar com o WebService,
                    // sendo assim devemos apagar todos os registros antes.
                    db.delete(AmbassadorEntry.TABLE_NAME, null, null);
                    for (ContentValues value : values) {
                        long _id = db.insert(AmbassadorEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
