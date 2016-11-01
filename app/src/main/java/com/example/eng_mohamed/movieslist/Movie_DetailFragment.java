package com.example.eng_mohamed.movieslist;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class Movie_DetailFragment extends Fragment {
    MovieData movie;
    public Movie_DetailFragment() {

    }

    String ImWhere;
    FloatingActionButton fab;

    NetworkStatus status;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ImWhere=getActivity().getSharedPreferences("sort", Context.MODE_PRIVATE).getString("sort","");
        movie=getArguments().getParcelable("movie");
        status=new NetworkStatus(getActivity());
        if (!(status.haveNetworkConnection()&&status.isOnline())) {
            Toast.makeText(getActivity(),"Check Your Internet Connection",Toast.LENGTH_LONG).show();
        }else {
            new FetchTrailerTask(getActivity(), rootView).execute(new Long[]{movie.ID});
            new FetchReviewsTask(getActivity(), rootView).execute(new Long[]{movie.ID});
        }
       TextView title=(TextView) rootView.findViewById(R.id.detail_title);
        title.setText(movie.title);
        TextView ov_view=(TextView) rootView.findViewById(R.id.detail_overview);
        ov_view.setText(movie.overview);
        TextView date=(TextView)rootView.findViewById(R.id.detail_releaseDate);
        date.setText(movie.release_date);
        TextView rate=(TextView)rootView.findViewById(R.id.detail_rate);
        rate.setText(String.format(" %s/10",movie.vote_avrage));
        ImageView poster=(ImageView)rootView.findViewById(R.id.detail_poster);
        Picasso.with(getContext()).load(movie.poster).placeholder(R.drawable.placeholder).error(R.drawable.fallback).into(poster);
        fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fabcolor)));
        fab.setImageResource(decideImage(ImWhere));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(ImWhere.equals(getString(R.string.action_favorites))){
                   if(checkIfFound(movie.ID)) {
                       deleteMovie(movie.title);
                       fab.setImageResource(R.drawable.add_favorite);
                       Toast.makeText(getActivity(), "Removed From Favorites", Toast.LENGTH_LONG).show();
                   }
                   else {
                       AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                       builder.setTitle("Add A Movie");
                       builder.setMessage("Are you sure you want to add it again?");
                       builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                                addMovie(movie);
                            fab.setImageResource(R.drawable.ic_favorite);
                           }
                       });

                       builder.setNegativeButton("Cancel",null);
                       builder.create().show();

                   }
               }else{
                    if(checkIfFound(movie.ID)){
                        Toast.makeText(getActivity(), "Already In Favorites", Toast.LENGTH_LONG).show();
                    }
                    else {
                        addMovie(movie);
                        fab.setImageResource(R.drawable.ic_favorite);
                        Toast.makeText(getActivity(), "Added To Favorites", Toast.LENGTH_LONG).show();
                    }
               }

            }
        });

        return rootView;
    }
public boolean checkIfFound(Long movieID){
    Uri uri=MoviesContract.FavoritesColumns.buildFavoriteUri(movieID);
    Cursor table=getActivity().getContentResolver().query(uri,null,null,null,null);

    boolean found = false;
    Log.e("fun!!!",String.valueOf(movieID));
       if (table.getCount()==0)
           found=false;
    else
       found=true;
//    try {
//        assert table != null;
//        while (table.moveToNext()) {
//            if(table.getLong(6)==movieID){
//                found=true;
//                Log.e("found fun!!!",String.valueOf(table.getLong(6)));
//            }
//        }
//
//    } finally {
//
//        assert table != null;
//        table.close();
//
//    }
return found;
}
public void addMovie(MovieData movie){
    ContentValues values = new ContentValues();
    values.clear();
    values.put(MoviesContract.FavoritesColumns.Movie_Poster, movie.poster);
    values.put(MoviesContract.FavoritesColumns.Movie_ReleaseDate, movie.release_date);
    values.put(MoviesContract.FavoritesColumns.Movie_Overview, movie.overview);
    values.put(MoviesContract.FavoritesColumns.Movie_Title, movie.title);
    values.put(MoviesContract.FavoritesColumns.Movie_VoteAvg, movie.vote_avrage);
    values.put(MoviesContract.FavoritesColumns.Movie_ID, movie.ID);
    values.put(MoviesContract.FavoritesColumns.Movie_BackDrop,movie.big_image);
    getActivity().getContentResolver().insert(MoviesContract.FavoritesColumns.CONTENT_URI, values);

}
    public void deleteMovie(String title){
        getActivity().getContentResolver().delete(MoviesContract.FavoritesColumns.CONTENT_URI,
                MoviesContract.FavoritesColumns.Movie_Title + "=?",
                new String[]{title});

    }
    public int decideImage(String ImWhere)
    {
         if(ImWhere.equals(R.string.action_favorites)) {
             return R.drawable.ic_favorite;
        }
         else {

             if (checkIfFound(movie.ID)) {

                 return R.drawable.ic_favorite ;
             } else {

                 return R.drawable.add_favorite;
             }

         }
    }



}
