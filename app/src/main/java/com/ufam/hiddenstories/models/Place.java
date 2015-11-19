package com.ufam.hiddenstories.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rli on 12/11/2015.
 */
public class Place implements Parcelable {
    private String id;
    private String district;
    private String city;
    private String country;
    private String name;
    private String description;
    private String addr;
    private String location;
    private String picturePlace;

    public Place(){

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPicturePlace() {
        return picturePlace;
    }

    public void setPicturePlace(String picturePlace) {
        this.picturePlace = picturePlace;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString( getId() );
        dest.writeString( getName() );
        dest.writeString( getAddr() );
        dest.writeString( getDescription() );
        dest.writeString(getCity());
        dest.writeString( getCountry() );
        dest.writeString(getLocation());
        dest.writeString( getPicturePlace() );
        dest.writeString( getLocation() );
        dest.writeString( getDistrict() );
    }

    public Place(Parcel parcel){
        setId(parcel.readString());
        setName(parcel.readString());
        setAddr(parcel.readString());
        setDescription(parcel.readString());
        setCity(parcel.readString());
        setCountry(parcel.readString());
        setLocation(parcel.readString());
        setPicturePlace(parcel.readString());
        setLocation(parcel.readString());
        setDistrict(parcel.readString());
    }

    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>(){
        @Override
        public Place createFromParcel(Parcel source) {
            return new Place(source);
        }
        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}
