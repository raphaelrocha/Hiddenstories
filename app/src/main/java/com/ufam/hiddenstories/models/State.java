package com.ufam.hiddenstories.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rli on 05/01/2016.
 */
public class State implements Parcelable{
    private String id;
    private String name;
    private String dateTime;
    private Country country;

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
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

    public State(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString( getId() );
        dest.writeString( getName() );
        dest.writeString( getDateTime() );
        dest.writeParcelable( getCountry() ,flags);
    }

    public State(Parcel parcel){
        setId(parcel.readString());
        setName(parcel.readString());
        setDateTime(parcel.readString());
        setCountry((Country) parcel.readParcelable(Country.class.getClassLoader()));
    }

    public static final Creator<State> CREATOR = new Creator<State>(){
        @Override
        public State createFromParcel(Parcel source) {
            return new State(source);
        }
        @Override
        public State[] newArray(int size) {
            return new State[size];
        }
    };
}
