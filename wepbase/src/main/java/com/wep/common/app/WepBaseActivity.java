package com.wep.common.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public abstract class WepBaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        for (int j = 0; j < menu.size(); j++) {
            MenuItem item = menu.getItem(j);
            item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    public abstract void onHomePressed();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }else if (id == R.id.action_home) {
            /*Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();*/
            onHomePressed();
        }else if (id == R.id.action_screen_shot) {
            ActionBarUtils.takeScreenshot(WepBaseActivity.this,findViewById(android.R.id.content).getRootView());
        }
        return super.onOptionsItemSelected(item);
    }
}
