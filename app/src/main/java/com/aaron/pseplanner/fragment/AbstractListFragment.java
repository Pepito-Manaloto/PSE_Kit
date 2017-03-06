package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.bean.Trade;
import com.aaron.pseplanner.bean.TradeEntry;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.listener.OnScrollShowHideFastScroll;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.PSEPlannerService;
import com.aaron.pseplanner.service.implementation.DefaultFormatService;
import com.aaron.pseplanner.service.implementation.FacadePSEPlannerService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Aaron on 2/17/2017.
 * Abstract ListFragment class with concrete implementation for showing/hiding fast scroll and method for updating the list adapter.
 */
public abstract class AbstractListFragment<T extends Parcelable> extends ListFragment
{
    protected PSEPlannerService client;
    protected FormatService formatService;
    protected TextView lastUpdatedTextView;

    /**
     * Initializes non-fragment user interface.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.client = new FacadePSEPlannerService(getActivity());
        this.formatService = new DefaultFormatService(getActivity());
    }

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
     * Updates the list view on UI thread, including the last updated text view.
     *
     * @param list        the new list
     * @param lastUpdated the last updated date
     */
    protected void updateListOnUiThread(final List<T> list, final String lastUpdated)
    {
        this.getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                setListAdapter(getArrayAdapter(list));

                if(lastUpdatedTextView != null)
                {
                    lastUpdatedTextView.setText(getActivity().getString(R.string.last_updated, lastUpdated));
                }
            }
        });
    }

    /**
     * Returns the ArrayAdapter that will be used to populate the ListFragment.
     *
     * @return ArrayAdapter
     */
    protected abstract ArrayAdapter getArrayAdapter(List<T> list);

    /**
     * Updates the list of this fragment list by getting the latest data through http request.
     *
     * @throws HttpRequestException http request failed
     */
    public abstract void updateList() throws HttpRequestException;
}
