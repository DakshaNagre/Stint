package com.oosd.project.taskmanagementapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.oosd.project.taskmanagementapp.Controllers.BoardController;
import com.oosd.project.taskmanagementapp.R;
import com.oosd.project.taskmanagementapp.util.Constants;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class NavDrawerActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private BoardController boardController;
    private List<Object> userDetails;
    private CircleImageView profilePhoto;
    private TextView name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navdrawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        boardController = new BoardController(this);
        userDetails = boardController.getAccountSettings();

        NavigationView navigationDrawer = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationDrawer.getHeaderView(0);

        profilePhoto = header.findViewById(R.id.profileNavPic);
        name = header.findViewById(R.id.profileNavName);
        name.setText(userDetails.get(0).toString());
        email = header.findViewById(R.id.profileNavEmail);
        email.setText(userDetails.get(1).toString());

        if(userDetails.get(4)!=null && Boolean.valueOf(userDetails.get(4).toString()))
        {
            System.out.println(Constants.S3_IMAGE_BUCKET + "/" + userDetails.get(5).toString());
            Picasso.get()
                    .load(Constants.S3_IMAGE_BUCKET + "/" + userDetails.get(5).toString())
                    .fit().centerCrop()
                    .into(profilePhoto);
        }

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_calendar, R.id.nav_account_settings, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logout(MenuItem menuItem){
        SharedPreferences settings = this.getSharedPreferences("user", this.MODE_PRIVATE);
        settings.edit().clear().commit();
        Intent intent = new Intent(NavDrawerActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

