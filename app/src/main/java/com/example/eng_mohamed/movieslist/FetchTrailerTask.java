package com.example.eng_mohamed.movieslist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.List;

/**
 * Created by Eng_Mohamed on 9/3/2016.
 */
public class FetchTrailerTask extends AsyncTask<Long , Void,List<TrailerData>> {
    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();
    List<TrailerData> mydata;
    List<TrailerData> trailerDataList = new ArrayList<>();
    TrailerAdapter adapter;
    Context context;
    View rootView;

    public FetchTrailerTask(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
    }

    public FetchTrailerTask() {
    }

    @Override
    protected List<TrailerData> doInBackground(Long... longs) {
        String base = "http://api.themoviedb.org/3/movie";
        Log.e("dawfsfsfd!!!!", longs[0] + "");
        Uri builder = Uri.parse(base).buildUpon()
                .appendPath(longs[0].toString()).appendPath("videos")
                .appendQueryParameter("api_key", context.getString(R.string.Appi_key)).build();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JsonStr = null;
        try {
            URL url = new URL(builder.toString());
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
            Log.e("dataaaaaaaa ", JsonStr);

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
            return getTrailersFromJson(JsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private List<TrailerData> getTrailersFromJson(String JsonStr) throws JSONException, NullPointerException {
        final String LIST = "results";
        final String key = "key";
        final String name = "name";
        final String site = "site";
        JSONObject Json = new JSONObject(JsonStr);
        JSONArray trailerArray = Json.getJSONArray(LIST);
        List<TrailerData> trailerList = new ArrayList<>();
        for (int i = 0; i < trailerArray.length(); i++) {
            String Link;
            String Name;
            String Site;

            JSONObject trailerObject = trailerArray.getJSONObject(i);
            Link = trailerObject.getString(key);
            Name = trailerObject.getString(name);
            Site = trailerObject.getString(site);
            StringBuffer buffer = new StringBuffer("https://www.youtube.com/watch?v=");
            buffer.append(Link);
            Link = buffer.toString();
            trailerList.add(new TrailerData(Link, Name, Site));


        }

        return trailerList;
    }

    @Override
    protected void onPostExecute(List<TrailerData> trailerDatas) {
        super.onPostExecute(trailerDatas);
        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.videosLinear);
        if (linearLayout == null) {
            ListView list = (ListView) rootView.findViewById(R.id.videosList);
            trailerDataList = trailerDatas;
            adapter = new TrailerAdapter(context, trailerDataList);

            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    TrailerData trailerData = trailerDataList.get(position);
                    Uri http = Uri.parse(trailerData.Link).buildUpon().build();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(http);
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);

                    }
                }
            });


        } else {
            View view;
            LayoutInflater inflater = LayoutInflater.from(context);

            for ( final TrailerData trailerData : trailerDatas) {

                view = inflater.inflate(R.layout.videos_list_item, linearLayout, false);
                ImageView imageView = (ImageView) view.findViewById(R.id.Site_logo);
                if (trailerData.Site.equals("YouTube"))
                    imageView.setImageResource(R.drawable.youtube);
                else
                    imageView.setImageResource(R.drawable.other);
                TextView textView = (TextView) view.findViewById(R.id.video_title);
                textView.setText(trailerData.Title);
                linearLayout.addView(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String str=trailerData.Link;
                        Uri http = Uri.parse(str).buildUpon().build();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(http);
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);

                        }
                    }
                });

            }


        }
    }
}