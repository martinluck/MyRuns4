package edu.dartmouth.cs.reshmi.myruns1;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * FetchEntriesLoader is the AsyncTask Loader class that loads the entries from the database
 *
 * @author Reshmi Suresh
 */

public class FetchEntriesLoader extends AsyncTaskLoader<List<ExerciseEntry>> {
    public Context mContext;

    public FetchEntriesLoader(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onStartLoading() {
        //Force start the loader everytime
        forceLoad();
    }

    @Override
    public List<ExerciseEntry> loadInBackground() {
        //Open the database
        ExerciseDataSource mDatastore = new ExerciseDataSource(this.mContext);
        try {
            mDatastore.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Call the method to return all entries and return it back to the calling fragment.
        return mDatastore.getAllEntries();

    }
}