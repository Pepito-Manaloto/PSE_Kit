package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaron.pseplanner.R;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class MidpointFragment extends Fragment
{
    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_midpoint, parent, false);

        return view;
    }
}
