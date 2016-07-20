package edu.dartmouth.cs.reshmi.myruns1;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.Calendar;

/**
 * ManualInputActivity displays a listview of items which the user can then select
 * to enter his activity and health information manually.
 * On selecting a particular item, a dialog fragment would pop up asking the user to enter data
 *
 * @author Reshmi Suresh
 */
public class ManualInputActivity extends ListActivity implements AdapterView.OnItemClickListener
{
    static final String[] FIELDS = new String[]{"Date", "Time", "Duration",
            "Distance", "Calories", "Heart Rate", "Comment"};

    Button mSave, mCancel;
    ListView mListView;
    final Calendar mDateAndTime = Calendar.getInstance();

    ExerciseEntry entry;
    private ExerciseDataSource dataSource;

    //Temporary variables to store the value input in the dialog fragments
    int duration = 0, calories = 0, heart_rate = 0, new_year = -1, new_month, new_day, new_hour, new_minute;
    double distance = 0;
    String comment = "";
    String activity_type;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Create a new instance of ExerciseDataSource and open the writable database
        dataSource = new ExerciseDataSource(this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Get the item selected in the ActivityType spinner in the StartFragment
        Intent intent = getIntent();
        activity_type = intent.getStringExtra(Globals.EXTRA_MESSAGE_ACTIVITY);

        // Create an ArrayAdapter for the ListView and bind the layout's ListView to it.
        setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, FIELDS));
        setContentView(R.layout.activity_manual_input);

        mSave = (Button) findViewById(R.id.button8);
        mCancel = (Button) findViewById(R.id.button9);
        mListView = getListView();

        // Define onClick events for the list entries
        mListView.setOnItemClickListener(this);

        //Save button handler
        mSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Create an instance of ExerciseEntry with default values
                entry = new ExerciseEntry(mDateAndTime, 0, 0, 0, 0, "Manual Entry", "Running");

                //If time is input by the user, get it in Calendar data type
                if(new_year != -1)
                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(new_year, new_month, new_day,
                            new_hour, new_minute, 0);
                    entry.setmDateTime(calendar);
                }

                //Set values of other variables in the instance to values input by the user
                // if they exist
                entry.setmActivityType(activity_type);
                entry.setmDuration(duration);
                entry.setmDistance(distance);
                entry.setmCalorie(calories);
                entry.setmHeartRate(heart_rate);
                entry.setComment(comment);

                //Create an instance of AsyncTaskAdd to add the entry in the background
                AsyncTaskAdd addToDB = new AsyncTaskAdd();
                addToDB.execute(entry);

                finish();
            }
        });

        //Cancel button handler
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Entry discarded.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * ListView item click handler. The method checks for the item at the clicked position
     * and accordingly opens the dialog fragment required.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        String selected = (String) (mListView.getItemAtPosition(position));

        if (selected.equals("Date"))
        {
            //Create a new instance of MyAlerDialogFragment and load it.
            DialogFragment newFragment = MyAlertDialogFragment.newInstance();
            MyAlertDialogFragment.type_of_dialog = "Date";
            newFragment.show(getFragmentManager(), "dialog");
        }
        else if(selected.equals("Time"))
        {
            MyAlertDialogFragment.type_of_dialog = "Time";
            DialogFragment newFragment = MyAlertDialogFragment.newInstance();
            newFragment.show(getFragmentManager(), "dialog");
        }
        else if(selected.equals("Duration"))
        {
            MyAlertDialogFragment.type_of_dialog = "Duration";
            DialogFragment newFragment = MyAlertDialogFragment.newInstance();
            newFragment.show(getFragmentManager(), "dialog");
        }
        else if(selected.equals("Distance"))
        {
            MyAlertDialogFragment.type_of_dialog = "Distance";
            DialogFragment newFragment = MyAlertDialogFragment.newInstance();
            newFragment.show(getFragmentManager(), "dialog");
        }
        else if(selected.equals("Calories"))
        {
            MyAlertDialogFragment.type_of_dialog = "Calories";
            DialogFragment newFragment = MyAlertDialogFragment.newInstance();
            newFragment.show(getFragmentManager(), "dialog");
        }
        else if(selected.equals("Heart Rate"))
        {
            MyAlertDialogFragment.type_of_dialog = "Heart Rate";
            DialogFragment newFragment = MyAlertDialogFragment.newInstance();
            newFragment.show(getFragmentManager(), "dialog");
        }
        else if(selected.equals("Comment"))
        {
            MyAlertDialogFragment.type_of_dialog = "Comment";
            DialogFragment newFragment = MyAlertDialogFragment.newInstance();
            newFragment.show(getFragmentManager(), "dialog");
        }
    }


    /**
     * Positive click handler for all the dialogs. The method checks which dialog fragment
     * was opened and accordingly stores the data typed in that dialog in the corresponding
     * variable.
     * @param name_of_dialog
     * @param dialog_data
     */
    public void doDialogPositiveClick(String name_of_dialog, String dialog_data)
    {
        if(name_of_dialog.equals("Duration"))
        {

            duration = Integer.parseInt(dialog_data);
        }
        else if(name_of_dialog.equals("Distance"))
        {
            distance = Integer.parseInt(dialog_data);
        }
        else if(name_of_dialog.equals("Calories"))
        {
            calories = Integer.parseInt(dialog_data);
        }
        else if(name_of_dialog.equals("Heart Rate"))
        {
            heart_rate = Integer.parseInt(dialog_data);
        }
        else if(name_of_dialog.equals("Comment"))
        {
            comment = dialog_data;
        }
    }

    public void doDialogNegativeClick() {
    }

    /**
     * Date set handler. This method is invoked from the MyAlertDialogFragment when the
     * user sets the date.
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    public void onDateSet(int year, int monthOfYear, int dayOfMonth)
    {
        new_year = year;
        new_month = monthOfYear;
        new_day = dayOfMonth;
    }

    /**
     * Time set handler. This method is invoked from the MyAlertDialogFragment when the
     * user sets the time.
     * @param hourOfDay
     * @param minute
     */
    public void onTimeSet(int hourOfDay, int minute)
    {
        new_hour = hourOfDay;
        new_minute = minute;
    }

    /**
     * AsyncTaskAdd used to add entries to the database using a background thread.
     */
    private class AsyncTaskAdd extends AsyncTask<ExerciseEntry, Void, String>
    {
        @Override
        protected String doInBackground(ExerciseEntry... params)
        {
            //Create a new ExerciseEntry to store in the database with all the selected values
            //from the dialog fragments or the default values
            long id = dataSource.createExerciseEntry(params[0]);

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
