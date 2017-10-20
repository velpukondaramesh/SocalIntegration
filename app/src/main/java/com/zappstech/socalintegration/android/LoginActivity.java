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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
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

import org.json.JSONObject;

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
    //facebook implementation
    private CallbackManager callbackManager;
    FacebookCallback<LoginResult> callback;

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

        //facebook
        callbackManager = CallbackManager.Factory.create();
    }

    @OnClick({R.id.btn_signin, R.id.btn_signup, R.id.txt_forgot, R.id.fbLogin, R.id.gLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_signin:
                if (Utils.isNetworkAvailable(this)) {
                    verifyingLoginDetailsFromServer();
                } else {
                    Utils.showToast(this, getResources().getString(R.string.interneterror));
                }
                break;
            case R.id.btn_signup:
                if (Utils.isNetworkAvailable(this)) {
                    Intent in_registration = new Intent(LoginActivity.this, RegistrationActivity.class);
                    startActivity(in_registration);
                } else {
                    Utils.showToast(this, getResources().getString(R.string.interneterror));
                }
                break;
            case R.id.txt_forgot:
                if (Utils.isNetworkAvailable(this)) {
                    Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
                } else {
                    Utils.showToast(this, getResources().getString(R.string.interneterror));
                }
                break;
            case R.id.fbLogin:
                if (Utils.isNetworkAvailable(this)) {
                    facebookLoginImplemented();
                    fbLogin.setReadPermissions("user_friends", "email", "public_profile");
                    fbLogin.registerCallback(callbackManager, callback);
                } else {
                    Utils.showToast(this, getResources().getString(R.string.interneterror));
                }
                break;
            case R.id.gLogin:
                if (Utils.isNetworkAvailable(this)) {
                    signIn();
                } else {
                    Utils.showToast(this, getResources().getString(R.string.interneterror));
                }
                break;
        }
    }

    //facebook implementation
    private void facebookLoginImplemented() {
        callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                try {
                                    // Application code
                                    String email = object.getString("email");
                                    String name = object.getString("name");
                                    String id = object.getString("id");
                                    String image_url = "http://graph.facebook.com/" + id + "/picture?type=large";

                                    Intent main = new Intent(LoginActivity.this, Home_Activity.class);
                                    main.putExtra("name", name);
                                    main.putExtra("email", email);
                                    main.putExtra("imageUrl", image_url);
                                    startActivity(main);

                                } catch (Exception e) {

                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        };
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
    }
}
