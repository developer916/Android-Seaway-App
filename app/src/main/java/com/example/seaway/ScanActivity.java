package com.example.seaway;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seaway.database.DatabaseHelper;
import com.example.seaway.form.PostDispatchForm;
import com.example.seaway.form.VinForm;
import com.example.seaway.lib.ApiClient;
import com.example.seaway.lib.TokenManager;
import com.example.seaway.response.VinResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;

//import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.pixotech.android.scanner.library.ScannerActivity;
import com.pixotech.android.scanner.library.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class ScanActivity  extends ScannerActivity {
    private View progressLayoutView, progressView;
    private VinForm vinForm = new VinForm();
    private PostDispatchForm postDispatchForm;
    private ImageButton backButton, enterVinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addLayout(R.layout.activity_scan);
        postDispatchForm = new PostDispatchForm();
        backButton = (ImageButton) findViewById(R.id.back_button);
        enterVinButton = (ImageButton) findViewById(R.id.enter_vin_button);
        backButton.setEnabled(true);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboardIntent = new Intent(ScanActivity.this, DashboardActivity.class);
                dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(dashboardIntent);
            }
        });
        enterVinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ScanActivity.this);
                dialog.setContentView(R.layout.vin_enter);
                final TextView enterVinView = (TextView) dialog.findViewById(R.id.enter_vin);
                Button okButton = (Button) dialog.findViewById(R.id.ok_button);
                Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
                dialog.show();

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = enterVinView.getText().toString();
                        if(!s.trim().equals("")){
                            processHandle(s);
                        }
                        dialog.hide();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                    }
                });
            }
        });

        progressLayoutView = (FrameLayout) findViewById(R.id.progress_bar_layout);
        progressView = (ProgressBar) progressLayoutView.findViewById(R.id.progress_bar);

    }
    @Override
    public void handleDecode(String s) {
//        vibrate();
        if(!TextUtils.isEmpty(s)){
            processHandle(s);
        } else {
            onResume();
        }
    }


    public void processHandle(String code){
        if(this.vinForm == null){
            this.vinForm = new VinForm();
        }
        this.vinForm.vin = code;
        Call<VinResponse> mServiceVinResponse = ApiClient.getInstance().getApi().vinDecode(this.vinForm);
        showProgress(true);
        mServiceVinResponse.enqueue(new Callback<VinResponse>() {
            @Override
            public void onResponse(Call<VinResponse> call, Response<VinResponse> response) {
                try {
                    if(response.isSuccessful()){
                        VinResponse mServiceVinResponse = response.body();
                        String result = mServiceVinResponse.getResult();
                        if(result.trim().equals("success")){
                            if(postDispatchForm== null ){
                                postDispatchForm = new PostDispatchForm();
                            }
                            postDispatchForm.years= mServiceVinResponse.getYears();
                            postDispatchForm.model = mServiceVinResponse.getModel();
                            postDispatchForm.make = mServiceVinResponse.getMake();
                            postDispatchForm.width = mServiceVinResponse.getWidth1();
                            postDispatchForm.height = mServiceVinResponse.getHeight();
                            postDispatchForm.length = mServiceVinResponse.getLength();
                            postDispatchForm.weight = mServiceVinResponse.getWeight();
                            postDispatchForm.vin = mServiceVinResponse.getVin();
                            postDispatchForm.dispatchOrderID = mServiceVinResponse.getPortNumber();
                            Intent dispatchIntent = new Intent(ScanActivity.this, DispatchActivity.class);
                            dispatchIntent.putExtra( "form" ,  postDispatchForm);
                            startActivity(dispatchIntent);
                            showProgress(false);
                        } else {
                            onResume();
                        }
                    }else{
                        showProgress(false);
                        String s = response.errorBody().string();
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            Toast.makeText(getApplicationContext(), R.string.failure_wrong_format, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), R.string.failure_wrong_format, Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        onResume();
                    }
                }catch (IOException e) {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), R.string.failure_wrong_format, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    onResume();
                }
            }

            @Override
            public void onFailure(Call<VinResponse> call, Throwable t) {
                showProgress(false);
                call.cancel();
                Toast.makeText(getApplicationContext(), R.string.failure_error, Toast.LENGTH_LONG).show();
                onResume();
            }
        });
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

}
