package com.ufam.hiddenstories;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.soundcloud.android.crop.Crop;
import com.ufam.hiddenstories.conn.ServerInfo;
import com.ufam.hiddenstories.conn.VolleyConnection;
import com.ufam.hiddenstories.interfaces.CustomVolleyCallbackInterface;
import com.ufam.hiddenstories.models.Category;
import com.ufam.hiddenstories.tools.NothingSelectedSpinnerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FormPlaceActivity extends BaseActivity implements CustomVolleyCallbackInterface, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private Toolbar mToolbar;
    private ImageView mIvLocal;
    private EditText mNameLocal, mDescriptionLocal;
    private TextView mAddrLocal;
    private VolleyConnection mVolleyConnection;
    private String mIMAGE_TO_SAVE;
    private Double mLAT, mLNG;
    private GoogleApiClient mGoogleApiClient;
    private Category mCategory;
    private Spinner mSpinCat;
    private ArrayAdapter<CharSequence> spinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_place);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mVolleyConnection = new VolleyConnection(this);

        mCategory = getIntent().getParcelableExtra("category");

        mToolbar = setUpToolbar("Novo Local", true, false);
        /*mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Novo Local");//texto temporário com o nome do local
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });*/

        mIvLocal = (ImageView) findViewById(R.id.new_place_ic);
        mNameLocal = (EditText) findViewById(R.id.new_place_name);
        mDescriptionLocal = (EditText) findViewById(R.id.new_place_description);
        mAddrLocal = (TextView) findViewById(R.id.new_place_addr);

        mNameLocal.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mDescriptionLocal.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        mIvLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCrop();
            }
        });

        mAddrLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndroidLocation();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            //showLongSnack("Adicionar um comentário");
            save();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void save(){
        String name = mNameLocal.getText().toString().trim();
        String addr = mAddrLocal.getText().toString().trim();
        String description = mDescriptionLocal.getText().toString().trim();
        String image = mIMAGE_TO_SAVE;
        String ext = ServerInfo.EXTENSION_IMAGE_FILE;

        String id_category = mCategory.getId();

        HashMap<String,String> params = new HashMap<String,String>();

        params.put("name", name);
        params.put("id_district", "1");//sem chave por enquanto em 10/12/2014
        params.put("id_category", id_category);
        params.put("description", description);
        params.put("addr",addr);
        params.put("latitude", mLAT.toString());
        params.put("longitude", mLNG.toString());
        params.put("picture", image);
        params.put("ext", ext);

        showDialog("Salvando...",false);

        mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.CREATE_PLACE, Request.Method.POST, false,params, "ACTION_CREATE_PLACE");
    }

    public void startCrop(){
        Crop.pickImage(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {

            beginCropWide(result.getData());

        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            //img.setImageURI(Crop.getOutput(result));
            Bitmap myImg = BitmapFactory.decodeFile(Crop.getOutput(result).getPath());

            Bitmap imgThumb = getbitpam(Crop.getOutput(result).getPath());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            myImg.compress(Bitmap.CompressFormat.JPEG, 20, stream);
            byte[] byte_arr = stream.toByteArray();


            mIvLocal.setImageBitmap(imgThumb);
            mIMAGE_TO_SAVE = Base64.encodeToString(byte_arr, Base64.DEFAULT);

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap getbitpam(String path){
        Bitmap imgthumBitmap=null;
        //int THUMBNAIL_SIZE_w=0;
        //int THUMBNAIL_SIZE_h=0;
        try
        {

            //THUMBNAIL_SIZE_w = 320;
            //THUMBNAIL_SIZE_h = 180;

            FileInputStream fis = new FileInputStream(path);
            imgthumBitmap = BitmapFactory.decodeStream(fis);

            //imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap,
            //        THUMBNAIL_SIZE_w, THUMBNAIL_SIZE_h, false);

            //ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
            //imgthumBitmap.compress(Bitmap.CompressFormat.JPEG, 20,bytearroutstream);
        }
        catch(Exception ex) {

        }
        return imgthumBitmap;
    }

    private void beginCropWide(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        //Crop.of(source, destination).start(this);
        //Crop.of(source, destination).
        Crop.of(source, destination).withAspect(16, 9).start(this);
    }




    private synchronized void getAndroidLocation(){
        Log.i("UPDATE_PROFILE", "getAndroidLocation()");
        showDialog("Buscando sua localização. Aguarde.",true);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("UPDATE_PROFILE", "onConnected(" + bundle + ")");

        hideDialog();

        Location l = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if(l != null){
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            try {
                //-23.548882, -46.631215

                mLAT =  l.getLatitude();
                mLNG =  l.getLongitude();
                List<Address> addresses = gc.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
                //List<Address> addresses = gc.getFromLocation(-23.548882, -46.631215, 1);
                Log.d("=getCountryName()=",addresses.toString());
                String pais;
                String estadoNome;
                String estadoSigla;
                String cidade;
                String bairro;
                String enderecoCompleto;
                String endereco;
                for (int i = 0; i < addresses.size(); i++) {
                    Log.d("getMaxAddressLineIndex", Integer.toString(addresses.get(i).getMaxAddressLineIndex()));
                    Log.d("=getCountryName()=",addresses.get(i).getCountryName());
                    pais = addresses.get(i).getCountryName();
                    Log.d("=getAdminArea()=",addresses.get(i).getAdminArea());
                    estadoNome = addresses.get(i).getAdminArea();
                    //Log.d("=getSubAdminArea()=",addresses.get(i).getSubAdminArea());
                    Log.d("=getLocality()=",addresses.get(i).getLocality());
                    cidade = addresses.get(i).getLocality();
                    Log.d("=getSubLocality()=",addresses.get(i).getSubLocality());
                    bairro = addresses.get(i).getSubLocality();
                    Log.d("=getThoroughfare()=",addresses.get(i).getThoroughfare());
                    endereco = addresses.get(i).getThoroughfare();
                    Log.d("=getSubThoroughfare()=", addresses.get(i).getSubThoroughfare());
                    Log.d("=getFeatureName()=",addresses.get(i).getFeatureName());
                    enderecoCompleto = addresses.get(i).getAddressLine(0);

                    endereco = addresses.get(i).getThoroughfare()+", "+addresses.get(i).getSubThoroughfare();

                    String aux[] = addresses.get(i).getAddressLine(1).split("-");
                    estadoSigla = aux[1].trim();
                    for (int j = 0; j <= addresses.get(i).getMaxAddressLineIndex(); j++) {
                        Log.d("=getAddressLine("+j+")=",addresses.get(i).getAddressLine(j));
                    }
                    //this.setText(endereco);
                    //this.city.setText(cidade);
                    //this.district.setText(bairro);
                    Log.i("SIGLA ESTADO",estadoSigla);
                    //String tempAddr = endereco+" "+bairro+" "+cidade+" / "+estadoSigla+" / "+pais;
                    String tempAddr = enderecoCompleto+" - "+cidade+" / "+estadoNome+" / "+pais;
                    this.mAddrLocal.setText(tempAddr);
                    //this.stateSpin.setSelection(spinAdapter.getPosition(estadoSigla)+ 1);

                    showLongSnack("Seu endereço foi atualizado com base nos dados coletados com o GPS.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showLongSnack("Não foi possível preencher o seu endereço automaticamnete, por favor preencha,");
            }

            //this.location.setText(Double.toString(l.getLatitude()) + ";" + Double.toString(l.getLongitude()));
            Log.i("APP", "latitude: " + "-3.088088");
            Log.i("APP", "longitude: " + "-59.964278");

        }else{
            //longAlert("Não foi possível deterinar sua localização");
            showLongSnack("Não foi possível determinar a sua localização.");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended(" + i + ")");
        hideDialog();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed(" + connectionResult + ")");
        hideDialog();
        showLongSnack("A localização falhou.");
    }

    @Override
    public void onStop(){
        super.onStop();
        mVolleyConnection.cancelRequest();
    }



    //VOLLEY
    @Override
    public void deliveryResponse(JSONArray response, String TAG) {

    }

    @Override
    public void deliveryResponse(JSONObject response, String TAG) {
        hideDialog();
        longAlert("Pronto.");
        finish();
    }

    @Override
    public void deliveryError(VolleyError error, String TAG) {
        hideDialog();
        longAlert("Algo deu errado.");
    }

}
