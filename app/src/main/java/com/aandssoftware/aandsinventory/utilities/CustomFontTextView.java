package com.aandssoftware.aandsinventory.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.application.RealmApplication;


public class CustomFontTextView extends AppCompatTextView {
  
  private int typefaceType;
  
  public CustomFontTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray array = context.getTheme().obtainStyledAttributes(
        attrs,
        R.styleable.CustomFontTextView,
        0, 0);
    try {
      typefaceType = array.getInteger(R.styleable.CustomFontTextView_font_name, 0);
    } finally {
      array.recycle();
    }
    if (VERSION.SDK_INT >= VERSION_CODES.CUPCAKE) {
      if (!isInEditMode()) {
        //setTypeface(RealmApplication.getInstance().getTypeFace(typefaceType));
      }
    }
  }
}
