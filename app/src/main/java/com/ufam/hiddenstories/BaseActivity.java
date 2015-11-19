package com.ufam.hiddenstories;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.ufam.hiddenstories.conn.ServerInfo;
import com.ufam.hiddenstories.models.Place;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public List<Place> popListPlaces(JSONArray ja){
        List<Place> listAux = new ArrayList<>();

        Place place1 = new Place();
        place1.setId("1");
        place1.setName("Teatro Amazonas");
        place1.setDescription(getResources().getString(R.string.tmp_place_description));
        place1.setAddr("Largo São Sebastião / CENTRO");
        place1.setDistrict("Centro");
        place1.setCity("Manaus");
        place1.setCountry("Brasil");
        place1.setPicturePlace(ServerInfo.imageFolder+"teatro_amazonas.jpg");
        place1.setLocation("123;456");
        listAux.add(place1);

        Place place2 = new Place();
        place2.setId("2");
        place2.setName("Alfândega");
        place2.setDescription(getResources().getString(R.string.tmp_alfandega));
        place2.setAddr("Getílio Vargas");
        place2.setDistrict("Centro");
        place2.setCity("Manaus");
        place2.setCountry("Brasil");
        place2.setPicturePlace(ServerInfo.imageFolder+"alfandega.jpg");
        place2.setLocation("123;456");
        listAux.add(place2);

        return(listAux);
    }

}
