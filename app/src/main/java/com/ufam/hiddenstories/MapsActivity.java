package com.ufam.hiddenstories;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ufam.hiddenstories.conn.ServerInfo;
import com.ufam.hiddenstories.conn.VolleyConnection;
import com.ufam.hiddenstories.interfaces.CustomVolleyCallbackInterface;
import com.ufam.hiddenstories.models.Place;
import com.ufam.hiddenstories.models.Rating;
import com.ufam.hiddenstories.models.User;
import com.ufam.hiddenstories.tools.GMapV2Direction;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, CustomVolleyCallbackInterface, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private Place mPlace;
    private GoogleApiClient mGoogleApiClient;
    private String mMode;
    private Toolbar mLocalToolbar;
    private VolleyConnection mVolleyConnection;
    private final String TAG = MapsActivity.this.getClass().getSimpleName();
    private FloatingActionButton mFab;
    private HashMap<Marker,Place> mMapPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mVolleyConnection = new VolleyConnection(this);

        mPlace = getIntent().getParcelableExtra("place");
        mMode = getIntent().getStringExtra("mode");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocalToolbar = setUpToolbar("Mapa",true,false);
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.app_bar_maps);
        appbar.bringToFront();

        mFab = (FloatingActionButton) findViewById(R.id.fab_eye);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLegend();
            }
        });

        mFab.setVisibility(View.INVISIBLE);


    }

    private void callServer(){
        Integer radius_distance = getDistanceRadius();
        showDialog("Buscando locais que estejam a no máximo "+radius_distance+" Km de você.",true);

        Location l = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        Double user_lat = null;
        Double user_lng = null;
        String radius = radius_distance.toString();
        if(l != null){
            user_lat = l.getLatitude();
            user_lng = l.getLongitude();
        }else{
            user_lat = -3.088281;//temp ufam
            user_lng = -59.964379;//temp ufam
        }

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("user_latitude",user_lat.toString());
        params.put("user_longitude",user_lng.toString());
        params.put("radius",radius);

        mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.GET_ALL_PLACE, Request.Method.POST, false,  params, "GET_ALL_PLACE");
        //mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.actionGetAllPro, Request.Method.POST, false,  params, "ACTION_GET_LIST_PRO_BY_PROXIMITY");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void startMapOne(){

        Location l = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        MarkerOptions myOptions = new MarkerOptions();

        LatLng myLocation = null;

        //mostra a localização do device.
        if(l != null){
            myLocation = new LatLng(l.getLatitude(), l.getLongitude());

            myOptions = new MarkerOptions();
            myOptions.position(myLocation).title("Você está aqui.").draggable(true);
            myOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.account));
            //mMap.addMarker(new MarkerOptions().position(myLocation).title("Você está aqui."));
            mMap.addMarker(myOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        }

        //mostra a localização do local.
        Double proLat = Double.parseDouble(mPlace.getLatitude());
        Double proLng = Double.parseDouble(mPlace.getLongitude());

        // Add a marker in Sydney and move the camera
        LatLng placeLocation = new LatLng(proLat, proLng);
        //mMap.addMarker(new MarkerOptions().position(placeLocation).title(mPlace.getName()).snippet(mPlace.getAddr()));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));

        //options = new MarkerOptions();
        //options.position(placeLocation).title(mPlace.getName()).snippet(mPlace.getAddr()).draggable(true);
        //options.icon(BitmapDescriptorFactory.fromResource(R.drawable.account));
        //mMap.addMarker(options);


        MarkerOptions options = new MarkerOptions();
        options.position(placeLocation).title(mPlace.getName()).snippet(mPlace.getAddr()).draggable(true);

        String idCat = mPlace.getCategory().getId();

        if(idCat.equals("1")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_blue));
        }else if(idCat.equals("2")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_brown));
        }else if(idCat.equals("3")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_green));
        }else if(idCat.equals("4")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_purpple));
        }else if(idCat.equals("5")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_red));
        }else if(idCat.equals("6")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_dark_red));
        }else if(idCat.equals("7")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_light_brown));
        }


        Marker mMarkerPlace = mMap.addMarker(options);
        //Marker mMarkerPlace = mMap.addMarker(new MarkerOptions().position(placeLocation).title(mPlace.getName()).snippet(mPlace.getAddr()));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(placeLocation).zoom(16).bearing(0).tilt(0).build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);

        mMap.animateCamera(update, 3000, new GoogleMap.CancelableCallback() {
            @Override
            public void onCancel() {
                Log.i("Script", "CancelableCallback.onCancel()");
            }

            @Override
            public void onFinish() {
                Log.i("Script", "CancelableCallback.onFinish()");
            }
        });

        mMarkerPlace.showInfoWindow();

        if(myLocation!=null){
            route(myLocation, placeLocation,
                    GMapV2Direction.MODE_DRIVING);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        if(mMode.equals("one")){
            mFab.setVisibility(View.INVISIBLE);
            startMapOne();
        }else if(mMode.equals("many")){
            mFab.setVisibility(View.VISIBLE);
            callServer();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        showLongSnack(getResources().getString(R.string.erro_000019));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showLongSnack(getResources().getString(R.string.erro_000020));
    }

    protected void route(LatLng sourcePosition, LatLng destPosition, String mode) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    GMapV2Direction md = new GMapV2Direction();
                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
                    PolylineOptions rectLine = new PolylineOptions().width(15).color(getResources().getColor(R.color.md_blue_400));

                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    Polyline polylin = mMap.addPolyline(rectLine);md.getDurationText(doc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        };

        new GMapV2DirectionAsyncTask(handler, sourcePosition, destPosition, GMapV2Direction.MODE_DRIVING).execute();
    }

    private void showDeviceLocation(boolean b){

        Location l = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        MarkerOptions options = new MarkerOptions();

        //mostra a localização do device.
        Double user_lat;
        Double user_lng;
        if(l != null){
            user_lat = l.getLatitude();
            user_lng = l.getLongitude();
        }else{
            user_lat = -3.088281;//temp ufam
            user_lng = -59.964379;//temp ufam
        }

        //LatLng myLocation = new LatLng(l.getLatitude(), l.getLongitude());
        LatLng myLocation = new LatLng(user_lat, user_lng);
        options = new MarkerOptions();
        options.position(myLocation).title("Você está aqui.").draggable(true);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.account));
        //mMap.addMarker(new MarkerOptions().position(myLocation).title("Você está aqui."));
        Marker mMarkerPlace = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

        //coloca o zoom no usuário
        if(b){
            //LatLng placeLocation = new LatLng(l.getLatitude(), l.getLongitude());
            //Marker mMarkerPlace = mMap.addMarker(new MarkerOptions().position(placeLocation).title(professional.getName()).snippet(professional.getAddr()));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).bearing(0).tilt(0).build();
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);

            mMap.animateCamera(update, 3000, new GoogleMap.CancelableCallback() {
                @Override
                public void onCancel() {
                    Log.i("Script", "CancelableCallback.onCancel()");
                }

                @Override
                public void onFinish() {
                    Log.i("Script", "CancelableCallback.onFinish()");
                }
            });

            mMarkerPlace.showInfoWindow();
        }

    }


    private void startMapMany(JSONObject jo){

        mMap.setOnInfoWindowClickListener(this);

        mMapPlace = new HashMap<Marker, Place>();

        JSONArray ja = null;
        try {
            boolean b = jo.getBoolean("success");
            if(b){
                ja = jo.getJSONArray("places");
                ArrayList<Place> places = new ArrayList<Place>();
                for(int i = 0, tam = ja.length(); i < tam; i++){
                    Place place = new Place();
                    place = popPlaceObj(ja.getJSONObject(i));
                    places.add(place);
                }

                showDeviceLocation(true);

                for(int i=0;i<places.size();i++){

                    //String[] parts = professionals.get(i).getProfessional().getLocation().split(";");

                    try{
                        Double lat = Double.parseDouble(places.get(i).getLatitude());
                        Double lng = Double.parseDouble(places.get(i).getLongitude());

                        customAddMarker(new LatLng(lat, lng), places.get(i).getName(), places.get(i).getAddr() + ", " + places.get(i).getDistrict().getName(),"everybody",  places.get(i));

                    }catch(Exception e){
                        //Log.i("MAP ERROR",parts.toString());
                        e.printStackTrace();
                    }

                }

            }else{
                Alert("Algo deu errado");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Alert("Algo deu errado");
        }
    }

    private void customAddMarker(LatLng latLng, String title, String snippet,String me, Place place){
        Log.i(TAG, "customAddMarker");
        MarkerOptions options = new MarkerOptions();
        options.position(latLng).title(title).snippet(snippet).draggable(true);

        String idCat = place.getCategory().getId();

        if(idCat.equals("1")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_blue));
        }else if(idCat.equals("2")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_brown));
        }else if(idCat.equals("3")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_green));
        }else if(idCat.equals("4")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_purpple));
        }else if(idCat.equals("5")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_red));
        }else if(idCat.equals("6")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_dark_red));
        }else if(idCat.equals("7")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_light_brown));
        }

        /*if(me.equals("me")){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.account));
        }*/

        Marker marker = mMap.addMarker(options);
        mMapPlace.put(marker,place);
        marker.showInfoWindow();

        /*if(mMode.equals("one")){
            mMarker.showInfoWindow();
        }if(me.equals("me")){
            mMarker.showInfoWindow();
        }*/
    }

    @Override
    public void deliveryResponse(JSONArray response, String flag) {
        hideDialog();
    }

    @Override
    public void deliveryResponse(JSONObject response, String flag) {
        hideDialog();
        Log.i(TAG,"deliveryResponse(Object)");
        Log.i(TAG, response.toString());
        startMapMany(response);
    }

    @Override
    public void deliveryError(VolleyError error, String flag) {
        hideDialog();
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.w(TAG,"abrir tela do local");
        Place place = mMapPlace.get(marker);
        Intent intent = new Intent(getActivity(), PlaceActivity.class);
        intent.putExtra("place", place);
        getActivity().startActivity(intent);
    }


    public class GMapV2DirectionAsyncTask extends AsyncTask<String, Void, Document> {

        private final String TAG = GMapV2DirectionAsyncTask.class.getSimpleName();
        private Handler handler;
        private LatLng  start, end;
        private String mode;

        public GMapV2DirectionAsyncTask(Handler handler, LatLng start, LatLng end, String mode) {
            this.start = start;
            this.end = end;
            this.mode = mode;
            this.handler = handler;
        }

        @Override
        protected Document doInBackground(String... params) {

            String url = "http://maps.googleapis.com/maps/api/directions/xml?"
                    + "origin=" + start.latitude + "," + start.longitude
                    + "&destination=" + end.latitude + "," + end.longitude
                    + "&sensor=false&units=metric&mode=" + mode;
            Log.d("url", url);
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);
                HttpResponse response = httpClient.execute(httpPost, localContext);
                InputStream in = response.getEntity().getContent();
                DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                Document doc = builder.parse(in);
                return doc;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Document result) {
            if (result != null) {
                Log.d(TAG, "---- GMapV2DirectionAsyncTask OK ----");
                Message message = new Message();
                message.obj = result;
                handler.dispatchMessage(message);
            } else {
                Log.d(TAG, "---- GMapV2DirectionAsyncTask ERROR ----");
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void showLegend(){
        final Dialog rankDialog;
        Log.i(TAG,"map_info()");
        String TAG = "set-rat";
        rankDialog = new Dialog(this, R.style.FullHeightDialog);
        rankDialog.setContentView(R.layout.dialog_map_info);
        rankDialog.setCancelable(true);

        Button close = (Button) rankDialog.findViewById(R.id.bt_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rankDialog.dismiss();
            }
        });
        rankDialog.show();
    }
}
