package com.aandssoftware.aandsinventory.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.aandssoftware.aandsinventory.R;

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
