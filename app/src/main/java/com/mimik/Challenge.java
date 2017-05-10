package com.mimik;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

import lazyloader.LazyImageLoadAdapter;

/**
 * Created by Arsalan on 2016-01-31.
 */
public class Challenge  extends Activity {

    ListView list;
    LazyImageLoadAdapter adapter;
    JSONArray JO_imglist; // to hold the JO from the getJSON
    //TextView testTV;



    //TODO
    String img_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge);




        // get width and height
        Display display = getWindowManager().getDefaultDisplay();
        float density  = getResources().getDisplayMetrics().density;
        Global_var.screen_width = display.getWidth() / density;
        Global_var.screen_height = display.getHeight() /density;



        list=(ListView)findViewById(R.id.challenge_list);



        Button b=(Button)findViewById(R.id.refresh);
        b.setOnClickListener(listener);

        // hide the stupid keybaord
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //testTV = (TextView)findViewById(R.id.testTV);


        // calling the asynctask
        new getfanfeedJSON().execute();


        //testTV.setText(Global_var.http_result.toString());
        //testTV.setText(Global_var.strarr_str(Global_var.http_result));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public void onDestroy()
    {
        // Remove adapter reference from list
        list.setAdapter(null);
        super.onDestroy();
    }

    public View.OnClickListener listener=new View.OnClickListener(){
        @Override
        public void onClick(View arg0) {

            //Refresh cache directory downloaded images
            adapter.imageLoader.clearCache();
            adapter.notifyDataSetChanged();
        }
    };

    // called in the lazyimageLoadAdapter
    public void onItemClick(int mPosition)
    {
        try {
            JSONObject temp_js_ob = Global_var.original_jsarray.getJSONObject(mPosition);
            String tempValue = temp_js_ob.getString("id");

            Intent display_image = new Intent(this, Display_image.class);
            display_image.putExtra("image_id", tempValue);
            //display_image.putExtra("img_lis",img_list);
            startActivity(display_image);

            Toast.makeText(Challenge.this,
                    "Image URL :" + tempValue,
                    Toast.LENGTH_LONG).show();
        }
        catch (Throwable t){
            Log.e("onlick in hone", "failed to create JSON");
        }
    }



    /**
     * making HHTP request to get JSON array for display mimiks
     * */
    private class getfanfeedJSON extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {

            // updating percentage value
            //testTV.setText(String.valueOf(progress[0]) + "%");

        }


        @Override
        protected String doInBackground(Void... params) {

            try {


                String link = MainActivity.website+"/mimik/php/test.php";

                //TODO: what to do with POST['command']
                String var  = URLEncoder.encode("command", "UTF-8")
                        + "=" + URLEncoder.encode("challenge", "UTF-8");

                var  += "&" + URLEncoder.encode("user_id", "UTF-8")
                        + "=" + URLEncoder.encode(Global_var.user_id, "UTF-8");

                //TODO remove it
                Log.d("getJSONforfanfeed done", var);

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
            //testTV.setText(result);

            try {
                JSONArray json_arr = new JSONArray(result);// create JSON array from string
                //img_list = Global_var.jsontostring(json_arr, "id");
                Global_var.original_jsarray = json_arr;

                // Create custom adapter for listview
                //TODO we dont need string mStrings for LazyImageAdapter
                adapter=new LazyImageLoadAdapter(Challenge.this, json_arr);

                //Set adapter to listview
                list.setAdapter(adapter);
            }
            catch (Throwable t){Log.e("My App", "Could not parse malformed JSON from challenge: \"" + result + "\"");}





            super.onPostExecute(result);

        }

    }





}