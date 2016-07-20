package edu.dartmouth.cs.reshmi.myruns1;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Original created by Fanglin Chen on 12/18/14.
 * Modified by Reshmi Suresh on 4/7/16 for MyRuns App
 */


public class MyRunsViewPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<Fragment> fragments;

    public static final int START = 0, HISTORY = 1, SETTINGS = 2;
    public static final String UI_TAB_START = "START", UI_TAB_HISTORY = "HISTORY", UI_TAB_SETTINGS = "SETTINGS";

    public MyRunsViewPagerAdapter(FragmentManager fragmentManager, ArrayList<Fragment> fragments)
    {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragments.get(position);
    }

    @Override
    public int getCount()
    {
        return fragments.size();
    }

    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case START: return UI_TAB_START;
            case HISTORY: return UI_TAB_HISTORY;
            case SETTINGS: return UI_TAB_SETTINGS;
            default: break;
        }
        return null;
    }
}
