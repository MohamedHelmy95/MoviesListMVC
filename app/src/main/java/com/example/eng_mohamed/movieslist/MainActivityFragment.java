package com.example.eng_mohamed.movieslist;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }
    String categorie=null;
    ImageAdapter adapt;
    MovieHandler handler;
    ArrayList<MovieData>detailList;
    MovieData[] ds= {};
    NetworkStatus status;
    int mGrid_position;
    @Override
    public void onStart() {
        super.onStart();
        status = new NetworkStatus(getActivity());
        if (status.haveNetworkConnection() && status.isOnline()) {
            Toast.makeText(getActivity(),"Connected",Toast.LENGTH_LONG).show();
            categorie = getActivity().getSharedPreferences("sort", Context.MODE_PRIVATE).getString("sort", getString(R.string.action_popular));
            SharedPreferences preferences = getActivity().getSharedPreferences("sort", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("sort", categorie);
            editor.apply();
            if (categorie.equals(getString(R.string.action_favorites))) {
                getMoviesAndPreview();
            } else {
                new FetchMovies(categorie).execute();
            }
        } else {
            getMoviesAndPreview();
            Toast.makeText(getActivity(),"Check Your Internet Connection",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("details",detailList);
        if(mGrid_position!=GridView.INVALID_POSITION){
            outState.putInt("position",mGrid_position);
        }
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null||!savedInstanceState.containsKey("details")) {
            detailList = new ArrayList<MovieData>(Arrays.asList(ds));
        }
        else{detailList=savedInstanceState.getParcelableArrayList("details");}

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView grid = (GridView) rootView.findViewById(R.id.movies_grid);
        handler= (MovieHandler) getActivity();
        if(savedInstanceState!=null&&savedInstanceState.containsKey("position")){
            mGrid_position=savedInstanceState.getInt("position");
            grid.smoothScrollToPosition(mGrid_position);
        }
        adapt = new ImageAdapter(getActivity(),detailList);
        grid.setAdapter(adapt);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           handler.setSelectedMovie( adapt.getItem(position));
            mGrid_position=position;



            }
        });

        return rootView;
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.moviecategories, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        item.setChecked(true);
        SharedPreferences preferences= getActivity().getSharedPreferences("sort", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        if (id == R.id.popular) {
            if (!(status.haveNetworkConnection()&&status.isOnline())) {
                Toast.makeText(getActivity(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
                getMoviesAndPreview();
            }else {
                editor.putString("sort", getString(R.string.action_popular));
                editor.apply();
                categorie = getActivity().getSharedPreferences("sort", Context.MODE_PRIVATE).getString("sort", "Popular");
                new FetchMovies(categorie).execute();
            }
        }
        else if (id == R.id.top_rated) {
            if (!(status.haveNetworkConnection()&&status.isOnline())) {
                Toast.makeText(getActivity(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
                getMoviesAndPreview();
            }else {
                editor.putString("sort", getString(R.string.action_top_rated));
                editor.apply();
                categorie = getActivity().getSharedPreferences("sort", Context.MODE_PRIVATE).getString("sort", "Top_Rated");
                new FetchMovies(categorie).execute();
            }
      }
      else if (id == R.id.favorites) {
            if (!(status.haveNetworkConnection()&&status.isOnline())) {
                Toast.makeText(getActivity(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
                getMoviesAndPreview();
            }else {
          editor.putString("sort",getString(R.string.action_favorites));
          editor.apply();
          getMoviesAndPreview();
            }
        }
            return true;
        }

    public void getMoviesAndPreview() {
        List<MovieData>movieDataList= new ArrayList<>();
        Cursor table=getActivity().getContentResolver().query(MoviesContract.FavoritesColumns.CONTENT_URI,null,null,null,null);


        try {
            while (table.moveToNext()) {
            Log.d("!!!URL!!!",table.getString(0));
                Log.d("!!!eeeeeeeeeeeeee!!!",String.valueOf(table.getLong(6)));
                movieDataList.add(new MovieData(table.getString(1),table.getString(2),table.getString(3),table.getString(4),table.getString(5),table.getLong(6),table.getString(7)));
            }
        } finally {
            table.close();

        }
        adapt.clear();
        for(int i=0;i<movieDataList.size();i++)
            adapt.add(movieDataList.get(i));

    }
    class FetchMovies extends AsyncTask<String, Void,List<MovieData>> {


        private final String LOG_TAG = FetchMovies.class.getSimpleName();
        String Type;

        public FetchMovies(String sort) {
            Type = sort;
        }

        @Override
        protected List<MovieData> doInBackground(String... strings) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String JsonStr = null;


            try {


                String API_url = null;
                if (Type.equals(getString(R.string.action_popular))) {
                    API_url = "http://api.themoviedb.org/3/movie/popular";
                } else if (Type.equals(getString(R.string.action_top_rated))) {
                    API_url = "http://api.themoviedb.org/3/movie/top_rated";
                }
                Uri builtUri = Uri.parse(API_url).buildUpon()
                        .appendQueryParameter("api_key", getString(R.string.Appi_key)).build();
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI" + builtUri.toString());

                // Create the request to , themoviedb and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                JsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(JsonStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private List<MovieData> getMoviesDataFromJson(String JsonStr)
                throws JSONException, NullPointerException {

            // These are the names of the JSON objects that need to be extracted.
            final String LIST = "results";
            final String Over = "overview";
            final String O_title = "original_title";
            final String json_poster = "poster_path";
            final String OWM_Date = "release_date";
            final String avrage = "vote_average";
            final String ID = "id";
            final String json_img = "backdrop_path";

            JSONObject forecastJson = new JSONObject(JsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(LIST);

            List<MovieData> detailList = new ArrayList<>();
            //String[] detailList = new String[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                String poster;
                String date;
                String overView;
                String title;
                String Avg;
                long M_ID;
                String back_drop;
                // Get the JSON object representing the day
                JSONObject movieObject = movieArray.getJSONObject(i);


                poster = movieObject.getString(json_poster);
                overView = movieObject.getString(Over);
                date = movieObject.getString(OWM_Date);
                title = movieObject.getString(O_title);
                Avg = movieObject.getString(avrage);
                M_ID=movieObject.getLong(ID);
                back_drop="http://image.tmdb.org/t/p/original"+movieObject.getString(json_img);
                StringBuffer buffer = new StringBuffer("http://image.tmdb.org/t/p/w185");
                buffer.append(poster);
                //Log.e("all movies","all : "+buffer.toString());

                detailList.add(new MovieData(buffer.toString(), date, overView, title, Avg,M_ID,back_drop));

                Log.d("Image" + i, detailList.get(i).poster);


            }
            return detailList;

        }


        @Override
        protected void onPostExecute(List<MovieData> detailList) {
            super.onPostExecute(detailList);
            adapt.clear();
            for (int i = 0; i < detailList.size(); i++)
                adapt.add(detailList.get(i));

        }

    }
        public void setHandler(MovieHandler handler){
    this.handler=handler;
}
}
