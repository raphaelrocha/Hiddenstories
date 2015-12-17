package com.ufam.hiddenstories;

import android.app.Activity;
import android.graphics.Point;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ufam.hiddenstories.models.Picture;
import com.ufam.hiddenstories.models.Place;
import com.ufam.hiddenstories.models.Rating;
import com.ufam.hiddenstories.tools.DataUrl;

public class PhotoActivity extends BaseActivity {

    private Rating mRating;
    private Place mPlace;
    private Picture mPicture;
    private TextView description;
    private AppBarLayout appBar;
    private SimpleDraweeView ivPhoto;
    private float scale;
    private int width, height, roundPixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Toolbar toolbar = setUpToolbar("Foto",true,false);

        appBar = (AppBarLayout) findViewById(R.id.appbar_photo);

        ivPhoto = (SimpleDraweeView) findViewById(R.id.iv_album_photo);
        description = (TextView) findViewById(R.id.photo_description);

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDescription();
                handleToolbar();
            }
        });

        mPlace = getIntent().getParcelableExtra("place");
        mRating = getIntent().getParcelableExtra("rating");
        mPicture = getIntent().getParcelableExtra("picture");


        if(mPicture.getText()!=null){
            if(!mPicture.getText().trim().equals("")){
                description.setText(mPicture.getText());
                description.setVisibility(View.VISIBLE);
            }else{
                description.setVisibility(View.INVISIBLE);
            }
        }

        scale = this.getResources().getDisplayMetrics().density;
        width = this.getResources().getDisplayMetrics().widthPixels - (int)(14 * scale + 0.5f);
        height = (width / 16) * 9;
        roundPixels = (int)(2 * scale + 0.5f);

        int w = 0;
        if( ivPhoto.getLayoutParams().width == FrameLayout.LayoutParams.MATCH_PARENT
                || ivPhoto.getLayoutParams().width == FrameLayout.LayoutParams.WRAP_CONTENT){

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

        Uri uri = Uri.parse(DataUrl.getUrlCustom(mPicture.getFile(), w));
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri( uri )
                .setOldController( ivPhoto.getController() )
                .build();

        //RoundingParams rp = RoundingParams.fromCornersRadii(roundPixels, roundPixels, 0, 0);
        ivPhoto.setController(dc);
        //ivPhoto.getHierarchy().setRoundingParams(rp);

        appBar.bringToFront();
    }

    private void handleDescription(){
        if(description.getVisibility()== View.INVISIBLE){
            if(!description.getText().toString().trim().equals("")){
                description.setVisibility(View.VISIBLE);
            }
        }else if (description.getVisibility()==View.VISIBLE){
            description.setVisibility(View.INVISIBLE);
        }
    }

    private void handleToolbar(){
        if(appBar.getVisibility()== View.INVISIBLE){
            appBar.setVisibility(View.VISIBLE);
        }else if (appBar.getVisibility()==View.VISIBLE){
            appBar.setVisibility(View.INVISIBLE);
        }
    }
}
