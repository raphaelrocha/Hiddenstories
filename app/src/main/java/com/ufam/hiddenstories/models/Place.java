package com.ufam.hiddenstories.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rli on 12/11/2015.
 */
public class Place implements Parcelable {

    private String id;

    private String name;
    private String description;
    private String addr;
    private String location;
    private String picturePlace;

    private String category;
    private String district;
    private String city;
    private String state;
    private String country;

    private String idDistrict;
    private String idCategory;
    private String idCity;
    private String idState;
    private String idCountry;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIdDistrict() {
        return idDistrict;
    }

    public void setIdDistrict(String idDistrict) {
        this.idDistrict = idDistrict;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public String getIdCity() {
        return idCity;
    }

    public void setIdCity(String idCity) {
        this.idCity = idCity;
    }

    public String getIdState() {
        return idState;
    }

    public void setIdState(String idState) {
        this.idState = idState;
    }

    public String getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(String idCountry) {
        this.idCountry = idCountry;
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
        dest.writeString( getIdCategory() );
        dest.writeString( getIdDistrict() );
        dest.writeString( getIdCity() );
        dest.writeString( getIdCountry() );
        dest.writeString( getIdState() );
        dest.writeString( getCategory() );

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
        setIdDistrict(parcel.readString());
        setIdCity(parcel.readString());
        setIdState(parcel.readString());
        setIdCountry(parcel.readString());
        setIdCategory(parcel.readString());
        setCategory(parcel.readString());

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
