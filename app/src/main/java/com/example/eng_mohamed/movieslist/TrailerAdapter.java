package com.example.eng_mohamed.movieslist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Eng_Mohamed on 9/3/2016.
 */
public class TrailerAdapter extends ArrayAdapter<TrailerData> {
    private Context context;
    private LayoutInflater inflater;

    private List<TrailerData> imageUrls;

    public TrailerAdapter(Context context, List<TrailerData> imageUrls) {
        super(context,0, imageUrls);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.imageUrls = imageUrls;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TrailerData trailerData=getItem(position);
        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.videos_list_item, parent, false);
        }
        ImageView imageView=(ImageView)convertView.findViewById(R.id.Site_logo);
        if (trailerData.Site.equals("YouTube"))
        imageView.setImageResource(R.drawable.youtube);
        else
            imageView.setImageResource(R.drawable.other);
        TextView textView=(TextView)convertView.findViewById(R.id.video_title);
        textView.setText(trailerData.Title);
        return convertView;

    }




}
