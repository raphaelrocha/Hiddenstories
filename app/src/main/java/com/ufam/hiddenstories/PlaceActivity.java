package com.ufam.hiddenstories;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ufam.hiddenstories.models.Place;
import com.ufam.hiddenstories.tools.DataUrl;


public class PlaceActivity extends BaseActivity {

    private TextView tvDescription;
    private ViewGroup mRoot;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Place mPlace;
    private SimpleDraweeView ivPlace;
    private float scale;
    private int width, height, roundPixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("APP","SDK: "+Build.VERSION.SDK_INT);
        // TRANSITIONS
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            Log.i("APP","Entrou na TRANSITIONS");
                /*Explode trans1 = new Explode();
                trans1.setDuration(3000);
                Fade trans2 = new Fade();
                trans2.setDuration(3000);

                getWindow().setEnterTransition( trans1 );
                getWindow().setReturnTransition( trans2 );*/

            TransitionInflater inflater = TransitionInflater.from( this );
            Transition transition = inflater.inflateTransition( R.transition.transitions );

            getWindow().setSharedElementEnterTransition(transition);

            Transition transition1 = getWindow().getSharedElementEnterTransition();
            transition1.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    Log.i("APP", "onTransitionStart()");
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    Log.i("APP", "onTransitionEnd()");
                    TransitionManager.beginDelayedTransition(mRoot, new Slide());
                    tvDescription.setVisibility( View.VISIBLE );
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    Log.i("APP", "onTransitionCancel()");
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    Log.i("APP", "onTransitionPause()");
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    Log.i("APP", "onTransitionResume()");
                }
            });
        }

        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_place);

        if(savedInstanceState != null){
            mPlace = savedInstanceState.getParcelable("place");
        }
        else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getParcelable("place") != null) {
                mPlace = getIntent().getExtras().getParcelable("place");
            } else {
                Toast.makeText(this, "Fail!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }


        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        //mCollapsingToolbarLayout.setTitle("1Teatro Amazonas");

        mToolbar = (Toolbar) findViewById(R.id.tb_place);
        mToolbar.setTitle(mPlace.getName());//texto temporário com o nome do local
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
        });

        mRoot = (ViewGroup) findViewById(R.id.place_ll_tv_description);
        tvDescription = (TextView) findViewById(R.id.place_tv_description);
        ivPlace = (SimpleDraweeView) findViewById(R.id.iv_place);


        //////drawee

        scale = this.getResources().getDisplayMetrics().density;
        width = this.getResources().getDisplayMetrics().widthPixels - (int)(14 * scale + 0.5f);
        height = (width / 16) * 9;

        roundPixels = (int)(2 * scale + 0.5f);

        int w = 0;
        if( ivPlace.getLayoutParams().width == FrameLayout.LayoutParams.MATCH_PARENT
                || ivPlace.getLayoutParams().width == FrameLayout.LayoutParams.WRAP_CONTENT){

            Display display = this.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize( size );

            try{
                w = size.x;
            }
            catch( Exception e ){
                w = display.getWidth();
            }
        }

        Uri uri = Uri.parse(DataUrl.getUrlCustom(mPlace.getPicturePlace(), w));
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri( uri )
                .setOldController( ivPlace.getController() )
                .build();

        RoundingParams rp = RoundingParams.fromCornersRadii(roundPixels, roundPixels, 0, 0);
        ivPlace.setController(dc);
        ivPlace.getHierarchy().setRoundingParams(rp);


        ////fim drawee

        TextView tvPlaceName = (TextView) findViewById(R.id.place_tv_name);
        TextView tvPlaceCity = (TextView) findViewById(R.id.place_tv_city);

        //tvPlaceName.setText(getResources().getString(R.string.tmp_place_name));//texto temporario com o nome do local
        //tvPlaceCity.setText(getResources().getString(R.string.tmp_place_city));//texto temporario com o nome da cidade

        tvPlaceName.setText(mPlace.getName());//texto temporario com o nome do local
        String cityDistrict = mPlace.getCity()+"/"+mPlace.getDistrict();
        tvPlaceCity.setText(cityDistrict);//texto temporario com o nome da cidade

        tvPlaceName.setTypeface(null, Typeface.BOLD);

        //tvDescription.setText(getResources().getString(R.string.tmp_place_description));//texto temporário de descrição
        tvDescription.setText(mPlace.getDescription());
        //tvDescription.setVisibility(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || savedInstanceState != null ? View.VISIBLE : View.INVISIBLE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Exibir no mapa", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                showMap();

            }
        });
    }

    private void showMap(){
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("place", mPlace);
        this.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            TransitionManager.beginDelayedTransition(mRoot, new Slide());
            tvDescription.setVisibility( View.INVISIBLE );
        }

        super.onBackPressed();
    }

}
