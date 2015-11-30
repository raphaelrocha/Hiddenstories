package com.ufam.hiddenstories;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ufam.hiddenstories.fragments.CategoryListFragment;
import com.ufam.hiddenstories.fragments.RatingListFragment;
import com.ufam.hiddenstories.models.Place;
import com.ufam.hiddenstories.models.Rating;

public class RatingListActivity extends BaseActivity {

    private RatingListFragment mFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_list);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Avaliações e comentáraios");
        //setSupportActionBar(toolbar);

        Toolbar toolbar = setUpToolbar("Avaliações e comentáraios",true,false);

        Place place = getIntent().getParcelableExtra("place");

        mFrag = (RatingListFragment) getSupportFragmentManager().findFragmentByTag("mainFrag");
        if(mFrag == null) {
            mFrag = new RatingListFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("place",place);
            mFrag.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.rating_list_frag_container, mFrag, "mainFrag");
            ft.commit();
        }
    }
}
