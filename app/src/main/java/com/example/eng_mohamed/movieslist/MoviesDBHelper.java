package com.example.eng_mohamed.movieslist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

/**
 * Created by Eng_Mohamed on 8/23/2016.
 */
public class MoviesDBHelper extends SQLiteOpenHelper {
    public MoviesDBHelper(Context context) {
        super(context, MoviesContract.DB_NAME, null, MoviesContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    String favorites=  String.format("CREATE TABLE %s (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " %s TEXT , "+ "%s TEXT , "+ "%s TEXT , "+ "%s TEXT , "+ "%s TEXT , "+ "%s LONG , %s TEXT )",
            MoviesContract.FavoritesColumns.TABLE,
            MoviesContract.FavoritesColumns.Movie_Poster,
            MoviesContract.FavoritesColumns.Movie_ReleaseDate,
            MoviesContract.FavoritesColumns.Movie_Overview,
            MoviesContract.FavoritesColumns.Movie_Title,
            MoviesContract.FavoritesColumns.Movie_VoteAvg,
            MoviesContract.FavoritesColumns.Movie_ID,
            MoviesContract.FavoritesColumns.Movie_BackDrop
    );

        Log.e("SQL QUERY!!!",favorites);
        sqLiteDatabase.execSQL(favorites);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoritesColumns.TABLE);
        onCreate(sqLiteDatabase);
    }

}
