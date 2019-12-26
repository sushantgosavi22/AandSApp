package com.aandssoftware.aandsinventory.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.widget.LinearLayoutCompat;
import com.aandssoftware.aandsinventory.R;

public class CustomEditText extends LinearLayoutCompat {
  
  public CustomEditText(Context context) {
    super(context);
    init(context, null);
  }
  
  public CustomEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }
  
  public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }
  
  
  private void init(Context context, AttributeSet attrs) {
    final LayoutInflater inflater = LayoutInflater.from(context);
    final View viewEditTextError = getView(inflater);
    /*rlEditTextField = viewEditTextError.findViewById(R.id.rlEditTextField);
    etText = viewEditTextError.findViewById(R.id.etText);
    tvErrorMsg = viewEditTextError.findViewById(R.id.tvErrorMsg);
    btnCancel = viewEditTextError.findViewById(R.id.btnCancel);
    btnCancel.setOnClickListener(v -> etText.getText().clear());
    // handling of the EditText scroll behaviour
    etText.setOnTouchListener(
        (view, motionEvent) -> {
          if (etText.hasFocus()) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
              view.getParent().requestDisallowInterceptTouchEvent(false);
              return true;
            }
          }
          return false;
        });
    
    errorHandlingViewAdapter =
        new ErrorHandlingViewAdapter(getContext(), rlEditTextField, tvErrorMsg, this);
    
    final TypedArray attribute =
        context.obtainStyledAttributes(attrs, R.styleable.EditTextErrorCustomView);
    setAttributes(attribute);
    addView(viewEditTextError);
    attribute.recycle();*/
  }
  
  protected View getView(LayoutInflater layoutInflater) {
    return layoutInflater.inflate(R.layout.custome_edit_text, null);
  }
  
  private void setAttributes(TypedArray attribute) {
    /* final String hint = attribute.getString(R.styleable.EditTextErrorCustomView_android_hint);
   final int inputType =
        attribute.getInt(
            R.styleable.EditTextErrorCustomView_android_inputType, InputType.TYPE_CLASS_TEXT);
    enableCancelOption =
        attribute.getBoolean(R.styleable.EditTextErrorCustomView_enableCancelOption, false);
    
    clearErrorOnTextChanged =
        attribute.getBoolean(R.styleable.EditTextErrorCustomView_clearErrorOnTextChanged, false);
    maxLength =
        attribute.getInt(R.styleable.EditTextErrorCustomView_maxLength, INVALID_INT_ATTRIBUTE);
    
    maxLengthErrorMsg = attribute.getString(R.styleable.EditTextErrorCustomView_maxLengthErrorMsg);
    
    final String fieldContentDescription =
        attribute.getString(R.styleable.EditTextErrorCustomView_fieldContentDescription);
    final String errorContentDescription =
        attribute.getString(R.styleable.EditTextErrorCustomView_errorContentDescription);
    final String cancelBtnContentDesc =
        attribute.getString(R.styleable.EditTextErrorCustomView_cancelButtonContentDesc);
    setEditTextContentDescription(fieldContentDescription);
    setCancelButtonContentDesc(cancelBtnContentDesc);
    if (!TextUtils.isEmpty(errorContentDescription)) {
      tvErrorMsg.setContentDescription(errorContentDescription);
    }
    
    getInputFilterList(attribute);
    setInputType(inputType);
    setHint(hint);
    final List<InputFilter> filterList = getInputFilterList(attribute);
    setInputFilters(filterList);*/
  }
}
