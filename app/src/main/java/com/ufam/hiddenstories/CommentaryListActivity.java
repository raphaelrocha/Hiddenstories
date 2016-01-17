package com.ufam.hiddenstories;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ufam.hiddenstories.fragments.CommentaryListFragment;
import com.ufam.hiddenstories.fragments.RatingListFragment;
import com.ufam.hiddenstories.models.Commentary;
import com.ufam.hiddenstories.models.Place;

public class CommentaryListActivity extends BaseActivity {

    private CommentaryListFragment mFrag;
    private Place mPlace;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentary_list);

        mToolbar = setUpToolbar("Comentários",true,false);
        /*mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Comentários");//texto temporário com o nome do local
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

        mPlace = getIntent().getParcelableExtra("place");

        mFrag = (CommentaryListFragment) getSupportFragmentManager().findFragmentByTag("mainFrag");
        if(mFrag == null) {
            mFrag = new CommentaryListFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("place",mPlace);
            mFrag.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.commentary_list_frag_container, mFrag, "mainFrag");
            ft.commit();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_commentary_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_home:

                // Complete with your code
                goToHome();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
