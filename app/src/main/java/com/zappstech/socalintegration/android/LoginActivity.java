package com.zappstech.socalintegration.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.zappstech.socalintegration.social.FbIntegrationActivity;
import com.zappstech.socalintegration.R;
import com.zappstech.socalintegration.api.ApiService;
import com.zappstech.socalintegration.api.RetroClient;
import com.zappstech.socalintegration.model.LoginResponse;
import com.zappstech.socalintegration.model.User;
import com.zappstech.socalintegration.social.GIntegrationActivity;
import com.zappstech.socalintegration.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ram on 10/20/2017.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    @Bind(R.id.input_email)
    EditText inputEmail;
    @Bind(R.id.input_layout_email)
    TextInputLayout inputLayoutEmail;
    @Bind(R.id.input_password)
    EditText inputPassword;
    @Bind(R.id.input_layout_password)
    TextInputLayout inputLayoutPassword;
    @Bind(R.id.btn_signin)
    Button btnSignin;
    @Bind(R.id.btn_signup)
    Button btnSignup;
    @Bind(R.id.txt_forgot)
    TextView txtForgot;
    @Bind(R.id.fbLogin)
    LoginButton fbLogin;
    @Bind(R.id.gLogin)
    SignInButton gLogin;

    private ProgressDialog pDialog;
    //google implementation
    private static final int SIGN_IN_REQUEST = 100;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @OnClick({R.id.btn_signin, R.id.btn_signup, R.id.txt_forgot, R.id.fbLogin, R.id.gLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_signin:
                verifyingLoginDetailsFromServer();
                break;
            case R.id.btn_signup:
                Intent in_registration = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(in_registration);
                break;
            case R.id.txt_forgot:
                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fbLogin:
                Intent in_fb = new Intent(LoginActivity.this, FbIntegrationActivity.class);
                startActivity(in_fb);
                break;
            case R.id.gLogin:
                /*Intent in_google = new Intent(LoginActivity.this, GIntegrationActivity.class);
                startActivity(in_google);*/
                if (Utils.isNetworkAvailable(this)) {
                    signIn();
                } else {
                    Utils.showToast(this, getResources().getString(R.string.interneterror));
                }
                break;
        }
    }

    private void verifyingLoginDetailsFromServer() {
        pDialog.setTitle("Please wait...");
        showDialog();

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        ApiService api = RetroClient.getApiService();
        Call<LoginResponse> call = api.Login(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                hideDialog();
                if (response.isSuccessful()) {
                    LoginResponse obj_loginResponse = response.body();
                    if (obj_loginResponse.getError()) {
                        User obj_user = obj_loginResponse.getUser();
                        Toast.makeText(getApplicationContext(), "" + obj_user.getName(), Toast.LENGTH_LONG).show();
                        //startIntentFromLogin(obj_user);
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalide User", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Something Wrong", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.i("Hello", "" + t);
                hideDialog();
                Toast.makeText(getApplicationContext(), "Throwable" + t, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void startIntentFromLogin(User obj) {
        Intent in = new Intent(LoginActivity.this, Home_Activity.class);
        in.putExtra("name", obj.getName());
        in.putExtra("email", obj.getEmail());
        in.putExtra("imageUrl", obj.getDateBirth());
        startActivity(in);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    //Google Sign in implemantation
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

                Intent main = new Intent(LoginActivity.this, Home_Activity.class);
                main.putExtra("name", name);
                main.putExtra("email", email);
                main.putExtra("imageUrl", strProfilePicture);
                startActivity(main);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Utils.showToast(this, "Login failed.");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
}
