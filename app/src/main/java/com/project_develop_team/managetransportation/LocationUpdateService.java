package com.project_develop_team.managetransportation;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project_develop_team.managetransportation.models.Tasks;

public class LocationUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static Boolean mRequestingLocationUpdates;

    private DatabaseReference databaseReference;

    private LocationRequest locationRequest;

    private GoogleApiClient mGoogleApiClient;

    public LocationUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mRequestingLocationUpdates = false;
        buildGoogleApiClient();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        return Service.START_REDELIVER_INTENT;
    }

    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createLocationRequest();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void createLocationRequest() {
        mGoogleApiClient.connect();
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setSmallestDisplacement(50)
                .setInterval(60000 * 5);
    }

    public void startLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
    }

    public void stopLocationUpdates() {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(final Location location) {
        final LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        databaseReference.child(getString(R.string.firebase_users)).child(getUid()).child(getString(R.string.firebase_latitude)).setValue(location.getLatitude());
        databaseReference.child(getString(R.string.firebase_users)).child(getUid()).child(getString(R.string.firebase_longitude)).setValue(location.getLongitude());

        databaseReference.child(getString(R.string.firebase_users_tasks)).child(getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Tasks tasks = dataSnapshot.getValue(Tasks.class);
                final LatLng destination = new LatLng(tasks.task_latitude_collect, tasks.task_longitude_collect);

                final String refKey = dataSnapshot.getKey();

                GoogleDirection.withServerKey(getString(R.string.server_key))
                        .from(myLatLng)
                        .to(destination)
                        .transportMode(TransportMode.DRIVING)
                        .avoid(AvoidType.TOLLS)
                        .avoid(AvoidType.HIGHWAYS)
                        .avoid(AvoidType.FERRIES)
                        .avoid(AvoidType.INDOOR)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                for (int i = 0; i < direction.getRouteList().size(); i++) {
                                    double taskDistance = Double.parseDouble(direction.getRouteList().get(i).getLegList().get(i).getDistance().getValue());

                                    if (tasks.task_date == 20170325) {
                                        double taskDistanceTomorrow = taskDistance * 2;
                                        databaseReference.child(getString(R.string.firebase_users_tasks)).child(getUid()).child(refKey).child(getString(R.string.firebase_task_average)).setValue(taskDistanceTomorrow);
                                    } else {
                                        databaseReference.child(getString(R.string.firebase_users_tasks)).child(getUid()).child(refKey).child(getString(R.string.firebase_task_average)).setValue(taskDistance);
                                    }
                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {

                            }
                        });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}