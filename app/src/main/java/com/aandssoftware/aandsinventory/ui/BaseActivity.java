package com.aandssoftware.aandsinventory.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindView;
import com.aandssoftware.aandsinventory.R;

public class BaseActivity extends AppCompatActivity {
  
  @Nullable
  @BindView(R.id.progress_bar)
  View progressBar;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  public void setupActionBar(String title) {
    setupActionBar(title, true);
  }
  
  public void setupActionBar(
      String title, boolean backButtonVisibility) {
    getSupportActionBar().setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
    getSupportActionBar().setDisplayShowCustomEnabled(true);
    LayoutInflater inflater = LayoutInflater.from(this);
    View customView = inflater.inflate(R.layout.custom_action_bar_layout, null);
    getSupportActionBar().setCustomView(customView);
    getSupportActionBar().setElevation(4);
    Toolbar parent = (Toolbar) customView.getParent();
    parent.setContentInsetsAbsolute(0, 0);
    setScreenTitle(title, backButtonVisibility);
  }
  
  private void setScreenTitle(String title, boolean backButtonVisibility) {
    TextView actionBarTitle =
        getSupportActionBar().getCustomView().findViewById(R.id.actionBarTitle);
    actionBarTitle.setText(title);
    ImageButton backButton = getSupportActionBar().getCustomView().findViewById(R.id.navBarBack);
    backButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    backButton.setVisibility(backButtonVisibility ? View.VISIBLE : View.GONE);
  }
  
  private void setScreenSubTitle(String subTitle) {
    TextView actionBarSubTitle =
        getSupportActionBar().getCustomView().findViewById(R.id.actionBarSubTitle);
    actionBarSubTitle.setVisibility(View.VISIBLE);
    actionBarSubTitle.setText(subTitle);
  }
  
  public void showSnackBarMessage(ViewGroup parentLayout, String message) {
    showSnackBarMessage(parentLayout, message, Snackbar.LENGTH_LONG);
  }
  
  public Snackbar showSnackBarMessage(ViewGroup parentLayout, String message, int duration) {
    Snackbar snackbar = Snackbar.make(parentLayout, message, duration);
    View sbView = snackbar.getView();
    sbView.setBackgroundColor(getResources().getColor(R.color.gravel_color));
    TextView sbTextView = sbView.findViewById(android.support.design.R.id.snackbar_text);
    sbTextView.setTextAppearance(this, R.style.bold_white_text_style_size_14);
    TextView snackbarActionTextView =
        snackbar.getView().findViewById(android.support.design.R.id.snackbar_action);
    if (snackbarActionTextView != null) {
      snackbar.show();
    }
    return snackbar;
  }
  
  /**
   * Show Progress Bar.
   */
  public void showProgressBar() {
    if (progressBar != null) {
      progressBar.setVisibility(View.VISIBLE);
    }
  }
  
  /**
   * Hide Progress Bar.
   */
  public void dismissProgressBar() {
    if (progressBar != null) {
      progressBar.setVisibility(View.GONE);
    }
  }
}
