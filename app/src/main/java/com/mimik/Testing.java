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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lazyloaderGrid.LazyImageLoadAdapterGrid;

/**
 * Created by Arsalan on 2015-11-26.
 */
public class Testing extends Activity {


    //private Post_request.HTTP_req getStreamdata = null;
    String user_id;
    Button follow;
    String todo_follow_button = "check";



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


        // calling the asynctask
        new getJSONformimik().execute();
        // calling checkforfriends so see if users are already friends
        //new checkfriends().execute();


        // hiding buttons
        Button b = (Button)findViewById(R.id.button2);
        b.setVisibility(View.GONE);
        Button b2 = (Button)findViewById(R.id.btnCapturePicture);
        b2.setVisibility(View.GONE);

        ImageView profile_img = (ImageView)findViewById(R.id.profile_pic);
        profile_img.setVisibility(View.VISIBLE);

        follow = (Button)findViewById(R.id.follow);
        follow.setVisibility(View.VISIBLE);





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
        //grid.setAdapter(null);
        super.onDestroy();
    }


    // this is attached with the button
    public View.OnClickListener listener=new View.OnClickListener(){
        @Override
        public void onClick(View arg0) {


        }
    };


    // this can be added in the LazyImageLoadAdapterGrid
    public void onItemClick(JSONObject mimik_js_obj)
    {
        //String tempValues = img_url;

        Intent show_mimik = new Intent(Testing.this, Show_mimik.class);
        show_mimik.putExtra("js_obj", mimik_js_obj.toString());

        startActivity(show_mimik);
    }



    // Image urls used in LazyImageLoadAdapter.java file



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


                String targetURL = "http://campushappens.com/mimik/php/test.php";

                //TODO: what to do with POST['command']
                String urlParameters  = URLEncoder.encode("command", "UTF-8");




                //TODO remove it 2
                Log.d("LOGTAG", "TESTING;"+targetURL);

                URL url;
                HttpURLConnection connection = null;
                try {
                    //TODO remove it 2

                    url = new URL(targetURL);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");

                    connection.setRequestProperty("Content-Length", "" +
                            Integer.toString(urlParameters.getBytes().length));
                    connection.setRequestProperty("Content-Language", "en-US");

                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);


                    //Send request
                    DataOutputStream wr = new DataOutputStream (
                            connection.getOutputStream ());
                    Log.d("LOGTAG", "before WB sending data");
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();

                    Log.d("LOGTAG", "after sending data");

                    //Create connection
                    //Get Response
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;

                    Log.d("LOGTAG", "before starting Buffer");
                    StringBuffer response = new StringBuffer();
                    while((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    Log.d("LOGTAG", "before retruning response;" + response);
                    return response.toString();

                } catch (Exception e) {

                    e.printStackTrace();
                    return null;

                } finally {

                    if(connection != null) {
                        connection.disconnect();
                    }
                }




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



            }
            catch (Throwable t){Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");}

            super.onPostExecute(result);

        }

    }





}