package com.mimik;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;


public class Mainmenu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);


        // hide the stupid keybaord
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

        if (id == R.id.myWall) {
            finish();
            Intent act = new Intent(this, Testing.class);
            startActivity(act);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void goto_home(View view){
        Intent act = new Intent(this, Home.class);
        startActivity(act);

    }

    public void goto_leader(View view){
        Intent act = new Intent(this, LeaderBoard.class);
        startActivity(act);

    }

    public void goto_userprofile(View view) {
        Intent act = new Intent(this, User_profile.class);
        startActivity(act);
    }

    public void goto_fanfeed(View view) {
        Intent act = new Intent(this, Fanfeed.class);
        startActivity(act);
    }


    public void goto_challenge(View view){
        Intent events = new Intent(this, Challenge.class);
        startActivity(events);

    }


    public void onResume(){
        super.onResume();
    }
}
