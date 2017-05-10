package com.mimik;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import lazyloader.ImageLoader;

/**
 * Created by Arsalan on 2015-11-28.
 */
public class Show_mimik extends Activity {

    public ImageLoader imageLoader;
    public TextView likes, upload_user, dislikes;
    public String img_path_org, img_path_mimik = "" ; //getIntent().getExtras().getString("image_url");
    public JSONObject mimik_jo = null;
    public Map<String, String> jsdata_map = new HashMap<>();
    public ImageView imageView;
    public int current_img; //0 for mimik, 1 for original

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showmimik);

        // Create ImageLoader object to download and show image in list
        // Call ImageLoader constructor to initialize FileCache
        imageLoader = new ImageLoader(this.getApplicationContext());

        imageView = (ImageView) findViewById(R.id.mimikImage);

        try {
            mimik_jo = new JSONObject(getIntent().getExtras().getString("js_obj"));
            jsdata_map.put("id", mimik_jo.getString("id"));
            jsdata_map.put("title", mimik_jo.getString("title"));
            jsdata_map.put("original_id", mimik_jo.getString("original_id"));
            jsdata_map.put("upload_user", mimik_jo.getString("upload_user"));
            jsdata_map.put("likes", mimik_jo.getString("likes"));
            jsdata_map.put("dislikes", mimik_jo.getString("dislikes"));
            jsdata_map.put("username", mimik_jo.getString("username"));


            img_path_mimik = Global_var.URL_mimik_img+jsdata_map.get("original_id") +"-"+jsdata_map.get("upload_user")
                     + "-"+jsdata_map.get("id") +".jpg";

            img_path_org = Global_var.URL_org_img + jsdata_map.get("original_id") + ".jpg";

        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: from Display to show mimik");
        }
        Toast.makeText(Show_mimik.this,
                "Mimik URL:" + Global_var.screen_height + mimik_jo.toString(),
                Toast.LENGTH_LONG).show();

       // new DownloadImageTask((ImageView) findViewById(R.id.mimikImage)).execute(img_id);
        current_img = 0;
        imageLoader.DisplayImage(img_path_mimik,imageView);

        //CustomImageView mImageView = (CustomImageView)findViewById(R.id.customImageVIew1);
        //imageLoader.DisplayImage(img_path, (CustomImageView) findViewById(R.id.customImageVIew1));


        // textview initializing
        likes = (TextView)findViewById(R.id.likes);
        upload_user = (TextView)findViewById(R.id.userId);
        dislikes = (TextView)findViewById(R.id.dislikes);

        likes.setText(jsdata_map.get("likes")+" likes");
        dislikes.setText(jsdata_map.get("dislikes")+" dislikes");
        upload_user.setText(jsdata_map.get("username"));
        upload_user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                Intent i = new Intent(getApplicationContext(), User_profile.class);
                i.putExtra("user_id", jsdata_map.get("upload_user"));
                startActivity(i);
                return false;
            }
        });



        final Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                vibrator.vibrate(100);

                if(current_img == 0)
                    {imageLoader.DisplayImage(img_path_org,imageView);
                        current_img=1;}
                else{imageLoader.DisplayImage(img_path_mimik,imageView);
                    current_img=0;}

                return true;
            }
        });

    }



    public void createNotification(View view) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, Home.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject").setSmallIcon(R.drawable.dislike1)
                .setContentIntent(pIntent)
                .addAction(R.drawable.dislike1, "Call", pIntent)
                .addAction(R.drawable.dislike1, "More", pIntent)
                .addAction(R.drawable.dislike1, "And more", pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


}
