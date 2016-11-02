package com.vipulasri.artisto.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.vipulasri.artisto.model.Artwork;


/**
 * Created by HP-HP on 12-10-2016.
 */
public class ArtsOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME ="ARTS_DB";
    public static final int DATABASE_VERSION = 2;

    public static final String CREATE_TABLE =
            "create table " + ArtsContract.ArtsEntry.TABLE_ARTS + " ("
                    + ArtsContract.ArtsEntry.ID + " integer primary key autoincrement, "
                    + ArtsContract.ArtsEntry.ART_WEB_LINK + " text , "
                    + ArtsContract.ArtsEntry.ART_ID + " text , "
                    + ArtsContract.ArtsEntry.ART_LONG_ID + " text , "
                    + ArtsContract.ArtsEntry.ART_OBJECT_NUMBER + " text , "
                    + ArtsContract.ArtsEntry.ART_TITLE + " text , "
                    + ArtsContract.ArtsEntry.ART_HAS_IMAGE + " integer default 0 , "
                    + ArtsContract.ArtsEntry.ART_MAKER + " text , "
                    + ArtsContract.ArtsEntry.ART_lONG_TITLE + " text , "
                    + ArtsContract.ArtsEntry.ART_SHOW_IMAGE + " integer default 0 , "
                    + ArtsContract.ArtsEntry.ART_PERMIT_DOWNLOAD + " integer default 0 , "
                    + ArtsContract.ArtsEntry.ART_WEB_IMAGE + " text , "
                    + ArtsContract.ArtsEntry.ART_WEB_IMAGE_WIDTH + " text , "
                    + ArtsContract.ArtsEntry.ART_WEB_IMAGE_HEIGHT + " text ) ; ";

    public ArtsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("ArtsOpenHelper", "Upgrading database from version " + oldVersion + " to " + newVersion + ". OLD DATA WILL BE DESTROYED");

        // Drop the table
        db.execSQL("DROP TABLE IF EXISTS " + ArtsContract.ArtsEntry.TABLE_ARTS);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + ArtsContract.ArtsEntry.TABLE_ARTS + "'");

        // re-create database
        onCreate(db);
    }

    public static ContentValues getArtContentValues(Artwork artwork) {

        ContentValues values = new ContentValues();
        values.put(ArtsContract.ArtsEntry.ART_WEB_LINK, artwork.getWebLink());
        values.put(ArtsContract.ArtsEntry.ART_ID, artwork.getId());
        values.put(ArtsContract.ArtsEntry.ART_LONG_ID, artwork.getLongId());
        values.put(ArtsContract.ArtsEntry.ART_HAS_IMAGE, artwork.getHasImage()?1:0);
        values.put(ArtsContract.ArtsEntry.ART_OBJECT_NUMBER, artwork.getObjectNumber());
        values.put(ArtsContract.ArtsEntry.ART_TITLE, artwork.getTitle());
        values.put(ArtsContract.ArtsEntry.ART_HAS_IMAGE, artwork.getHasImage()?1:0);
        values.put(ArtsContract.ArtsEntry.ART_MAKER, artwork.getPrincipalOrFirstMaker());
        values.put(ArtsContract.ArtsEntry.ART_lONG_TITLE, artwork.getLongTitle());
        values.put(ArtsContract.ArtsEntry.ART_SHOW_IMAGE, artwork.getShowImage()?1:0);
        values.put(ArtsContract.ArtsEntry.ART_PERMIT_DOWNLOAD, artwork.getPermitDownload()?1:0);
        values.put(ArtsContract.ArtsEntry.ART_WEB_IMAGE, artwork.getOriginalImage());
        values.put(ArtsContract.ArtsEntry.ART_WEB_IMAGE_HEIGHT, artwork.getWebImage().getHeight());
        values.put(ArtsContract.ArtsEntry.ART_WEB_IMAGE_WIDTH, artwork.getWebImage().getWidth());

        return values;
    }
}
