package com.mimik;

import android.app.Activity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Arsalan on 2015-11-26.
 */
public class Global_var extends Activity {

    public static String username = "";
    public static String user_id = "";

    public static float screen_width = 0;
    public static float screen_height = 0;

    // File upload url (replace the ip with your server address)
    public static final String FILE_UPLOAD_URL = "http://campushappens.com/mimik/fileUpload.php";

    // Directory name to store captured images and videos  Android File Upload
    public static final String IMAGE_DIRECTORY_NAME = "Mimik";

    // this is the link where original images are stored
    //USED in LazyLoaderadaptor
    public static final String URL_org_img = "http://campushappens.com/mimik/upload/original/";

    //USED in LazyLoaderadaptorGrid
    public static final String URL_mimik_img = "http://campushappens.com/mimik/upload/";


    // this will be assigned to new value after every http_req instance
    //public static String[] http_result;
    public static JSONArray original_jsarray;
    public static JSONArray mimik_jsarray;



    public void logLargeString(String str) {
        if(str.length() > 3000) {
            Log.i("Long TEXT", str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i("Long TEXT", str); // continuation
        }
    }



    // takes jsonarray and outputs string array for the key
    //Used in Home
public static String[] jsontostring(JSONArray json_arr, String key){
    String[] stringsArray = new String[json_arr.length()-1]; // because first item is user name
    Log.d("LENGGT", Integer.toString(json_arr.length()));

    JSONObject jo;; // = new JSONObject();

    //start from 1 because 1st item is user name
    for (int i = 1; i < json_arr.length(); i++) {

        try{
             jo = json_arr.getJSONObject(i);
            Log.d("JO_ITEMS", jo.getString(key));

            if (jo.has(key))// check if there is key
            {
                stringsArray[i-1] = jo.getString(key);// i-1 because we start from 1
            }
        }
        catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + key + "\"");
        }

    }
    return stringsArray;
    }// end jsontostring


    public static String strarr_str(String[] str_arr){
        String temp = "";
        for (String s : str_arr) {
            //Log.d("ARRY ITEMS", s);
            temp += s;
        }

        return temp;
    }

    // USED in loginactivity
    public static JSONArray RemoveJSONArray( JSONArray jarray,int pos) {

        JSONArray Njarray=new JSONArray();
        try{
            for(int i=0;i<jarray.length();i++){
                if(i!=pos)
                    Njarray.put(jarray.get(i));
            }
        }catch (Exception e){e.printStackTrace();}
        return Njarray;

    }

}
