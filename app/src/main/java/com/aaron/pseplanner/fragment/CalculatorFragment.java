package com.aaron.pseplanner.fragment;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaron.pseplanner.R;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */

public class CalculatorFragment extends Fragment
{
    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_calculator, parent, false);

        return view;
    }
}
