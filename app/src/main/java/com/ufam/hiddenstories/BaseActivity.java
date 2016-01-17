package com.ufam.hiddenstories;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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

import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.ufam.hiddenstories.conn.ServerInfo;
import com.ufam.hiddenstories.conn.VolleyConnectionQueue;
import com.ufam.hiddenstories.models.Category;
import com.ufam.hiddenstories.models.City;
import com.ufam.hiddenstories.models.Commentary;
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
    private final String TAG = BaseActivity.this.getClass().getSimpleName();
    private int DEFALT_RADIUS = 50;

    @Override
    protected void attachBaseContext(Context base) {
        Log.i(TAG, "install");
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fresco.initialize(this);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        VolleyConnectionQueue.getINSTANCE().startQueue(this);
        session = new SessionManager(this);

        pref = getApplicationContext().getSharedPreferences("hiddenstories.ufam.com", 0); // 0 - for private mode
        editor = pref.edit();

        //MUDA COR DA BARRA DE NAVEHAÇÃO DO SISTEMA
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    protected Context getContext() {
        return this;
    }

    public void forceStartVolleyQueue(){
        VolleyConnectionQueue.getINSTANCE().startQueue(this);
    }

    protected Activity getActivity() {
        return this;
    }

    public Rating popRatingObj(JSONObject jo)throws JSONException{
        Rating rating = new Rating();

        rating.setId(jo.getString("id"));
        rating.setValue(jo.getString("rating_value"));
        rating.setText(jo.getString("rating_text"));
        rating.setDateTime(jo.getString("date_time"));

        if(jo.has("user")){
            rating.setUser(popUser(jo.getJSONObject("user")));
        }

        if(jo.has("place")){
            rating.setPlace(popPlaceObj(jo.getJSONObject("place")));
        }

        return(rating);
    }

    public Commentary popCommentaryObj(JSONObject jo){
        Commentary c = null;
        try {
            c = new Commentary();
            c.setId(jo.getString("id"));
            c.setText(jo.getString("comment_text"));
            c.setDateTime(jo.getString("date_time"));

            if(jo.has("user")){
                c.setUser(popUser(jo.getJSONObject("user")));
            }

            if(jo.has("place")){
                c.setPlace(popPlaceObj(jo.getJSONObject("place")));
            }

            if(jo.has("rating")){
                c.setRating(popRatingObj(jo.getJSONObject("rating")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return(c);
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
            c.setPicture(ServerInfo.IMAGE_FOLDER+jo.getString("picture"));
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
            place.setDateTime(jo.getString("date_time"));
            if(jo.has("district")){
                place.setDistrict(popDistrictObj(jo.getJSONObject("district")));
            }
            if(jo.has("category")){
                place.setCategory(popCategoryObj(jo.getJSONObject("category")));
            }
            if(jo.has("ratings")){
                JSONArray ja = jo.getJSONArray("ratings");
                ArrayList<Rating> ratings = new ArrayList<Rating>();
                for(int i=0;i<ja.length();i++){
                    Rating r = popRatingObj(ja.getJSONObject(i));
                    ratings.add(r);
                }
                place.setRatings(ratings);
            }
            if(jo.has("distance")){
                place.setDistance(jo.getString("distance"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return(place);
    }

    public User popUser(JSONObject jo){
        User user = null;
        try {
            user = new User();
            user.setId(jo.getString("id"));
            user.setEmail(jo.getString("email"));
            user.setName(jo.getString("name"));
            if(jo.getString("picture_profile").contains("graph.facebook.com")){
                user.setPictureProfile(jo.getString("picture_profile"));
            }else{
                user.setPictureProfile(ServerInfo.IMAGE_FOLDER + jo.getString("picture_profile"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        tv.setTextColor(Color.WHITE);
        tv.setText(msg);
        snack.show();
    }

    public void showLongSnack(String msg){
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), null, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextColor(Color.WHITE);
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
            //toolbar.setTitleTextColor(0xFFFFFFFF);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));

            /*if(transparent == true) {
                toolbar.getBackground().setAlpha(0);
            }*/
        }

        return toolbar;
    }

    public void showDialog(String msg, boolean cancelable) {
        dialog.setCancelable(cancelable);
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


        LoginManager.getInstance().logOut();
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
        int radius;
        try {
            radius = getPrefs().getInt("ul-distance_radius",DEFALT_RADIUS);
        }catch (Exception e){
            e.printStackTrace();
            radius = DEFALT_RADIUS;
        }
        return radius;
    }

    public int getDefaultRadius(){
        return this.DEFALT_RADIUS;
    }

    public User getUserLoggedObj(){
        Gson gson = new Gson();
        return gson.fromJson(getPrefs().getString("ul-obj",null), User.class);
    }

    public void updatePrefs(User user){

        Log.w(TAG,"updatePrefs(User user)");

        getSession().setLogin(true);
        getEditorPref().putBoolean("ul", true); // Storing boolean - true/false

        Gson gson = new Gson();
        String jsonUserLogged = gson.toJson(user);

        getEditorPref().putString("ul-obj", jsonUserLogged); // Storing string
        getEditorPref().commit(); // commit changes
    }

    protected void goToHome(){
        Intent intent  = new Intent(this, CategoryListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
