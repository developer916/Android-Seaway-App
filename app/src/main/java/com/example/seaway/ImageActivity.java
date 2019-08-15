package com.example.seaway;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seaway.form.ImageUploadForm;
import com.example.seaway.lib.ApiClient;
import com.example.seaway.response.ImageUploadResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageActivity extends AppCompatActivity  implements View.OnClickListener {

    private static final int PICK_FROM_GALLERY = 1958;
    private static final int PICK_FROM_CAMERA = 1959;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CAMERA = 2;
    private static String[] PERMISSION_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String[] PERMISSION_CAMERA = {
            Manifest.permission.CAMERA
    };

    public TextView imageUploadTextView;
    public LinearLayout selectedImageLayoutView;
    public ImageButton removeImageView;
    public ImageView previewImageView;
    public Button uploadButton, finishButton;
    public int dispatchID, dispatchOrderID;
    public ImageUploadForm imageUploadForm;
    private View progressLayoutView, progressView;
    private TextView dispatchIDView;

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        int rc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (rc != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_CAMERA,
                    REQUEST_CAMERA
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Toolbar app_bar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(app_bar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_forward);

        verifyStoragePermissions(this);
        imageUploadTextView = (TextView) findViewById(R.id.image_upload_text);
        selectedImageLayoutView = (LinearLayout) findViewById(R.id.selected_image_layout);
        removeImageView = (ImageButton) findViewById(R.id.remove_image);
        previewImageView = (ImageView) findViewById(R.id.preview_image);
        uploadButton = (Button) findViewById(R.id.uploadBtn);
        finishButton = (Button) findViewById(R.id.finishBtn);
        imageUploadTextView.setEnabled(true);
        imageUploadTextView.setOnClickListener(this);
        removeImageView.setEnabled(true);
        removeImageView.setOnClickListener(this);
        uploadButton.setEnabled(true);
        uploadButton.setOnClickListener(this);
        finishButton.setEnabled(true);
        finishButton.setOnClickListener(this);
        selectedImageLayoutView.setVisibility(View.GONE);
        progressLayoutView = (FrameLayout) findViewById(R.id.progress_bar_layout);
        progressView = (ProgressBar) progressLayoutView.findViewById(R.id.progress_bar);
        dispatchIDView = (TextView) findViewById(R.id.dispatchID);

        imageUploadForm = new ImageUploadForm();
        if(savedInstanceState  == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                dispatchID = 0;
                dispatchOrderID = 0;
            }else{
                dispatchID= extras.getInt("dispatchID");
                dispatchOrderID = extras.getInt("dispatchOrderID");
            }
        } else {
            dispatchID = (Integer) savedInstanceState.getSerializable("dispatchID");
            dispatchOrderID = (Integer) savedInstanceState.getSerializable("dispatchOrderID");
        }
        dispatchIDView.setText(String.valueOf(dispatchOrderID));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.image_upload_text){
            showUploadChooserDialog();
        } else if(id == R.id.remove_image){
            this.imageUploadForm.image = "";
            selectedImageLayoutView.setVisibility(View.GONE);
        } else if(id == R.id.uploadBtn){
            this.imageUploadForm.DispatchID = dispatchID;
            if(this.imageUploadForm != null && this.imageUploadForm.is_valid()){
                processImageUpload();
            }

        } else if(id == R.id.finishBtn){
            Intent dashboardIntent = new Intent(this, DashboardActivity.class);
            dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(dashboardIntent);
        }
    }

    private void showUploadChooserDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_FROM_GALLERY);
    }

    public void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    selectedImageLayoutView.setVisibility(View.VISIBLE);
                    previewImageView.setImageBitmap(bitmap);
                    this.imageUploadForm.image = encodeImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            selectedImageLayoutView.setVisibility(View.VISIBLE);
            previewImageView.setImageBitmap(bitmap);
            this.imageUploadForm.image = encodeImage(bitmap);
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return "data:image/jpeg;base64," + encImage;
    }

    public void processImageUpload() {
        showProgress(true);
        Call<ImageUploadResponse> mServiceImageUploadResponse = ApiClient.getInstance().getApi().saveImage(this.imageUploadForm);
        mServiceImageUploadResponse.enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                uploadButton.setEnabled(true);
                showProgress(false);
                if(response.isSuccessful()){
                    ImageUploadResponse imageUploadResponse = response.body();
                    if(imageUploadResponse.result.trim().equals("success")){
                        imageUploadForm = new ImageUploadForm();
                        Intent newImageIntent = new Intent(ImageActivity.this, ImageActivity.class);
                        newImageIntent.putExtra("dispatchID" , dispatchID);
                        newImageIntent.putExtra("dispatchOrderID", dispatchOrderID);
                        newImageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(newImageIntent);
                        finish();

                    } else{
                        Toast.makeText(getApplicationContext(), imageUploadResponse.msg, Toast.LENGTH_LONG).show();
                    }
                } else{
                    showProgress(false);
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                showProgress(false);
                call.cancel();
                uploadButton.setEnabled(true);
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
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent dashboardIntent = new Intent(this, DashboardActivity.class);
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(dashboardIntent);
    }
}
