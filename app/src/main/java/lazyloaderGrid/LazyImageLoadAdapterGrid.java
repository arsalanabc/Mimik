package lazyloaderGrid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mimik.Display_image;
import com.mimik.Global_var;
import com.mimik.Home;
import com.mimik.R;
import com.mimik.Show_mimik;
import com.mimik.User_profile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import lazyloader.ImageLoader;

/**
 * Created by Arsalan on 2015-11-28.
 */
public class LazyImageLoadAdapterGrid  extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private String[] data;
    private JSONArray js_array;

    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    public LazyImageLoadAdapterGrid(Activity a, String[] d, JSONArray js_arr) {
        activity = a;
        data=d;
        js_array = js_arr;
        inflater = (LayoutInflater)activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Create ImageLoader object to download and show image in list
        // Call ImageLoader constructor to initialize FileCache
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return js_array.length();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{


        public ImageView image;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi=convertView;
        ViewHolder holder;

        JSONObject js_obj = null;
        Map<String, String> jsdata_map = new HashMap<>();
        String img_path = ""; // this is only need to show the image in the gridview

        try {
            js_obj = js_array.getJSONObject(position);
            jsdata_map.put("id", js_obj.getString("id"));
            jsdata_map.put("title", js_obj.getString("title"));
            jsdata_map.put("original_id", js_obj.getString("original_id"));
            jsdata_map.put("user_id", js_obj.getString("upload_user"));

            img_path = Global_var.URL_mimik_img+"thumb-"+jsdata_map.get("original_id") +"-"+jsdata_map.get("user_id")
                    + "-"+jsdata_map.get("id") +".jpg";
            Log.d("mimik path", img_path );
        }catch (Throwable t){ Log.e("My App", "Could not parse malformed JSON: \"" + position + "\"");}


        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.imagedisplay_grid, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            //holder.text = (TextView) vi.findViewById(R.id.grid_item_label);
            //holder.text1=(TextView)vi.findViewById(R.id.grid_item_label);
            holder.image=(ImageView)vi.findViewById(R.id.grid_item_image);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();


        // holder.text.setText("Company "+position);
        // holder.text1.setText("company description "+position);
        ImageView image = holder.image;

        //DisplayImage function from ImageLoader Class
        imageLoader.DisplayImage( img_path , image);

        // get the image path for the original image not the thumb
        // String img_path_orig = Global_var.URL_mimik_img+jsdata_map.get("original_id") +"-"+jsdata_map.get("user_id")
        //       + "-"+jsdata_map.get("id") +".jpg";

        /******** Set Item Click Listner for LayoutInflater for each row ***********/
        vi.setOnClickListener(new OnItemClickListener(js_obj));
        return vi;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

    }


    /********* Called when Item click in GridView ************/
    private class OnItemClickListener  implements View.OnClickListener {
        private JSONObject js_ob_mimik;

        OnItemClickListener(JSONObject js_o){
            js_ob_mimik = js_o;
        }

        @Override
        public void onClick(View arg0) {

            if(activity.getClass() == User_profile.class)
            {
                Show_mimik sct = (Show_mimik)activity;
                //;lk;lk.getIntent().putExtra("js_obj", js_ob_mimik.toString());
                //startActivity(show_mimik);

            }
            else{
            Display_image sct = (Display_image)activity;
            sct.onItemClick(js_ob_mimik);}
        }
    }
}

