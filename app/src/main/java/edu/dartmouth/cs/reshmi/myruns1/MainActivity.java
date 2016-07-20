package edu.dartmouth.cs.reshmi.myruns1;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import edu.dartmouth.cs.reshmi.myruns1.view.SlidingTabLayout;

/**
 * This is the Main Activity that gets called when the app opens.
 * It displays a tabbed view Activity where the user can slide between StartFragment,
 * HistoryFragment and SettingsFragment.
 *
 * @author Reshmi Suresh
 */
public class MainActivity extends Activity
{
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private MyRunsViewPagerAdapter myViewPagerAdapter;

    private HistoryFragment mHistoryTabFragment;
    private StartFragment mStartTabFragment;
    private SettingsFragment mSettingsTabFragment;


    /**
     * The onCreate method just initializes the different fragments and adds them
     * to the ViewPagerAdapter.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the SlidingTabLayout and ViewPager defined in main layout.
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create a fragment list of our three main fragments in order.
        mHistoryTabFragment = new HistoryFragment();
        mSettingsTabFragment = new SettingsFragment();
        mStartTabFragment = new StartFragment();

        fragments = new ArrayList<Fragment>();
        fragments.add(mStartTabFragment);
        fragments.add(mHistoryTabFragment);
        fragments.add(mSettingsTabFragment);

        // Use our custom sublcass of FragmentPagerAdapter to bind the SlidingTabLayout and
        // ViewPager together.
        myViewPagerAdapter = new MyRunsViewPagerAdapter(this.getFragmentManager(), fragments);
        viewPager.setAdapter(myViewPagerAdapter);

        // Equally space tabs
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

        // Refresh the HistoryFragment each time the page is selected
        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position){

                    case 1:
                        mHistoryTabFragment.updateHistoryEntries();
                        break;
                    default:
                        break;
                }            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
        });
    }
}
