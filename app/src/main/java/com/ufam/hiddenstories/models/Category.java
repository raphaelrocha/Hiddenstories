package com.ufam.hiddenstories.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rli on 19/11/2015.
 */
public class Category implements  Parcelable{
    private String id;
    private String name;
    private String picture;

    public Category(){}

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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString( getId() );
        dest.writeString( getName() );
        dest.writeString( getPicture() );
    }

    public Category(Parcel parcel){
        setId(parcel.readString());
        setName(parcel.readString());
        setPicture(parcel.readString());
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>(){
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }
        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
