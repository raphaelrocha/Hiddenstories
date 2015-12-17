package com.ufam.hiddenstories;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.soundcloud.android.crop.Crop;
import com.ufam.hiddenstories.adapters.AlbumListAdapter;
import com.ufam.hiddenstories.adapters.RatingListAdapter;
import com.ufam.hiddenstories.conn.ServerInfo;
import com.ufam.hiddenstories.conn.VolleyConnection;
import com.ufam.hiddenstories.interfaces.CustomVolleyCallbackInterface;
import com.ufam.hiddenstories.interfaces.RecyclerViewOnClickListenerHack;
import com.ufam.hiddenstories.models.Picture;
import com.ufam.hiddenstories.models.Place;
import com.ufam.hiddenstories.models.Rating;
import com.ufam.hiddenstories.tools.ItemDecorationAlbumColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class AlbumActivity extends BaseActivity implements RecyclerViewOnClickListenerHack, View.OnClickListener,CustomVolleyCallbackInterface{

    private VolleyConnection mVolleyConnection;
    private Rating mRating;
    private Place mPlace;
    protected RecyclerView mRecyclerView;
    private ArrayList<Picture> mList;
    private String mTEXT = "";
    private String mIMAGE_TO_SAVE="asdsadsadsa";
    private int mCONTA_SNACK_ALERT; //garante q seja exibido apenas um snackalert no erro.
    private Dialog legendDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Toolbar toolbar = setUpToolbar("Álbum",true,false);

        mCONTA_SNACK_ALERT=0;

        mPlace = getIntent().getParcelableExtra("place");
        mRating = getIntent().getParcelableExtra("rating");

        mVolleyConnection = new VolleyConnection(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCrop();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list_album);
        mRecyclerView.setHasFixedSize(true);

       //mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, mRecyclerView, this));

        /*LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        //llm.setReverseLayout(true);
        mRecyclerView.setLayoutManager(llm);*/

        GridLayoutManager llm = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(llm);
        //mRecyclerView.addItemDecoration(new ItemDecorationAlbumColumns(getResources().getDimensionPixelSize(R.dimen.album_grid), getResources().getInteger(R.integer.abc_max_action_buttons)));
        mRecyclerView.addItemDecoration(new ItemDecorationAlbumColumns(10, 4));
    }

    private void callServer(){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("id_place",mPlace.getId());
        mVolleyConnection.callServerApiByJsonArrayRequest(ServerInfo.LIST_PICTURE_BY_PLACE, Request.Method.POST,params,"LIST_PICTURE_BY_PLACE");
    }

    private void sendImage(){
        showDialog("Enviando sua foto.");
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("id_place",mPlace.getId());
        params.put("place_name",mPlace.getName());
        params.put("text",mTEXT);
        params.put("file_string",mIMAGE_TO_SAVE);
        params.put("ext",ServerInfo.EXTENSION_IMAGE_FILE);
        mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.SEND_PICTURE, Request.Method.POST,params,"SEND_PICTURE");
    }

    public void setList(ArrayList<Picture> p){
        mList = p;
        AlbumListAdapter adapter = new AlbumListAdapter(this, mList);
        adapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);
    }


    public void setCardView(JSONArray ja){
        ArrayList<Picture> pictures = new ArrayList<Picture>();
        try {
            for(int i = 0, tam = ja.length(); i < tam; i++){
                Picture p = new Picture();
                p = popPicture(ja.getJSONObject(i));
                pictures.add(p);
            }
        }catch (JSONException e){
            if(mCONTA_SNACK_ALERT==0){
                mCONTA_SNACK_ALERT++;
                Log.i("SNAK", "---- Lançou o snak ----");
                showLongSnack(getResources().getString(R.string.list_empty));
            }
        }
        setList(pictures);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCONTA_SNACK_ALERT=0;
        callServer();
    }

    private void puttext(){

        Log.i("ALBUM_ACTIVITY", "putLegend()");

        legendDialog = new Dialog(AlbumActivity.this, R.style.FullHeightDialog);
        legendDialog.setContentView(R.layout.dialog_legend);
        legendDialog.setCancelable(true);

        final EditText legend = (EditText) legendDialog.findViewById(R.id.edt_legend);
        legend.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        Button updateButton = (Button) legendDialog.findViewById(R.id.bt_send_legend);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTEXT =  legend.getText().toString().trim();
                sendImage();
                legendDialog.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it
        legendDialog.show();


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


            //mIvLocal.setImageBitmap(imgThumb);
            mIMAGE_TO_SAVE = Base64.encodeToString(byte_arr, Base64.DEFAULT);

            puttext();

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
        //Crop.of(source, destination).withAspect(16, 9).start(this);
        Crop.of(source, destination).start(this);
    }











    @Override
    public void deliveryResponse(JSONArray response, String TAG) {
        hideDialog();
        setCardView(response);
    }

    @Override
    public void deliveryResponse(JSONObject response, String TAG) {
        hideDialog();
        callServer();
    }

    @Override
    public void deliveryError(VolleyError error, String TAG) {
        hideDialog();
        longAlert("Algo deu errado.");
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onClickListener(View view, int position) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("place",mPlace);
        intent.putExtra("rating",mRating);
        intent.putExtra("picture",mList.get(position));
        startActivity(intent);
    }

    @Override
    public void onLongPressClickListener(View view, int position) {

    }



}
