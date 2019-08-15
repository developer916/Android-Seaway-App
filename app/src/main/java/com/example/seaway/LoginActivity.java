package com.example.seaway;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.seaway.database.DatabaseHelper;
import com.example.seaway.form.LoginForm;
import com.example.seaway.lib.AccessToken;
import com.example.seaway.lib.ApiClient;
import com.example.seaway.lib.TokenManager;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private DatabaseHelper db;
    private EditText userNameView, passwordView;
    private Button loginButton;
    private View progressLayoutView, progressView;
    private LoginForm loginForm = new LoginForm();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DatabaseHelper(this);
        progressLayoutView = (FrameLayout) findViewById(R.id.progress_bar_layout);
        progressView = (ProgressBar) progressLayoutView.findViewById(R.id.progress_bar);
        loginButton = (Button) findViewById(R.id.login_in_button);
        userNameView = (EditText) findViewById(R.id.user_name);
        passwordView = (EditText) findViewById(R.id.password);
        loginButton.setEnabled(true);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login_in_button){
            loginButton.setEnabled(false);
            if (check_validation()) {
                String userName = userNameView.getText().toString();
                String password = passwordView.getText().toString();

                if (this.loginForm == null) {
                    this.loginForm = new LoginForm();
                }

                this.loginForm.userid = userName;
                this.loginForm.password = password;

                if (this.loginForm != null && this.loginForm.is_valid()) {
                    showProgress(true);
                    processLogin();
                } else {
                    loginButton.setEnabled(true);
                    Toast.makeText(this, R.string.invalid_request, Toast.LENGTH_SHORT).show();
                }
            } else {
                loginButton.setEnabled(true);
            }
        }
    }

    public void processLogin(){
        Call<AccessToken> mServiceLogin = ApiClient.getInstance().getApi().login(this.loginForm);
        mServiceLogin.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                loginButton.setEnabled(true);
                showProgress(false);
                try {
                    if(response.isSuccessful()){
                        AccessToken mServiceAccessToken = response.body();
                        String result = mServiceAccessToken.getResult();
                        if(result.trim().equals("success")){
                            TokenManager.getInstance(LoginActivity.this).saveToken(mServiceAccessToken);
                            String date = new SimpleDateFormat("MM/dd/yyyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                            db.insertUpdateLoginData(mServiceAccessToken.userName,  date);

                            Intent dashboardIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                            dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(dashboardIntent);

                        } else {
                            Toast.makeText(getApplicationContext(), R.string.invalid_user_name_or_password, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String s = response.errorBody().string();
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            Toast.makeText(getApplicationContext(), R.string.failure_wrong_format, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), R.string.failure_wrong_format, Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }catch (IOException e) {
                    Toast.makeText(getApplicationContext(), R.string.failure_wrong_format, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                showProgress(false);
                call.cancel();
                loginButton.setEnabled(true);
                Toast.makeText(getApplicationContext(), R.string.failure_error, Toast.LENGTH_LONG).show();
            }
        });

    }

    public boolean check_validation() {
        userNameView.setError(null);
        passwordView.setError(null);
        boolean valid = true;
        View focusView = null;
        String userName = userNameView.getText().toString();
        String password = passwordView.getText().toString();

        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_password_can_not_be_empty));
            focusView = passwordView;
            valid = false;
        }

        if (TextUtils.isEmpty(userName)) {
            userNameView.setError(getString(R.string.error_user_name_can_not_be_empty));
            focusView = userNameView;
            valid = false;
        }

        if (!valid) {
            focusView.requestFocus();
        }
        return valid;
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
