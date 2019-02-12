package com.microsoft.nativeo365calendarevents;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.app.ProgressDialog;

public class MainActivity extends AppCompatActivity {

  private final static String TAG = MainActivity.class.getSimpleName();

  private ProgressDialog progress;
  private ListView listEvents;
  private LinearLayout panelSignIn;
  private LinearLayout panelEvents;
  private LinearLayout panelLoadEvent;

  private void onSignin() {
    Toast.makeText(MainActivity.this, "Hello <user>!", Toast.LENGTH_LONG).show();

    setPanelVisibility(false,true,false);
  }

  private void onSignout() {
    setPanelVisibility(true, false, false);
  }

  private void onLoadEvents() {
    Toast.makeText(MainActivity.this,
            "Successfully loaded events from Office 365 calendar",
            Toast.LENGTH_LONG
    ).show();
  }

  private void setPanelVisibility(Boolean showSignIn, Boolean showLoadEvents, Boolean showList) {
    panelSignIn.setVisibility(showSignIn ? View.VISIBLE : View.GONE);
    panelLoadEvent.setVisibility(showLoadEvents ? View.VISIBLE : View.GONE);
    panelEvents.setVisibility(showList ? View.VISIBLE : View.GONE);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    listEvents = findViewById(R.id.list_events);
    panelSignIn = findViewById(R.id.panel_signIn);
    panelEvents = findViewById(R.id.panel_events);
    panelLoadEvent = findViewById(R.id.panel_loadEvent);

    (findViewById(R.id.btn_signIn)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onSignin();
      }
    });

    (findViewById(R.id.btn_signOut)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onSignout();
      }
    });

    (findViewById(R.id.btn_loadEvent)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onLoadEvents();
      }
    });

    setPanelVisibility(true, false, false);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
