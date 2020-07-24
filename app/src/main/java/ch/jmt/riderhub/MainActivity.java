package ch.jmt.riderhub;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.navigation.NavigationView;

import java.sql.Time;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentLogin.fragmentBtnSelected,
        FragmentMap.fragmentBtnSelected, FragmentRegister.fragmentBtnSelected, FragmentRegisterSecond.fragmentBtnSelected {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    TextView drawerHeaderTextView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    DatePicker datePicker;
    TimePicker timePicker;

    List<Rideout> rideouts = new ArrayList<>();
    Rideout tempRideout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);


        View drawerHeaderView = navigationView.getHeaderView(0);
        drawerHeaderTextView = (TextView) drawerHeaderView.findViewById(R.id.drawerHeaderTextView);
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        drawerHeaderTextView.setText(currentDateTimeString);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        // default fragment
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_fragment, new FragmentLogin());
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId() == R.id.home){
            loadHomeFragment();
        } else if(item.getItemId() == R.id.map){
            loadMapFragment();
        } else if(item.getItemId() == R.id.rideout){
            loadRideoutFragment();
        }
        return true;
    }

    private void loadHomeFragment(){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment, new FragmentLogin());
        fragmentTransaction.commit();
    }

    private void loadMapFragment(){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment, new FragmentMap());
        fragmentTransaction.commit();
    }

    private void loadRideoutFragment(){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment, new FragmentRideout());
        fragmentTransaction.commit();
    }

    @Override
    public void onHomeBtnSelected() {
        loadHomeFragment();
    }

    @Override
    public void onLoginBtnSelected() {
        loadMapFragment();
    }

    @Override
    public void onNextBtnSelected() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment, new FragmentRegisterSecond());
        fragmentTransaction.commit();
    }


    // action handlers
    public void goToRegistration(View view){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment, new FragmentRegister());
        fragmentTransaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createRideout(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View createView = inflater.inflate(R.layout.fragment_rideout_create, null);
        builder.setView(createView);

        final boolean[] dateSet = {false};
        final boolean[] timeSet = {false};
        final boolean[] datePickerPressed = {false};
        final boolean[] timePickerPressed ={false};

        // get ui elements
        datePicker = createView.findViewById(R.id.rideoutDatePicker);
        datePicker.setCalendarViewShown(false);
        datePicker.setMinDate(System.currentTimeMillis());
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                setRideoutDate(dayOfMonth, monthOfYear, year);
                dateSet[0] = true;
                datePickerPressed[0] = true;
                Log.e("Date", "Date has been set");
            }
        });

        timePicker = createView.findViewById(R.id.rideoutTimePicker);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setRideoutTime(hourOfDay, minute);
                timeSet[0] = true;
                timePickerPressed[0] = true;
            }
        });


        final ImageButton showDatePickerBtn = createView.findViewById(R.id.rideoutCreateDateBtn);
        final ImageButton showTimePickerBtn = createView.findViewById(R.id.rideoutCreateTimeBtn);


        showDatePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!datePickerPressed[0]){
                    showDatePickerBtn.setVisibility(View.VISIBLE);
                    datePicker.setVisibility(View.VISIBLE);
                    showTimePickerBtn.setVisibility(View.GONE);
                    timePicker.setVisibility(View.GONE);
                    datePickerPressed[0] = true;
                } else{
                    datePicker.setVisibility(View.GONE);
                    showTimePickerBtn.setVisibility(View.VISIBLE);
                    datePickerPressed[0] = false;
                }
            }
        });

        showTimePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!timePickerPressed[0]){
                    showTimePickerBtn.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.VISIBLE);
                    showDatePickerBtn.setVisibility(View.GONE);
                    datePicker.setVisibility(View.GONE);
                    timePickerPressed[0] = true;
                } else{
                    timePicker.setVisibility(View.GONE);
                    showDatePickerBtn.setVisibility(View.VISIBLE);
                    timePickerPressed[0] = false;
                }
            }
        });

        builder.setMessage("New Rideout");

        if(dateSet[0] == true && timeSet[0] == true){
             builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
        }


        builder.show();
    }

    private void setRideoutDate(int day, int month, int year){

    }

    private void setRideoutTime(int hour, int minute){

    }

    public void showTimePicker(View view){

    }

    public void showDatePicker(View view){

    }
}