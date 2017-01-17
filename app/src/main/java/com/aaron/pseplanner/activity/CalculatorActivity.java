package com.aaron.pseplanner.activity;


import android.support.v4.app.Fragment;

import com.aaron.pseplanner.fragment.CalculatorTabsFragment;

@Deprecated
public class CalculatorActivity extends MainFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new CalculatorTabsFragment();
    }
}
