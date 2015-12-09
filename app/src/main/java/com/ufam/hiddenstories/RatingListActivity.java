package com.ufam.hiddenstories;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.ufam.hiddenstories.conn.ServerInfo;
import com.ufam.hiddenstories.conn.VolleyConnection;
import com.ufam.hiddenstories.fragments.CategoryListFragment;
import com.ufam.hiddenstories.fragments.RatingListFragment;
import com.ufam.hiddenstories.interfaces.CustomVolleyCallbackInterface;
import com.ufam.hiddenstories.models.Place;
import com.ufam.hiddenstories.models.Rating;
import com.ufam.hiddenstories.models.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class RatingListActivity extends BaseActivity implements CustomVolleyCallbackInterface{

    private RatingListFragment mFrag;
    private Rating mRating;
    private Place mPlace;

    protected VolleyConnection mVolleyConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_list);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Avaliações e comentáraios");
        //setSupportActionBar(toolbar);

        Toolbar toolbar = setUpToolbar("Avaliações e comentáraios",true,false);

        mVolleyConnection = new VolleyConnection(this);

        mPlace = getIntent().getParcelableExtra("place");
        mRating = getIntent().getParcelableExtra("rating");


        mFrag = (RatingListFragment) getSupportFragmentManager().findFragmentByTag("mainFrag");
        if(mFrag == null) {
            mFrag = new RatingListFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("place",mPlace);
            mFrag.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.rating_list_frag_container, mFrag, "mainFrag");
            ft.commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rating_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_rating) {
            //showLongSnack("Adicionar um comentário");
            rat_btn();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void rat_btn(){

        final Dialog rankDialog;
        final RatingBar ratingBar;
        Log.i("PROFESSIONAL_PROFILE","rat_bnt()");
        String TAG = "set-rat";
        rankDialog = new Dialog(this, R.style.FullHeightDialog);
        rankDialog.setContentView(R.layout.dialog_rating);
        rankDialog.setCancelable(true);
        ratingBar = (RatingBar) rankDialog.findViewById(R.id.dialog_ratingbar);
        ratingBar.setRating(0);

        TextView text = (TextView) rankDialog.findViewById(R.id.rank_dialog_text1);
        text.setText(mPlace.getName());

        final EditText comments = (EditText) rankDialog.findViewById(R.id.edt_comment_rat);
        comments.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        if(mRating==null){
            mRating = new Rating();
        }
        if(mRating.getValue()!=null){
            ratingBar.setRating(Float.parseFloat(mRating.getValue()));
            TAG = "update-rat";
            if(mRating.getText()!=null){
                comments.setText(mRating.getText());
            }
        }else{
            mRating = new Rating();
        }

        final String SEND_TAG = TAG;
        Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ratingBar.getRating();
                Log.i("RANK DIALOG","CLICK");
                User user = getUserFromPrefers();
                mRating.setIdUser(user.getId());
                mRating.setIdPlace(mPlace.getId());
                mRating.setValue(Integer.toString(Math.round(ratingBar.getRating())));
                mRating.setText(comments.getText().toString().trim());
                //EditText comments = (EditText) rankDialog.findViewById(R.id.edt_comment_rat);
                setRating(mRating,SEND_TAG);
                rankDialog.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it
        rankDialog.show();
    }

    public void setRating(Rating rating,String TAG){

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("id_user",rating.getIdUser());
        params.put("id_place",rating.getIdPlace());
        params.put("value",rating.getValue());
        params.put("text",rating.getText());

        if(TAG.equals("set-rat")){
            Log.i("PROFESSIONAL_PROFILE","set commentary: "+params.toString());
            mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.SET_RATING, params,TAG);

        }else if(TAG.equals("update-rat")){
            Log.i("PROFESSIONAL_PROFILE", "update commentary: " + params.toString());
            //Log.i("PROFESSIONAL_PROFILE","update commentary: "+getPrefs().getString("ul-id", null) + ";~;" + getProfessional().getIdProfessional()+";~;"+Math.round(value)+";~;"+comments);
            mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.UPDATE_RATING, params, TAG);
        }
    }

    @Override
    public void deliveryResponse(JSONArray response, String TAG) {

    }

    @Override
    public void deliveryResponse(JSONObject response, String TAG) {
        mFrag.callServer();
    }

    @Override
    public void deliveryError(VolleyError error, String TAG) {

    }
}
