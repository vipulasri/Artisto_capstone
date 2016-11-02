package com.vipulasri.artisto.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by HP-HP on 12-10-2016.
 */
public class ArtsProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ArtsOpenHelper mOpenHelper;

    // Codes for the UriMatcher //////
    private static final int ART = 100;
    private static final int ART_WITH_ID = 200;
    ////////

    private static UriMatcher buildUriMatcher(){
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ArtsContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, ArtsContract.ArtsEntry.TABLE_ARTS, ART);
        matcher.addURI(authority, ArtsContract.ArtsEntry.TABLE_ARTS + "/#", ART_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ArtsOpenHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            // All Flavors selected
            case ART:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArtsContract.ArtsEntry.TABLE_ARTS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            // Individual flavor based on Id selected
            case ART_WITH_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArtsContract.ArtsEntry.TABLE_ARTS,
                        projection,
                        ArtsContract.ArtsEntry.ART_LONG_ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default:{
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case ART:{
                return ArtsContract.ArtsEntry.CONTENT_DIR_TYPE;
            }
            case ART_WITH_ID:{
                return ArtsContract.ArtsEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case ART: {
                long id = db.insert(ArtsContract.ArtsEntry.TABLE_ARTS, null, values);
                // insert unless it is already contained in the database
                if (id > 0) {
                    returnUri = ArtsContract.ArtsEntry.buildUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch(match){
            case ART:
                numDeleted = db.delete(
                        ArtsContract.ArtsEntry.TABLE_ARTS, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + ArtsContract.ArtsEntry.TABLE_ARTS + "'");
                break;
            case ART_WITH_ID:
                numDeleted = db.delete(ArtsContract.ArtsEntry.TABLE_ARTS, ArtsContract.ArtsEntry.ART_LONG_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});

                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + ArtsContract.ArtsEntry.TABLE_ARTS + "'");

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;

        if (values == null){
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch(sUriMatcher.match(uri)){
            case ART:{
                numUpdated = db.update(ArtsContract.ArtsEntry.TABLE_ARTS,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case ART_WITH_ID: {
                numUpdated = db.update(ArtsContract.ArtsEntry.TABLE_ARTS,
                        values,
                        ArtsContract.ArtsEntry.ART_LONG_ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }

}
