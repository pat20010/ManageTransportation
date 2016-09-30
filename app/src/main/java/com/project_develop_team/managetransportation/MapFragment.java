package com.project_develop_team.managetransportation;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.location.Location;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.Language;

import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.latTextView)
    TextView latTextView;
    @BindView(R.id.lonTextView)
    TextView lonTextView;

    LocationRequest locationRequest;

    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;

    Location mLocation;

    private Marker marker;

    private Polyline polyline;

    LocationAvailability locationAvailability;

    Snackbar snackbar;

    public MapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        snackbar.dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            Toast.makeText(getActivity(), "Has Google Play", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Error! No Google Play!!", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void handleNewLocation(Location location) {

        final LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        GoogleDirection.withServerKey("AIzaSyDK9lA1T5JogUKexBmbv0oP5fpKd3t5m84")
                .from(myLatLng)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .avoid(AvoidType.TOLLS)
                .avoid(AvoidType.HIGHWAYS)
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.INDOOR)
                .language(Language.THAI)
                .unit(Unit.METRIC)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {

                            if (marker != null && polyline != null) {
                                marker.remove();
                                polyline.remove();
                            }

                            marker = mMap.addMarker(new MarkerOptions().position(myLatLng).title("You are here"));
                            mMap.addMarker(new MarkerOptions().position(destination).title("DPU"));

                            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                            polyline = mMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;

        latTextView.setText(String.valueOf(mLocation.getLatitude()));
        lonTextView.setText(String.valueOf(mLocation.getLongitude()));
        handleNewLocation(location);
    }

    private LatLng destination = new LatLng(13.870302, 100.5485638);

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(getActivity(), "Connected to Google Play", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        snackbar = Snackbar.make(getView(), R.string.location_fail, Snackbar.LENGTH_INDEFINITE)
                .setAction("ตกลง", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

        locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setInterval(5000)
                    .setFastestInterval(2000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
            snackbar.dismiss();
        } else {
            snackbar.show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(),
                "Error! Cannot connect to Google Play",
                Toast.LENGTH_LONG).show();
    }
}