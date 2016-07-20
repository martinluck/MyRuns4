package edu.dartmouth.cs.reshmi.myruns1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.sql.SQLException;

/**
 * DisplayEntryActivity shows all the details of a particular run and also allows deletion of a run.
 * It reads from the database using AsyncTask and shows all the details like- Input type,
 * Activity type, date, duration, distance, calories and heart-rate.
 *
 * @author Reshmi Suresh
 */

public class DisplayEntryActivity extends AppCompatActivity
{
    private ExerciseDataSource dataSource;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_entry);

        //Create a new instance of ExerciseDataSource and open the writable database
        dataSource = new ExerciseDataSource(this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Get all the column values from the database for a ListView entry in the History fragment
        //from the intent
        Intent intent = getIntent();
        id = intent.getLongExtra(HistoryFragment.ID, -1);
        String input = intent.getStringExtra(HistoryFragment.INPUT);
        String activity = intent.getStringExtra(HistoryFragment.ACTIVITY);
        String date = intent.getStringExtra(HistoryFragment.DATE);
        String duration = intent.getStringExtra(HistoryFragment.DURATION);
        String distance = intent.getStringExtra(HistoryFragment.DISTANCE);
        String calories = intent.getStringExtra(HistoryFragment.CALORIES);
        String heart_rate = intent.getStringExtra(HistoryFragment.HEART_RATE);

        //Set the values obtained in the respective EditTexts
        EditText etInput = (EditText)findViewById(R.id.editText7);
        EditText etActivity = (EditText)findViewById(R.id.editText8);
        EditText etDate = (EditText)findViewById(R.id.editText9);
        EditText etDuration = (EditText)findViewById(R.id.editText10);
        EditText etDistance = (EditText)findViewById(R.id.editText11);
        EditText etCalories = (EditText)findViewById(R.id.editText12);
        EditText etHeartRate = (EditText)findViewById(R.id.editText13);

        etInput.setText(input);
        etActivity.setText(activity);
        etDate.setText(date);
        etDuration.setText(duration);
        etDistance.setText(distance);
        etCalories.setText(calories);
        etHeartRate.setText(heart_rate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Delete button handler to delete the currently viewed entry in the database
     * @param item
     * @return boolean
     */
    public boolean clickEvent(MenuItem item)
    {
        //Create an instance of AsyncTaskDelete to delete the entry in a background thread
        AsyncTaskDelete deleteFromDB = new AsyncTaskDelete();
        deleteFromDB.execute((int) id);
        finish();
        return true;
    }

    /**
     * AsyncTaskDelete to perform the deletion of an Exercise entry in the background.
     */
    private class AsyncTaskDelete extends AsyncTask<Integer, Void, Void>
    {
        @Override
        protected Void doInBackground(Integer... params)
        {
            dataSource.deleteExerciseEntry(params[0]);
            return null;
        }
    }
}
