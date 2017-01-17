package com.aaron.pseplanner.activity;

import android.support.v4.app.Fragment;

import com.aaron.pseplanner.fragment.HomeFragment;

public class HomeActivity extends MainFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new HomeFragment();
    }
}
