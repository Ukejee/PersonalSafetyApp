package com.ukejee.das;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ukejee.das.databinding.ActivityNearestPoliceStationBinding;
import com.ukejee.das.models.Spot;
import com.ukejee.das.models.google.GoogleNearbySearchResponse;
import com.ukejee.das.models.google.ResultsItem;
import com.ukejee.das.web.AppRepository;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearestPoliceStationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private final int PERMISSION_ID = 1;
    private Dialog mProgressDialog;
    private FusedLocationProviderClient fusedLocationClient;
    private ArrayList<MarkerOptions> spotMarkerList = new ArrayList<>();
    private MarkerOptions userCurrentLocationMarkerOptions;
    private ActivityNearestPoliceStationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNearestPoliceStationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setUpUi(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(6.5764, 3.3653), 10.5f));
    }

    private void setUpUi(final Activity activity){
        mProgressDialog = new Dialog(this);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.findPoliceStationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    if(isLocationEnabled()){
                        showProgressDialog();
                        getUserLocation();
                    }
                    else{
                        showAlert("Please  enable your location and try again");
                    }

                }
                else{
                    requestPermission();
                }
            }
        });

    }

    private void getUserLocation(){

        try{
            Task<Location> locationResult = fusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull final Task<Location> task) {
                    if(task.isSuccessful()){
                        final Location location = task.getResult();
                        userCurrentLocationMarkerOptions = new MarkerOptions()
                                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                .anchor(0.5f, 0.5f)
                                .title("Your current location");

                        getNearestPoliceStation(location);

                    }
                    else{
                        cancelProgressDialog();
                        showAlert("Something went wrong");
                    }
                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
            showAlert("Something went wrong");
        }
    }

    private void getNearestPoliceStation(final Location userLocation){
        AppRepository repository = new AppRepository();
        String location = userLocation.getLatitude() + "," + userLocation.getLongitude();
        repository.getGoogleMapsApiService().getNearbySearch(getString(R.string.google_maps_key),
                location,"2000", "police")
                .enqueue(new Callback<GoogleNearbySearchResponse>() {
                    @Override
                    public void onResponse(Call<GoogleNearbySearchResponse> call, Response<GoogleNearbySearchResponse> response) {

                        if(response.isSuccessful()){
                            final ArrayList<Spot> hospitals = new ArrayList<>();

                            for(ResultsItem resultsItem : response.body().getResults()){
                                hospitals.add(new Spot(resultsItem.getName(),
                                        resultsItem.getGeometry().getLocation().getLat(),
                                        resultsItem.getGeometry().getLocation().getLng()));
                            }

                            final ArrayList<Marker> markerList = new ArrayList<>();
                            cancelProgressDialog();

                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {

                                    for(MarkerOptions markerOptions: getSpotMarkerOption(hospitals)){
                                        googleMap.addMarker(markerOptions);
                                        markerList.add(googleMap.addMarker(markerOptions));
                                    }
                                    googleMap.animateCamera(autoZoomLevel(markerList));
                                }
                            });

                        }
                    }

                    @Override
                    public void onFailure(Call<GoogleNearbySearchResponse> call, Throwable t) {
                        cancelProgressDialog();
                        showAlert("Something went wrong");
                    }
                });
    }

    private ArrayList<MarkerOptions> getSpotMarkerOption(ArrayList<Spot> spotList) {

        for (Spot spot : spotList) {
            String name = spot.getName();
            double latitude = spot.getLat();
            double longitude = spot.getLng();
            LatLng latLng = new LatLng(latitude, longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng).title(name).anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(this, R.drawable.ic_hospital_pin)));
            spotMarkerList.add(markerOptions);
        }
        spotMarkerList.add(userCurrentLocationMarkerOptions);

        return spotMarkerList;
    }




    private CameraUpdate autoZoomLevel(ArrayList<Marker> markerList ){
        if (markerList.size() == 1) {
            double latitude = markerList.get(0).getPosition().latitude;
            double longitude = markerList.get(0).getPosition().longitude;
            LatLng latLng = new LatLng(latitude, longitude);

            return CameraUpdateFactory.newLatLngZoom(latLng, 13f);
        } else {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markerList) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            int padding = 200; // offset from edges of the map in pixels

            return CameraUpdateFactory.newLatLngBounds(bounds, padding);
        }
    }


    private void requestPermission(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_ID){

        }
    }

    private Boolean isLocationEnabled(){
        LocationManager locationManager =  (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert(String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error!");
            builder.setMessage(message);
            builder.setPositiveButton("OK", null);
            builder.create();
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showProgressDialog(){
        if (mProgressDialog.isShowing()){ cancelProgressDialog(); }
        mProgressDialog.setContentView(R.layout.progress_bar);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
    }

    private void cancelProgressDialog(){
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
