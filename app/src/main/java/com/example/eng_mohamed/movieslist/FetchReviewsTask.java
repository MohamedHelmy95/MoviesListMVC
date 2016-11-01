package com.example.eng_mohamed.movieslist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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
public class FetchReviewsTask extends AsyncTask<Long , Void,List<ReviewData>> {
    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    List<ReviewData> reviewDataList = new ArrayList<>();
    ReviewData reviewData;
    ReviewsAdapter adapter;
    Context context;
    View rootView;
    LayoutInflater inflater;

    public FetchReviewsTask(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
        inflater = LayoutInflater.from(context);
    }

    public FetchReviewsTask() {
    }

    @Override
    protected List<ReviewData> doInBackground(Long... longs) {
        String base = "http://api.themoviedb.org/3/movie";
        Log.e("dawfsfsfd!!!!", longs[0] + "");
        Uri builder = Uri.parse(base).buildUpon()
                .appendPath(longs[0].toString()).appendPath("reviews")
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

    private List<ReviewData> getTrailersFromJson(String JsonStr) throws JSONException, NullPointerException {
        final String LIST = "results";
        final String link = "url";
        final String name = "author";
        final String content = "content";
        JSONObject Json = new JSONObject(JsonStr);
        JSONArray trailerArray = Json.getJSONArray(LIST);
        List<ReviewData> reviewDatas = new ArrayList<>();
        for (int i = 0; i < trailerArray.length(); i++) {
            String Link;
            String Author;
            String Content;

            JSONObject reviewObject = trailerArray.getJSONObject(i);
            Author = reviewObject.getString(name);
            Content = reviewObject.getString(content);
            Link = reviewObject.getString(link);
            reviewDatas.add(new ReviewData(Author, Content, Link));


        }

        return reviewDatas;
    }

    @Override
    protected void onPostExecute(List<ReviewData> reviewDatas) {
        super.onPostExecute(reviewDatas);
        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.reviewsLinear);
        if (linearLayout == null) {
            ListView list = (ListView) rootView.findViewById(R.id.reviewsList);
            reviewDataList = reviewDatas;


            adapter = new ReviewsAdapter(context, reviewDataList);

            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    ReviewData reviewData = reviewDataList.get(position);
                    Uri http = Uri.parse(reviewData.link).buildUpon().build();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(http);
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);

                    }
                }
            });
        } else {
            inflater = LayoutInflater.from(context);
            View view;
            for (final ReviewData reviewData : reviewDatas) {

                view = inflater.inflate(R.layout.review_list_item, linearLayout, false);
                TextView textView = (TextView) view.findViewById(R.id.review_author);
                textView.setText(reviewData.author);
                TextView text = (TextView) view.findViewById(R.id.review_content);
                text.setText(reviewData.review);
                linearLayout.addView(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri http = Uri.parse(reviewData.link).buildUpon().build();
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