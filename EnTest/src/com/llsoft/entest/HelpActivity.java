package com.llsoft.entest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class HelpActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_help);
  }

  @Override
  public void onBackPressed() {

    // check if settings.txt exists in the internal storage
    String filename = Utils.getInternalPath(HelpActivity.this) + "/files/settings.txt";

    if (!Utils.isFileExists(filename)) {

      Intent settingsIntent = new Intent(HelpActivity.this, EmailSettingsActivity.class);
      startActivity(settingsIntent);

      finish();
    }
    else {
      Intent testPageIntent = new Intent(HelpActivity.this, TestPageActivity.class);
      startActivity(testPageIntent);
    		
      super.onBackPressed();
    }
  }
}
