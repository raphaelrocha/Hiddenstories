package com.ufam.hiddenstories.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rli on 17/12/2015.
 */
public class Picture implements Parcelable{
    String id,idPlace,file,text,dateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(String idPlace) {
        this.idPlace = idPlace;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
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

    public Picture(){}

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString( getId() );
        dest.writeString( getIdPlace() );
        dest.writeString( getFile() );
        dest.writeString( getText() );
        dest.writeString( getDateTime() );
    }

    public Picture(Parcel parcel){
        setId(parcel.readString());
        setIdPlace(parcel.readString());
        setFile(parcel.readString());
        setText(parcel.readString());
        setDateTime(parcel.readString());
    }

    public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>(){
        @Override
        public Picture createFromParcel(Parcel source) {
            return new Picture(source);
        }
        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };
}
