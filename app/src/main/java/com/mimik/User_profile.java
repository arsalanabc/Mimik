package com.mimik;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewDebug;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lazyloaderGrid.LazyImageLoadAdapterGrid;

/**
 * Created by Arsalan on 2015-11-26.
 */
public class User_profile extends Activity {

    GridView grid;
    LazyImageLoadAdapterGrid adapter;
    //private Post_request.HTTP_req getStreamdata = null;
    String profile_id;
    Button follow;




    // upload tut
    // LogCat tag
   // private static final String TAG = MainActivity.class.getSimpleName();



    // end upload tut

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_image);


        // get width and height
        Display display = getWindowManager().getDefaultDisplay();
        float density  = getResources().getDisplayMetrics().density;
        Global_var.screen_width = display.getWidth() / density;
        Global_var.screen_height = display.getHeight() /density;

        //testing user_id
        TextView tv = (TextView)findViewById(R.id.testTV);
        tv.setVisibility(View.VISIBLE);
        tv.setText("extra is " + this.getIntent().getStringExtra("user_id") + "G-U-ID is " + Global_var.user_id+"username"+Global_var.username);


        // if username on showmimik is clicked, and user hasnt clicked his own username
        if(this.getIntent().hasExtra("user_id")
                && !this.getIntent().getStringExtra("user_id").equals(Global_var.user_id)) {

            String extra_user_id = this.getIntent().getStringExtra("user_id");
            // need the user id to show the mimiks

                profile_id = extra_user_id;
                follow = (Button)findViewById(R.id.follow);
                follow.setVisibility(View.VISIBLE);
                // calling checkforfriends so see if users are already friends
                new checkforfriends("check").execute();


        } else// if MyWall button is clicked
            profile_id = Global_var.user_id;



        // calling the asynctask
        new getJSONformimik().execute();





        grid=(GridView)findViewById(R.id.gridView);

        // Create custom adapter for listview
        //adapter = new LazyImageLoadAdapterGrid(this, mStrings );

        //Set adapter to listview
        //grid.setAdapter(adapter);

        //int c_width = measureCellWidth(getBaseContext(), grid.getRootView());
        //grid.setColumnWidth(c_width);




        // hiding buttons
        Button b = (Button)findViewById(R.id.button2);
        b.setVisibility(View.GONE);
        Button b2 = (Button)findViewById(R.id.btnCapturePicture);
        b2.setVisibility(View.GONE);

        ImageView profile_img = (ImageView)findViewById(R.id.profile_pic);
        profile_img.setVisibility(View.VISIBLE);







        // upload stuff
        // Changing action bar background color
        // These two lines are not needed
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getResources().getString(R.color.action_bar))));




        /**
         * Capture image button click event
         */

    }


    @Override
    public void onResume()
    {
        super.onResume();
    }


    @Override
    public void onDestroy()
    {
        // Remove adapter reference from list
        grid.setAdapter(null);
        super.onDestroy();
    }


    // this is attached with the button
    public View.OnClickListener listener=new View.OnClickListener(){
        @Override
        public void onClick(View arg0) {

            //Refresh cache directory downloaded images
            adapter.imageLoader.clearCache();
            adapter.notifyDataSetChanged();
        }
    };


    // this can be added in the LazyImageLoadAdapterGrid
    public void onItemClick(JSONObject mimik_js_obj)
    {
        //String tempValues = img_url;

        Intent show_mimik = new Intent(User_profile.this, Show_mimik.class);
        show_mimik.putExtra("js_obj", mimik_js_obj.toString());

        startActivity(show_mimik);
    }



    // Image urls used in LazyImageLoadAdapter.java file


    // lets get JSON from POST server
    /**
     * making HHTP request to get JSON array for display mimiks
     * */
    private class getJSONformimik extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            super.onPreExecute();
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {

        }


        @Override
        protected String doInBackground(Void... params) {

            try {


                String link = MainActivity.website+"/mimik/index.php";

                //TODO: what to do with POST['command']
                String var  = URLEncoder.encode("command", "UTF-8")
                        + "=" + URLEncoder.encode("userpagedata", "UTF-8");

                var += "&" + URLEncoder.encode("user_id", "UTF-8")
                        + "=" + URLEncoder.encode(profile_id, "UTF-8");


                //TODO remove it
                Log.d("getJSONformimik done", var);

                return Post_request.excutePost(link,var);




            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());


            }

        }



        @Override
        protected void onPostExecute(String result) {
            Log.d("onPostExecute", "Display- server response: " + result);

            // showing the server response in an alert dialog
            // showAlert(result);
            //finish();


            try {
                JSONArray json_arr = new JSONArray(result);// create JSON array from string
                Global_var.mimik_jsarray = json_arr;

                adapter = new LazyImageLoadAdapterGrid(User_profile.this, new String[]{}, json_arr);
                grid.setAdapter(adapter);


            }
            catch (Throwable t){Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");}

            super.onPostExecute(result);

        }

    }

    // this is to add friend/follow and check for friend status in database
    private class checkforfriends extends AsyncTask<Void, Integer, String> {

        private final String value;

        checkforfriends(final String val) {
            this.value = val;
        }

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {


                String link = MainActivity.website+"/mimik/index.php";

                //TODO: what to do with POST['command']
                String par  = URLEncoder.encode("command", "UTF-8")
                        + "=" + URLEncoder.encode("followbutton", "UTF-8");

                par += "&" + URLEncoder.encode("user_id", "UTF-8")
                        + "=" + URLEncoder.encode(Global_var.user_id, "UTF-8");
                par += "&" + URLEncoder.encode("friend_id", "UTF-8")
                        + "=" + URLEncoder.encode(profile_id, "UTF-8");
                par += "&" + URLEncoder.encode("todo", "UTF-8")
                        + "=" + URLEncoder.encode(value, "UTF-8");


                //TODO remove it
                //Log.d("follow done", par);
                return Post_request.excutePost(link,par);


            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }// end catch
        } // end doIn

        @Override
        protected void onPostExecute(String result) {
            //Log.d("followresult", result);

            String status =  "";
            try {
                JSONObject json_arr = new JSONObject(result);// create JSON array from string
                status = json_arr.getString("error");

            }catch (Throwable t){Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");}

            //follow.setText("a"+status+"a");


            switch (status){
                //default: follow.setText("123"+result); break;
                case "followed":
                    follow.setText("Unfollow");
                    follow.setBackgroundColor(Color.RED);

                    follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new checkforfriends("unfollow").execute();
                        }
                    });
                    break;
                case "notfollowed":
                    follow.setBackgroundColor(Color.BLUE);

                    follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new checkforfriends("follow").execute();
                        }
                    });
                    break;
            }

            super.onPostExecute(result);

        }


    }


    /*
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    * */


}