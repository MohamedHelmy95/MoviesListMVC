package com.example.eng_mohamed.movieslist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eng_Mohamed on 9/4/2016.
 */
public class ReviewData implements Parcelable {
    String author;
    String review;
    String link;

    public ReviewData(String author, String review,String link) {
        this.author = author;
        this.review = review;
        this.link = link;
    }

    protected ReviewData(Parcel in) {
        author = in.readString();
        review = in.readString();
        link=in.readString();
    }

    public static final Creator<ReviewData> CREATOR = new Creator<ReviewData>() {
        @Override
        public ReviewData createFromParcel(Parcel in) {
            return new ReviewData(in);
        }

        @Override
        public ReviewData[] newArray(int size) {
            return new ReviewData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(review);
        parcel.writeString(link);
    }
}
