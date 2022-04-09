package com.example.datacollectionapp.screens.record.viewholder;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.models.RecordField;
import com.example.datacollectionapp.screens.record.GPSTracker;
import com.example.datacollectionapp.screens.record.NewRecordActivity;

import java.util.List;

public class LocationRecordViewHolder extends RecordViewHolder {
    private Button buttonCurrentLocation;
    private Button buttonPickLocation;
    private EditText latitude;
    private EditText longitude;
    private GPSTracker gpsTracker;
    private LinearLayout linearLayout;


    public LocationRecordViewHolder(@NonNull View itemView, Context context, List<RecordField> recordFields) {
        super(itemView, itemView.findViewById(R.id.textFieldName), context, recordFields);
        buttonCurrentLocation = itemView.findViewById(R.id.buttonCurrentLocation);
        buttonPickLocation = itemView.findViewById(R.id.buttonPickLocation);
        linearLayout = itemView.findViewById(R.id.linearLayout);
        latitude = itemView.findViewById(R.id.editLatitude);
        longitude = itemView.findViewById(R.id.editLongitude);
        gpsTracker = new GPSTracker(context);
        buttonCurrentLocation.setOnClickListener(this::setCurrentLocation);
    }

    public void setCurrentLocation(View view) {
        if (gpsTracker.canGetLocation()) {
            Log.i(NewRecordActivity.TAG, "Starting to retrieve location");
            Location location = gpsTracker.getLocation();
            if (location != null) {
                String lat = Location.convert(location.getLatitude(), Location.FORMAT_DEGREES);
                String lon = Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
                latitude.setText(lat);
                longitude.setText(lon);
                RecordField recordField = getFieldAtPosition(getAdapterPosition());
                recordField.setValue(lat + "," + lon);
                Log.i(NewRecordActivity.TAG, "Setting location field on record complete");
            } else {
                Toast.makeText(getContext(), "Couldn't receive location! Please try again", Toast.LENGTH_SHORT).show();
                Log.e(NewRecordActivity.TAG, "Couldn't retrieve location using location manager");
            }
        } else {
            gpsTracker.showGPSSettingsAlert();
            Log.i(NewRecordActivity.TAG, "GPS not enabled on the device");
        }
    }

    public void stopLocationUpdates() {
        gpsTracker.stopLocationUpdate();
    }

    public EditText getLatitude() {
        return latitude;
    }

    public EditText getLongitude() {
        return longitude;
    }

    public void hideButtons() {
        buttonCurrentLocation.setVisibility(View.GONE);
        buttonPickLocation.setVisibility(View.GONE);
    }

}
