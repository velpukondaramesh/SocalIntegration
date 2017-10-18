package com.zappstech.socalintegration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

public class GIntegrationActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int SIGN_IN_REQUEST = 100;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton btnSignIn;
    private Button btnGooglePlusCustom, btnSignOut;
    private TextView tvName, tvEmail;
    private ImageView imgProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        initUI();
    }

    public void initUI() {
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(this);
        btnGooglePlusCustom = (Button) findViewById(R.id.btnGooglePlusCustom);
        btnGooglePlusCustom.setOnClickListener(this);
        //
        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        imgProfilePicture = (ImageView) findViewById(R.id.imgProfilePicture);
        btnSignOut = (Button) findViewById(R.id.btnSignOut);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // Customizing G+ button
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());
    }

    //////---------------------- GOOGLE INTEGRATION ---------------------///////

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, SIGN_IN_REQUEST);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            try {
                GoogleSignInAccount acct = result.getSignInAccount();
                String name = acct.getDisplayName();
                String email = acct.getEmail();
                Uri uri = acct.getPhotoUrl();
                String strProfilePicture = "";
                if (uri != null) {
                    strProfilePicture = uri.toString();
                }
                Utils.showToast(this, "Login Successfully.");
                //Name
                if (name != null) {
                    tvName.setVisibility(View.VISIBLE);
                    tvName.setText("Name : " + name);
                }
                //Email
                if (email != null) {
                    tvEmail.setVisibility(View.VISIBLE);
                    tvEmail.setText("Email : " + email);
                }
                //Profile Picture
                if (strProfilePicture != null) {
                    imgProfilePicture.setVisibility(View.VISIBLE);
                    Picasso.with(this).load(strProfilePicture).into(imgProfilePicture);
                }
                //Sign Out
                btnSignOut.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Utils.showToast(this, "Login failed.");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Utils.showToast(this, "Login failed.");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnGooglePlusCustom) {
            if (Utils.isNetworkAvailable(this)) {
                signIn();
            } else {
                Utils.showToast(this, getResources().getString(R.string.interneterror));
            }
        }
    }

    public void onClickSignOut(View view) {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        tvName.setVisibility(View.GONE);
                        tvEmail.setVisibility(View.GONE);
                        imgProfilePicture.setVisibility(View.GONE);
                        btnSignOut.setVisibility(View.GONE);
                        Utils.showToast(GIntegrationActivity.this, "SignOut Successfully");
                    }
                });
    }
}
