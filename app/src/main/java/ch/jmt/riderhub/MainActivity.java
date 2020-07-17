package ch.jmt.riderhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentLogin.fragmentBtnSelected,
        FragmentMap.fragmentBtnSelected, FragmentRegister.fragmentBtnSelected, FragmentRegisterSecond.fragmentBtnSelected {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    TextView drawerHeaderTextView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

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

    @Override
    public void onHomeBtnSelected() {
        loadHomeFragment();
    }

    public void goToRegistration(View view){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment, new FragmentRegister());
        fragmentTransaction.commit();
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
}