package com.ufam.hiddenstories.tools;

import android.util.Log;


import com.ufam.hiddenstories.models.Rating;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by rli on 19/12/2015.
 */
public class RatingAvgCalculator {

    public Float calc(ArrayList<Rating> list){
        float sum = 0;
        int size = list.size();

        for (int i=0; i < size; i++){
            sum = sum + Integer.parseInt(list.get(i).getValue());
        }

        return sum/size;
    }

    public String formatValue(Float numero){

        String retorno;

        if(numero != null){
            Log.w("AvgCalculator",numero.toString());
            try{

                DecimalFormat formatter = new DecimalFormat("#.#");
                retorno = formatter.format(numero);

                if(retorno.contains(".") || retorno.contains(",")){
                    retorno = retorno;
                }else{
                    Log.w("AvgCalculator","add ,0");
                    retorno = retorno+",0";
                }

            }catch(Exception e){
                e.printStackTrace();
                retorno = "0,0";
            }
        }else{
            Log.w("AvgCalculator","null");
            retorno = "0,0";
        }

        return retorno;
    }
}
