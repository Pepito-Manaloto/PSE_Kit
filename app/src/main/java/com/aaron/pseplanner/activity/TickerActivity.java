package com.aaron.pseplanner.activity;

import android.support.v4.app.Fragment;

import com.aaron.pseplanner.fragment.TickerFragment;

@Deprecated
public class TickerActivity extends MainFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new TickerFragment();
    }
}
