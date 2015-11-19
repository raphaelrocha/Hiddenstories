package com.ufam.hiddenstories;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.ufam.hiddenstories.fragments.PlaceListFragment;

public class PlaceListActivity extends BaseActivity {

    private Toolbar mToolbar;
    private PlaceListFragment mFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_place_list);


        mToolbar = (Toolbar) findViewById(R.id.tb_place_list);
        mToolbar.setTitle("Locais");//texto temporário com o nome do local
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

        // FRAGMENT
        mFrag = (PlaceListFragment) getSupportFragmentManager().findFragmentByTag("mainFrag");
        if(mFrag == null) {
            mFrag = new PlaceListFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.place_list_frag_container_AComentaryList, mFrag, "mainFrag");
            ft.commit();
        }
    }

}
