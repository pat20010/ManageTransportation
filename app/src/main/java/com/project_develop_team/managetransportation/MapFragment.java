package com.project_develop_team.managetransportation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project_develop_team.managetransportation.models.Tasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LocationAvailability locationAvailability;

    private DatabaseReference databaseReference;

    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;

    private Snackbar snackbar;

    private ProgressDialog progressDialog;

    Marker marker;

    Map<String, Marker> markers;

    String todayDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getContext().startService(new Intent(getContext(), LocationUpdateService.class));

        SimpleDateFormat currentDateFormat = new SimpleDateFormat(getString(R.string.date_tasks_format), Locale.getDefault());
        todayDate = currentDateFormat.format(new Date());
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
    public void onDestroy() {
        super.onDestroy();
        getContext().stopService(new Intent(getContext(), LocationUpdateService.class));
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
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        } else {
            Toast.makeText(getActivity(), R.string.no_google_play, Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        final LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (marker != null) {
            marker.remove();

            marker = mMap.addMarker(new MarkerOptions().position(myLatLng).title(getString(R.string.you_are_here))
                    .flat(true).anchor(0.6f, 0.6f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_vespa)));
        }
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(myLatLng).title(getString(R.string.you_are_here))
                    .flat(true).anchor(0.6f, 0.6f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_vespa)));
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onConnected(Bundle bundle) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);

        locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        mMap.setMyLocationEnabled(true);

        if (!locationAvailability.isLocationAvailable()) {
            progressDialog.show();

            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    requestLocationAvailability();

                    progressDialog.dismiss();
                }
            };
            handler.postDelayed(runnable, 2000);
        } else {
            requestLocationAvailability();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(),
                R.string.not_connect_google_play,
                Toast.LENGTH_LONG).show();
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void loadMarker() {
        databaseReference.child(getString(R.string.firebase_users_tasks)).child(getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Tasks tasks = dataSnapshot.getValue(Tasks.class);
                markers = new HashMap<>();

                double taskLatCollect = Double.parseDouble(tasks.task_latitude_collect);
                double taskLongCollect = Double.parseDouble(tasks.task_longitude_collect);

                double taskLatDeliver = Double.parseDouble(tasks.task_latitude_deliver);
                double taskLongDeliver = Double.parseDouble(tasks.task_longitude_deliver);

                if (!(tasks.task_latitude_collect.equals("0") && tasks.task_longitude_collect.equals("0")) && tasks.task_date.equals(todayDate)) {
                    final Marker taskMarkerCollect = mMap.addMarker(new MarkerOptions().position(new LatLng(taskLatCollect, taskLongCollect))
                            .title(tasks.task_name_collect)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_azure)));

                    markers.put(dataSnapshot.getKey(), taskMarkerCollect);

                }
                if (tasks.task_latitude_collect.equals("0") && tasks.task_longitude_collect.equals("0") && tasks.task_date.equals(todayDate)) {
                    Marker taskMarkerDeliver = mMap.addMarker(new MarkerOptions().position(new LatLng(taskLatDeliver, taskLongDeliver))
                            .title(tasks.task_name_deliver)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_pink)));

                    markers.put(dataSnapshot.getKey(), taskMarkerDeliver);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final Tasks tasks = dataSnapshot.getValue(Tasks.class);
                markers = new HashMap<>();

                double taskLatCollect = Double.parseDouble(tasks.task_latitude_collect);
                double taskLongCollect = Double.parseDouble(tasks.task_longitude_collect);

                double taskLatDeliver = Double.parseDouble(tasks.task_latitude_deliver);
                double taskLongDeliver = Double.parseDouble(tasks.task_longitude_deliver);

                if (!(tasks.task_latitude_collect.equals("0") && tasks.task_longitude_collect.equals("0")) && tasks.task_date.equals(todayDate)) {
                    final Marker taskMarkerCollect = mMap.addMarker(new MarkerOptions().position(new LatLng(taskLatCollect, taskLongCollect))
                            .title(tasks.task_name_collect)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_azure)));

                    markers.put(dataSnapshot.getKey(), taskMarkerCollect);

                }
                if (tasks.task_latitude_collect.equals("0") && tasks.task_longitude_collect.equals("0") && tasks.task_date.equals(todayDate)) {
                    Marker taskMarkerDeliver = mMap.addMarker(new MarkerOptions().position(new LatLng(taskLatDeliver, taskLongDeliver))
                            .title(tasks.task_name_deliver)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_pink)));

                    markers.put(dataSnapshot.getKey(), taskMarkerDeliver);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final Tasks tasks = dataSnapshot.getValue(Tasks.class);
                markers = new HashMap<>();

                double taskLatCollect = Double.parseDouble(tasks.task_latitude_collect);
                double taskLongCollect = Double.parseDouble(tasks.task_longitude_collect);

                double taskLatDeliver = Double.parseDouble(tasks.task_latitude_deliver);
                double taskLongDeliver = Double.parseDouble(tasks.task_longitude_deliver);

                if (!(tasks.task_latitude_collect.equals("0") && tasks.task_longitude_collect.equals("0")) && tasks.task_date.equals(todayDate)) {
                    final Marker taskMarkerCollect = mMap.addMarker(new MarkerOptions().position(new LatLng(taskLatCollect, taskLongCollect))
                            .title(tasks.task_name_collect)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_azure)));

                    markers.put(dataSnapshot.getKey(), taskMarkerCollect);

                }
                if (tasks.task_latitude_collect.equals("0") && tasks.task_longitude_collect.equals("0") && tasks.task_date.equals(todayDate)) {
                    Marker taskMarkerDeliver = mMap.addMarker(new MarkerOptions().position(new LatLng(taskLatDeliver, taskLongDeliver))
                            .title(tasks.task_name_deliver)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_pink)));

                    markers.put(dataSnapshot.getKey(), taskMarkerDeliver);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void requestLocationAvailability() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        snackbar = Snackbar.make(getView(), R.string.location_fail, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

        locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (locationAvailability.isLocationAvailable()) {
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setSmallestDisplacement(100)
                    .setInterval(50000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 12));
            loadMarker();
            snackbar.dismiss();
        } else {
            snackbar.show();
        }
    }
}
