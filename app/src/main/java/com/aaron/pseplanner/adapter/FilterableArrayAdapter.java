package com.aaron.pseplanner.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.aaron.pseplanner.bean.Stock;
import com.aaron.pseplanner.service.LogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 6/18/2017.
 * ArrayAdapter with filter capability.
 */
public abstract class FilterableArrayAdapter<T extends Stock> extends ArrayAdapter<T>
{
    protected String className;

    public FilterableArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<T> objects)
    {
        super(context, resource, objects);
        this.className = getClass().getSimpleName();
    }

    /**
     * Filters the list of objects in the implementing adapter.
     *
     * @param searchQuery the search query used in filtering
     */
    public void filter(String searchQuery)
    {
        String searched = searchQuery.trim().toLowerCase();

        this.getActualList().clear();

        if(searched.length() == 0)
        {
            this.getActualList().addAll(this.getTempList());
        }
        else
        {
            String symbol;

            for(T dto : this.getTempList())
            {
                symbol = dto.getSymbol().toLowerCase();

                if(symbol.startsWith(searched))
                {
                    this.getActualList().add(dto);
                }
            }
        }

        LogManager.debug(this.className, "filter", "New list size -> " + this.getActualList().size());
    }

    /**
     * Returns the actual list of the adapter.
     *
     * @return ArrayList<T> the list adapter
     */
    protected abstract ArrayList<T> getActualList();

    /**
     * Returns the temporary list that is used in filtering the actual list of the adapter.
     *
     * @return ArrayList<T> the temp list adapter
     */
    protected abstract ArrayList<T> getTempList();
}
