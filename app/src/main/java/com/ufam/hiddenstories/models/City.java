package com.ufam.hiddenstories.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rli on 05/01/2016.
 */
public class City implements Parcelable{
    private String id;
    private String name;
    private String dateTime;
    private State state;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

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

    public City(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString( getId() );
        dest.writeString( getName() );
        dest.writeString( getDateTime() );
        dest.writeParcelable( getState() ,flags);
    }

    public City(Parcel parcel){
        setId(parcel.readString());
        setName(parcel.readString());
        setDateTime(parcel.readString());
        setState((State) parcel.readParcelable(State.class.getClassLoader()));
    }

    public static final Creator<City> CREATOR = new Creator<City>(){
        @Override
        public City createFromParcel(Parcel source) {
            return new City(source);
        }
        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };
}
