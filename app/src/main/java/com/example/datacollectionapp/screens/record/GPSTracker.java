package com.example.datacollectionapp.screens.record;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class GPSTracker extends Service implements LocationListener {

    private static final int MIN_DISTANCE_CHANGE_FOR_UPDATE = 10;
    private static final long MIN_TIME_BETWEEN_UPDATES = 1000 * 15;

    protected LocationManager locationManager;

    private final Context context;
    private Location location;

    private boolean gpsEnabled = false;
    private boolean networkEnabled = false;
    private boolean canGetLocation = false;

    public GPSTracker(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    public Location getLocation() {
        if (location == null) {
            updateLocation();
        }
        return location;
    }
    public void updateLocation() {
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (canGetLocation && gpsEnabled) {
            Log.i(NewRecordActivity.TAG, "GPS enabled");
            try {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                    Log.i(NewRecordActivity.TAG, "Requesting location access permission from device");
                }

                Log.i(NewRecordActivity.TAG, "Retrieving location via GPS");
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BETWEEN_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATE,
                        this);

                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                Log.e(NewRecordActivity.TAG, e.getMessage());
            }
        }

        if (canGetLocation && networkEnabled && location == null) {
            Log.i(NewRecordActivity.TAG, "Network enabled");
            try {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                    Log.i(NewRecordActivity.TAG, "Requesting location access permission from device");
                }

                Log.i(NewRecordActivity.TAG, "Retrieving location via network");
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BETWEEN_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATE,
                        this);

                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                Log.e(NewRecordActivity.TAG, e.getMessage());
            }
        }
    }

    public void stopLocationUpdate() {
        locationManager.removeUpdates(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location = location;
        Log.i(NewRecordActivity.TAG, "Location changed");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {}

    public boolean canGetLocation() {
        gpsEnabled = false;
        networkEnabled = false;

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            gpsEnabled = true;
            canGetLocation = true;
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            networkEnabled = true;
            canGetLocation = true;
        }
        if (!gpsEnabled && !networkEnabled) {
            canGetLocation = false;
        }
        return canGetLocation;
    }

    public void showGPSSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Location settings");
        alertDialog.setMessage("Location access is not enabled. Turn on the option in settings");

        alertDialog.setPositiveButton("Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent);
        });

        alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        alertDialog.show();
    }
}
