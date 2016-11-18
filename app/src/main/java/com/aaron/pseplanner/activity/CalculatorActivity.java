package com.aaron.pseplanner.activity;

import android.app.Fragment;

import com.aaron.pseplanner.fragment.CalculatorFragment;

@Deprecated
public class CalculatorActivity extends MainFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new CalculatorFragment();
    }
}
