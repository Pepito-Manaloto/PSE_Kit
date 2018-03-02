package com.aaron.pseplanner.listener;

import android.support.v7.widget.SearchView;

import com.aaron.pseplanner.adapter.FilterableArrayAdapter;
import com.aaron.pseplanner.service.LogManager;

/**
 * Created by Aaron on 6/18/2017.
 * Search listener for ticker and trade plan list, includes autocomplete.
 */
public class SearchOnQueryTextListener implements SearchView.OnQueryTextListener
{
    public static final String CLASS_NAME = SearchOnQueryTextListener.class.getSimpleName();
    private FilterableArrayAdapter listAdapter;

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        // Not implemented
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        if(this.listAdapter == null)
        {
            LogManager.warn(CLASS_NAME, "onQueryTextChange", "List Adapter is null.");
            return false;
        }

        this.listAdapter.filter(newText);

        return true;
    }

    public void setListAdapater(FilterableArrayAdapter listAdapter)
    {
        this.listAdapter = listAdapter;
    }
}
