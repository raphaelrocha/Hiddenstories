package com.ufam.hiddenstories;

import android.graphics.Typeface;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;


public class PlaceActivity extends AppCompatActivity {

    private TextView tvDescription;
    private ViewGroup mRoot;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

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

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        //mCollapsingToolbarLayout.setTitle("1Teatro Amazonas");

        mToolbar = (Toolbar) findViewById(R.id.tb_place);
        mToolbar.setTitle(getResources().getString(R.string.tmp_place_name));//texto temporário com o nome do local
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

        TextView tvPlaceName = (TextView) findViewById(R.id.place_tv_name);
        TextView tvPlaceCity = (TextView) findViewById(R.id.place_tv_city);

        tvPlaceName.setText(getResources().getString(R.string.tmp_place_name));//texto temporario com o nome do local
        tvPlaceCity.setText(getResources().getString(R.string.tmp_place_city));//texto temporario com o nome da cidade
        tvPlaceName.setTypeface(null, Typeface.BOLD);

        tvDescription.setText(getResources().getString(R.string.tmp_place_description));//texto temporário de descrição
        //tvDescription.setVisibility(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || savedInstanceState != null ? View.VISIBLE : View.INVISIBLE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
