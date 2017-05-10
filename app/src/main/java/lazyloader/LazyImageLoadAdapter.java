package lazyloader;

import android.app.Activity;
import android.text.Layout;
import android.util.Log;
import android.widget.BaseAdapter;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.mimik.Challenge;
import com.mimik.Display_image;
import com.mimik.Fanfeed;
import com.mimik.Global_var;
import com.mimik.Home;
import com.mimik.LeaderBoard;
import com.mimik.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arsalan on 2015-11-26.
 */
//Adapter class extends with BaseAdapter and implements with OnClickListener
public class LazyImageLoadAdapter extends BaseAdapter implements OnClickListener{

    private Activity activity;
    //private String[] data; // this is not used anymore
    private JSONArray js_array;

    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;



    // TODO: (reverted)added layoutType to pass the layout type ie grid or list
    public LazyImageLoadAdapter(Activity a, /*String[] d, */ JSONArray j_arr ) {
        activity = a;
        //data=d;
        js_array = j_arr;


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

        public TextView title;
        public TextView text1;
        public TextView ch_by;
        public ImageView image, org_mim, ch_mim;



    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi=convertView;
        ViewHolder holder;
        JSONObject js_obj;
        Map<String, String> jsdata_map = new HashMap<>();

        //============================
        // if activity is Challenge
        //============================

        if(activity.getClass() == Challenge.class){
            try {
                js_obj = js_array.getJSONObject(position);
                jsdata_map.put("id", js_obj.getString("id"));
                jsdata_map.put("ch_mimik_id", js_obj.getString("ch_mimik_id"));
                jsdata_map.put("by_who", js_obj.getString("by_who"));
                jsdata_map.put("username", js_obj.getString("username"));

            }catch (Throwable t){ Log.e("My App", "Could not parse malformed JSON: \"" + position + "\"");}

            if(convertView==null){

                /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
                vi = inflater.inflate(R.layout.challenge_tile, null); // we are using challenge tile

                /****** View Holder Object to contain tabitem.xml file elements ******/

                holder = new ViewHolder();

                // these are coming from challenge_tile layout
                holder.org_mim = (ImageView)vi.findViewById(R.id.org_mimik);
                holder.ch_mim = (ImageView)vi.findViewById(R.id.ch_mimik);
                holder.ch_by = (TextView)vi.findViewById(R.id.challenged_by);


                /*
                holder.title = (TextView) vi.findViewById(R.id.title);
                holder.text1=(TextView)vi.findViewById(R.id.text1);
                holder.image=(ImageView)vi.findViewById(R.id.image);
*/
                /************  Set holder with LayoutInflater ************/
                vi.setTag( holder );
            }
            else
                holder=(ViewHolder)vi.getTag();

            holder.ch_by.setText(jsdata_map.get("username"));
            //holder.title.setText("uploaded by: "+jsdata_map.get("username") +"("+jsdata_map.get("likes")+")");
            ImageView image = holder.org_mim;

            //DisplayImage function from ImageLoader Class. this populates the list view
            imageLoader.DisplayImage(Global_var.URL_org_img+jsdata_map.get("ch_mimik_id") +".jpg", image);



        } // end if activity.getClass() == Challenge.class






        //====================================
        // if activity is leaderboard/Fanfeed
        //====================================
        else if(activity.getClass() == LeaderBoard.class
                || activity.getClass() == Fanfeed.class ){ // (IF LeaderBoard or Fanfeed)

            try {
                js_obj = js_array.getJSONObject(position);
                jsdata_map.put("id", js_obj.getString("id"));
                jsdata_map.put("likes", js_obj.getString("likes"));
                jsdata_map.put("dislikes", js_obj.getString("dislikes"));
                jsdata_map.put("original_id", js_obj.getString("original_id"));
                jsdata_map.put("upload_user", js_obj.getString("upload_user"));
                jsdata_map.put("username", js_obj.getString("username"));

                jsdata_map.put("friend", js_obj.getString("friend"));
                jsdata_map.put("title", js_obj.getString("title"));
                jsdata_map.put("upload_date", js_obj.getString("upload_date"));

            }catch (Throwable t){ Log.e("My App", "Could not parse malformed JSON: \"" + position + "\"");}

            if(convertView==null){

                /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
                vi = inflater.inflate(R.layout.listview_row, null); // we are using listview_row items

                /****** View Holder Object to contain tabitem.xml file elements ******/

                holder = new
                        ViewHolder();

                // these are coming from listview_row layout
                holder.title = (TextView) vi.findViewById(R.id.title);
                holder.text1=(TextView)vi.findViewById(R.id.text1);
                holder.image=(ImageView)vi.findViewById(R.id.image);

                /************  Set holder with LayoutInflater ************/
                vi.setTag( holder );
            }
            else
                holder=(ViewHolder)vi.getTag();

            holder.title.setText(jsdata_map.get("original_id") +"-"+jsdata_map.get("upload_user")
            + "-"+jsdata_map.get("id") +".jpg");
            //holder.title.setText("uploaded by: "+jsdata_map.get("username") +"("+jsdata_map.get("likes")+")");
            ImageView image = holder.image;

            //DisplayImage function from ImageLoader Class. this populates the list view
            imageLoader.DisplayImage(Global_var.URL_mimik_img+jsdata_map.get("original_id") +"-"+jsdata_map.get("upload_user")
                    + "-"+jsdata_map.get("id") +".jpg", image);

        } // (IF LeaderBoard)
        //
        else { //(not LeaderBoard) this is if activity is not LeaderBoard

            try {
                js_obj = js_array.getJSONObject(position);
                jsdata_map.put("id", js_obj.getString("id"));
                jsdata_map.put("title", js_obj.getString("title"));
                jsdata_map.put("mimik_posted", js_obj.getString("mimik_posted"));

            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + position + "\"");
            }

            if (convertView == null) {

                /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
                vi = inflater.inflate(R.layout.listview_row, null);

                /****** View Holder Object to contain tabitem.xml file elements ******/

                holder = new
                        ViewHolder();
                holder.title = (TextView) vi.findViewById(R.id.title);
                holder.text1 = (TextView) vi.findViewById(R.id.text1);
                holder.image = (ImageView) vi.findViewById(R.id.image);

                /************  Set holder with LayoutInflater ************/
                vi.setTag(holder);
            } else
                holder = (ViewHolder) vi.getTag();


            //holder.title.setText("Image_id "+ jsdata_map.get("id"));
            holder.title.setText("title " + jsdata_map.get("title") + "(" + jsdata_map.get("mimik_posted") + ")");
            ImageView image = holder.image;

            //DisplayImage function from ImageLoader Class. this populates the list view
            imageLoader.DisplayImage(Global_var.URL_org_img + jsdata_map.get("id") + ".jpg", image);

        }// end of else (not LeaderBoard)


        /******** Set Item Click Listner for LayoutInflater for each row ***********/
        vi.setOnClickListener(new OnItemClickListener(position));
        return vi;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements OnClickListener{
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            if(activity.getClass() == Home.class)
            {Home sct = (Home)activity;
            sct.onItemClick(mPosition);}

            else if(activity.getClass() == LeaderBoard.class)
            {LeaderBoard sct = (LeaderBoard)activity;
                sct.onItemClick(mPosition);}
        }
    }
}
