package com.ufam.hiddenstories;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.ufam.hiddenstories.conn.ServerInfo;
import com.ufam.hiddenstories.conn.VolleyConnectionQueue;
import com.ufam.hiddenstories.models.Category;
import com.ufam.hiddenstories.models.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VolleyConnectionQueue.getINSTANCE().startQueue(this);

    }

    public Category popListCategory(JSONObject jo){
        Category category = new Category();

        try {
            category.setId(jo.getString("id"));
            category.setName(jo.getString("name"));
            category.setPicture(ServerInfo.imageFolder+jo.getString("picture"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return(category);
    }

    public Place popListPlaces(JSONObject jo){
        Place place = new Place();

        try {
            place.setId(jo.getString("id"));
            place.setIdCategory(jo.getString("id_category"));
            place.setIdDistrict(jo.getString("id_district"));
            place.setIdCity(jo.getString("id_city"));
            place.setIdState(jo.getString("id_state"));
            place.setIdCountry(jo.getString("id_country"));
            place.setName(jo.getString("name"));
            place.setDescription(jo.getString("description"));
            place.setAddr(jo.getString("addr"));
            place.setPicturePlace(ServerInfo.imageFolder+jo.getString("picture_place"));
            place.setLocation(jo.getString("location"));
            place.setCategory(jo.getString("name_category"));
            place.setDistrict(jo.getString("name_district"));
            place.setCity(jo.getString("name_city"));
            place.setState(jo.getString("name_state"));
            place.setCountry(jo.getString("name_country"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return(place);
    }
}
