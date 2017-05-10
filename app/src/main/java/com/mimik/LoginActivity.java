package com.mimik;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arsalan on 2015-11-24.
 */
public class LoginActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    //private AutoCompleteTextView mEmailView1;
    private AutoCompleteTextView mUsername;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox mRememberMe;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Set up the login form.
        mUsername = (AutoCompleteTextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        // userInfo can be replaced with R.String
        sharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);



        if ( sharedPreferences.contains("username") && sharedPreferences.getString("username", null) != null) {
            mUsername.setText(sharedPreferences.getString("username", "username"));
        }

        // setting username to sign up username
        Bundle extras = getIntent().getExtras();
        String userName;
        if (extras != null) {
            userName = extras.getString("username");
            mUsername.setText(userName);
        }

        populateAutoComplete();


        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // IMPORTANT LINE
                attemptLogin();
                // login();

            }
        });

        mRememberMe = (CheckBox)findViewById(R.id.remember_me);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.

        mUsername.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.

        String username = mUsername.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsername.setError(getString(R.string.error_field_required));
            focusView = mUsername;
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
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUsername.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }


        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            //cant use the toast
            //Toast.makeText(LoginActivity.this, "test",  Toast.LENGTH_LONG).show();

            try {
                String username = mEmail; // TODO: fix this
                String password = mPassword;
                /*
                String link = "https://csclub.uwaterloo.ca/~a42ahmed/tungle/php/server.php?username="
                        +username+"&password="+password;
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader
                        (new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line="";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
                */

                //String link=MainActivity.website+"/tungle/php/server.php";
                String link = MainActivity.website+"/mimik/index.php";

                String data  = URLEncoder.encode("username", "UTF-8")
                        + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(password, "UTF-8");
                //TODO: what to do with POST['command']
                data += "&" + URLEncoder.encode("command", "UTF-8")
                        + "=" + URLEncoder.encode("login", "UTF-8");

                //TODO remove it
                Log.d("LOGTAG", "start;"+data);

                return Post_request.excutePost(link,data);

            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());


            }


        }

        @Override
        protected void onPostExecute(final String success) {
            // echo the following in PHP server
            //if Failed: JSON error
            //if Passed: JSON array for the home activity

            mAuthTask = null;
            showProgress(false);
            String login_result = "failed";
            String login_result_id = "";

            //TODO: remove it
            // testing starts here
            //Log.d("LOGTAG", "PASSSED;" + 132132);

            Log.d("B4 JSON", "user");
            //TODO: testing JSON/ clean up required
            try {
                //JSONArray json_arr = new JSONArray(success);// create JSON array from string
                JSONObject json_ob = new JSONObject(success); // create JSON obj from string
                //JSONObject js_ob_first = json_arr.getJSONObject(0);    // getting first item of JS_arr and making it JS object
                //Log.d("My App", obj.toString());

                // setting http_result
                //Global_var.http_result_jsarray = Global_var.RemoveJSONArray(json_arr,0);
                //Global_var.http_result = Global_var.jsontostring(json_arr, "id");

                Log.d("getting_string", json_ob.getString("user"));

                //if login is successful
                if(json_ob.has("user_id") && json_ob.has("user")){
                    login_result = json_ob.getString("user");
                    login_result_id = json_ob.getString("user_id");
                }//end if
                else {login_result = json_ob.getString("error");}


            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + success + "\"");
            }



            // TODO fix it after
            // TODO: check for user id but first read the json(see above)
            if (!login_result.equals("failed")) {
                //Log.d("after_log", login_result);


                TextView status = (TextView) findViewById(R.id.textView6);
                TextView role = (TextView) findViewById(R.id.textView7);

                // can be deleted
                //status.setText("Login Successful"+success+ "asd");
                //role.setText(success);
                // here

                // assign username to global var
                Global_var.username = login_result;
                Global_var.user_id = login_result_id;

                finish();
                // after login is successful, goto home.class

                // for remembering the user login
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.putString("username", mEmail);
                editor.putString("user_id", login_result_id);

                if(mRememberMe.isChecked()){
                    editor.putString("password", mPassword);
                    editor.putBoolean("AUTO_ISCHECK", true);
                }

                editor.commit();


                // finish loginactivity
                finish();


                Intent newInt = new Intent();
                newInt.setClass(LoginActivity.this, Mainmenu.class);
                //newInt.putExtra("userinfo", sharedPreferences.getString("userInfo", null));
                //newInt.putExtra("USERNAME_ID", success);
                newInt.putExtra("img_list",Global_var.URL_org_img);
                startActivity(newInt);


            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                // TODO: revert these DONE
                //mPasswordView.setError("Login failed. Try again!");
                mPasswordView.setError(success);

                //Toast.makeText(getApplicationContext(), "Login failed. Try again!", Toast.LENGTH_SHORT).show();
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    // delete this
    public void login() {
        String username = mUsername.getText().toString();
        String password = mPasswordView.getText().toString();
        TextView status = (TextView) findViewById(R.id.textView6);
        TextView role = (TextView) findViewById(R.id.textView7);

        new SigninActivity(this, status, role, 0).execute(username, password);

    }


}
