package com.project_develop_team.managetransportation;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

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

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.project_develop_team.managetransportation.models.Tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;


public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private LocationAvailability locationAvailability;

    private DatabaseReference databaseReference;

    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;

    private Marker marker;

    private Polyline polyline;

    private Snackbar snackbar;

    private ProgressDialog progressDialog;

    private String refKey;

    private double taskDistance;

    public MapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
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

    private void handleRouteLocation(final Location location) {

        final LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        databaseReference.child("users-tasks").child(getUid()).limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (final DataSnapshot pointsSnapshot : dataSnapshots) {
                    final Tasks tasks = pointsSnapshot.getValue(Tasks.class);
                    final LatLng destination = new LatLng(tasks.latitude, tasks.longitude);

                    taskDistance = SphericalUtil.computeDistanceBetween(myLatLng, destination);

                    GoogleDirection.withServerKey(getString(R.string.server_key))
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
                                        marker = mMap.addMarker(new MarkerOptions().position(myLatLng).title("You are here")
                                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_pink)));
                                        mMap.addMarker(new MarkerOptions().position(destination).title(tasks.taskName)
                                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_azure)));

                                        ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                                        polyline = mMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
                                    }
                                }

                                @Override
                                public void onDirectionFailure(Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onLocationChanged(final Location location) {

        final LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        handleRouteLocation(location);

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (location != null) {
                    databaseReference.child("users-tasks").child(getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> dataSnapshotIterable = dataSnapshot.getChildren();
                            for (DataSnapshot pointsDataSnapshot : dataSnapshotIterable) {
                                Tasks tasks = pointsDataSnapshot.getValue(Tasks.class);
                                final LatLng destination = new LatLng(tasks.latitude, tasks.longitude);

                                refKey = pointsDataSnapshot.getKey();

                                taskDistance = SphericalUtil.computeDistanceBetween(myLatLng, destination);

                                Map<String, Object> updateChildren = new HashMap<>();
                                updateChildren.put("/users-tasks/" + getUid() + "/" + refKey + "/" + "taskDistance", taskDistance);

                                databaseReference.updateChildren(updateChildren);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
        handler.postDelayed(runnable, 5000);
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

        databaseReference.child("users-tasks").child(getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                mMap.clear();
                for (DataSnapshot pointsSnapshot : dataSnapshots) {
                    Tasks tasks = pointsSnapshot.getValue(Tasks.class);

                    mMap.addMarker(new MarkerOptions().position(new LatLng(tasks.latitude, tasks.longitude)).title(tasks.taskName)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_azure)));
                }
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
                .setAction("ตกลง", new View.OnClickListener() {
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
                    .setInterval(5000)
                    .setFastestInterval(3000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 14));
            loadMarker();
            snackbar.dismiss();
        } else {
            snackbar.show();
        }
    }

    public double formatDistance(double meters) {
        if (meters < 1000) {
            return ((int) meters);
        } else if (meters < 10000) {
            return formatDec(meters / 1000f, 3);
        } else {
            return ((int) (meters / 1000f));
        }
    }

    public double formatDec(double val, int dec) {
        int factor = (int) Math.pow(10, dec);
        int front = (int) (val);
        int back = (int) Math.abs(val * (factor)) % factor;

        return Double.parseDouble(front + "." + back);
    }
}
