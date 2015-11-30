package com.ufam.hiddenstories.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ufam.hiddenstories.R;
import com.ufam.hiddenstories.interfaces.RecyclerViewOnClickListenerHack;
import com.ufam.hiddenstories.models.Category;
import com.ufam.hiddenstories.models.Rating;

import java.util.List;
import java.util.Random;


/**
 * Created by viniciusthiengo on 4/5/15.
 */
public class RatingListAdapter extends RecyclerView.Adapter<RatingListAdapter.MyViewHolder> {
    private Context mContext;
    private List<Rating> mList;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    private float scale;
    private int width, height, roundPixels;


    public RatingListAdapter(Context c, List<Rating> l){
        mContext = c;
        mList = l;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        scale = mContext.getResources().getDisplayMetrics().density;
        width = mContext.getResources().getDisplayMetrics().widthPixels - (int)(14 * scale + 0.5f);
        height = (width / 16) * 9;

        roundPixels = (int)(2 * scale + 0.5f);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;

        v = mLayoutInflater.inflate(R.layout.rating_item_list, viewGroup, false);

        MyViewHolder mvh = new MyViewHolder(v);
        return mvh;
    }

    public Random rand = new Random();

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {

        /*ControllerListener listener = new BaseControllerListener(){
            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                //Log.i("LOG", "onFinalImageSet");
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                //Log.i("LOG", "onFailure");
            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
                //Log.i("LOG", "onIntermediateImageFailed");
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
                //Log.i("LOG", "onIntermediateImageSet");
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
                //Log.i("LOG", "onRelease");
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                //Log.i("LOG", "onSubmit");
            }
        };

        int w = 0;
        if( myViewHolder.ivPlace.getLayoutParams().width == FrameLayout.LayoutParams.MATCH_PARENT
            || myViewHolder.ivPlace.getLayoutParams().width == FrameLayout.LayoutParams.WRAP_CONTENT){

            Display display = ( (Activity) mContext ).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize( size );

            try{
                w = size.x;
            }
            catch( Exception e ){
                w = display.getWidth();
            }
        }

        Uri uri = Uri.parse(DataUrl.getUrlCustom(mList.get(position).getPicturePlace(), w));
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri( uri )
                .setOldController( myViewHolder.ivPlace.getController() )
                .build();

        RoundingParams rp = RoundingParams.fromCornersRadii(roundPixels, roundPixels, 0, 0);
        myViewHolder.ivPlace.setController(dc);
        myViewHolder.ivPlace.getHierarchy().setRoundingParams(rp);

        myViewHolder.tvPlaceCity.setText(mList.get(position).getCity() + "/" + mList.get(position).getDistrict());
        myViewHolder.tvPlaceName.setText(mList.get(position).getName());*/

        myViewHolder.tvNameUser.setText(mList.get(position).getNameUser());
        myViewHolder.ratingBar.setRating(Float.parseFloat(mList.get(position).getValue()));
        myViewHolder.tvText.setText(mList.get(position).getText());
        myViewHolder.tvDateTime.setText(mList.get(position).getDateTime());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r){
        mRecyclerViewOnClickListenerHack = r;
    }


    public void addListItem(Rating rat, int position){
        mList.add(rat);
        notifyItemInserted(position);
    }


    public void removeListItem(int position){
        mList.remove(position);
        notifyItemRemoved(position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tvNameUser;
        public TextView tvText;
        public TextView tvDateTime;
        public SimpleDraweeView ivUser;
        public RatingBar ratingBar;


        public MyViewHolder(View itemView) {
            super(itemView);

            ivUser = (SimpleDraweeView) itemView.findViewById(R.id.iv_user);
            ratingBar = (RatingBar) itemView.findViewById(R.id.small_ratingbar);
            tvNameUser = (TextView) itemView.findViewById(R.id.tv_name_user);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);
            tvDateTime = (TextView) itemView.findViewById(R.id.tv_date_time);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mRecyclerViewOnClickListenerHack != null){
                mRecyclerViewOnClickListenerHack.onClickListener(v, getPosition());
            }
        }
    }
}
