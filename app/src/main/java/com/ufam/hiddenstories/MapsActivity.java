package com.ufam.hiddenstories;

import android.location.Location;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

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
import com.ufam.hiddenstories.models.Place;

import org.json.JSONObject;

import java.util.HashMap;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private Place mPlace;
    private GoogleApiClient mGoogleApiClient;
    private String mode;
    private Toolbar mLocalToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        mPlace = getIntent().getParcelableExtra("place");
        mode = "one";

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocalToolbar = setUpToolbar("Mapa",true,false);
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.app_bar_maps);
        appbar.bringToFront();
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

        MarkerOptions options = new MarkerOptions();

        //mostra a localização do device.
        if(l != null){
            LatLng myLocation = new LatLng(l.getLatitude(), l.getLongitude());

            options = new MarkerOptions();
            options.position(myLocation).title("Você está aqui.").draggable(true);
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.account));
            //mMap.addMarker(new MarkerOptions().position(myLocation).title("Você está aqui."));
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        }

        //mostra a localização do local.
        String[] parts = mPlace.getLocation().split(";");
        Double proLat = Double.parseDouble(parts[0]);
        Double proLng = Double.parseDouble(parts[1]);

        // Add a marker in Sydney and move the camera
        LatLng placeLocation = new LatLng(proLat, proLng);
        //mMap.addMarker(new MarkerOptions().position(placeLocation).title(mPlace.getName()).snippet(mPlace.getAddr()));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));

        //options = new MarkerOptions();
        //options.position(placeLocation).title(mPlace.getName()).snippet(mPlace.getAddr()).draggable(true);
        //options.icon(BitmapDescriptorFactory.fromResource(R.drawable.account));
        //mMap.addMarker(options);
        Marker mMarkerPlace = mMap.addMarker(new MarkerOptions().position(placeLocation).title(mPlace.getName()).snippet(mPlace.getAddr()));

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
    }


    @Override
    public void onConnected(Bundle bundle) {

        if(mode.equals("one")){
            startMapOne();
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
}
