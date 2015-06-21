package nl.tue.hti.g33.thermostat;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.common.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import nl.tue.hti.g33.thermostat.utils.Thermostat;
import nl.tue.hti.g33.thermostat.utils.ThermostatProvider;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class MainFragment extends Fragment implements ThermostatProvider {

    private static HomeFragment home = null;
    private static WeekScheduleFragment weekSchedule = null;
    private static VacationModeFragment vacationMode = null;

    @Override
    public Thermostat provideThermostat() {

        return mThermostat;
    }

    static class TabPagerItem {

        private final CharSequence mTitle;
        private final int mIndicatorColor;
        private final int mDividerColor;
        private Fragment mContext;

        TabPagerItem(CharSequence title, int indicatorColor, int dividerColor, Fragment context) {

            mTitle = title;
            mIndicatorColor = indicatorColor;
            mDividerColor = dividerColor;
            mContext = context;
        }

        Fragment createFragment() {

            if (mTitle == mContext.getString(R.string.tab_home)) {
                if (home == null)
                    return (home = new HomeFragment());
                return home;
            }
            if (mTitle == mContext.getString(R.string.tab_week_schedule)) {
                if (weekSchedule == null)
                    return (weekSchedule = new WeekScheduleFragment());
                return weekSchedule;
            }
            if (mTitle == mContext.getString(R.string.tab_vacation_mode)) {
                if (vacationMode == null)
                    return (vacationMode = new VacationModeFragment());
                return vacationMode;
            }
            return null;
        }

        CharSequence getTitle() {

            return mTitle;
        }

        int getIndicatorColor() {

            return mIndicatorColor;
        }

        int getDividerColor() {

            return mDividerColor;
        }
    }

    private SlidingTabLayout mSlidingTabLayout;

    private ViewPager mViewPager;

    private List<TabPagerItem> mTabs = new ArrayList<>();

    private Thermostat mThermostat;

    private static final String LOG_TAG = "MainFragment";

    public MainFragment() {

        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            mThermostat = ((ThermostatProvider) getActivity()).provideThermostat();
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "Context must implement ThermostatProvider interface!");
            throw new IllegalArgumentException(LOG_TAG + "Initialisation failed due to" +
                    "context not implementing ThermostatProvider.");
        }

        mTabs.add(new TabPagerItem(
                getString(R.string.tab_home), // Title
                Color.BLUE, // Indicator color
                Color.GRAY, // Divider color
                this
        ));

        mTabs.add(new TabPagerItem(
                getString(R.string.tab_week_schedule), // Title
                Color.rgb(0x22, 0x8b, 0x22), // Indicator color
                Color.GRAY, // Divider color
                this
        ));

        mTabs.add(new TabPagerItem(
                getString(R.string.tab_temperatures), // Title
                Color.YELLOW, // Indicator color
                Color.GRAY, // Divider color
                this
        ));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPager.setAdapter(new TabsFragmentPagerAdapter(getChildFragmentManager()));

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        // Set a TabColorizer to customize the indicator and divider colors. Here we just retrieve
        // the tab at the position, and return it's set color
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {

                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {

                return mTabs.get(position).getDividerColor();
            }

        });
    }

    class TabsFragmentPagerAdapter extends FragmentPagerAdapter {

        TabsFragmentPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            return mTabs.get(i).createFragment();
        }

        @Override
        public int getCount() {

            return mTabs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mTabs.get(position).getTitle();
        }
    }
}
