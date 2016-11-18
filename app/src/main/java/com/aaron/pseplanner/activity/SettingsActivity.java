package com.aaron.pseplanner.activity;

import android.app.Fragment;

import com.aaron.pseplanner.fragment.SettingsFragment;

@Deprecated
public class SettingsActivity extends MainFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new SettingsFragment();
    }
}
