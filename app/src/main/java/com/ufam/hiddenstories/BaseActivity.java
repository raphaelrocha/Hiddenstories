package com.ufam.hiddenstories;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.ufam.hiddenstories.conn.ServerInfo;
import com.ufam.hiddenstories.conn.VolleyConnectionQueue;
import com.ufam.hiddenstories.models.Category;
import com.ufam.hiddenstories.models.City;
import com.ufam.hiddenstories.models.Country;
import com.ufam.hiddenstories.models.District;
import com.ufam.hiddenstories.models.Picture;
import com.ufam.hiddenstories.models.Place;
import com.ufam.hiddenstories.models.Rating;
import com.ufam.hiddenstories.models.State;
import com.ufam.hiddenstories.models.User;
import com.ufam.hiddenstories.provider.SearchableProvider;
import com.ufam.hiddenstories.tools.GPSTracker;
import com.ufam.hiddenstories.tools.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    protected ProgressDialog dialog;
    private SessionManager session;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    final String TAG = BaseActivity.this.getClass().getSimpleName();

    @Override
    protected void attachBaseContext(Context base) {
        Log.i(TAG, "install");
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        VolleyConnectionQueue.getINSTANCE().startQueue(this);
        session = new SessionManager(this);

        pref = getApplicationContext().getSharedPreferences("hiddenstories.ufam.com", 0); // 0 - for private mode
        editor = pref.edit();
    }

    protected Context getContext() {
        return this;
    }

    public void forceStartVolleyQueue(){
        VolleyConnectionQueue.getINSTANCE().startQueue(this);
    }


    public Rating popListRating(JSONObject jo)throws JSONException{
        Rating rating = new Rating();

        rating.setId(jo.getString("id_rating"));
        rating.setIdUser(jo.getString("id_user"));
        rating.setIdPlace(jo.getString("id_place"));
        rating.setValue(jo.getString("rating_value"));
        rating.setText(jo.getString("rating_text"));
        rating.setNameUser(jo.getString("name_user"));
        rating.setEmailUser(jo.getString("email_user"));
        rating.setDateTime(jo.getString("date_time"));
        rating.setImageUser(ServerInfo.IMAGE_FOLDER+jo.getString("picture_user"));

        return(rating);
    }

    public Country popCountryObj(JSONObject jo){
        Country c = null;
        try {
            c = new Country();
            c.setId(jo.getString("id"));
            c.setName(jo.getString("name"));
            c.setDateTime(jo.getString("date_time"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return c;
    }

    public State popStateObj(JSONObject jo){
        State s = null;

        try {
            s = new State();
            s.setId(jo.getString("id"));
            s.setName(jo.getString("name"));
            s.setDateTime(jo.getString("date_time"));
            s.setCountry(popCountryObj(jo.getJSONObject("country")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }

    public City popCityObj(JSONObject jo){
        City c = null;

        try {
            c = new City();
            c.setId(jo.getString("id"));
            c.setName(jo.getString("name"));
            c.setDateTime(jo.getString("date_time"));
            c.setState(popStateObj(jo.getJSONObject("state")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return c;
    }

    public District popDistrictObj(JSONObject jo){
        District d = null;

        try {
            d = new District();
            d.setId(jo.getString("id"));
            d.setName(jo.getString("name"));
            d.setDateTime(jo.getString("date_time"));
            d.setCity(popCityObj(jo.getJSONObject("city")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return d;
    }

    public Category popCategoryObj(JSONObject jo){
        Category c = null;

        try {
            c = new Category();
            c.setId(jo.getString("id"));
            c.setName(jo.getString("name"));
            c.setDateTime(jo.getString("date_time"));
            c.setPicture(jo.getString("picture"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return c;
    }

    public Place popPlaceObj(JSONObject jo){
        Place place = new Place();

        try {
            place.setId(jo.getString("id"));
            place.setName(jo.getString("name"));
            place.setDescription(jo.getString("description"));
            place.setAddr(jo.getString("addr"));
            place.setPicturePlace(ServerInfo.IMAGE_FOLDER+jo.getString("picture_place"));
            place.setLatitude(jo.getString("latitude"));
            place.setLongitude(jo.getString("longitude"));
            if(jo.has("district")){
                place.setDistrict(popDistrictObj(jo.getJSONObject("district")));
            }
            if(jo.has("category")){
                place.setCategory(popCategoryObj(jo.getJSONObject("category")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return(place);
    }

    public User popUser(JSONObject jo) throws JSONException {
        User user = new User();
        user.setId(jo.getString("id"));
        user.setEmail(jo.getString("email"));
        user.setName(jo.getString("name"));
        user.setPictureProfile(ServerInfo.IMAGE_FOLDER+jo.getString("picture_profile"));
        return user;
    }

    public User getUserFromPrefers(){
        User user = new User();
        user.setId(getPrefs().getString("ul-id",null));
        user.setName(getPrefs().getString("ul-name",null));
        user.setEmail(getPrefs().getString("ul-email",null));
        user.setPictureProfile(getPrefs().getString("ul-picture_profile",null));
        return user;
    }

    public Picture popPicture(JSONObject jo) throws JSONException {
        Picture p = new Picture();
        p.setId(jo.getString("id"));
        p.setIdPlace(jo.getString("id_place"));
        p.setText(jo.getString("picture_text"));
        p.setDateTime(jo.getString("date_time"));
        p.setFile(ServerInfo.IMAGE_FOLDER+jo.getString("file_string"));
        return p;
    }

    public void showSnack(String msg){
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), null, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setText(msg);
        snack.show();
    }

    public void showLongSnack(String msg){
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), null, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setText(msg);
        snack.show();
    }

    public void Alert(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void longAlert(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    public Toolbar setUpToolbar(String titleActivity, boolean setDisplayHomeAsUpEnabled, boolean transparent){
        //criando a toolbar
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //HABILITA MENU DE CONFIGURAÇÕES E SETINHA VOLTAR.
        if(toolbar != null){
            //setando o titulo da toolbar
            if(titleActivity.equals("")){
                toolbar.setTitle(R.string.app_name);
            }else if(titleActivity.equals("none")){
                toolbar.setTitle("");
            }else{
                toolbar.setTitle(titleActivity);
            }
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(setDisplayHomeAsUpEnabled);
            //FAZ A SETINHA VOLTAR
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    finish();
                }
            });
            //MUDA COR DO TITULO DO TOOLBAR
            toolbar.setTitleTextColor(0xFFFFFFFF);

            /*if(transparent == true) {
                toolbar.getBackground().setAlpha(0);
            }*/
        }

        return toolbar;
    }

    public void showDialog(String msg) {
        if (!dialog.isShowing()){
            dialog.setMessage(msg);
            dialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        finish();
                        dialog.dismiss();
                    }
                    return true;
                }
            });
            dialog.show();
        }
    }

    public void hideDialog() {
        if (dialog.isShowing())
            dialog.dismiss();
    }

    public SharedPreferences.Editor getEditorPref(){
        return this.editor;
    }

    public SharedPreferences getPrefs(){
        return this.pref;
    }

    public SessionManager getSession(){
        return this.session;
    }

    public void hideKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void logoutUser(){
        session.setLogin(false);
        clearSearchHistory();
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();


        //LoginManager.getInstance().logOut();
        //AppController.getINSTANCE().setFacebookLogin(false);

        Intent intent  = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    protected void clearSearchHistory(){
        SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this,
                SearchableProvider.AUTHORITY,
                SearchableProvider.MODE);

        searchRecentSuggestions.clearHistory();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public GPSTracker getGpsTracker (){
        GPSTracker gpsTracker = new GPSTracker(this);

        if (gpsTracker.getIsGPSTrackingEnabled())
        {
            //Double lat = gpsTracker.getLatitude();
            //Double lng = gpsTracker.getLongitude();
            //showLongSnack(lat.toString()+lng.toString());
            /*String stringLatitude = String.valueOf(gpsTracker.latitude);
            textview = (TextView)findViewById(R.id.fieldLatitude);
            textview.setText(stringLatitude);

            String stringLongitude = String.valueOf(gpsTracker.longitude);
            textview = (TextView)findViewById(R.id.fieldLongitude);
            textview.setText(stringLongitude);

            String country = gpsTracker.getCountryName(this);
            textview = (TextView)findViewById(R.id.fieldCountry);
            textview.setText(country);

            String city = gpsTracker.getLocality(this);
            textview = (TextView)findViewById(R.id.fieldCity);
            textview.setText(city);

            String postalCode = gpsTracker.getPostalCode(this);
            textview = (TextView)findViewById(R.id.fieldPostalCode);
            textview.setText(postalCode);

            String addressLine = gpsTracker.getAddressLine(this);
            textview = (TextView)findViewById(R.id.fieldAddressLine);
            textview.setText(addressLine);*/

            return gpsTracker;
        }
        else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            //gpsTracker.showSettingsAlert();
            //showLongSnack("Erro ao consultar GPS");
            return null;
        }
    }

    //raio para buscas
    public void saveDistanceRadius(int value){
        Log.w(TAG,"saveDistanceRadius(int value)");
        getEditorPref().putInt("ul-distance_radius", value); // Storing boolean - true/false
        getEditorPref().commit(); // commit changes
    }

    //raio para buscas
    public int getDistanceRadius(){
        return getPrefs().getInt("ul-distance_radius",50);
    }

}
