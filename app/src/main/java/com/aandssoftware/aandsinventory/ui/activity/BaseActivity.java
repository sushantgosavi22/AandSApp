package com.aandssoftware.aandsinventory.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.common.Utils;
import com.aandssoftware.aandsinventory.ui.component.CustomProgressBar;

public class BaseActivity extends AppCompatActivity {
  
  @Nullable
  @BindView(R.id.progress_bar)
  CustomProgressBar progressBar;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  public void setupActionBar(String title) {
    setupActionBar(title, true);
  }
  
  public void setupActionBar(
      String title, boolean backButtonVisibility) {
    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
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
    backButton.setOnClickListener(v -> finish());
    backButton.setVisibility(backButtonVisibility ? View.VISIBLE : View.GONE);
  }
  
  private void setScreenSubTitle(String subTitle) {
    TextView actionBarSubTitle =
        getSupportActionBar().getCustomView().findViewById(R.id.actionBarSubTitle);
    actionBarSubTitle.setVisibility(View.VISIBLE);
    actionBarSubTitle.setText(subTitle);
  }
  
  public void showSnackBarMessage(String message) {
    Utils.showSnackBarMessage(findViewById(android.R.id.content), message);
  }
  
  /**
   * Show Progress Bar.
   */
  public void showProgressBar() {
    if (progressBar != null) {
      progressBar.showProgressBar();
    }
  }
  
  /**
   * Hide Progress Bar.
   */
  public void dismissProgressBar() {
    if (progressBar != null) {
      progressBar.dismissProgressBar();
    }
  }
}
