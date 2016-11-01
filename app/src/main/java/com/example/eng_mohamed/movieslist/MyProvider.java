package com.example.eng_mohamed.movieslist;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Eng_Mohamed on 9/1/2016.
 */
public class MyProvider extends ContentProvider {
    private SQLiteDatabase db;
    private static final UriMatcher matcher = buildUriMatcher();
    private MoviesDBHelper moviesDBHelper;
    public static final int FAVORITES_LIST = 1;
    public static final int FAVORITES_ITEM = 2;
    public static final int MOVIES_LIST = 3;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MoviesContract.Favorites,FAVORITES_LIST);
        matcher.addURI(authority, MoviesContract.Favorites +"/#" ,FAVORITES_ITEM);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        boolean ret = true;
        moviesDBHelper = new MoviesDBHelper(getContext());
        db = moviesDBHelper.getWritableDatabase();

        if (db == null) {
            ret = false;
        }

        if (db.isReadOnly()) {
            db.close();
            db = null;
            ret = false;
        }
        return ret;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
    String sortOrder) {
        Cursor cursor;

        switch (matcher.match(uri)) {
            case FAVORITES_LIST: {
                cursor = moviesDBHelper.getReadableDatabase().query(
                        MoviesContract.FavoritesColumns.TABLE, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            }
            case FAVORITES_ITEM: {
                long id=MoviesContract.FavoritesColumns.getMovie_ID(uri);

                cursor = moviesDBHelper.getReadableDatabase().query(
                        MoviesContract.FavoritesColumns.TABLE, projection, MoviesContract.FavoritesColumns.Movie_ID+"=?", new String[]{id+""},
                        null, null, sortOrder);


                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = matcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case FAVORITES_LIST:
                return MoviesContract.FavoritesColumns.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final SQLiteDatabase database = moviesDBHelper.getWritableDatabase();
        Uri uri1;

        switch (matcher.match(uri)) {
            case FAVORITES_LIST: {
                long _id = database.insert(MoviesContract.FavoritesColumns.TABLE, null, contentValues);
                if (_id > 0) {
                    uri1 = MoviesContract.FavoritesColumns.buildFavoriteUri(_id);
                }

                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return uri1;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDBHelper.getWritableDatabase();
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (matcher.match(uri)) {
            case FAVORITES_LIST: {
                rowsDeleted = db.delete(
                        MoviesContract.FavoritesColumns.TABLE, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDBHelper.getWritableDatabase();
        int rowsUpdated;

        switch (matcher.match(uri)) {
            case FAVORITES_LIST: {
                rowsUpdated = db.update(MoviesContract.FavoritesColumns.TABLE, contentValues, selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        moviesDBHelper.close();
        db.close();
    }
}
