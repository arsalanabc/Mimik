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
public class Display_image extends Activity {

    GridView grid;
    LazyImageLoadAdapterGrid adapter;
    //private Post_request.HTTP_req getStreamdata = null;
    private ProgressBar progressBar;
    TextView tv1;



    // upload tut
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    String img_id;


    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int SELECT_IMAGE_REQUEST_CODE = 101;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video

    private Button btnCapturePicture, btnRecordVideo;
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


        // set the image id
        img_id = getIntent().getStringExtra("image_id");

        // setting up textview for testing
        tv1 = (TextView)findViewById(R.id.testTV);
        //tv1.setText(img_id);


        // setting up the progressbar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // calling the asynctask
        new getJSONformimik().execute();

        grid=(GridView)findViewById(R.id.gridView);

        // Create custom adapter for listview
        //adapter = new LazyImageLoadAdapterGrid(this, mStrings );

        //Set adapter to listview
        //grid.setAdapter(adapter);

        //int c_width = measureCellWidth(getBaseContext(), grid.getRootView());
        //grid.setColumnWidth(c_width);




        // no button
        Button b=(Button)findViewById(R.id.button2);
        b.setOnClickListener(listener);

        // hide the stupid keybaord
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // upload stuff
        // Changing action bar background color
        // These two lines are not needed
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getResources().getString(R.color.action_bar))));

        btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);


        /**
         * Capture image button click event
         */
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                //captureImage();

                // select from gallery
                selectImage();
            }
        });

        // Checking camera availability not in use anymore
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }

        // end upload stuff


    }

    // upload functions
    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
            }
    }
    /* to upload a pivture from the gallery
    */
    private void selectImage() {


        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Start the Intent
        startActivityForResult(galleryIntent, SELECT_IMAGE_REQUEST_CODE);

    }
    /**
     * Launching camera app to capture image
     */
    private void captureImage() {

        ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            fileUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        /*
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
*/
    }
    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }



    /**
    * handling the result after user picks the image
     * */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                fileUri = data.getData();
                    launchUploadActivity(true);


            } else {
                Toast.makeText(this, "You haven't picked Image"+fileUri,
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong"+fileUri, Toast.LENGTH_LONG)
                    .show();
        }

    }





    /**
     * Receiving activity result method will be called after closing the camera
     * */
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // quality testing
        switch (requestCode) {

            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                if (requestCode == SELECT_IMAGE_REQUEST_CODE2121)
                    if (resultCode == RESULT_OK) {
                        try {
                            Bitmap thumbnail =  MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), fileUri);

                            //imgView.setImageBitmap(thumbnail);
                            //fileUri = getRealPathFromURI(fileUri);
                            launchUploadActivity(true, thumbnail);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
        }
        // quality testing end

        /*

        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // video successfully recorded
                // launching upload activity
                launchUploadActivity(false);

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        } // ignore up to here
    } */

    // part of the quality testing
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(Display_image.this, UploadActivity.class);
        i.putExtra("filePath", getRealPathFromURI(fileUri)); //fileUri.getPath());
        i.putExtra("isImage", isImage);
        i.putExtra("img_id", img_id);

        startActivity(i);
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Global_var.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Global_var.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    // end upload functions

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

        Intent show_mimik = new Intent(this, Show_mimik.class);
        show_mimik.putExtra("js_obj", mimik_js_obj.toString());

        startActivity(show_mimik);

        //Toast.makeText(Display_image.this,
        //        "Image URL :"+Global_var.screen_height+mimik_js_obj.toString(),
        //        Toast.LENGTH_LONG).show();
    }



    // Image urls used in LazyImageLoadAdapter.java file


    // lets get JSON from POST server

    // getJSON called in onCreate


    private String[] mStrings={
            "http://i729.photobucket.com/albums/ww291/jhart18/Loin.jpg",
            "http://androidexample.com/media/webservice/LazyListView_images/image1.png",
            "http://campushappens.com/mimik/upload/3275.jpg",
            "http://campushappens.com/mimik/upload/3276.jpg",
            "http://campushappens.com/mimik/upload/3277.png",
            "http://i729.photobucket.com/albums/ww291/jhart18/Loin.jpg",
            "http://campushappens.com/mimik/upload/original/1 (2).jpg",
            "http://campushappens.com/mimik/upload/original/1.jpg",
            "http://campushappens.com/mimik/upload/original/10 (2).jpg",
            "http://campushappens.com/mimik/upload/original/10.jpg",
            "http://campushappens.com/mimik/upload/original/100 (2).jpg",
            "http://campushappens.com/mimik/upload/original/100.jpg",
            "http://campushappens.com/mimik/upload/original/101.jpg",
            "http://campushappens.com/mimik/upload/original/102.jpg",
            "http://campushappens.com/mimik/upload/original/103.jpg",
            "http://campushappens.com/mimik/upload/original/104.jpg",
            "http://campushappens.com/mimik/upload/original/105.jpg",
            "http://campushappens.com/mimik/upload/original/106.jpg",
            "http://campushappens.com/mimik/upload/original/107.jpg",
            "http://campushappens.com/mimik/upload/original/108.jpg",
            "http://campushappens.com/mimik/upload/original/109.jpg",
            "http://campushappens.com/mimik/upload/original/11 (2).jpg",
            "http://campushappens.com/mimik/upload/original/11.jpg",
            "http://campushappens.com/mimik/upload/original/110.jpg",
            "http://campushappens.com/mimik/upload/original/111.jpg",
            "http://campushappens.com/mimik/upload/original/112.jpg",
            "http://campushappens.com/mimik/upload/original/113.jpg",
            "http://campushappens.com/mimik/upload/original/114.jpg",
            "http://campushappens.com/mimik/upload/original/115.jpg",
            "http://campushappens.com/mimik/upload/original/116.jpg",
            "http://campushappens.com/mimik/upload/original/117.jpg",
            "http://campushappens.com/mimik/upload/original/118.jpg",
            "http://campushappens.com/mimik/upload/original/119.jpg",
            "http://campushappens.com/mimik/upload/original/12 (2).jpg",
            "http://campushappens.com/mimik/upload/original/12.jpg",
            "http://campushappens.com/mimik/upload/original/120.jpg",
            "http://campushappens.com/mimik/upload/original/121.jpg",
            "http://campushappens.com/mimik/upload/original/122.jpg",
            "http://campushappens.com/mimik/upload/original/123.jpg",
            "http://campushappens.com/mimik/upload/original/124.jpg",
            "http://campushappens.com/mimik/upload/original/125.jpg",
            "http://campushappens.com/mimik/upload/original/126.jpg",
            "http://campushappens.com/mimik/upload/original/127.jpg",
            "http://campushappens.com/mimik/upload/original/127_Hours.jpg",
            "http://campushappens.com/mimik/upload/original/128.jpg",
            "http://campushappens.com/mimik/upload/original/129.jpg",
            "http://campushappens.com/mimik/upload/original/13 (2).jpg",
            "http://campushappens.com/mimik/upload/original/13.jpg",
            "http://campushappens.com/mimik/upload/original/130.jpg",
            "http://campushappens.com/mimik/upload/original/131.jpg",
            "http://campushappens.com/mimik/upload/original/132.jpg",
            "http://campushappens.com/mimik/upload/original/133.jpg",
            "http://campushappens.com/mimik/upload/original/134.jpg",
            "http://campushappens.com/mimik/upload/original/135.jpg",
            "http://campushappens.com/mimik/upload/original/136.jpg",
            "http://campushappens.com/mimik/upload/original/137.jpg",
            "http://campushappens.com/mimik/upload/original/138.jpg",
            "http://campushappens.com/mimik/upload/original/139.jpg",
            "http://campushappens.com/mimik/upload/original/14 (2).jpg",
            "http://campushappens.com/mimik/upload/original/14.jpg",
            "http://campushappens.com/mimik/upload/original/140.jpg",
            "http://campushappens.com/mimik/upload/original/141.jpg",
            "http://campushappens.com/mimik/upload/original/142.jpg",
            "http://campushappens.com/mimik/upload/original/143.jpg",
            "http://campushappens.com/mimik/upload/original/144.jpg",
            "http://campushappens.com/mimik/upload/original/145.jpg",
            "http://campushappens.com/mimik/upload/original/146.jpg",
            "http://campushappens.com/mimik/upload/original/147.jpg",
            "http://campushappens.com/mimik/upload/original/148.jpg",
            "http://campushappens.com/mimik/upload/original/149.jpg",
            "http://campushappens.com/mimik/upload/original/15 (2).jpg",
            "http://campushappens.com/mimik/upload/original/15.jpg",
            "http://campushappens.com/mimik/upload/original/150.jpg",
            "http://campushappens.com/mimik/upload/original/151.jpg",
            "http://campushappens.com/mimik/upload/original/152.jpg",
            "http://campushappens.com/mimik/upload/original/153.jpg",
            "http://campushappens.com/mimik/upload/original/154.jpg",
            "http://campushappens.com/mimik/upload/original/155.jpg",
            "http://campushappens.com/mimik/upload/original/156.jpg",
            "http://campushappens.com/mimik/upload/original/157.jpg",
            "http://campushappens.com/mimik/upload/original/158.jpg",
            "http://campushappens.com/mimik/upload/original/159.jpg",
            "http://campushappens.com/mimik/upload/original/16 (2).jpg",
            "http://campushappens.com/mimik/upload/original/16.jpg",
            "http://campushappens.com/mimik/upload/original/160.jpg",
            "http://campushappens.com/mimik/upload/original/161.jpg",
            "http://campushappens.com/mimik/upload/original/162.jpg",
            "http://campushappens.com/mimik/upload/original/163.jpg",
            "http://campushappens.com/mimik/upload/original/164.jpg",
            "http://campushappens.com/mimik/upload/original/165.jpg",
            "http://campushappens.com/mimik/upload/original/166.jpg",
            "http://campushappens.com/mimik/upload/original/167.jpg",
            "http://campushappens.com/mimik/upload/original/168.jpg",
            "http://campushappens.com/mimik/upload/original/169.jpg",
            "http://campushappens.com/mimik/upload/original/17 (2).jpg",
            "http://campushappens.com/mimik/upload/original/17.jpg",
            "http://campushappens.com/mimik/upload/original/170.jpg",
            "http://campushappens.com/mimik/upload/original/171.jpg",
            "http://campushappens.com/mimik/upload/original/172.jpg",
            "http://campushappens.com/mimik/upload/original/173.jpg",
            "http://campushappens.com/mimik/upload/original/174.jpg",
            "http://campushappens.com/mimik/upload/original/175.jpg",
            "http://campushappens.com/mimik/upload/original/176.jpg",
            "http://campushappens.com/mimik/upload/original/177.jpg",
            "http://campushappens.com/mimik/upload/original/178.jpg",
            "http://campushappens.com/mimik/upload/original/179.jpg",
            "http://campushappens.com/mimik/upload/original/18 (2).jpg",
            "http://campushappens.com/mimik/upload/original/18.jpg",
            "http://campushappens.com/mimik/upload/original/180.jpg",
            "http://campushappens.com/mimik/upload/original/19 (2).jpg",
            "http://campushappens.com/mimik/upload/original/19.jpg",
            "http://campushappens.com/mimik/upload/original/2 (2).jpg",
            "http://campushappens.com/mimik/upload/original/2.jpg",
            "http://campushappens.com/mimik/upload/original/20 (2).jpg",
            "http://campushappens.com/mimik/upload/original/20.jpg",
            "http://campushappens.com/mimik/upload/original/21 (2).jpg",
            "http://campushappens.com/mimik/upload/original/21.jpg",
            "http://campushappens.com/mimik/upload/original/22 (2).jpg",
            "http://campushappens.com/mimik/upload/original/22.jpg",
            "http://campushappens.com/mimik/upload/original/23 (2).jpg",
            "http://campushappens.com/mimik/upload/original/23.jpg",
            "http://campushappens.com/mimik/upload/original/24 (2).jpg",
            "http://campushappens.com/mimik/upload/original/24.jpg",
            "http://campushappens.com/mimik/upload/original/25 (2).jpg",
            "http://campushappens.com/mimik/upload/original/25.jpg",
            "http://campushappens.com/mimik/upload/original/26 (2).jpg",
            "http://campushappens.com/mimik/upload/original/26.jpg",
            "http://campushappens.com/mimik/upload/original/27 (2).jpg",
            "http://campushappens.com/mimik/upload/original/27.jpg",
            "http://campushappens.com/mimik/upload/original/28 (2).jpg",
            "http://campushappens.com/mimik/upload/original/28.jpg",
            "http://campushappens.com/mimik/upload/original/29 (2).jpg"
    };

    //private String[] img_list;


    /**
     * making HHTP request to get JSON array for display mimiks
     * */
    private class getJSONformimik extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            //txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }


        @Override
        protected String doInBackground(Void... params) {

            try {


                String link = MainActivity.website+"/mimik/index.php";

                //TODO: what to do with POST['command']
                String var  = URLEncoder.encode("command", "UTF-8")
                        + "=" + URLEncoder.encode("mimikdata", "UTF-8");

                var += "&" + URLEncoder.encode("org_img_id", "UTF-8")
                        + "=" + URLEncoder.encode(img_id, "UTF-8");


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
            tv1.setText(result);

            try {
            JSONArray json_arr = new JSONArray(result);// create JSON array from string
            Global_var.mimik_jsarray = json_arr;

            adapter = new LazyImageLoadAdapterGrid(Display_image.this, mStrings, json_arr);
            grid.setAdapter(adapter);
             }
            catch (Throwable t){Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");}





            super.onPostExecute(result);

        }

    }




}