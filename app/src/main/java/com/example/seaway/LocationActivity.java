package com.example.seaway;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seaway.adapter.DropoffLocationAdapter;
import com.example.seaway.adapter.PickupLocationAdapter;
import com.example.seaway.adapter.TransporterAdapter;
import com.example.seaway.database.DatabaseHelper;
import com.example.seaway.form.CustomForm;
import com.example.seaway.lib.ApiClient;
import com.example.seaway.list.DropOffLocationList;
import com.example.seaway.list.PickupLocationList;
import com.example.seaway.list.TransporterList;
import com.example.seaway.response.CustomerResponse;
import com.example.seaway.response.DropOffLocationResponse;
import com.example.seaway.response.PickupLocationResponse;
import com.example.seaway.response.TransporterResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationActivity extends AppCompatActivity {

    private View progressLayoutView, progressView;
    private DatabaseHelper db;
    private ListView locationLVView;
    private EditText locationSearchView;
    private String locationType;
    private CustomForm customForm = new CustomForm();
    private ArrayList<PickupLocationList> mPickupLocations;
    private ArrayList<DropOffLocationList> mDropOffLocations;
    private ArrayList<TransporterList> mTransporters;
    private PickupLocationAdapter adapterPickupLocation;
    private DropoffLocationAdapter adapterDropOffLocation;
    private TransporterAdapter adapterTransporter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            locationType = bundle.getString("location_type");
        } else {
            finish();
        }
        db = new DatabaseHelper(this);
        Toolbar app_bar = (Toolbar) findViewById(R.id.app_bar);
        TextView app_bar_title = (TextView) findViewById(R.id.app_bar_title);
        if(locationType.trim().equals("pickup_location")){
            app_bar_title.setText(getResources().getString(R.string.pickup_location));
        } else if(locationType.trim().equals("drop_location")){
            app_bar_title.setText(getResources().getString(R.string.drop_off_location));
        } else if(locationType.trim().equals("transporter")){
            app_bar_title.setText(getResources().getString(R.string.transporter));
        }
        setSupportActionBar(app_bar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_forward);

        progressLayoutView = (FrameLayout) findViewById(R.id.progress_bar_layout);
        progressView = (ProgressBar) progressLayoutView.findViewById(R.id.progress_bar);
        locationLVView = (ListView) findViewById(R.id.location_list);
        locationSearchView =(EditText) findViewById(R.id.location_search);
        locationSearchView.clearFocus();

        getLocations();
    }

    public void getLocations(){
        this.customForm.result="success";
        showProgress(true);
        if(locationType.trim().equals("pickup_location")){
            Call<PickupLocationResponse> mServicePickupLocation = ApiClient.getInstance().getApi().getPickupLocations(this.customForm);
            mServicePickupLocation.enqueue(new Callback<PickupLocationResponse>() {
                @Override
                public void onResponse(Call<PickupLocationResponse> call, Response<PickupLocationResponse> response) {
                    if (response.isSuccessful()){
                        PickupLocationResponse pickupLocationResponse = response.body();
                        String result= pickupLocationResponse.result;
                        if(result.trim().equals("success")){
                            ArrayList<PickupLocationList> pickupLocations = response.body().pickupLocations;
                            mPickupLocations = db.getAllPickupLocations();
                            if((pickupLocations.size() +1) != mPickupLocations.size()){
                                db.deleteAllCustomers();
                                for (PickupLocationList location : pickupLocations) {
                                    db.insertPickupLocation(location.getPickUpLocationID(), location.getLocationName());
                                }
                            }

                            showResult();
                            showProgress(false);
                        } else {
                            showProgress(false);
                        }
                    } else {
                        showProgress(false);
                    }
                }

                @Override
                public void onFailure(Call<PickupLocationResponse> call, Throwable t) {
                    showProgress(false);
                    call.cancel();
                    Toast.makeText(getApplicationContext(), R.string.failure_error, Toast.LENGTH_LONG).show();
                }
            });
        } else if(locationType.trim().equals("drop_location")){
            Call<DropOffLocationResponse> mServiceDropOffLocation = ApiClient.getInstance().getApi().getDropoffLocations(this.customForm);
            mServiceDropOffLocation.enqueue(new Callback<DropOffLocationResponse>() {
                @Override
                public void onResponse(Call<DropOffLocationResponse> call, Response<DropOffLocationResponse> response) {
                    if (response.isSuccessful()){
                        DropOffLocationResponse dropOffLocationResponse = response.body();
                        String result= dropOffLocationResponse.result;
                        if(result.trim().equals("success")){
                            ArrayList<DropOffLocationList> dropLocations = response.body().dropOfflocations;
                            mDropOffLocations = db.getAllDropOffLocations();
                            if((dropLocations.size() +1) != mDropOffLocations.size()){
                                db.deleteAllCustomers();
                                for (DropOffLocationList location : dropLocations) {
                                    db.insertDropoffLocation(location.getDropOffLocationID(), location.getLocationName());
                                }
                            }

                            showResult();
                            showProgress(false);
                        } else {
                            showProgress(false);
                        }
                    } else {
                        showProgress(false);
                    }
                }

                @Override
                public void onFailure(Call<DropOffLocationResponse> call, Throwable t) {
                    showProgress(false);
                    call.cancel();
                    Toast.makeText(getApplicationContext(), R.string.failure_error, Toast.LENGTH_LONG).show();
                }
            });
        } else if(locationType.trim().equals("transporter")){
            Call<TransporterResponse> mServiceTransporter = ApiClient.getInstance().getApi().getTransporters(this.customForm);
            mServiceTransporter.enqueue(new Callback<TransporterResponse>() {
                @Override
                public void onResponse(Call<TransporterResponse> call, Response<TransporterResponse> response) {
                    if (response.isSuccessful()){
                        TransporterResponse transporterResponse = response.body();
                        String result= transporterResponse.result;
                        if(result.trim().equals("success")){
                            ArrayList<TransporterList> transporters = response.body().transporters;
                            mTransporters = db.getAllTransporters();
                            if((transporters.size() +1) != mTransporters.size()){
                                db.deleteAllCustomers();
                                for (TransporterList transporter : transporters) {
                                    db.insertTransporter(transporter.getTransporterID(), transporter.getCompanyName());
                                }
                            }

                                showResult();
                                showProgress(false);
                        } else {
                            showProgress(false);
                        }
                    } else{
                        showProgress(false);
                    }
                }

                @Override
                public void onFailure(Call<TransporterResponse> call, Throwable t) {
                    showProgress(false);
                    call.cancel();
                    Toast.makeText(getApplicationContext(), R.string.failure_error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void showResult(){

        if(locationType.trim().equals("pickup_location")){
            mPickupLocations = db.getAllPickupLocations();
            adapterPickupLocation = new PickupLocationAdapter(getApplicationContext(), mPickupLocations);
            locationLVView.setTextFilterEnabled(true);
            locationLVView.setAdapter(adapterPickupLocation);

            locationLVView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = getIntent();
                    intent.putExtra("pickupLocationID", (String) view.getTag());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

            locationSearchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapterPickupLocation.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else if(locationType.trim().equals("drop_location")){
            mDropOffLocations = db.getAllDropOffLocations();
            adapterDropOffLocation = new DropoffLocationAdapter(getApplicationContext(), mDropOffLocations);
            locationLVView.setTextFilterEnabled(true);
            locationLVView.setAdapter(adapterDropOffLocation);

            locationLVView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = getIntent();
                    intent.putExtra("dropoffLocationID", (String) view.getTag());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

            locationSearchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapterDropOffLocation.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else if(locationType.trim().equals("transporter")){
            mTransporters = db.getAllTransporters();
            adapterTransporter = new TransporterAdapter(getApplicationContext(), mTransporters);

            locationLVView.setTextFilterEnabled(true);
            locationLVView.setAdapter(adapterTransporter);

            locationLVView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = getIntent();
                    intent.putExtra("transporterID", (String) view.getTag());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

            locationSearchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapterTransporter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

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

            progressLayoutView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressLayoutView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = getIntent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
