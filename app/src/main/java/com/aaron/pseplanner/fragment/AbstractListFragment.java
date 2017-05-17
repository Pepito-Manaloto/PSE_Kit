package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.listener.OnScrollShowHideFastScroll;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PSEPlannerService;
import com.aaron.pseplanner.service.implementation.DefaultFormatService;
import com.aaron.pseplanner.service.implementation.FacadePSEPlannerService;

import java.util.List;

/**
 * Created by Aaron on 2/17/2017.
 * Abstract ListFragment class with concrete implementation for showing/hiding fast scroll and method for updating the list adapter.
 */
public abstract class AbstractListFragment<T extends Parcelable> extends ListFragment
{
    public static final String CLASS_NAME = AbstractListFragment.class.getSimpleName();

    protected PSEPlannerService pseService;
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

        this.pseService = new FacadePSEPlannerService(getActivity());
        this.formatService = new DefaultFormatService(getActivity());

        LogManager.debug(CLASS_NAME, "onCreateView", "");
    }

    /**
     * Called after onCreateView(), sets the action listeners of the UI.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnScrollListener(new OnScrollShowHideFastScroll());

        LogManager.debug(CLASS_NAME, "onActivityCreated", "");
    }

    /**
     * Updates the list view on UI thread, including the last updated text view.
     *
     * @param list        the new list
     * @param lastUpdated the last updated date
     */
    protected void updateListOnUiThread(final List<T> list, final String lastUpdated)
    {
        if(list != null && !list.isEmpty())
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
    public abstract void updateListFromWeb() throws HttpRequestException;

    /**
     * Updates the list of this fragment list by getting the latest data from the database.
     */
    public abstract void updateListFromDatabase();
}
