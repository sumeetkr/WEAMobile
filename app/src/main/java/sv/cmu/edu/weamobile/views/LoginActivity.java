package sv.cmu.edu.weamobile.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sv.cmu.edu.weamobile.R;
import sv.cmu.edu.weamobile.data.GeoLocation;
import sv.cmu.edu.weamobile.utility.Constants;
import sv.cmu.edu.weamobile.utility.GPSTracker;
import sv.cmu.edu.weamobile.utility.Logger;
import sv.cmu.edu.weamobile.utility.WEAHttpClient;
import sv.cmu.edu.weamobile.utility.WEASharedPreferences;
import sv.cmu.edu.weamobile.utility.WEAUtil;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private AutoCompleteTextView activationCodeView;
    private EditText mOrganizationView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        activationCodeView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mOrganizationView = (EditText) findViewById(R.id.password);
        mOrganizationView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(registrationReceiver,
                new IntentFilter("new-register-event"));

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(registrationReceiver!= null){
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(registrationReceiver);
        }

    }

    private void populateAutoComplete() {
        //getLoaderManager().initLoader(0, null, this);


        String user_name = WEASharedPreferences.getStringProperty(getApplicationContext(), Constants.USER_NAME);
        if(user_name != null || user_name != ""){
         activationCodeView.setText(user_name);
//         activationCodeView.setInputType(InputType.TYPE_NULL);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Reset errors.
        activationCodeView.setError(null);
        mOrganizationView.setError(null);

        // Store values at the time of the login attempt.
        String userId = activationCodeView.getText().toString();
        String password = mOrganizationView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mOrganizationView.setError(getString(R.string.error_invalid_password));
            focusView = mOrganizationView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userId)) {
            activationCodeView.setError("Invalid Code");
            focusView = activationCodeView;
            cancel = true;
        } else if (!isActivationCodeValid(userId)) {
            activationCodeView.setError(getString(R.string.error_invalid_email));
            focusView = activationCodeView;
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

            GPSTracker tracker = new GPSTracker(this);
            if(tracker.canGetLocation()){
                GeoLocation location = tracker.getNetworkGeoLocation();

                String phoneId = WEASharedPreferences.getStringProperty(this,Constants.PHONE_ID);
                String token = WEASharedPreferences.getStringProperty(this, Constants.PHONE_TOKEN);

                if(phoneId!= null && !phoneId.isEmpty() && token!= null && !token.isEmpty()) {
                    showProgress(false);
                    Toast.makeText(this, "Phone already registered.", Toast.LENGTH_SHORT).show();
                }else{
                    Logger.log("Phone is registered");
                    WEAHttpClient.registerPhoneAync(this, location);
                }
            }else{
                final Context context = this;
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Enable Location Sharing", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            tracker.stopUsingGPS();

//            mAuthTask = new ActivateUserTask(userId, this);
//            mAuthTask.execute((Void) null);
        }
    }

    private boolean isActivationCodeValid(String code) {
        //TODO: Replace this with your own logic
        boolean isValid = false;
        if(code!= null && code != "" && code.compareTo(Constants.ACTIVATION_CODE)==0){
            isValid = true;
        }
        return isValid;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;
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
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            emails.add(cursor.getString(ProfileQuery.ADDRESS));
//            cursor.moveToNext();
//        }

//        addEmailsToAutoComplete(emails);
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

        activationCodeView.setAdapter(adapter);
    }

    private BroadcastReceiver registrationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra(Constants.PHONE_ID)){
                Logger.log("Phone successfully registered");
                showProgress(false);

                //send heart beat after registration
                WEAUtil.sendHeartBeat(context);

                Intent dialogIntent = new Intent(context, MainActivity.class);
                dialogIntent.setAction("android.intent.action.MAIN");
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                WEAUtil.showMessageIfInDebugMode(context, "Registration successful, showing main view.");
                context.startActivity(dialogIntent);

                finish();
            }else{
                showProgress(false);
                activationCodeView.setError("Server error");
                activationCodeView.requestFocus();
            }
        }
    };
}



