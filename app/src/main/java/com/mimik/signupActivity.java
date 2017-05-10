package com.mimik;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Arsalan on 7/14/2014.
 */
public class signupActivity extends Activity implements LoaderCallbacks<Cursor> {

    /**
     Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserSignupTask mAuthTask = null;

    // UI references from signup_activity layout
    private EditText mUsername;
    private EditText mPass;
    private EditText mconPass;
    private EditText mEmail;
    private View mProgressView;
    private View mLoginFormView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.signup_layout);

        mProgressView = findViewById(R.id.signup_progress);

        mUsername = (EditText) findViewById(R.id.signup_username);
        mPass = (EditText) findViewById(R.id.signup_pass);
        mconPass = (EditText) findViewById(R.id.signup_con_pass);
        mEmail = (EditText) findViewById(R.id.signup_email);
        mLoginFormView = findViewById(R.id.signup_form);





        //clicking the signup botton
        Button signupButton = (Button) findViewById(R.id.signup_page);
        signupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // IMPORTANT LINE
                attemptLogin();
                // login();

            }
        });



    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.

        mUsername.setError(null);
        mPass.setError(null);
        mconPass.setError(null);
        mEmail.setError(null);

        // Store values at the time of the login attempt.

        String username = mUsername.getText().toString();
        String password = mPass.getText().toString();
        String con_password = mconPass.getText().toString();
        String email = mEmail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // check for username
        if (TextUtils.isEmpty(username)) {
            mUsername.setError("Please enter username");
            focusView = mUsername;
            cancel = true;
        }


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPass.setError(getString(R.string.error_invalid_password));
            focusView = mPass;
            cancel = true;
        }
        if (TextUtils.isEmpty(con_password)) {
            mconPass.setError(getString(R.string.error_invalid_password));
            focusView = mconPass;
            cancel = true;
        }

        if (!isPasswordValid(con_password, password)) {
            mconPass.setError("Passwords don't match");
            focusView = mconPass;
            cancel = true;
        }

        // Check for a valid email entries.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }




        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserSignupTask(email, username, password);
            mAuthTask.execute((Void) null);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserSignupTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private final String mUsername_local;

        UserSignupTask(String email,String username,String password) {
            mEmail = email;
            mPassword = password;
            mUsername_local = username;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                String username = mUsername_local;
                String email = mEmail;
                String password = mPassword;

                // POST all the data
                String link = MainActivity.website+"/mimik/index.php";

                String data  = URLEncoder.encode("username", "UTF-8")
                        + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(password, "UTF-8");
                data += "&" + URLEncoder.encode("email", "UTF-8")
                        + "=" + URLEncoder.encode(email, "UTF-8");
                data += "&" + URLEncoder.encode("command", "UTF-8")
                        + "=" + URLEncoder.encode("register", "UTF-8");

                return Post_request.excutePost(link,data);


/* this is another way to talk to the server. It works fine
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter
                        (conn.getOutputStream());
                wr.write( data );
                wr.flush();
                BufferedReader reader = new BufferedReader
                        (new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while((line = reader.readLine()) != null)
                {

                    sb.append(line);
                    break;
                }

                return sb.toString();
                //return ServerInterface.login();
*/


            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }


        }

        @Override
        protected void onPostExecute(final String success) {
            // echo the following in PHP server
            //if Failed: failed
            //if Passed: user ID

            //TODO: remove it
            Log.d("LOGTAG", "PASSSED;" + success);

            mAuthTask = null;
            showProgress(false);
            String signin_result = "failed";



            //TODO: testing JSON/ clean up required
            boolean signed_in = false;
            try {
                JSONObject json_raw = new JSONObject(success); // create JSON obj from string
                //JSONObject js_array = json_raw.getJSONObject("user");    // this will return correct
                Log.d("My App", json_raw.toString());
                //Log.d("getting_string", json_raw.getString("user"));

                //if sign is successful

                //if(!json_raw.getString("user").isEmpty()){
                if(json_raw.has("user")){
                    //Log.d("My App2", "what?");
                    signin_result = json_raw.getString("user");
                    signed_in = true;
                }//end if
                else {signin_result = json_raw.getString("error");}


            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + success + "\"");
            }

            if (signed_in) {

                finish();


                Intent newInt = new Intent();
                newInt.setClass(signupActivity.this, LoginActivity.class);
                //newInt.putExtra("userinfo", sharedPreferences.getString("userInfo", null));
                newInt.putExtra("username", signin_result);
                startActivity(newInt);


            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPass.setError("Login failed. Try again!");

                mUsername.setError(signin_result);
                //Toast.makeText(getApplicationContext(), "Login failed. Try again!", Toast.LENGTH_SHORT).show();
                mUsername.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private boolean isPasswordValid(String password, String con_password) {
        return password.equals(con_password);
    }



}

