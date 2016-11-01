package com.example.eng_mohamed.movieslist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eng_Mohamed on 8/2/2016.
 */
public class MovieData implements Parcelable {
    String title;
    String poster;
    String release_date;
    String overview;
    String vote_avrage;
    String big_image;
    long ID;
    public MovieData(){}
    public MovieData(String Url, String date, String overView, String Title, String avrage,long ID,String img ){
        this.poster=Url;
        this.release_date=date;
        this.overview=overView;
        this.title=Title;
        this.vote_avrage=avrage;
        this.ID=ID;
        big_image=img;
    }

    protected MovieData(Parcel in) {
        poster = in.readString();
        release_date=in.readString();
        overview=in.readString();
        title=in.readString();
        vote_avrage=in.readString();
        ID=in.readLong();
        big_image=in.readString();
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(poster);
        parcel.writeString(release_date);
        parcel.writeString(overview);
        parcel.writeString(title);
        parcel.writeString(vote_avrage);
        parcel.writeLong(ID);
        parcel.writeString(big_image);
    }
}
