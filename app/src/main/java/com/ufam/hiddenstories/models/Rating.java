package com.ufam.hiddenstories.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rli on 28/11/2015.
 */
public class Rating implements Parcelable {

    String id;
    String idUser;
    String idPlace;
    String value;
    String text;
    String nameUser;
    String emailUser;
    String dateTime;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public Rating(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(String idPlace) {
        this.idPlace = idPlace;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString( getId() );
        dest.writeString( getIdUser() );
        dest.writeString( getIdPlace() );
        dest.writeString( getValue() );
        dest.writeString(getText());
    }

    public Rating(Parcel parcel){
        setId(parcel.readString());
        setIdUser(parcel.readString());
        setIdPlace(parcel.readString());
        setValue(parcel.readString());
        setText(parcel.readString());
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
