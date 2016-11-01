package com.example.eng_mohamed.movieslist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eng_Mohamed on 9/3/2016.
 */
public class TrailerData implements Parcelable{

    String Link;
    String Title;
    String Site;

    public TrailerData(String link, String name, String site) {
        Link = link;
        this.Title = name;
        Site = site;
    }

    protected TrailerData(Parcel in) {
        Link = in.readString();
        Title = in.readString();
        Site = in.readString();
    }

    public static final Creator<TrailerData> CREATOR = new Creator<TrailerData>() {
        @Override
        public TrailerData createFromParcel(Parcel in) {
            return new TrailerData(in);
        }

        @Override
        public TrailerData[] newArray(int size) {
            return new TrailerData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Link);
        parcel.writeString(Title);
        parcel.writeString(Site);
    }
}
