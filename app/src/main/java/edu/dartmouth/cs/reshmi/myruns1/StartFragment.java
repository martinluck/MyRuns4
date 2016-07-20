package edu.dartmouth.cs.reshmi.myruns1;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * Start fragment that allows user to choose input type and activity type and start activity entry.
 *
 * @author Reshmi Suresh
 */
public class StartFragment extends Fragment
{
    Button mStart, mSync;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_start, container, false);
        mStart = (Button) myView.findViewById(R.id.button4);
        mSync = (Button) myView.findViewById(R.id.button5);
        final Spinner mInputType = (Spinner) myView.findViewById(R.id.spinner);
        final Spinner mActivityType = (Spinner) myView.findViewById(R.id.spinner2);

        //Check if the spinner item is Manual Entry, or Map based entry and open the
        //corresponding Activity.
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (mInputType.getSelectedItem().toString().equals("Manual Entry"))
                {
                    //Send the item selected in the Activity Type spinner to ManualInputActivity
                    //to store in the database
                    Intent intent = new Intent(getActivity().getBaseContext(), ManualInputActivity.class);
                    intent.putExtra(Globals.EXTRA_MESSAGE_ACTIVITY, mActivityType.getSelectedItem().toString());
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(getActivity().getBaseContext(), MapsActivity.class);
                    String activity_type = mActivityType.getSelectedItem().toString();
                    if(mInputType.getSelectedItem().toString().equals("Automatic"))
                        activity_type="unknown";
                    intent.putExtra(Globals.EXTRA_MESSAGE_ACTIVITY, activity_type);
                    intent.putExtra(Globals.EXTRA_MESSAGE_INPUT, mInputType.getSelectedItem().toString());
                    intent.putExtra(Globals.EXTRA_TASK_TYPE, Globals.TASK_TYPE_NEW);

                    startActivity(intent);
                }
            }
        });
        return myView;
    }
}
