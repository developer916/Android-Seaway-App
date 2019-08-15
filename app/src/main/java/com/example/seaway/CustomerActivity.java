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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.seaway.adapter.CustomerAdapter;
import com.example.seaway.database.Customers;
import com.example.seaway.database.DatabaseHelper;
import com.example.seaway.form.CustomForm;
import com.example.seaway.lib.ApiClient;
import com.example.seaway.list.CustomersList;
import com.example.seaway.response.CustomerResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerActivity extends AppCompatActivity {

    private View progressLayoutView, progressView;
    private DatabaseHelper db;
    private ArrayList<CustomersList> customers;
    private CustomerAdapter adapter;
    private ArrayList<CustomersList> mCustomers = new ArrayList<>();
    private ListView customerLVView;
    private EditText customerSearchView;
    private CustomForm customForm = new CustomForm();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        db = new DatabaseHelper(this);

        Toolbar app_bar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(app_bar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_forward);

        progressLayoutView = (FrameLayout) findViewById(R.id.progress_bar_layout);
        progressView = (ProgressBar) progressLayoutView.findViewById(R.id.progress_bar);
        customerLVView = (ListView) findViewById(R.id.customer_list);
        customerSearchView =(EditText) findViewById(R.id.customer_search);
        customerSearchView.clearFocus();

        getAllCustomers();
    }

    public void getAllCustomers(){
        this.customForm.result = "success";
        Call<CustomerResponse> mServiceCustomer = ApiClient.getInstance().getApi().getCustomers(this.customForm);
        showProgress(true);
        mServiceCustomer.enqueue(new Callback<CustomerResponse>() {
            @Override
            public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                if (response.isSuccessful()){
                    CustomerResponse customerResponse = response.body();
                    String result= customerResponse.result;
                    if(result.trim().equals("success")){
                        List<CustomersList> customersLists = response.body().customers;
                        customers = db.getAllCustomers();
                        if(customersLists.size() != customers.size()){
                            db.deleteAllCustomers();
                            for (CustomersList customer : customersLists) {
                                db.insertUpdateCustomer(customer.getCustomerId(), customer.getCompanyName());
                            }
                        }

                        showResult();
                        showProgress(false);
                    } else {
                        showProgress(false);
                    }
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<CustomerResponse> call, Throwable t) {
                showProgress(false);
                call.cancel();
                Toast.makeText(getApplicationContext(), R.string.failure_error, Toast.LENGTH_LONG).show();
            }
        });


    }

    public void showResult(){
        mCustomers = db.getAllCustomers();

        adapter = new CustomerAdapter(getApplicationContext(), mCustomers);
        customerLVView.setTextFilterEnabled(true);
        customerLVView.setAdapter(adapter);

        customerLVView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                intent.putExtra("customerID", (String) view.getTag());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        customerSearchView.addTextChangedListener(new TextWatcher() {
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
