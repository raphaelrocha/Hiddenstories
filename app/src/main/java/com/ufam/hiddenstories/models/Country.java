package com.ufam.hiddenstories.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rli on 05/01/2016.
 */
public class Country implements Parcelable{
    private String id;
    private String name;
    private String dateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Country(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString( getId() );
        dest.writeString( getName() );
        dest.writeString( getDateTime() );
    }

    public Country(Parcel parcel){
        setId(parcel.readString());
        setName(parcel.readString());
        setDateTime(parcel.readString());
    }

    public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator<Country>(){
        @Override
        public Country createFromParcel(Parcel source) {
            return new Country(source);
        }
        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };
}
