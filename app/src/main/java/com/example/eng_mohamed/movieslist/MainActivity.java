package com.example.eng_mohamed.movieslist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MovieHandler {
    MovieData movieData;
    static boolean mTwoPane;
    MainActivityFragment mainActivityFragment = new MainActivityFragment();
    public MainActivity() {

    }
    NetworkStatus status;


    @Override
    protected void onStart() {
        super.onStart();
        status=new NetworkStatus(this);
        if (!(status.haveNetworkConnection()&&status.isOnline())) {
            Toast.makeText(this,"Check Your Internet Connection",Toast.LENGTH_LONG).show();
            SharedPreferences preferences = this.getSharedPreferences("sort", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("sort", getString(R.string.action_favorites));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FrameLayout flPanel2 = (FrameLayout) findViewById(R.id.flPanel_Two);
        if (null == flPanel2) {
            mTwoPane = false;
        } else {
            mTwoPane = true;
        }

        if (null == savedInstanceState) {
            status=new NetworkStatus(this);



            mainActivityFragment.setHandler(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.flPanel_One, mainActivityFragment).commit();

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void setSelectedMovie(MovieData selectedMovie) {
        movieData=selectedMovie;
            //Case Two Pane UI
            if (mTwoPane) {
                Movie_DetailFragment detailsFragment = new Movie_DetailFragment();
                Bundle extras = new Bundle();
                extras.putParcelable("movie", movieData);
                detailsFragment.setArguments(extras);
                getSupportFragmentManager().beginTransaction().replace(R.id.flPanel_Two, detailsFragment).commit();
            } else {
                //Case Single Pane UI
                Intent i = new Intent(this, Movie_Detail.class);
                i.putExtra("movie", selectedMovie);
                startActivity(i);
            }
        }

    public static boolean getPaneStatus(){
        boolean paneStatus=mTwoPane;
    return  paneStatus;
    }
}
