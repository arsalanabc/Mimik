package com.mimik;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

import lazyloader.LazyImageLoadAdapter;


/**
 * Created by Arsalan on 2015-11-24.
 */
public class LeaderBoard extends Activity {

    ListView list;
    LazyImageLoadAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leader_board);


        // get width and height
        Display display = getWindowManager().getDefaultDisplay();
        float density  = getResources().getDisplayMetrics().density;
        Global_var.screen_width = display.getWidth() / density;
        Global_var.screen_height = display.getHeight() /density;



        list=(ListView)findViewById(R.id.lb_list);

        Button refresh=(Button)findViewById(R.id.refresh);
        refresh.setOnClickListener(listener);

        // hide the stupid keybaord
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



        // calling the asynctask
        new getJSONforHome().execute();
    }




    @Override
    public void onDestroy()
    {
        // Remove adapter refference from list
        list.setAdapter(null);
        super.onDestroy();
    }

    public OnClickListener listener=new OnClickListener(){
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
            //String tempValue = temp_js_ob.getString("id");

            Intent show_mimik = new Intent(this, Show_mimik.class);
            show_mimik.putExtra("js_obj", temp_js_ob.toString());

            startActivity(show_mimik);

            Toast.makeText(LeaderBoard.this,
                    "Image URL :" + temp_js_ob.toString(),
                    Toast.LENGTH_LONG).show();
        }
        catch (Throwable t){
            Log.e("onlick in hone","failed to create JSON");
        }
    }

    // Image urls used in LazyImageLoadAdapter.java file



    /**
     * making HHTP request to get JSON array for display mimiks
     * */
    private class getJSONforHome extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {

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
                        + "=" + URLEncoder.encode("leader", "UTF-8");

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
            //testTV.setText(result);

            try {
                JSONArray json_arr = new JSONArray(result);// create JSON array from string
                //img_list = Global_var.jsontostring(json_arr, "id");
                Global_var.original_jsarray = json_arr;

                // Create custom adapter for listview
                //TODO we dont need string mStrings for LazyImageAdapter
                adapter=new LazyImageLoadAdapter(LeaderBoard.this, json_arr);

                //Set adapter to listview
                list.setAdapter(adapter);
            }
            catch (Throwable t){Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");}

            super.onPostExecute(result);

        }

    }





}