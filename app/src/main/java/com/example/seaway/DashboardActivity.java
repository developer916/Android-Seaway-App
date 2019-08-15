package com.example.seaway;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.seaway.database.DatabaseHelper;
import com.example.seaway.database.LoginData;
import com.example.seaway.lib.TokenManager;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton menuButton;
    private DatabaseHelper db;
    private TextView navigationShopNameView, navigationUserNameView, shopNameView, userNameView, loginDateView;
    private DrawerLayout drawer;
    private NavigationView nv;
    private String userName, loginDate;
    private Button addDispatchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        final Toolbar app_bar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(app_bar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        db = new DatabaseHelper(this);

        userNameView =(TextView) findViewById(R.id.user_name1);
        loginDateView = (TextView) findViewById(R.id.login_date);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nv = (NavigationView) findViewById(R.id.nav_view);
        addDispatchButton = (Button) findViewById(R.id.addDispatchButton);
        addDispatchButton.setEnabled(true);
        addDispatchButton.setOnClickListener(this);
        View headerView = nv.getHeaderView(0);
        navigationShopNameView = (TextView) headerView.findViewById(R.id.shop_name);
        navigationUserNameView =(TextView) headerView.findViewById(R.id.user_name);
        LoginData loginData = db.getLoginData();
        if(loginData !=null){
            userName = getResources().getString(R.string.welcome) + " " +loginData.getUserName();
            loginDate = loginData.getLoginDate();
        }

        userNameView.setText(userName);
        loginDateView.setText(loginDate);
        navigationUserNameView.setText(userName);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.logout){
                    drawer.closeDrawer(nv);
                    new AlertDialog.Builder(DashboardActivity.this)
                            .setMessage(R.string.logout_text)
                            .setPositiveButton(R.string.logout_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    TokenManager.getInstance(getApplicationContext()).clear();
                                    Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
                                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(loginIntent);
                                }
                            })
                            .setNegativeButton(R.string.logout_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                } else if(id == R.id.addDispatch){
                    Intent newIntent = new Intent(DashboardActivity.this, ScanActivity.class);
                    startActivity(newIntent);
                } else if(id == R.id.currentDispatch){
                    Intent newIntent = new Intent(DashboardActivity.this, CurrentDispatchActivity.class);
                    startActivity(newIntent);
                }
                return true;
            }
        });

        menuButton = (ImageButton) findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(nv);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent scanIntent = new Intent(DashboardActivity.this, ScanActivity.class);
        startActivity(scanIntent);
    }
}
