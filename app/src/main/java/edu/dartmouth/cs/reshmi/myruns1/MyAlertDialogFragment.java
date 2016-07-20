package edu.dartmouth.cs.reshmi.myruns1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

/**
  * MyRunsDialogFragment handles all the customized dialog boxes in our project.
  * differentiated by the type_of_dialog string.
  * Ref: http://developer.android.com/reference/android/app/DialogFragment.html
 */

public class MyAlertDialogFragment extends DialogFragment
{
    public static String type_of_dialog = "";

    public static MyAlertDialogFragment newInstance()
    {
        //Initialize frag as a new MyAlertDialogFragment
        MyAlertDialogFragment frag = new MyAlertDialogFragment();
        return frag;
    }


    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        //Setup Custom dialogs based on the type_of_dialog parameter.

        //Case for Date
        if (type_of_dialog.equals("Date"))
        {
            final Calendar now;
            int year, month, day;
            //Get the current date and set it as the default date when the dialog shows up.
            now = Calendar.getInstance();
            year = now.get(Calendar.YEAR);
            month = now.get(Calendar.MONTH);
            day = now.get(Calendar.DAY_OF_MONTH);
            Log.d("TAGG20", ""+now.getTimeInMillis());

            return new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener()
            {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                {
                    ((ManualInputActivity) getActivity()).onDateSet(year, monthOfYear, dayOfMonth);
                }
            }, year, month, day);

        }
        //Case for time
        else if(type_of_dialog.equals("Time"))
        {
            final Calendar now;
            //Get the current time and set it as the default date when the dialog shows up.
            int hour, minute;
            now = Calendar.getInstance();
            hour = now.get(Calendar.HOUR_OF_DAY);
            minute = now.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                {
                    ((ManualInputActivity) getActivity()).onTimeSet(hourOfDay, minute);
                }
            }, hour, minute, false);
        }

        //For all the other cases of ManualActivity.
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder((getActivity()));

        alertDialogBuilder.setView(promptsView);

        // Add an EditText that would be common in all the dialogs.
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editText6);

        //Check if the type is 'Comment' then change the keyboard type to QWERTY else
        // leave it as Numeric.
        if (type_of_dialog.equals("Comment"))
        {
            userInput.setInputType(InputType.TYPE_CLASS_TEXT);
            userInput.setHint("How did it go? Notes here.");
        }
        else
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        alertDialogBuilder.setTitle(type_of_dialog);
        //Set the positive and negative buttons, for OK and cancel.
        alertDialogBuilder
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                ((ManualInputActivity) getActivity()).doDialogPositiveClick(type_of_dialog, userInput.getText().toString());
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((ManualInputActivity) getActivity()).doDialogNegativeClick();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();

        return alertDialog;

    }
}