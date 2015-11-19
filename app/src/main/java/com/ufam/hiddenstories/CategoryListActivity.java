package com.ufam.hiddenstories;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ufam.hiddenstories.fragments.CategoryListFragment;
import com.ufam.hiddenstories.fragments.PlaceListFragment;

public class CategoryListActivity extends BaseActivity {

    private CategoryListFragment mFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Categorias");
        setSupportActionBar(toolbar);

        mFrag = (CategoryListFragment) getSupportFragmentManager().findFragmentByTag("mainFrag");
        if(mFrag == null) {
            mFrag = new CategoryListFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.category_list_frag_container, mFrag, "mainFrag");
            ft.commit();
        }
    }

}
