package com.ufam.hiddenstories.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rli on 25/11/2015.
 */
public class User implements  Parcelable{
    private String id;
    private String name;
    private String email;
    private String pictureProfile;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(){}

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

    public String getPictureProfile() {
        return pictureProfile;
    }

    public void setPictureProfile(String pictureProfile) {
        this.pictureProfile = pictureProfile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString( getId() );
        dest.writeString( getName() );
        dest.writeString( getEmail() );
        dest.writeString( getPictureProfile() );
    }

    public User(Parcel parcel){
        setId(parcel.readString());
        setName(parcel.readString());
        setEmail(parcel.readString());
        setPictureProfile(parcel.readString());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>(){
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}

