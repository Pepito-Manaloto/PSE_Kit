package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.listener.OnScrollShowHideFastScroll;

import java.util.List;

/**
 * Created by Aaron on 2/17/2017.
 * Abstract ListFragment class with concrete implementation for showing/hiding fast scroll and method for updating the list adapter.
 */
public abstract class AbstractListFragment extends ListFragment
{
    /**
     * Called after onCreateView(), sets the action listeners of the UI.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnScrollListener(new OnScrollShowHideFastScroll());
    }

    /**
     * Updates the list view on UI thread.
     *
     * @param list the new list
     */
    protected void updateListOnUiThread(final List<? extends Parcelable> list)
    {
        this.getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                setListAdapter(getArrayAdapter());
            }
        });
    }

    /**
     * Returns the ArrayAdapter that will be used to populate the ListFragment.
     *
     * @return ArrayAdapter
     */
    protected abstract ArrayAdapter getArrayAdapter();
}
