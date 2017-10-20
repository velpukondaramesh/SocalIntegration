package com.zappstech.socalintegration.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.common.SignInButton;
import com.zappstech.socalintegration.social.FbIntegrationActivity;
import com.zappstech.socalintegration.R;
import com.zappstech.socalintegration.api.ApiService;
import com.zappstech.socalintegration.api.RetroClient;
import com.zappstech.socalintegration.model.LoginResponse;
import com.zappstech.socalintegration.model.User;
import com.zappstech.socalintegration.social.GIntegrationActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ram on 10/20/2017.
 */

public class LoginActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
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
                Intent in_google = new Intent(LoginActivity.this, GIntegrationActivity.class);
                startActivity(in_google);
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
}
