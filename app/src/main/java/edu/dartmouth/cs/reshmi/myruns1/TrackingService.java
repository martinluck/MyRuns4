package edu.dartmouth.cs.reshmi.myruns1;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class TrackingService extends Service implements LocationListener {
    private ExerciseEntry mEntry;
    private final IBinder binder = new TrackingServiceBinder();
    private int mInputType;
    private LocationManager mLocationManager;
    private boolean mIsStarted;
    private NotificationManager mNotificationManager;

    @Override
    public void onLocationChanged(Location location) {
        if (mEntry != null && location!=null) {
            //On location updates, add new location to exercise entry object and send broadcast
            //for the mapsactivity to draw the trace.
            mEntry.insertLocation(location);
            sendUpdate();
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public class TrackingServiceBinder extends Binder {
        public ExerciseEntry getExerciseEntry() {
            return mEntry;
        }

        TrackingService getService() {
            return TrackingService.this;
        }

    }

    @Override
    public void onCreate() {
        mIsStarted = false;

        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        start(intent);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        start(intent);
        return binder;
    }

    @Override
    public void onDestroy() {


        cancelLocationUpdates();
        mNotificationManager.cancelAll();
        mIsStarted = false;
        super.onDestroy();
    }

    private void start(Intent intent) {
        if (mIsStarted) {
            return;
        }
        mIsStarted = true;

        initializeNotification();

        startLocationUpdate();

        mEntry = new ExerciseEntry();
        mEntry.setmActivityType(intent.getExtras().getString(Globals.EXTRA_MESSAGE_ACTIVITY));
        if (mEntry.getmActivityType().equals("unknown")) {
            mEntry.setmInputType("Automatic");
        } else {
            mEntry.setmInputType("GPS");
        }
    }

    private void initializeNotification() {
        Intent mapDisplayIntent = new Intent(getApplicationContext(), MainActivity.class);
        mapDisplayIntent.setAction(Intent.ACTION_MAIN);
        mapDisplayIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mapDisplayIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent mapDisplayPendingIntent = PendingIntent.getActivity(getBaseContext(),
                0, mapDisplayIntent, 0);

        // Setup and show notification
        Notification trackNotification = new Notification.Builder(this)
                .setContentTitle("MyRuns")
                .setContentText("Recording your path now. Click to view.")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(mapDisplayPendingIntent)
                .build();

        // Set the flags to signify ongoing event
        trackNotification.flags |= Notification.FLAG_ONGOING_EVENT;

        // Start notification
        mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(5, trackNotification);
    }

    private void startLocationUpdate() {

        // Setup criteria for location manager
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = mLocationManager.getBestProvider(criteria, true);
        Log.d(Globals.TAG, "Provider: " + provider);

        // Auto-generated permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // Start requesting location updates
        mLocationManager.requestLocationUpdates(provider, 0, 0, TrackingService.this);
    }
    private void sendUpdate() {
        Intent intent = new Intent(
                MapsActivity.LocationUpdateReceiver.class.getName());
        intent.putExtra("update", true);
        this.sendBroadcast(intent);

    }
    private void cancelLocationUpdates() {
        // Auto-generated permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.removeUpdates(this);
    }
}
