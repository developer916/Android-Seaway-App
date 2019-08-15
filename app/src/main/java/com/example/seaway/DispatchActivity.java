package com.example.seaway;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seaway.database.Customers;
import com.example.seaway.database.DatabaseHelper;
import com.example.seaway.form.BuyerForm;
import com.example.seaway.form.PostDispatchForm;
import com.example.seaway.lib.ApiClient;
import com.example.seaway.lib.TokenManager;
import com.example.seaway.list.BuyerList;
import com.example.seaway.list.DropOffLocationList;
import com.example.seaway.list.PickupLocationList;
import com.example.seaway.list.TransporterList;
import com.example.seaway.response.BuyerResponse;
import com.example.seaway.response.DispatchResponse;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DispatchActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView dispatchTypeView, itemTypeView,customerView, buyerView, titleView, keysView, pickupLocationView, dropOffLocationView,
            transporterView, orderDateView, pickupDateView, deliveryDateView, deliveredDateView, paidView, cashedView, pickupView, dispatchOrderIDView;
    private EditText yearView, makeView, modelView, vinView, colorView, lotView, priceView, storageFeeView, otherFeeView, vehicleWidthView, vehicleLengthView, vehicleHeightView, vehicleWeightView, cbmView, checkView;
    private Button postButton;
    private CharSequence[] availableDispatchTypes, availableItemTypes, availableBuyers, availableTitles, availableKeys, availablePaids, availableCashed, availablePickups;
    private PostDispatchForm postDispatchForm;
    private final int CUSTOMER_CODE = 100;
    private final int PICKUP_LOCATION_CODE= 101;
    private final int DROPOFF_LOCATION_CODE= 102;
    private final int TRANSPORTER_CODE= 103;
    private DatabaseHelper db;
    private String customerId, buyerId="", pickupLocationId, dropoffLocationId, transporterId;
    private BuyerForm buyerForm = new BuyerForm();
    private View progressLayoutView, progressView;
    private ArrayList<BuyerList> mBuyerList;
    private LinearLayout checkLinearLayoutView;
    private Float widthFloat, heightFloat, lengthFloat, weightFloat, cbmFloat;
    private CheckBox orderDateNullView, pickupDateNullView, deliveryDateNullView, deliveredDateNullView;
    private Calendar orderDateCalendar, pickupDateCalendar, deliveryDateCalendar, deliveredDateCalendar;
    private DatePickerDialog.OnDateSetListener order_date, pickup_date, delivery_date, delivered_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);

        db = new DatabaseHelper(this);
        Toolbar app_bar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(app_bar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_forward);


        checkLinearLayoutView = (LinearLayout) findViewById(R.id.check_linearlayout);
        dispatchTypeView = (TextView) findViewById(R.id.dispatch_type);
        itemTypeView =(TextView) findViewById(R.id.item_type);
        customerView = (TextView) findViewById(R.id.customer);
        buyerView =(TextView) findViewById(R.id.buyer);
        titleView = (TextView) findViewById(R.id.title);
        keysView = (TextView) findViewById(R.id.keys);
        pickupLocationView = (TextView) findViewById(R.id.pickup_location);
        dropOffLocationView = (TextView) findViewById(R.id.drop_off_location);
        transporterView = (TextView) findViewById(R.id.transporter);
        orderDateView = (TextView) findViewById(R.id.order_date);
        pickupDateView = (TextView) findViewById(R.id.pickup_date);
        deliveryDateView = (TextView) findViewById(R.id.delivery_date);
        deliveredDateView = (TextView) findViewById(R.id.delivered_date);
        paidView = (TextView) findViewById(R.id.paid);
        cashedView = (TextView) findViewById(R.id.cashed);
        pickupView = (TextView) findViewById(R.id.pickup);
        dispatchOrderIDView = (TextView) findViewById(R.id.dispatch_order_id);

        yearView = (EditText) findViewById(R.id.year);
        makeView = (EditText) findViewById(R.id.make);
        modelView = (EditText) findViewById(R.id.model);
        vinView = (EditText) findViewById(R.id.vin);
        colorView = (EditText) findViewById(R.id.color);
        lotView = (EditText) findViewById(R.id.lot);
        priceView = (EditText) findViewById(R.id.price);
        checkView = (EditText) findViewById(R.id.check);
        storageFeeView = (EditText) findViewById(R.id.storage_fee);
        otherFeeView = (EditText) findViewById(R.id.other_fee);
        vehicleWidthView = (EditText) findViewById(R.id.vehicle_width);
        vehicleLengthView = (EditText) findViewById(R.id.vehicle_length);
        vehicleHeightView = (EditText) findViewById(R.id.vehicle_height);
        vehicleWeightView = (EditText) findViewById(R.id.vehicle_weight);
        cbmView = (EditText) findViewById(R.id.cbm);
        postButton = (Button) findViewById(R.id.post_button);
        progressLayoutView = (FrameLayout) findViewById(R.id.progress_bar_layout);
        progressView = (ProgressBar) progressLayoutView.findViewById(R.id.progress_bar);

        //checkboxs

        orderDateNullView = (CheckBox) findViewById(R.id.order_date_null);
        pickupDateNullView = (CheckBox) findViewById(R.id.pickup_date_null);
        deliveryDateNullView = (CheckBox) findViewById(R.id.delivery_date_null);
        deliveredDateNullView = (CheckBox) findViewById(R.id.delivered_date_null);

        // calendar instance

        orderDateCalendar = Calendar.getInstance();
        pickupDateCalendar = Calendar.getInstance();
        deliveryDateCalendar = Calendar.getInstance();
        deliveredDateCalendar = Calendar.getInstance();

        order_date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                orderDateCalendar.set(Calendar.YEAR, year);
                orderDateCalendar.set(Calendar.MONTH, month);
                orderDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel("order_date");
            }
        };

        pickup_date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                pickupDateCalendar.set(Calendar.YEAR, year);
                pickupDateCalendar.set(Calendar.MONTH, month);
                pickupDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel("pickup_date");
            }
        };
        delivery_date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                deliveryDateCalendar.set(Calendar.YEAR, year);
                deliveryDateCalendar.set(Calendar.MONTH, month);
                deliveryDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel("delivery_date");
            }
        };
        delivered_date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                deliveredDateCalendar.set(Calendar.YEAR, year);
                deliveredDateCalendar.set(Calendar.MONTH, month);
                deliveredDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel("delivered_date");
            }
        };

        dispatchTypeView.setEnabled(true);
        dispatchTypeView.setOnClickListener(this);
        itemTypeView.setEnabled(true);
        itemTypeView.setOnClickListener(this);
        customerView.setEnabled(true);
        customerView.setOnClickListener(this);
        buyerView.setEnabled(true);
        buyerView.setOnClickListener(this);
        titleView.setEnabled(true);
        titleView.setOnClickListener(this);
        keysView.setEnabled(true);
        keysView.setOnClickListener(this);
        pickupLocationView.setEnabled(true);
        pickupLocationView.setOnClickListener(this);
        dropOffLocationView.setEnabled(true);
        dropOffLocationView.setOnClickListener(this);
        transporterView.setEnabled(true);
        transporterView.setOnClickListener(this);
        paidView.setEnabled(true);
        paidView.setOnClickListener(this);
        cashedView.setEnabled(true);
        cashedView.setOnClickListener(this);
        pickupView.setEnabled(true);
        pickupView.setOnClickListener(this);
        orderDateView.setEnabled(true);
        orderDateView.setOnClickListener(this);

        pickupDateView.setEnabled(true);
        pickupDateView.setOnClickListener(this);

        deliveryDateView.setEnabled(true);
        deliveryDateView.setOnClickListener(this);

        deliveredDateView.setEnabled(true);
        deliveredDateView.setOnClickListener(this);
        postButton.setEnabled(true);
        postButton.setOnClickListener(this);
        String date = new SimpleDateFormat("MM/dd/yyyyy", Locale.getDefault()).format(new Date());
        orderDateView.setText(date);
        pickupView.setText("Don't Agree");
        cashedView.setText("No");


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            postDispatchForm = (PostDispatchForm) bundle.getSerializable("form");
            if (postDispatchForm != null) {
                yearView.setText(postDispatchForm.years);
                makeView.setText(postDispatchForm.make);
                modelView.setText(postDispatchForm.model);
                vinView.setText(postDispatchForm.vin);

                if(!postDispatchForm.width.trim().equals("")){
                    widthFloat = Float.parseFloat(postDispatchForm.width);
                    vehicleWidthView.setText(String.format("%.2f", widthFloat));
                }
                if(!postDispatchForm.height.trim().equals("")) {
                    heightFloat = Float.parseFloat(postDispatchForm.height);
                    vehicleHeightView.setText(String.format("%.2f", heightFloat));
                }
                if(!postDispatchForm.length.trim().equals("")){
                    lengthFloat = Float.parseFloat(postDispatchForm.length);
                    vehicleLengthView.setText(String.format("%.2f", lengthFloat));
                }
                if(!postDispatchForm.weight.trim().equals("")){
                    weightFloat = Float.parseFloat(postDispatchForm.weight);
                    vehicleWeightView.setText(String.format("%.2f", weightFloat));
                }
                changeCBM();
                dispatchOrderIDView.setText(postDispatchForm.dispatchOrderID);

            }else{
                finish();
            }
        } else {
            finish();
        }

        vehicleWidthView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    changeCBM();
                }
            }
        });
        vehicleHeightView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    changeCBM();
                }
            }
        });
        vehicleLengthView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    changeCBM();
                }
            }
        });
        vehicleWeightView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    changeCBM();
                }
            }
        });

        orderDateNullView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    orderDateView.setText(R.string.set_null);
                    orderDateView.setEnabled(false);
                } else {
                    orderDateView.setText("");
                    orderDateView.setEnabled(true);
                }
            }
        });

        pickupDateNullView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    pickupDateView.setText(R.string.set_null);
                    pickupDateView.setEnabled(false);
                } else {
                    pickupDateView.setText("");
                    pickupDateView.setEnabled(true);
                }
            }
        });

        deliveryDateNullView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    deliveryDateView.setText(R.string.set_null);
                    deliveryDateView.setEnabled(false);
                } else {
                    deliveryDateView.setText("");
                    deliveryDateView.setEnabled(true);
                }
            }
        });

        deliveredDateNullView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    deliveredDateView.setText(R.string.set_null);
                    deliveredDateView.setEnabled(false);
                } else {
                    deliveredDateView.setText("");
                    deliveredDateView.setEnabled(true);
                }
            }
        });
    }

    public void changeCBM(){
        String widthValue = vehicleWidthView.getText().toString();
        String heightValue = vehicleHeightView.getText().toString();
        String lengthValue = vehicleLengthView.getText().toString();



        if(!widthValue.trim().equals("") && !heightValue.trim().equals("") && !lengthValue.trim().equals("")){
            widthFloat = Float.parseFloat(widthValue);
            heightFloat = Float.parseFloat(heightValue);
            lengthFloat = Float.parseFloat(lengthValue);
            cbmFloat =Float.valueOf((float) (lengthFloat * heightFloat * widthFloat* 25.4*25.4* 25.4 /1000/1000/ 1000));
            cbmView.setText(String.format("%.2f", cbmFloat));
        }
    }

    private void updateLabel(String date) {

        String myFormat = "MM/dd/YYYY"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        if(date.equals("order_date")){
            orderDateView.setText(sdf.format(orderDateCalendar.getTime()));
        } else if(date.equals("pickup_date")){
            pickupDateView.setText(sdf.format(pickupDateCalendar.getTime()));
        } else if(date.equals("delivery_date")){
            deliveryDateView.setText(sdf.format(deliveryDateCalendar.getTime()));
        } else if(date.equals("delivered_date")){
            deliveredDateView.setText(sdf.format(deliveredDateCalendar.getTime()));
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.dispatch_type){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DispatchActivity.this);
            dialogBuilder.setTitle(R.string.select_dispatch_type);
            List<String> mAvailableTypes = new ArrayList<String>();
            mAvailableTypes.add("Our Dispatch");
            mAvailableTypes.add("Client Dispatch");
            availableDispatchTypes = mAvailableTypes.toArray(new String[mAvailableTypes.size()]);
            dialogBuilder.setItems(availableDispatchTypes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedText = availableDispatchTypes[which].toString();
                    dispatchTypeView.setText(selectedText);
                    dialog.dismiss();
                }
            }).show();
        } else if(id == R.id.item_type){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DispatchActivity.this);
            dialogBuilder.setTitle(R.string.select_item_type);
            List<String> mAvailableItemTypes = new ArrayList<String>();
            mAvailableItemTypes.add("Vehicle");
            mAvailableItemTypes.add("Boat");
            mAvailableItemTypes.add("Motorcycle");
            mAvailableItemTypes.add("Heavy Equipment");

            availableItemTypes = mAvailableItemTypes.toArray(new String[mAvailableItemTypes.size()]);
            dialogBuilder.setItems(availableItemTypes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedText = availableItemTypes[which].toString();
                    itemTypeView.setText(selectedText);
                    dialog.dismiss();
                }
            }).show();
        } else if(id == R.id.customer){
            Intent regionIntent = new Intent(DispatchActivity.this, CustomerActivity.class);
            startActivityForResult(regionIntent,CUSTOMER_CODE);
        } else if(id == R.id.buyer){
              List<String> mBuyerString = new ArrayList<String>();
              for(BuyerList buyer : mBuyerList){
                  mBuyerString.add(buyer.getBuyerAndActionNumber());
              }
              availableBuyers = mBuyerString.toArray(new String[mBuyerString.size()]);
              AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DispatchActivity.this);
              dialogBuilder.setTitle(R.string.select_buyer);
              dialogBuilder.setItems(availableBuyers, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      String selectedText = availableBuyers[which].toString();
                      buyerView.setText(selectedText);
                      for(BuyerList buyer : mBuyerList){
                          if(buyer.getBuyerAndActionNumber().trim().equals(selectedText)){
                            buyerId = buyer.getBuyerID();
                          }
                      }
                      dialog.dismiss();
                  }
              }).show();
        } else if(id == R.id.title){
            List<String> mTitleString = new ArrayList<String>();
            mTitleString.add("Yes");
            mTitleString.add("No");
            mTitleString.add("Bos");
            mTitleString.add("Mailed");
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DispatchActivity.this);
            dialogBuilder.setTitle(R.string.select_title);
            availableTitles = mTitleString.toArray(new String[mTitleString.size()]);
            dialogBuilder.setItems(availableTitles, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectText = availableTitles[which].toString();
                    titleView.setText(selectText);
                    dialog.dismiss();
                }
            }).show();
        } else if(id == R.id.keys){
            List<String> mKeysString = new ArrayList<String>();
            mKeysString.add("0");
            mKeysString.add("1");
            mKeysString.add("2");
            mKeysString.add("3");
            mKeysString.add("4");
            mKeysString.add("5");
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DispatchActivity.this);
            availableKeys = mKeysString.toArray(new String[mKeysString.size()]);
            dialogBuilder.setTitle(R.string.select_keys);
            dialogBuilder.setItems(availableKeys, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectText = availableKeys[which].toString();
                    keysView.setText(selectText);
                    dialog.dismiss();
                }
            }).show();
        } else if(id == R.id.pickup_location){
            Intent regionIntent = new Intent(DispatchActivity.this, LocationActivity.class);
            regionIntent.putExtra("location_type", "pickup_location");
            startActivityForResult(regionIntent,PICKUP_LOCATION_CODE);
        } else if(id == R.id.drop_off_location){
            Intent regionIntent = new Intent(DispatchActivity.this, LocationActivity.class);
            regionIntent.putExtra("location_type", "drop_location");
            startActivityForResult(regionIntent,DROPOFF_LOCATION_CODE);
        } else if(id == R.id.transporter){
            Intent regionIntent = new Intent(DispatchActivity.this, LocationActivity.class);
            regionIntent.putExtra("location_type", "transporter");
            startActivityForResult(regionIntent,TRANSPORTER_CODE);
        } else if(id == R.id.paid){
            List<String> mPaidString = new ArrayList<String>();
            mPaidString.add("Yes");
            mPaidString.add("No");
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DispatchActivity.this);
            availablePaids= mPaidString.toArray(new String[mPaidString.size()]);
            dialogBuilder.setItems(availablePaids, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectText = availablePaids[which].toString();
                    paidView.setText(selectText);
                    if(selectText.trim().equals("Yes")){
                        checkLinearLayoutView.setVisibility(View.VISIBLE);
                    } else {
                        checkLinearLayoutView.setVisibility(View.GONE);
                    }
                    dialog.dismiss();
                }
            }).show();
        } else if(id == R.id.cashed){
            List<String> mCashedString = new ArrayList<String>();
            mCashedString.add("No");
            mCashedString.add("Yes");
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DispatchActivity.this);
            availableCashed= mCashedString.toArray(new String[mCashedString.size()]);
            dialogBuilder.setItems(availableCashed, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectText = availableCashed[which].toString();
                    cashedView.setText(selectText);
                    dialog.dismiss();
                }
            }).show();
        } else if(id == R.id.pickup){
            List<String> mPickupString = new ArrayList<String>();
            mPickupString.add("Don't Agree");
            mPickupString.add("Agree");

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DispatchActivity.this);
            availablePickups= mPickupString.toArray(new String[mPickupString.size()]);
            dialogBuilder.setItems(availablePickups, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectText = availablePickups[which].toString();
                    pickupView.setText(selectText);
                    dialog.dismiss();
                }
            }).show();
        } else if(id == R.id.order_date){
            new DatePickerDialog(DispatchActivity.this, order_date, orderDateCalendar.get(Calendar.YEAR), orderDateCalendar.get(Calendar.MONTH),
                    orderDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if(id == R.id.delivery_date){
            new DatePickerDialog(DispatchActivity.this, delivery_date, deliveryDateCalendar.get(Calendar.YEAR), deliveryDateCalendar.get(Calendar.MONTH),
                    deliveryDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if(id == R.id.delivered_date){
            new DatePickerDialog(DispatchActivity.this, delivered_date, deliveredDateCalendar.get(Calendar.YEAR), deliveredDateCalendar.get(Calendar.MONTH),
                    deliveredDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if(id == R.id.pickup_date){
            new DatePickerDialog(DispatchActivity.this, pickup_date, pickupDateCalendar.get(Calendar.YEAR), pickupDateCalendar.get(Calendar.MONTH),
                    pickupDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if(id == R.id.post_button){
//            Intent imageIntent = new Intent(DispatchActivity.this, ImageActivity.class);
//            imageIntent.putExtra("dispatchID" , 13043);
//            imageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(imageIntent);

            postButton.setEnabled(false);
            if(check_validation()){
                this.postDispatchForm.userId = TokenManager.getInstance(this).getToken().getToken();
                progressSaveDispatch();
            } else {
                postButton.setEnabled(true);
            }
        }
    }

    public void progressSaveDispatch(){


        Call<DispatchResponse> mServiceDispatchResponse = ApiClient.getInstance().getApi().saveDispatch(this.postDispatchForm);
        mServiceDispatchResponse.enqueue(new Callback<DispatchResponse>() {
            @Override
            public void onResponse(Call<DispatchResponse> call, Response<DispatchResponse> response) {
                if(response.isSuccessful()){
                    postButton.setEnabled(true);
                    DispatchResponse dispatchResponse = response.body();
                    String result = dispatchResponse.result;
                    if(result.trim().equals("success")){
                        Intent imageIntent = new Intent(DispatchActivity.this, ImageActivity.class);
                        imageIntent.putExtra("dispatchID" , dispatchResponse.dispatchID);
                        imageIntent.putExtra("dispatchOrderID", dispatchResponse.dispatchOrderID);
                        imageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(imageIntent);
                    }
                    showProgress(false);
                } else{
                    showProgress(false);
                    postButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<DispatchResponse> call, Throwable t) {
                showProgress(false);
                call.cancel();
                postButton.setEnabled(true);
                Toast.makeText(getApplicationContext(), R.string.failure_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean check_validation() {
        boolean valid = true;
        View focusView = null;
        //set error null
        cbmView.setError(null);
        vehicleWeightView.setError(null);
        vehicleHeightView.setError(null);
        vehicleLengthView.setError(null);
        vehicleWidthView.setError(null);
        pickupView.setError(null);
        cashedView.setError(null);
        paidView.setError(null);
        deliveredDateView.setError(null);
        dropOffLocationView.setError(null);
        pickupLocationView.setError(null);
        colorView.setError(null);
        vinView.setError(null);
        modelView.setError(null);
        makeView.setError(null);
        yearView.setError(null);
        customerView.setError(null);
        itemTypeView.setError(null);
        dispatchTypeView.setError(null);
        orderDateView.setError(null);
        transporterView.setError(null);
        priceView.setError(null);
        pickupDateView.setError(null);
        deliveryDateView.setError(null);
        deliveredDateView.setError(null);
        titleView.setError(null);
        keysView.setError(null);


        //get strings of insert values
        String cbmValue = cbmView.getText().toString();
        String weightValue = vehicleWeightView.getText().toString();
        String heightValue = vehicleHeightView.getText().toString();
        String lengthValue = vehicleLengthView.getText().toString();
        String widthValue = vehicleWidthView.getText().toString();
        String pickupValue = pickupView.getText().toString();
        String cashedValue = cashedView.getText().toString();
        String paidValue = paidView.getText().toString();
        String colorValue = colorView.getText().toString();
        String vinValue = vinView.getText().toString();
        String modelValue = modelView.getText().toString();
        String makeValue = makeView.getText().toString();
        String yearValue = yearView.getText().toString();
        String customerValue = customerView.getText().toString();
        String itemTypeValue = itemTypeView.getText().toString();
        String dispatchTypeValue = dispatchTypeView.getText().toString();
        String dropOffLocationValue = dropOffLocationView.getText().toString();
        String pickupLocationValue = pickupLocationView.getText().toString();
        String orderDateValue = orderDateView.getText().toString();
        String transporterValue = transporterView.getText().toString();
        String priceValue = priceView.getText().toString();
        String pickupDateValue = pickupDateView.getText().toString();
        String deliveryDateValue = deliveryDateView.getText().toString();
        String deliveredDateValue = deliveredDateView.getText().toString();
        String titleValue = titleView.getText().toString();
        String keysValue = keysView.getText().toString();
        String storageFeeValue = storageFeeView.getText().toString();
        String otherFeeValue = otherFeeView.getText().toString();
        String checkValue = checkView.getText().toString();
        String lotValue = lotView.getText().toString();

        this.postDispatchForm.lot = lotValue;
        this.postDispatchForm.orderDate = orderDateValue;
        this.postDispatchForm.pickupDate = pickupDateValue;
        this.postDispatchForm.transporter = transporterId;
        this.postDispatchForm.price = priceValue;
        this.postDispatchForm.deliveryDate = deliveryDateValue;
        this.postDispatchForm.deliveredDate = deliveredDateValue;
        this.postDispatchForm.title = titleValue;
        this.postDispatchForm.keys = keysValue;
        this.postDispatchForm.storageFee = storageFeeValue;
        this.postDispatchForm.otherFee = otherFeeValue;
        this.postDispatchForm.check = checkValue;
        this.postDispatchForm.cbm= cbmValue;

        if(TextUtils.isEmpty(weightValue)){
            vehicleWeightView.setError(getString(R.string.error_field_required));
            focusView = vehicleWeightView;
            valid = false;
        } else {
            this.postDispatchForm.weight = weightValue;
        }

        if(TextUtils.isEmpty(heightValue)){
            vehicleHeightView.setError(getString(R.string.error_field_required));
            focusView = vehicleHeightView;
            valid = false;
        } else {
            this.postDispatchForm.height = heightValue;
        }

        if(TextUtils.isEmpty(lengthValue)){
            vehicleLengthView.setError(getString(R.string.error_field_required));
            focusView = vehicleLengthView;
            valid = false;
        } else {
            this.postDispatchForm.length = lengthValue;
        }


        if(TextUtils.isEmpty(widthValue)){
            vehicleWidthView.setError(getString(R.string.error_field_required));
            focusView = vehicleWidthView;
            valid = false;
        } else {
            this.postDispatchForm.width = widthValue;
        }

        if(TextUtils.isEmpty(pickupValue)){
            pickupView.setError(getString(R.string.error_field_required));
            focusView = pickupView;
            valid = false;
        } else {
            if(!pickupValue.trim().equals("Agree")){
                this.postDispatchForm.agreePickup = "";
            } else{
                this.postDispatchForm.agreePickup = pickupValue;
            }

        }

        if(TextUtils.isEmpty(paidValue)){
            paidView.setError(getString(R.string.error_field_required));
            focusView = paidView;
            valid = false;
        } else {
            if(paidValue.trim().equals("Yes")){
                if(TextUtils.isEmpty(checkValue)){
                    checkView.setError(getString(R.string.error_field_required));
                    focusView = checkView;
                    valid = false;
                }
                this.postDispatchForm.paid = 1;
            } else{
                this.postDispatchForm.paid = 0;
            }
        }

        if(!TextUtils.isEmpty(cashedValue)){
            if(cashedValue.trim().equals("No")){
                this.postDispatchForm.cashed = 0;
            } else {
                this.postDispatchForm.cashed = 1;
            }
        }


        if(TextUtils.isEmpty(pickupValue)){
            pickupView.setError(getString(R.string.error_field_required));
            focusView = pickupView;
            valid = false;
        } else {
            if(!pickupValue.trim().equals("Agree")){
                this.postDispatchForm.agreePickup = "";
            } else{
                this.postDispatchForm.agreePickup = pickupValue;
            }

        }

        if(TextUtils.isEmpty(pickupLocationValue)){
            pickupLocationView.setError(getString(R.string.error_field_required));
            focusView = pickupLocationView;
            valid = false;
        } else {
            this.postDispatchForm.pickUpLocation = pickupLocationId;
        }

        if(TextUtils.isEmpty(dropOffLocationValue)){
            dropOffLocationView.setError(getString(R.string.error_field_required));
            focusView = dropOffLocationView;
            valid = false;
        } else {
            this.postDispatchForm.dropOffLocation = dropoffLocationId;
        }

//        if(TextUtils.isEmpty(orderDateValue)){
//            orderDateView.setError(getString(R.string.error_field_required));
//            focusView = orderDateView;
//            valid = false;
//        } else {
//
//        }

        if(!TextUtils.isEmpty(transporterValue)){
            if(TextUtils.isEmpty(priceValue)){
                priceView.setError(getString(R.string.error_field_required));
                focusView = priceView;
                valid = false;
            }
            if(TextUtils.isEmpty(pickupDateValue)){
                pickupDateView.setError(getString(R.string.error_field_required));
                focusView = pickupDateView;
                valid = false;
            }
            if(TextUtils.isEmpty(deliveryDateValue)){
                deliveryDateView.setError(getString(R.string.error_field_required));
                focusView = deliveryDateView;
                valid = false;
            }
        }

        if(!TextUtils.isEmpty(deliveredDateValue)){
            if(TextUtils.isEmpty(titleValue)){
                titleView.setError(getString(R.string.error_field_required));
                focusView = deliveryDateView;
                valid = false;
            }
        }


        if(TextUtils.isEmpty(colorValue)){
            colorView.setError(getString(R.string.error_field_required));
            focusView = colorView;
            valid = false;
        } else {
            this.postDispatchForm.color = colorValue;
        }

        if(TextUtils.isEmpty(vinValue)){
            vinView.setError(getString(R.string.error_field_required));
            focusView = vinView;
            valid = false;
        } else {
            this.postDispatchForm.vin = vinValue;
        }

        if(TextUtils.isEmpty(modelValue)){
            modelView.setError(getString(R.string.error_field_required));
            focusView = modelView;
            valid = false;
        } else {
            this.postDispatchForm.model = modelValue;
        }

        if(TextUtils.isEmpty(makeValue)){
            makeView.setError(getString(R.string.error_field_required));
            focusView = makeView;
            valid = false;
        } else {
            this.postDispatchForm.make = makeValue;
        }

        if(TextUtils.isEmpty(yearValue)){
            yearView.setError(getString(R.string.error_field_required));
            focusView = yearView;
            valid = false;
        } else {
            this.postDispatchForm.years = yearValue;
        }

        if(TextUtils.isEmpty(customerValue)){
            customerView.setError(getString(R.string.error_field_required));
            focusView = customerView;
            valid = false;
        } else {
            this.postDispatchForm.customer = customerId;
            this.postDispatchForm.buyer = buyerId;
        }

        if(TextUtils.isEmpty(itemTypeValue)){
            itemTypeView.setError(getString(R.string.error_field_required));
            focusView = itemTypeView;
            valid = false;
        } else {
            this.postDispatchForm.loading = itemTypeValue;
        }

        if(TextUtils.isEmpty(dispatchTypeValue)){
            dispatchTypeView.setError(getString(R.string.error_field_required));
            focusView = dispatchTypeView;
            valid = false;
        } else {
            if(dispatchTypeValue.trim().equals("Our Dispatch")){
                this.postDispatchForm.dispatchType = 0;
            } else {
                this.postDispatchForm.dispatchType = 1;
            }
        }

        if (!valid) {
            focusView.requestFocus();
        }
        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CUSTOMER_CODE){
            customerView.setEnabled(true);
            if(resultCode == RESULT_OK){
                customerId = data.getStringExtra("customerID");
                Customers customer = db.getCustomer(customerId);
                if(customer != null){
                    customerView.setText(customer.getName());
                }
                getCustomerBuyer();
            }
        } else if(requestCode == PICKUP_LOCATION_CODE){
            if(resultCode == RESULT_OK){
                pickupLocationId = data.getStringExtra("pickupLocationID");
                if(pickupLocationId.trim().equals("0")){
                    pickupLocationView.setText("Self Dispatch");
                } else{
                    PickupLocationList pickupLocationList = db.getPickupLocation(pickupLocationId);
                    if(pickupLocationList != null){
                        pickupLocationView.setText(pickupLocationList.getLocationName());
                    }
                }

            }
        } else if(requestCode == DROPOFF_LOCATION_CODE){
            if(resultCode == RESULT_OK) {
                dropoffLocationId = data.getStringExtra("dropoffLocationID");
                if (dropoffLocationId.trim().equals("0")) {
                    dropOffLocationView.setText("Self Dispatch");
                } else {
                    DropOffLocationList dropOffLocationList = db.getDropOffLocation(dropoffLocationId);
                    if (dropOffLocationList != null) {
                        dropOffLocationView.setText(dropOffLocationList.getLocationName());
                    }
                }
            }
        } else if(requestCode == TRANSPORTER_CODE){
            if(resultCode == RESULT_OK) {
                transporterId = data.getStringExtra("transporterID");
                if(transporterId.trim().equals("0")){
                    transporterView.setText("Self Dispatch");
                } else {
                    TransporterList transporterList = db.getTransporter(transporterId);
                    if(transporterList != null){
                        transporterView.setText(transporterList.getCompanyName());
                    }
                }
            }
        }
    }

    //get buyers list

    public void getCustomerBuyer(){
        if(this.buyerForm == null){
            this.buyerForm = new BuyerForm();
        }
        this.buyerForm.result = "success";
        this.buyerForm.id = customerId;

        Call<BuyerResponse> mServiceBuyerResponse = ApiClient.getInstance().getApi().getBuyers(this.buyerForm);
        showProgress(true);
        mServiceBuyerResponse.enqueue(new Callback<BuyerResponse>() {
            @Override
            public void onResponse(Call<BuyerResponse> call, Response<BuyerResponse> response) {
                if(response.isSuccessful()){
                    BuyerResponse buyerResponse = response.body();
                    if(buyerResponse.getResult().trim().equals("success")){
                        mBuyerList= response.body().buyer;
                    }
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<BuyerResponse> call, Throwable t) {
                showProgress(false);
                call.cancel();
                Toast.makeText(getApplicationContext(), R.string.failure_error, Toast.LENGTH_LONG).show();
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
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}


