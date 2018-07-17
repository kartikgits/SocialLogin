package com.northwindlabs.kartikeya.socialmedialogin;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgProfile,imgLogin;
    private TextView txtDetails;
    private Button btnLogout;
    private final String PACKAGE_NAME = "com.northwindlabs.kartikeya.socialmedialogin";
    private final String LOG_TAG = "MainActivity";

    private final String LINKED_IN_URL = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address,picture-urls::(original))";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        computePakageHash();
        initializeControls();
    }

    private void initializeControls(){
        imgLogin = (ImageView)findViewById(R.id.imgLogin);
        imgLogin.setOnClickListener(this);
        btnLogout = (Button)findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        imgProfile = (ImageView)findViewById(R.id.imgProfile);
        txtDetails = (TextView)findViewById(R.id.txtDetails);

        //Default
        imgLogin.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
        imgProfile.setVisibility(View.GONE);
        txtDetails.setVisibility(View.GONE);
    }

    private void computePakageHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    PACKAGE_NAME,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.e(LOG_TAG,e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgLogin:
                handleLogin();
                break;
            case R.id.btnLogout:
                handleLogout();
                break;
        }
    }

    private void handleLogout(){
        LISessionManager.getInstance(getApplicationContext()).clearSession();
        imgLogin.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
        imgProfile.setVisibility(View.GONE);
        txtDetails.setVisibility(View.GONE);
    }

    private void handleLogin(){
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                //Authentication was successful. Other calls with the SDK
                imgLogin.setVisibility(View.GONE);
                btnLogout.setVisibility(View.VISIBLE);
                imgProfile.setVisibility(View.VISIBLE);
                txtDetails.setVisibility(View.VISIBLE);
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

                    Picasso.with(getApplicationContext()).load(pictureUrl).into(imgProfile);

                    StringBuilder sb = new StringBuilder();
                    sb.append("First Name: "+firstName);
                    sb.append("\n\n");
                    sb.append("Last Name: "+lastName);
                    sb.append("\n\n");
                    sb.append("Email: "+emailAddress);
                    txtDetails.setText(sb);
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
}
