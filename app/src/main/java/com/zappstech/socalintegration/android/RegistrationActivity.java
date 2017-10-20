package com.zappstech.socalintegration.android;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.zappstech.socalintegration.R;
import com.zappstech.socalintegration.api.ApiService;
import com.zappstech.socalintegration.api.RetroClient;
import com.zappstech.socalintegration.model.RegistrationResponse;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ram on 10/20/2017.
 */

public class RegistrationActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.input_firstname)
    EditText inputFirstname;
    @Bind(R.id.input_layout_firstname)
    TextInputLayout inputLayoutFirstname;
    @Bind(R.id.input_email)
    EditText inputEmail;
    @Bind(R.id.input_layout_email)
    TextInputLayout inputLayoutEmail;
    @Bind(R.id.input_password)
    EditText inputPassword;
    @Bind(R.id.input_layout_password)
    TextInputLayout inputLayoutPassword;
    @Bind(R.id.input_contact)
    EditText inputContact;
    @Bind(R.id.input_layout_contact)
    TextInputLayout inputLayoutContact;
    @Bind(R.id.radioMale)
    RadioButton radioMale;
    @Bind(R.id.radioFemale)
    RadioButton radioFemale;
    @Bind(R.id.radioSex)
    RadioGroup radioSex;
    @Bind(R.id.input_date_birth)
    EditText inputDateBirth;
    @Bind(R.id.sp_country)
    Spinner spCountry;
    @Bind(R.id.btn_signup)
    Button btnSignup;

    private String[] list = new String[]{"Clinic", "Hospital", "Lab", "Other"};
    private String selected_spinner;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        toolbar.setTitle("Registration");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Calendar newCalendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                inputDateBirth.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        ArrayAdapter<CharSequence> typeAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, list);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCountry.setAdapter(typeAdapter);

        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_spinner = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    @OnClick({R.id.input_date_birth, R.id.btn_signup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.input_date_birth:
                fromDatePickerDialog.show();
                break;
            case R.id.btn_signup:
                sendDataToServer();
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

    private void sendDataToServer() {
        pDialog.setMessage("Registering ...");
        showDialog();

        ApiService api = RetroClient.getApiService();
        String name = inputFirstname.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String contact = inputContact.getText().toString();
        int selectedId = radioSex.getCheckedRadioButtonId();
        radioMale = (RadioButton) findViewById(selectedId);
        String gender = radioMale.getText().toString();
        String date_birth = inputDateBirth.getText().toString();

        Call<RegistrationResponse> call = api.insertNewUser(name, email, password, contact, gender, date_birth, selected_spinner);

        call.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                hideDialog();
                if (response.isSuccessful()) {
                    RegistrationResponse obj_responseModel = new RegistrationResponse();
                    obj_responseModel = response.body();
                    if (obj_responseModel.getError()) {
                        Toast.makeText(getApplicationContext(), obj_responseModel.getData(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), obj_responseModel.getData(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Something Wrong", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                Log.i("Hello", "" + t);
                hideDialog();
                Toast.makeText(getApplicationContext(), "Throwable" + t, Toast.LENGTH_LONG).show();
            }
        });
    }
}
