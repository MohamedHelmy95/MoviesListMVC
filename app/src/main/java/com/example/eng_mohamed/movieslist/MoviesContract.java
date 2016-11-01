package com.example.eng_mohamed.movieslist;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Eng_Mohamed on 8/22/2016.
 */
public class MoviesContract implements BaseColumns {
    public static final String DB_NAME = "com.example.eng_mohamed.movieslist";
    public static final int DB_VERSION = 1;
    public static final String AUTHORITY = "com.example.eng_mohamed.movieslist.favoritesProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String Favorites = "favorites";

    public static class FavoritesColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(Favorites).build();

        public static final String TABLE = "favorites";
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"+AUTHORITY+"/" + TABLE;

        public static final String Movie_Poster = "PosterURL";
        public static final String Movie_Title = "name";
        public static final String Movie_ReleaseDate = "Release_Date";
        public static final String Movie_Overview = "Overview";
        public static final String Movie_VoteAvg = "Vote_Average";
        public static final String Movie_ID = "Site_ID";
        public static final String Movie_BackDrop = "Back_Drop";

        public static final String _ID = BaseColumns._ID;

        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getMovie_ID(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }



}
