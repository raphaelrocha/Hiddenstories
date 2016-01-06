package com.ufam.hiddenstories.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rli on 28/11/2015.
 */
public class Commentary implements Parcelable {

    private String id;
    private User user;
    private Place place;
    private String text;
    private String dateTime;
    private Rating rating;

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

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

    public Commentary(){
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( getId() );
        dest.writeString(getText());
        dest.writeString(getDateTime());
        dest.writeParcelable( getUser() ,flags);
        dest.writeParcelable( getPlace() ,flags);
        dest.writeParcelable( getRating() ,flags);
    }

    public Commentary(Parcel parcel){
        setId(parcel.readString());
        setText(parcel.readString());
        setDateTime(parcel.readString());
        setUser((User) parcel.readParcelable(User.class.getClassLoader()));
        setPlace((Place) parcel.readParcelable(Place.class.getClassLoader()));
        setRating((Rating) parcel.readParcelable(Rating.class.getClassLoader()));
    }

    public static final Creator<Commentary> CREATOR = new Creator<Commentary>(){
        @Override
        public Commentary createFromParcel(Parcel source) {
            return new Commentary(source);
        }
        @Override
        public Commentary[] newArray(int size) {
            return new Commentary[size];
        }
    };
}
