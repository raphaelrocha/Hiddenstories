package com.ufam.hiddenstories.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rli on 05/01/2016.
 */
public class District implements Parcelable{
    private String id;
    private String name;
    private String dateTime;
    private City city;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
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

    public District(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString( getId() );
        dest.writeString( getName() );
        dest.writeString( getDateTime() );
        dest.writeParcelable( getCity() ,flags);
    }

    public District(Parcel parcel){
        setId(parcel.readString());
        setName(parcel.readString());
        setDateTime(parcel.readString());
        setCity((City) parcel.readParcelable(City.class.getClassLoader()));
    }

    public static final Creator<District> CREATOR = new Creator<District>(){
        @Override
        public District createFromParcel(Parcel source) {
            return new District(source);
        }
        @Override
        public District[] newArray(int size) {
            return new District[size];
        }
    };
}
