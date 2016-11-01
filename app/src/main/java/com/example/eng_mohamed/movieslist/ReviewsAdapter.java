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
public class ReviewsAdapter extends ArrayAdapter<ReviewData> {
    private Context context;
    private LayoutInflater inflater;

    private List<ReviewData> imageUrls;

    public ReviewsAdapter(Context context, List<ReviewData> imageUrls) {
        super(context,0, imageUrls);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.imageUrls = imageUrls;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReviewData reviewData=getItem(position);
        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, parent, false);
        }
        TextView textView=(TextView)convertView.findViewById(R.id.review_author);
        textView.setText(reviewData.author);
        TextView text=(TextView)convertView.findViewById(R.id.review_content);
        text.setText(reviewData.review);

        return convertView;

    }




}
