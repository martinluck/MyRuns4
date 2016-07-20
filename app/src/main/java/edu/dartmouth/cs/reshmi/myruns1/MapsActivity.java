package edu.dartmouth.cs.reshmi.myruns1;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * MapsActivity displays the map with a tracker when the user is collecting data, and also
 * displays the previously collected data
 *
 * @author Reshmi Suresh
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private boolean mIsBound;
    private TrackingService mTrackingService;
    private Intent mServiceIntent;


    private Marker mStartMarker;
    private Marker mFinishMarker;
    private Polyline mLocationTrace;

    private ExerciseDataSource mDataSource;
    private ExerciseEntry mExerciseEntry;

    private boolean mIsNewTask;
    private TextView mTextType;
    private TextView mTextAvgSpeed;
    private TextView mTextCurSpeed;
    private TextView mTextClimb;
    private TextView mTextCalorie;
    private TextView mTextDistance;
    private boolean zoomOnce;
    private LocationUpdateReceiver mLocationUpdateReceiver;
    public class LocationUpdateReceiver extends BroadcastReceiver {
        //Receive broadcast that new location data has been addded
        @Override
        public void onReceive(Context ctx, Intent intent) {
            Log.e("onReceive", "onReceive");
            // update trace
            updatePath();
            // update exercise stats
            updatePathText();
        }
    }

    @Override
    protected void onDestroy (){
        super.onDestroy();
        //Check if orientation change or app close
        if (isFinishing()) {
            stopTrackingService();
        } else {
        }


    }
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        // when tracking service is connected, save the instance of service and
        // exercise entry
        public void onServiceConnected(ComponentName name, IBinder service) {
            TrackingService.TrackingServiceBinder binder = (TrackingService.TrackingServiceBinder) service;

            // save the service object
            mTrackingService = binder.getService();
            // save the exercise entry
            mExerciseEntry = binder.getExerciseEntry();
        }

        public void onServiceDisconnected(ComponentName name) {
            // stopService(mServiceIntent);
            mTrackingService = null;
        }
    };

    @Override
    protected void onPause() {

        if (mIsNewTask) {
            unregisterReceiver(mLocationUpdateReceiver);
        }
        // unbind the service
        doUnbindService();

        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getAndSetupMapIfNeeded();

        // init database
        mDataSource = new ExerciseDataSource(this);
        try {
            mDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // init broadcast receiver
        mLocationUpdateReceiver = new LocationUpdateReceiver();
        Intent i = getIntent();
        Bundle extras = i.getExtras();

        // finish itself if extras is null
        if (extras == null) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.cancelAll();

            finish();
            return;
        }

        if(extras.getString(Globals.EXTRA_TASK_TYPE).equals(Globals.TASK_TYPE_NEW)){
            mIsNewTask = true;
        } else {
            mIsNewTask = false;
        }

        mTextType = (TextView) findViewById(R.id.Type);
        mTextAvgSpeed = (TextView) findViewById(R.id.AvgSpeed);
        mTextCurSpeed = (TextView) findViewById(R.id.CurSpeed);
        mTextClimb = (TextView) findViewById(R.id.Climb);
        mTextCalorie = (TextView) findViewById(R.id.Calorie);
        mTextDistance = (TextView) findViewById(R.id.Distance);

        if (mIsNewTask) {
            String activityType = extras.getString(Globals.EXTRA_MESSAGE_ACTIVITY);

            startTrackingService(activityType);
        } else {
            ((Button) findViewById(R.id.buttonMapSave))
                    .setVisibility(View.GONE);
            ((Button) findViewById(R.id.buttonMapCancel))
                    .setVisibility(View.GONE);

            long rowid = extras.getLong(Globals.EXTRA_ROWID);
            // read Location trace from database
            try {
                mExerciseEntry = mDataSource.fetchEntryByIndex(rowid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void startTrackingService(String activityType) {
        mServiceIntent = new Intent(this, TrackingService.class);
        mServiceIntent.putExtra(Globals.EXTRA_MESSAGE_ACTIVITY, activityType);

        // start the service first. This is becase the OnStartCommand of service can be called only once. Hence we use that.
        startService(mServiceIntent);
        bindService(mServiceIntent, mServiceConnection,
                Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void stopTrackingService() {

        if (mTrackingService != null) {
            doUnbindService();
            stopService(mServiceIntent);
        }
    }

    private void doUnbindService() {
        if (mIsBound) {
            //unbind service
            unbindService(mServiceConnection);
            mIsBound = false;
        }
    }

    public void onSaveClicked(View v) {

        v.setEnabled(false);

        if (mExerciseEntry != null) {
            // update duration
            mExerciseEntry.updateDuration();
            // insert the entry to the database
            new AsyncTaskAdd().execute(mExerciseEntry);
        }

        // stop tracking service
        stopTrackingService();
        finish();
    }

    public void onCancelClicked(View v) {
        v.setEnabled(false);
        stopTrackingService();
        finish();
    }

    // we need to handle back button in here
    @Override
    public void onBackPressed() {
        stopTrackingService();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //IF history task, show delete button
        MenuItem menuitem;
        if (!mIsNewTask) {
            menuitem = menu.add(Menu.NONE, 0, 0,
                    "Delete");
            menuitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                if (mExerciseEntry != null) {
                    mDataSource.deleteExerciseEntry((int) (long) mExerciseEntry.getId());
                }
                finish();
                return true;
            default:
                finish();
                return false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // register the receiver for receiving the location update broadcast
        if (mIsNewTask) {
            IntentFilter intentFilter = new IntentFilter(
                    LocationUpdateReceiver.class.getName());
            registerReceiver(mLocationUpdateReceiver, intentFilter);
            updatePath();
            updatePathText();
        }



    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mLocationTrace = mMap.addPolyline(new PolylineOptions());
        updatePath();
        updatePathText();

    }

    private void updatePath() {
        if (mExerciseEntry == null
                || mExerciseEntry.getLocationLatLngList().size() == 0) {
            return;
        }

        // get the trace from mExerciseEntry
        ArrayList<LatLng> trace = mExerciseEntry.getLocationLatLngList();

        // get the start and end location
        LatLng begin = trace.get(0);
        LatLng end = trace.get(trace.size() - 1);

        //Draw start/stop markers and the trace
        if (mStartMarker == null) {
            mStartMarker = mMap.addMarker(new MarkerOptions().position(begin)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.green_mark)));
        }

        mLocationTrace.setPoints(trace);

        if (mFinishMarker != null) {
            mFinishMarker.setPosition(end);
        } else {
            mFinishMarker = mMap.addMarker(new MarkerOptions().position(end)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.red_mark)));
        }

        // Recenter the map to the end point, and Zoom in
        mMap.moveCamera(CameraUpdateFactory.newLatLng(end));
        if(!zoomOnce){
            mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
            zoomOnce=true;
        }
    }

    private void updatePathText() {
        if (mExerciseEntry == null) {
            return;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String avg_speed = "";
        String cur_speed = "";
        String climb = "";
        String distance = "";
        String type = "Type: "+(mExerciseEntry.getmActivityType());
        if(HistoryFragment.getIsKm(getApplicationContext())){
            avg_speed = "Avg speed: "
                    + decimalFormat.format(mExerciseEntry.getAvgSpeed() * 3600 / 1000) + " km/h";
            cur_speed = "Cur speed: "
                    + decimalFormat.format(mExerciseEntry.getCurSpeed()* 3600 / 1000) + " km/h";
            climb = "Climb: "
                    + decimalFormat.format(mExerciseEntry.getClimb()/1000) + " km" ;

            distance = "Distance: " + decimalFormat.format(mExerciseEntry.getmDistance()/1000) + " km";

        } else {
            avg_speed = "Avg speed: "
                    + decimalFormat.format((mExerciseEntry.getAvgSpeed() * 3600 / 1000 )/1.6)+ " miles/h";
            cur_speed = "Cur speed: "
                    + decimalFormat.format((mExerciseEntry.getCurSpeed()* 3600 / 1000)/1.6) + " miles/h";
            climb = "Climb: "
                    + decimalFormat.format(mExerciseEntry.getClimb()/1600) + " miles" ;

            distance = "Distance: " + decimalFormat.format(mExerciseEntry.getmDistance()/1600) + " miles";
        }
        String calorie = "Calorie: "
                + decimalFormat.format(mExerciseEntry.getmCalorie());
        mTextType.setText(type);
        mTextAvgSpeed.setText(avg_speed);
        mTextCurSpeed.setText(cur_speed);
        mTextClimb.setText(climb);
        mTextCalorie.setText(calorie);
        mTextDistance.setText(distance);
    }

    private void getAndSetupMapIfNeeded() {
        if (mMap == null) {
            Log.d(Globals.TAG, "Map is being setup");
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    private class AsyncTaskAdd extends AsyncTask<ExerciseEntry, Void, String>
    {
        @Override
        protected String doInBackground(ExerciseEntry... params)
        {
           //Write exercise entry object to the DB
            long id = mDataSource.createExerciseEntry(params[0]);

            return ""+id;

        }

        @Override
        protected void onPostExecute(String result)
        {
            //Make a toast notifying the user the id of the entry saved
            Toast.makeText(getApplicationContext(), "Entry #" + result + " saved.", Toast.LENGTH_SHORT).show();
        }
    }

}
