package com.aaron.pseplanner.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aaron.pseplanner.bean.Trade;
import com.aaron.pseplanner.fragment.TradePlanFragment;

import java.util.ArrayList;

/**
 * Created by aaron.asuncion on 2/22/2017.
 */

public class TradePlanPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<Trade> tradePlanList;
    private int size;

    /**
     * Default constructor.
     *
     * @param fm            the fragment manager
     * @param tradePlanList the list of trade plan
     * @param size          the number of trade plan in the list
     */
    public TradePlanPagerAdapter(FragmentManager fm, ArrayList<Trade> tradePlanList, int size)
    {
        super(fm);

        this.tradePlanList = tradePlanList;
        this.size = size;
    }

    @Override
    public Fragment getItem(int position)
    {
        return TradePlanFragment.newInstance(tradePlanList.get(position));
    }

    @Override
    public int getCount()
    {
        return size;
    }
}
