package com.aandssoftware.aandsinventory.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.listing.ListType;

public class SplashActivity extends AppCompatActivity {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    new Handler().postDelayed(new Runnable() {
      public void run() {
        Intent intent = new Intent(SplashActivity.this, CarouselDashboardActivity.class);
        startActivity(intent);
        finish();
      }
    }, 3*1000);
  }
}
