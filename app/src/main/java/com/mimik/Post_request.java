package com.mimik;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Arsalan on 2015-11-24.
 */
public class Post_request {

    public class HTTP_req extends AsyncTask<Void, Void, String> {

        private final String post_url;
        private final String post_var;

        HTTP_req(String targetURL, String urlParameters) {
            post_url = targetURL;
            post_var = urlParameters;
        }

        @Override
        protected String doInBackground(Void... params) {
            return excutePost(post_url, post_var);
        }

        @Override
        protected void onPostExecute(String result) {
            //Global_var.http_result = result;

        }


    }




    public static String excutePost(String targetURL, String urlParameters)
    {

        //TODO remove it 2
        Log.d("LOGTAG", "FROMPOSTREQ;"+targetURL);

        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }


}
