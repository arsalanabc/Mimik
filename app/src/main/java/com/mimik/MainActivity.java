package com.mimik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    //add this variable for Global declaration:
    public static String website = "https://campushappens.netfirms.com";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences;

        sharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);



        if (sharedPreferences.getBoolean("AUTO_ISCHECK", false) ) {


            Intent intent = new Intent();

            //intent.putExtra("USERNAME_ID", sharedPreferences.getString("username", "Ghost of a previous user"));
            //intent.putExtra("userinfo", sharedPreferences.getString("userInfo", "userInfo is missing"));
            //intent.putExtra("username", sharedPreferences.getString("username", "username is missing"));
            //intent.putExtra("password", sharedPreferences.getString("password", "password is missing"));



            // assign username to global var
            Global_var.username = sharedPreferences.getString("username", "username is missing");
            Global_var.user_id = sharedPreferences.getString("user_id", "username is missing");

            //finish home activity
            finish();

            intent.setClass(MainActivity.this, Mainmenu.class);

            startActivity(intent);



            // this is to forget user from the preference (for signing out)
            /*
            Editor ed = sharedPreferences.edit();
            ed.clear();
            ed.commit();
            */

        }
        else{
            this.setContentView(R.layout.activity_main);

        }


    }

    public void sign_in(View view){
        Intent sign_in = new Intent(this, LoginActivity.class);
        startActivity(sign_in);

    }
    public void sign_up(View view){
        Intent sign_up = new Intent(this, signupActivity.class);
        startActivity(sign_up);

    }

}