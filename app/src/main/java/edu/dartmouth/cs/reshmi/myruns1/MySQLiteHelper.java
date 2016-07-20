package edu.dartmouth.cs.reshmi.myruns1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * MySQLiteHelper used to create and upgrade the database
 *
 * @author Reshmi Suresh
 */
public class MySQLiteHelper extends SQLiteOpenHelper
{
    //Initialization of string that stores the table name
    public static final String TABLE_EXERCISE = "exercise";

    //Initialization of strings that store the column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_INPUT_TYPE = "input";
    public static final String COLUMN_ACTIVITY_TYPE = "activity";
    public static final String COLUMN_DATE_AND_TIME = "dateandtime";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_HEART_RATE = "heartrate";
    public static final String COLUMN_AVG_PACE = "avg_pace";
    public static final String COLUMN_AVG_SPEED = "avg_speed";
    public static final String COLUMN_CLIMB = "climb";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_GPS_DATA = "gps_data";

    //Initialization of strings that store the database name and version number
    public static final String DATABASE_NAME = "exercise";
    public static final int DATABASE_VERSION = 5;

    //Initialization of string that has the query to create the table with specified column types
    public static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_EXERCISE + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_INPUT_TYPE + " TEXT NOT NULL, " + COLUMN_ACTIVITY_TYPE + " TEXT NOT NULL, " + COLUMN_DATE_AND_TIME
            + " DATETIME not null, " + COLUMN_DURATION + " INTEGER, " + COLUMN_DISTANCE + " FLOAT, " + COLUMN_CALORIES + " INTEGER, " + COLUMN_HEART_RATE + " INTEGER, "
            + COLUMN_AVG_PACE + " INTEGER, " + COLUMN_AVG_SPEED + " INTEGER, " + COLUMN_CLIMB + " INTEGER, " + COLUMN_COMMENT + " TEXT, " + COLUMN_GPS_DATA + " BLOB " + ");";

    public MySQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //Create the table using the string containing the create table query
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Delete table if the table version has changed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        //Create a new table for the current version
        onCreate(db);
    }
}
