package com.example.eng_mohamed.movieslist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by Eng_Mohamed on 8/1/2016.
 */
public class ImageAdapter extends ArrayAdapter<MovieData>{

    private Context context;
    private LayoutInflater inflater;

    private List<MovieData> imageUrls;

    public ImageAdapter(Context context, List<MovieData> imageUrls) {
        super(context, 0, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      MovieData movieDetail=getItem(position);
        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie, parent, false);
        }
        ImageView imageView=(ImageView)convertView.findViewById(R.id.grid_item);
        Picasso.with(getContext()).load(movieDetail.poster).placeholder(R.drawable.placeholder).error(R.drawable.fallback).into(imageView);
        TextView textView=(TextView)convertView.findViewById(R.id.Movie_Title);
        textView.setText(movieDetail.title);

        return convertView;

    }


}

