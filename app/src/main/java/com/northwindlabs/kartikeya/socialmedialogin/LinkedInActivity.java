package com.northwindlabs.kartikeya.socialmedialogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


public class LinkedInActivity extends LoadingActivity implements View.OnClickListener{

    private ImageView mImageView, linkedin_login;
    private TextView mTextViewProfile;
    private final String LOG_TAG = "LinkedInActivity";

    private final String LINKED_IN_URL = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address,picture-urls::(original))";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkedin);
        intitializeControls();
        handleLogin();
    }

    private void intitializeControls(){
        findViewById(R.id.button_linkedin_signout).setOnClickListener(this);
        linkedin_login = findViewById(R.id.linkedin_login);
        mImageView = findViewById(R.id.logo);
        mTextViewProfile = findViewById(R.id.profile);
    }

    private void handleLogout(){
        LISessionManager.getInstance(getApplicationContext()).clearSession();
        linkedin_login.setVisibility(View.VISIBLE);
        mImageView.getLayoutParams().width = (getResources().getDisplayMetrics().widthPixels / 100) * 64;
        mImageView.setImageResource(R.mipmap.ic_launcher);
        mTextViewProfile.setText(null);

        findViewById(R.id.button_linkedin_signout).setVisibility(View.GONE);
    }

    private void handleLogin(){
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                //Authentication was successful. Other calls with the SDK
                linkedin_login.setVisibility(View.GONE);
                findViewById(R.id.button_facebook_signout).setVisibility(View.VISIBLE);
                fetchPersonalInfo();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors
                Log.e(LOG_TAG,error.toString());
            }
        }, true);
    }

    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Add this line to existing onActivityResult() method
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }

    private void fetchPersonalInfo(){
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, LINKED_IN_URL, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                // Success!
                try {
                    JSONObject jsonObject = apiResponse.getResponseDataAsJson();
                    String firstName = jsonObject.getString("firstName");
                    String lastName = jsonObject.getString("lastName");
                    String pictureUrl = jsonObject.getString("pictureUrl");
                    String emailAddress = jsonObject.getString("emailAddress");

                    Picasso.with(getApplicationContext()).load(pictureUrl).into(mImageView);

                    StringBuilder sb = new StringBuilder();
                    sb.append("DisplayName: "+firstName+ " " +lastName);
                    sb.append("\n\n");
                    sb.append("Email: "+emailAddress);
                    mTextViewProfile.setText(sb);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                // Error making GET request!
                Log.e(LOG_TAG,liApiError.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_facebook_signout:
                handleLogout();
                break;
        }
    }
}