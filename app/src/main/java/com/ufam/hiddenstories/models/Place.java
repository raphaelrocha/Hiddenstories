package com.ufam.hiddenstories.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rli on 12/11/2015.
 */
public class Place implements Parcelable {

    private String id;
    private District district;
    private Category category;
    private String name;
    private String description;
    private String addr;
    private String latitude;
    private String longitude;
    private String picturePlace;
    private String dateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPicturePlace() {
        return picturePlace;
    }

    public void setPicturePlace(String picturePlace) {
        this.picturePlace = picturePlace;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Place(){
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
        dest.writeString(getLatitude());
        dest.writeString( getPicturePlace() );
        dest.writeString( getLongitude() );
        dest.writeParcelable( getDistrict() ,flags);
        dest.writeParcelable( getCategory() ,flags);
    }

    public Place(Parcel parcel){
        setId(parcel.readString());
        setName(parcel.readString());
        setAddr(parcel.readString());
        setDescription(parcel.readString());
        setLatitude(parcel.readString());
        setPicturePlace(parcel.readString());
        setLongitude(parcel.readString());
        setDistrict((District) parcel.readParcelable(District.class.getClassLoader()));
        setCategory((Category) parcel.readParcelable(Category.class.getClassLoader()));
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
