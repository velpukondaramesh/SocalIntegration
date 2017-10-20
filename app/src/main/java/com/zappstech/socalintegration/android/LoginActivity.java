package com.zappstech.socalintegration.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.zappstech.socalintegration.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
                break;
            case R.id.btn_signup:
                break;
            case R.id.txt_forgot:
                break;
            case R.id.fbLogin:
                break;
            case R.id.gLogin:
                break;
        }
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
