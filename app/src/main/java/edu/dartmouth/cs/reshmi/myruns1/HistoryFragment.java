package edu.dartmouth.cs.reshmi.myruns1;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * History fragment that displays user activity history.
 *
 * @author Reshmi Suresh
 */

public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ExerciseEntry>>
{
    //Declaration of public static keys, that will be used as identifiers for the extras
    //passed in the intents
    public final static String INPUT = "input", ACTIVITY = "activity", DATE = "time",
            DURATION = "duration", DISTANCE = "distance", CALORIES = "calories", HEART_RATE = "heartrate", ID = "id";

    private static MyAdapter entryAdapter;

    private static ArrayList<HistoryFragmentItems> currentEntries;
    private static LoaderManager loaderManager;

    public static View myView;
    public static List<ExerciseEntry> entries;
    public static Context mAppContext;

    boolean unit;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentEntries = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_history, container, false);

        // Pass context and data to the custom adapter
        entryAdapter = new MyAdapter(getActivity(), currentEntries);

        // Get ListView from fragment_history.xml
        ListView listView = (ListView) myView.findViewById(R.id.listview);

        // setListAdapter
        listView.setAdapter(entryAdapter);

        // Define onClick events for the list entries
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //Pass all the fields of the selected entry in the List View, to be displayed
                //in the DsiplayEntryActivity
                if(entries.get(position).getmInputType().equals("Manual Entry")){
                    Intent intent = new Intent(getActivity(), DisplayEntryActivity.class);
                    intent.putExtra(ID, entries.get(position).getId());
                    intent.putExtra(INPUT, entries.get(position).getmInputType());
                    intent.putExtra(ACTIVITY, entries.get(position).getmActivityType());
                    intent.putExtra(DATE, getDate(entries.get(position).getmDateTime().getTimeInMillis(), "hh:mm:ss MMM dd yyyy"));
                    intent.putExtra(DURATION, entries.get(position).getmDuration()+"secs");
                    intent.putExtra(CALORIES, entries.get(position).getmCalorie()+" cals");
                    intent.putExtra(HEART_RATE, entries.get(position).getmHeartRate()+" bpm");
                    //Depending on the unit display in miles or kilometers
                    if(unit)
                        intent.putExtra(DISTANCE, (entries.get(position).getmDistance()*1.61)+" Kilometers");
                    else
                        intent.putExtra(DISTANCE, entries.get(position).getmDistance()+" Miles");

                    startActivity(intent);

                } else{
                    Intent intent = new Intent(getActivity().getBaseContext(), MapsActivity.class);
                    intent.putExtra(Globals.EXTRA_ROWID, entries.get(position).getId());
                    intent.putExtra(Globals.EXTRA_TASK_TYPE, Globals.TASK_TYPE_HISTORY);
                    startActivity(intent);

                }
            }
        });
        loaderManager = getActivity().getLoaderManager();

        mAppContext = getActivity();

        return myView;
    }

    /**
     * Formats the data into a title and description to be displayed in the List View
     * of the History Fragment
     * @param exEntries
     * @return ArrayList<HistoryFragmentItems>
     */
    private ArrayList<HistoryFragmentItems> generateData(List<ExerciseEntry> exEntries)
    {
        entries = exEntries;
        //Check whether Unit for the distance field is specified as Kilometer
        unit = getIsKm(mAppContext);

        ArrayList<HistoryFragmentItems> items = new ArrayList<HistoryFragmentItems>();
        String title, description;
        //Format the data for all entries in the database
        for(int i=0;i<entries.size();i++)
        {
            title = entries.get(i).getmInputType() + ": " + entries.get(i).getmActivityType() + ", "
                    + getDate(entries.get(i).getmDateTime().getTimeInMillis(), "hh:mm:ss MMM dd yyyy");

            DecimalFormat decimalFormat = new DecimalFormat("#.##");

            //Depending on the unit display in miles or kilometers
            if(unit)
                if(entries.get(i).getmInputType().equals("Manual Entry"))
                    description = (entries.get(i).getmDistance()*1.61) + " Kilometers, " + entries.get(i).getmDuration() + "secs";
                else
                    description = decimalFormat.format(entries.get(i).getmDistance()/1000) + " Kilometers, " + entries.get(i).getmDuration() + "secs";

            else
                if(entries.get(i).getmInputType().equals("Manual Entry"))
                    description = entries.get(i).getmDistance() + " Miles, " + entries.get(i).getmDuration() + "secs";
                else
                    description = decimalFormat.format(entries.get(i).getmDistance()/1600) + " Miles, " + entries.get(i).getmDuration() + "secs";

            items.add(new HistoryFragmentItems(title, description));
        }

        return items;
    }

    /**
     * Method to check the unit type in the preference fragment. It will return true if the unit is
     * metrics and false if it is in imeprial standards.
     * @param context
     * @return boolean
     */
    public static boolean getIsKm(Context context) {

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);

        String[] unit_display_options = context.getResources().getStringArray(
                R.array.entries_unit_preference);

        String option = settings.getString("list_preference",
                "Miles");

        String option_metric = context.getString(R.string.kilometers);
        if (option.equals(option_metric))
            return true;
        else
            return false;
    }

    /**
     * Converts date and time in milliseconds to the specified format
     * @param milliSeconds
     * @param dateFormat
     * @return String
     */
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public void onResume() {
        //Refresh the entries in the fragment every time the fragment is created or resumed
        updateHistoryEntries();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Method to update force refresh the list.
     */
    public void updateHistoryEntries() {
        loaderManager.initLoader(1, null, this).forceLoad();

    }


    // Methods implemented by the LoaderCallbacks.
    @Override
    public Loader<List<ExerciseEntry>> onCreateLoader(int id, Bundle args) {
        //Initialize a new loader as the FetchEntriesLoader
        return new FetchEntriesLoader(mAppContext);
    }

    @Override
    public void onLoadFinished(Loader<List<ExerciseEntry>> loader, List<ExerciseEntry> data) {
        //Called when the loader is done executing. The data is returned from the execution of the loader.
        currentEntries.clear();
        //Get the strings from the data and add them to the current items.
        currentEntries.addAll(generateData(data));
        //Notify the adapter that the current items have changed, hence it would update the list.
        entryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<ExerciseEntry>> loader) {
        //Clear all current items and notify the adapter.
        currentEntries.clear();
        entryAdapter.notifyDataSetChanged();
    }
}
