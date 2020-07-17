package ch.jmt.riderhub;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Locale;

public class FragmentMap extends Fragment implements OnMapReadyCallback{

    // interface for clickevent handling
    public interface fragmentBtnSelected{
        public void onHomeBtnSelected();
    }

    private View view;
    private fragmentBtnSelected listener;
    private MapView mapView;
    private TextView textView3;
    private GoogleMap mMap;

    private PlacesClient placesClient;
    private FusedLocationProviderClient flpClient;

    private LocationManager locationManager;
    private double latitude;
    private double longitude;

    private boolean locationPermissionGranted = false;
    private Location currentLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    // default location (sydney, AU)
    private LatLng latLng = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        flpClient = LocationServices.getFusedLocationProviderClient(getActivity());

        textView3 = (TextView) view.findViewById(R.id.textView3);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);
        mapView.onResume();

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        getLocationPermission();
        if(currentLocation != null) {
            latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if(context instanceof FragmentLogin.fragmentBtnSelected){
            listener = (FragmentMap.fragmentBtnSelected) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement listener");
        }
    }

    private void getCurrentLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = flpClient.getLastLocation();
                locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {
                        if(location != null){
                            currentLocation = location;
                            textView3.setText(String.valueOf(String.format(Locale.US, "%s -- %s", location.getLatitude(), location.getLongitude())));
                            mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    MarkerOptions options = new MarkerOptions().position(latLng).title("My location");
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                    googleMap.addMarker(options);
                                }
                            });
                        } else{
                            locationRequest = LocationRequest.create();
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            locationRequest.setInterval(20 * 1000);
                            locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    if (locationResult == null) {
                                        textView3.setText("Error in retrieving loc");
                                        return;
                                    }
                                    for (Location location : locationResult.getLocations()) {
                                        if (location != null) {
                                            latitude = location.getLatitude();
                                            longitude = location.getLongitude();

                                            textView3.setText(String.valueOf(String.format(Locale.US, "%s -- %s", latitude, longitude)));
                                        }
                                    }
                                }
                            };
                            flpClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                        }
                    }
                });
            } else{
                textView3.setText("No Permission");
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                currentLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            getCurrentLocation();
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    getCurrentLocation();
                }
            }
        }
        updateLocationUI();
    }


}
