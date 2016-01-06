package com.ufam.hiddenstories;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class AdjustActivity extends BaseActivity {

    private Menu mOptionsMenu;
    private SeekBar mSeekbar;
    private TextView mDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust);

        Toolbar localToolbar = setUpToolbar(getString(R.string.adjust_activity), true, false);

        ImageButton defaulRadius = (ImageButton) findViewById(R.id.button_restore_radius);
        mSeekbar = (SeekBar) findViewById(R.id.seek_bar_radius);
        mDistance = (TextView) findViewById(R.id.distance);

        int saved_radius = getDistanceRadius();
        mSeekbar.setProgress(saved_radius);
        mDistance.setText(saved_radius+" Km");

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                //distance.setText(Integer.toString(progress+)+"km");
                mDistance.setText(progress+" Km");
                /*int value = progress+10;
                if(value>50){
                    distance.setText("50 km");
                    value=50;
                }else{
                    distance.setText(value+" km");
                }*/
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        defaulRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSeekbar.setProgress(50);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_category_list, menu);
        //return true;
        Log.i("CategoryListActivity", "onCreateOptionsMenu()");
        this.mOptionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_adjust, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_select_cat) {

            saveDistanceRadius(mSeekbar.getProgress());
            Alert("Suas preferências foram salvas.");
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
