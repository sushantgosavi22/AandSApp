package com.aandssoftware.aandsinventory.utilities;

import android.content.Context;
import android.graphics.Typeface;


public class FontFactory {
  
  final String LATO_REGULAR = "font/Lato-Regular.ttf";
  final String LATO_SEMIBOLD = "font/Lato-Semibold.ttf";
  final String LATO_BOLD = "font/Lato-Bold.ttf";
  final String LATO_HEAVY = "font/Lato-Heavy.ttf";

    Typeface regular;
    Typeface bold;
    Typeface heavy;
    Typeface semibold;

    public FontFactory(Context context){
        regular = Typeface.createFromAsset(context.getAssets(),LATO_REGULAR);
        bold = Typeface.createFromAsset(context.getAssets(),LATO_BOLD);
        heavy = Typeface.createFromAsset(context.getAssets(),LATO_HEAVY);
        semibold = Typeface.createFromAsset(context.getAssets(),LATO_SEMIBOLD);
    }

    public Typeface getRegular(){
        return regular;
    }

    public Typeface getBold() {
        return bold;
    }

    public Typeface getHeavy() {
        return heavy;
    }

    public Typeface getSemibold() {
        return semibold;
    }
}
