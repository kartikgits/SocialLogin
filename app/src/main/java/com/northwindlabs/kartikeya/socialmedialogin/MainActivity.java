package com.northwindlabs.kartikeya.socialmedialogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView linkedInLoginImageButton, twitterLoginImageButton, imgProfile;
    //private com.facebook.login.widget.LoginButton facebookLoginButton;
    private com.google.android.gms.common.SignInButton googleLoginButton;
    private TextView txtDetails;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeButtons();
        setVisibilities();
    }

    private void initializeButtons(){
        linkedInLoginImageButton = findViewById(R.id.linkedin_login);
        //facebookLoginButton = findViewById(R.id.facebook_login);
        googleLoginButton = findViewById(R.id.google_login);
        twitterLoginImageButton = findViewById(R.id.twitter_login);
        btnLogout = findViewById(R.id.btnLogout);
        imgProfile = findViewById(R.id.imgProfile);
        txtDetails = findViewById(R.id.txtDetails);
    }

    private void setVisibilities(){
        //Default
        linkedInLoginImageButton.setVisibility(View.VISIBLE);
        //facebookLoginButton.setVisibility(View.VISIBLE);
        googleLoginButton.setVisibility(View.VISIBLE);
        twitterLoginImageButton.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
        imgProfile.setVisibility(View.GONE);
        txtDetails.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.linkedin_login){
            Intent intent = new Intent(this, LinkedInActivity.class);
            startActivity(intent);
        } //else if (view.getId() == R.id.facebook_login){
            //Intent intent = new Intent(this, FacebookActivity.class);
            //startActivity(intent);
        /*}*/ else if (view.getId() == R.id.google_login){
            Intent intent = new Intent(this, GoogleActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.twitter_login){
            Intent intent = new Intent(this, TwitterActivity.class);
            startActivity(intent);
        }
    }

}