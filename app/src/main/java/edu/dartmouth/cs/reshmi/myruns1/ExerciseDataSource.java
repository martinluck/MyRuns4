package edu.dartmouth.cs.reshmi.myruns1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Helper class containing functions to insert, delete and view all records in the database.
 *
 * @author Reshmi Suresh
 */
public class ExerciseDataSource
{
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_INPUT_TYPE,
            MySQLiteHelper.COLUMN_ACTIVITY_TYPE,
            MySQLiteHelper.COLUMN_DATE_AND_TIME,
            MySQLiteHelper.COLUMN_DURATION,
            MySQLiteHelper.COLUMN_DISTANCE,
            MySQLiteHelper.COLUMN_CALORIES,
            MySQLiteHelper.COLUMN_AVG_PACE,
            MySQLiteHelper.COLUMN_AVG_SPEED,
            MySQLiteHelper.COLUMN_CLIMB,
            MySQLiteHelper.COLUMN_HEART_RATE,
            MySQLiteHelper.COLUMN_COMMENT,
            MySQLiteHelper.COLUMN_GPS_DATA};

    public ExerciseDataSource(Context context)
    {
        dbHelper = new MySQLiteHelper(context);
    }

    /**
     * Create and/or open a database that will be used for reading and writing
     * @throws SQLException
     */
    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Close any open database object
     */
    public void close()
    {
        dbHelper.close();
    }

    /**
     * Inserts the values passed to it into the database under the appropriate columns
     * @param entry
     * @return long
     */

    public long createExerciseEntry(ExerciseEntry entry){
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_INPUT_TYPE, entry.getmInputType());
        values.put(MySQLiteHelper.COLUMN_ACTIVITY_TYPE, entry.getmActivityType());
        values.put(MySQLiteHelper.COLUMN_DURATION, entry.getmDuration());
        values.put(MySQLiteHelper.COLUMN_DISTANCE, entry.getmDistance());
        values.put(MySQLiteHelper.COLUMN_CALORIES, entry.getmCalorie());
        values.put(MySQLiteHelper.COLUMN_HEART_RATE, entry.getmHeartRate());

        long dt = entry.getmDateTime().getTimeInMillis();
        values.put(MySQLiteHelper.COLUMN_DATE_AND_TIME, dt);
        values.put(MySQLiteHelper.COLUMN_AVG_SPEED, entry.getAvgSpeed());
        values.put(MySQLiteHelper.COLUMN_COMMENT, entry.getComment());


        byte[] byteLocations = entry.getLocationByteArray();
        if (byteLocations.length > 0) {
            values.put(MySQLiteHelper.COLUMN_GPS_DATA, byteLocations);
        }
        long insertID = database.insert(MySQLiteHelper.TABLE_EXERCISE, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_EXERCISE, allColumns,
                MySQLiteHelper.COLUMN_ID + " = " + insertID, null, null, null, null);
        cursor.moveToFirst();

        cursor.close();
        return insertID;
    }


    /**
     * Deletes the record of the specified id
     * @param id
     */
    public void deleteExerciseEntry(int id)
    {
        database.delete(MySQLiteHelper.TABLE_EXERCISE, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    /**
     * Returns all the entries in the database
     * @return List<ExerciseEntry>
     */
    public List<ExerciseEntry> getAllEntries()
    {
        List<ExerciseEntry> entries = new ArrayList<ExerciseEntry>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_EXERCISE, allColumns,
                null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            ExerciseEntry entry = cursorToEntry(cursor,false);
            entries.add(entry);
            cursor.moveToNext();
        }
        cursor.close();

        return entries;
    }

    public ExerciseEntry fetchEntryByIndex(long rowId) throws android.database.SQLException {
        ExerciseEntry entry = null;

        Cursor cursor = database.query(true, MySQLiteHelper.TABLE_EXERCISE, allColumns,
                MySQLiteHelper.COLUMN_ID + "=" + rowId, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            entry = cursorToEntry(cursor, true);
        }

        cursor.close();

        return entry;
    }
    /**
     * Sets the values of an instance of Exercise entry to the values in the corresponding
     * columns
     * @param cursor
     * @return ExerciseEntry
     */
    private ExerciseEntry cursorToEntry(Cursor cursor, boolean location)
    {

        ExerciseEntry entry = new ExerciseEntry();
        entry.setId(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
        entry.setmInputType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INPUT_TYPE)));
        entry.setmActivityType(cursor.getString(cursor
                .getColumnIndex(MySQLiteHelper.COLUMN_ACTIVITY_TYPE)));
        Calendar cal= GregorianCalendar.getInstance();
        cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_DATE_AND_TIME)));
        entry.setmDateTime(cal);
        entry.setmDuration(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_DURATION)));
        entry.setmDistance(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_DISTANCE)));
        entry.setAvgPace(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_AVG_PACE)));
        entry.setAvgSpeed(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_AVG_SPEED)));
        entry.setmCalorie(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CALORIES)));
        entry.setClimb(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CLIMB)));
        entry.setmHeartRate(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_HEART_RATE)));
        entry.setComment(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMMENT)));
        if (location) {
            byte[] byteTrack = cursor.getBlob(cursor
                    .getColumnIndex(MySQLiteHelper.COLUMN_GPS_DATA));
            entry.setLocationListFromByteArray(byteTrack);
        }
        return entry;
    }
}
