package com.ufam.hiddenstories.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rli on 28/11/2015.
 */
public class Rating implements Parcelable {

    private String id;
    private User user;
    private Place place;
    private String value;
    private String text;
    private String dateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Rating(){
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( getId() );
        dest.writeString( getValue() );
        dest.writeString(getText());
        dest.writeString(getDateTime());
        dest.writeParcelable( getUser() ,flags);
        dest.writeParcelable( getPlace() ,flags);
    }

    public Rating(Parcel parcel){
        setId(parcel.readString());
        setValue(parcel.readString());
        setText(parcel.readString());
        setDateTime(parcel.readString());
        setUser((User) parcel.readParcelable(User.class.getClassLoader()));
        setPlace((Place) parcel.readParcelable(Place.class.getClassLoader()));
    }

    public static final Parcelable.Creator<Rating> CREATOR = new Parcelable.Creator<Rating>(){
        @Override
        public Rating createFromParcel(Parcel source) {
            return new Rating(source);
        }
        @Override
        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };
}
