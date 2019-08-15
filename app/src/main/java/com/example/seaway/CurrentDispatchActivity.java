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

import com.example.seaway.adapter.CurrentDispatchAdapter;
import com.example.seaway.form.CustomForm;
import com.example.seaway.lib.ApiClient;
import com.example.seaway.list.CurrentDispatchList;
import com.example.seaway.response.CurrentDispatchResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentDispatchActivity extends AppCompatActivity {

    public EditText dispatchSearchView;
    public ListView dispatchListView;
    private View progressLayoutView, progressView;
    public CustomForm customForm = new CustomForm();
    public ArrayList<CurrentDispatchList> mCurrentDispatch;
    public CurrentDispatchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_dispatch);

        Toolbar app_bar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(app_bar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_forward);

        dispatchSearchView =(EditText) findViewById(R.id.dispatch_search);
        dispatchListView =(ListView) findViewById(R.id.dispatch_list);
        progressLayoutView = (FrameLayout) findViewById(R.id.progress_bar_layout);
        progressView = (ProgressBar) progressLayoutView.findViewById(R.id.progress_bar);
        onGetResult();
    }

    public void onGetResult(){
        this.customForm.result = "success";
        showProgress(true);
        Call<CurrentDispatchResponse> mServiceCurrentDispatchResponse = ApiClient.getInstance().getApi().getCurrentDispatch(this.customForm);
        mServiceCurrentDispatchResponse.enqueue(new Callback<CurrentDispatchResponse>() {
            @Override
            public void onResponse(Call<CurrentDispatchResponse> call, Response<CurrentDispatchResponse> response) {
                if(response.isSuccessful()){
                    showProgress(false);
                    CurrentDispatchResponse currentDispatchResponse = response.body();
                    if(currentDispatchResponse.result.trim().equals("success")){
                        mCurrentDispatch = currentDispatchResponse.list;
                        onShowResult();
                    } else {
                        showProgress(false);
                        Toast.makeText(getApplicationContext(), R.string.failure_wrong_format, Toast.LENGTH_LONG).show();
                    }

                } else {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), R.string.failure_wrong_format, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<CurrentDispatchResponse> call, Throwable t) {
                showProgress(false);
                call.cancel();
                Toast.makeText(getApplicationContext(), R.string.failure_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onShowResult(){
        adapter = new CurrentDispatchAdapter(getApplicationContext(), mCurrentDispatch);
        dispatchListView.setTextFilterEnabled(true);
        dispatchListView.setAdapter(adapter);

        dispatchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int dispatchID = (int) view.getTag();
                int dispatchOrderID = 0;
                if(mCurrentDispatch.size()>0){
                    for(CurrentDispatchList currentDispatch: mCurrentDispatch){
                        if(currentDispatch.getDispatchID() == dispatchID){
                            dispatchOrderID = currentDispatch.getDispatchOrderID();
                        }
                    }
                }
                Intent imageIntent = new Intent(CurrentDispatchActivity.this, ImageActivity.class);
                imageIntent.putExtra("dispatchID" , dispatchID);
                imageIntent.putExtra("dispatchOrderID", dispatchOrderID);
                startActivity(imageIntent);

            }
        });

        dispatchSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
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
