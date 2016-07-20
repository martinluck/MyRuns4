package edu.dartmouth.cs.reshmi.myruns1;

/**
 * MyAdapter creates the custom ListView for the History fragment
 *
 * @author Reshmi Suresh
 */
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyAdapter extends ArrayAdapter<HistoryFragmentItems> {

    private final Context context;
    private final ArrayList<HistoryFragmentItems> itemsArrayList;

    public MyAdapter(Context context, ArrayList<HistoryFragmentItems> itemsArrayList) {

        super(context, R.layout.custom_row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get rowView from inflater
        View rowView = inflater.inflate(R.layout.custom_row, parent, false);

        // Get the two text view from the rowView
        TextView labelView = (TextView) rowView.findViewById(R.id.label);
        TextView valueView = (TextView) rowView.findViewById(R.id.value);

        // Set the text for textView
        labelView.setText(itemsArrayList.get(position).getTitle());
        valueView.setText(itemsArrayList.get(position).getDescription());

        return rowView;
    }
}
