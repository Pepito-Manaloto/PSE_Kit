package com.aaron.pseplanner.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.fragment.TradePlanFragment;

import java.util.ArrayList;

/**
 * Created by aaron.asuncion on 2/22/2017.
 */

public class TradePlanPagerAdapter extends FragmentPagerAdapter
{
    private ArrayList<TradeDto> tradeDtoPlanList;
    private int size;

    /**
     * Default constructor.
     *
     * @param fm            the fragment manager
     * @param tradeDtoPlanList the list of trade plan
     * @param size          the number of trade plan in the list
     */
    public TradePlanPagerAdapter(FragmentManager fm, ArrayList<TradeDto> tradeDtoPlanList, int size)
    {
        super(fm);

        this.tradeDtoPlanList = tradeDtoPlanList;
        this.size = size;
    }

    @Override
    public Fragment getItem(int position)
    {
        return TradePlanFragment.newInstance(tradeDtoPlanList.get(position));
    }

    @Override
    public int getCount()
    {
        return size;
    }
}
