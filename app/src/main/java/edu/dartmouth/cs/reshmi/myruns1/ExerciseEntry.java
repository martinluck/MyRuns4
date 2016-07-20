package edu.dartmouth.cs.reshmi.myruns1;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Helper class for creating a data structure to handle all the entries in a row of the
 * database as one object.
 *
 * @author Reshmi Suresh
 */
public class ExerciseEntry
{
    private Long id;

    private String mInputType;        // Manual, GPS or automatic
    private String mActivityType;     // Running, cycling etc.
    private Calendar mDateTime;    // When does this entry happen
    private int mDuration;         // Exercise duration in seconds
    private double mDistance;      // Distance traveled. Either in meters or feet.
    private double mAvgPace;       // Average pace
    private double mAvgSpeed;      // Average speed
    private double mCurrentSpeed;  // Current Speed
    private int mCalorie;          // Calories burnt
    private double mClimb;         // Climb. Either in meters or feet.
    private int mHeartRate;        // Heart rate
    private String mComment;       // Comments
    private ArrayList<LatLng> mLocationList; // Location list

    private Location mLastLocation;

    public ExerciseEntry() {
        this.mInputType = "";
        this.mActivityType = "";
        this.mDateTime = Calendar.getInstance();
        this.mDuration = 0;
        this.mDistance = 0;
        this.mAvgPace = 0;
        this.mAvgSpeed = 0;
        this.mCalorie = 0;
        this.mClimb = 0;
        this.mHeartRate = 0;
        this.mComment = "";
        mCurrentSpeed = -1;
        mLocationList = new ArrayList<LatLng>();

        mLastLocation = null;
    }

    public ExerciseEntry(Calendar date_and_time, int duration, double distance, int calories, int heart_rate, String input_type, String activity)
    {
        this.mInputType = input_type;
        this.mActivityType = activity;
        this.mDateTime = date_and_time;
        this.mDuration = duration;
        this.mDistance = distance;
        this.mCalorie = calories;
        this.mAvgPace = 0;
        this.mAvgSpeed = 0;
        this.mCalorie = 0;
        this.mClimb = 0;
        this.mHeartRate = heart_rate;
        this.mComment = "";
        mCurrentSpeed = -1;
        mLocationList = new ArrayList<LatLng>();

        mLastLocation = null;
    }

    public String getmInputType() {
        return mInputType;
    }

    public void setmInputType(String mInputType) {
        this.mInputType = mInputType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getmActivityType() {
        return mActivityType;
    }

    public void setmActivityType(String mActivityType) {
        this.mActivityType = mActivityType;
    }

    public Calendar getmDateTime() {
        return mDateTime;
    }

    public void setmDateTime(Calendar mDateTime) {
        this.mDateTime = mDateTime;
    }

    public int getmDuration() {
        return mDuration;
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public double getmDistance() {
        return mDistance;
    }

    public void setmDistance(double mDistance) {
        this.mDistance = mDistance;
    }

    public int getmCalorie() {
        return mCalorie;
    }

    public void setmCalorie(int mCalorie) {
        this.mCalorie = mCalorie;
    }

    public int getmHeartRate() {
        return mHeartRate;
    }

    public void setmHeartRate(int mHeartRate) {
        this.mHeartRate = mHeartRate;
    }

    public void updateDuration() {
        mDuration = (int) ((System.currentTimeMillis() - mDateTime
                .getTimeInMillis()) / 1000);

        if (mDuration != 0) {
            mAvgSpeed = mDistance / mDuration;
        }
    }

    public double getAvgPace() {
        return mAvgPace;
    }

    public void setAvgPace(double avgPace) {
        this.mAvgPace = avgPace;
    }

    public double getAvgSpeed() {
        return mAvgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.mAvgSpeed = avgSpeed;
    }


    public double getClimb() {
        return mClimb;
    }

    public void setClimb(double climb) {
        this.mClimb = climb;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }

    public ArrayList<LatLng> getLocationLatLngList() {
        return this.mLocationList;
    }

    // insert a new location to the trace
    // Synchronized method to maintain read/write lock.
    public synchronized void insertLocation(Location location) {
        mLocationList.add(new LatLng(location.getLatitude(), location
                .getLongitude()));

        if (mLastLocation == null) {
            setAvgSpeed(0);
            setClimb(0);
            setAvgSpeed(0);
            setClimb(0);
            setmDistance(0);
            setmCalorie(0);
        } else {
            mDistance += Math.abs(location.distanceTo(mLastLocation));
            mClimb += location.getAltitude() - mLastLocation.getAltitude();
            mCalorie = (int) (mDistance / 15.0);
        }

        updateDuration();
        mCurrentSpeed = location.getSpeed();
        mLastLocation = location;
    }

    public double getCurSpeed() {
        return mCurrentSpeed;
    }

    // Convert Location ArrayList to byte array, to store in SQLite database
    public byte[] getLocationByteArray() {
        int[] intArray = new int[mLocationList.size() * 2];

        for (int i = 0; i < mLocationList.size(); i++) {
            intArray[i * 2] = (int) (mLocationList.get(i).latitude * 1E6);
            intArray[(i * 2) + 1] = (int) (mLocationList.get(i).longitude * 1E6);
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length
                * Integer.SIZE);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(intArray);

        return byteBuffer.array();
    }

    // Convert byte array to Location ArrayList
    public void setLocationListFromByteArray(byte[] bytePointArray) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytePointArray);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();

        int[] intArray = new int[bytePointArray.length / Integer.SIZE];
        intBuffer.get(intArray);

        int locationNum = intArray.length / 2;

        for (int i = 0; i < locationNum; i++) {
            LatLng latLng = new LatLng((double) intArray[i * 2] / 1E6F,
                    (double) intArray[i * 2 + 1] / 1E6F);
            mLocationList.add(latLng);
        }
    }

}
