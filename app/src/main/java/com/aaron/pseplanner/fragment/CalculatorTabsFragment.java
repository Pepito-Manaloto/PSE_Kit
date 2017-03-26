package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.service.LogManager;

/**
 * Created by Aaron on 1/16/2017.
 * Contains the calculator tabs: calculator, dividend, and midpoint.
 */
public class CalculatorTabsFragment extends Fragment
{
    public static final String CLASS_NAME = CalculatorTabsFragment.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_calculator_tabs, null);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        viewPager.setAdapter(new CalculatorTabPagerAdapter(getChildFragmentManager()));

        // The setupWithViewPager() does not work without the runnable. Maybe a support library bug.
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        LogManager.debug(CLASS_NAME, "onCreateView", "");

        return view;
    }

    /**
     * Enum Tab represents each calculator tab.
     */
    private enum Tab
    {
        Calculator(new CalculatorFragment()), Dividend(new DividendFragment()), Midpoint(new MidpointFragment());

        private static final int LENGTH = Tab.values().length;
        private Fragment fragment;

        /**
         * Constructor.
         */
        Tab(Fragment fragment)
        {
            this.fragment = fragment;
        }

        /**
         * Returns the fragment of this Tab.
         */
        public Fragment getFragment()
        {
            return fragment;
        }

        /**
         * Returns the fragment of the Tab with the matching ordinal.
         *
         * @param ordinal the ordinal to match
         * @return the Tab fragment of the matched ordinal
         */
        public static Fragment getFragment(int ordinal)
        {
            for(Tab tab : Tab.values())
            {
                if(tab.ordinal() == ordinal)
                {
                    return tab.getFragment();
                }
            }

            return null;
        }

        /**
         * Returns the name of the Tab with the matching ordinal.
         *
         * @param ordinal the ordinal to match
         * @return the Tab name of the matched ordinal
         */
        public static String getName(int ordinal)
        {
            for(Tab tab : Tab.values())
            {
                if(tab.ordinal() == ordinal)
                {
                    return tab.name();
                }
            }

            return null;
        }
    }

    /**
     *
     */
    private static class CalculatorTabPagerAdapter extends FragmentPagerAdapter
    {
        CalculatorTabPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        /**
         * Returns the corresponding fragment of the selected tab.
         */
        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            return Tab.getFragment(position);
        }

        /**
         * Total tab count.
         */
        @Override
        public int getCount()
        {
            return Tab.LENGTH;
        }

        /**
         * Sets the title of the tab.
         */
        @Override
        public CharSequence getPageTitle(int position)
        {
            return Tab.getName(position);
        }
    }
}
