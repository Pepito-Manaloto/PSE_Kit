package com.aaron.pseplanner.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.adapter.FilterableArrayAdapter;
import com.aaron.pseplanner.bean.Stock;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.listener.OnScrollShowHideFastScroll;
import com.aaron.pseplanner.listener.SearchOnQueryTextListener;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PSEPlannerService;
import com.aaron.pseplanner.service.implementation.TradePlanFormatService;
import com.aaron.pseplanner.service.implementation.FacadePSEPlannerService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;

/**
 * Created by Aaron on 2/17/2017.
 * Abstract ListFragment class with concrete implementation for showing/hiding fast scroll and method for updating the list adapter.
 */
public abstract class AbstractListFragment<T extends Stock & Parcelable> extends ListFragment
{
    public static final String CLASS_NAME = AbstractListFragment.class.getSimpleName();

    @BindView(R.id.textview_last_updated)
    protected TextView lastUpdatedTextView;

    protected PSEPlannerService pseService;
    protected FormatService formatService;
    protected SearchOnQueryTextListener searchListener;
    protected Unbinder unbinder;

    protected CompositeDisposable compositeDisposable;

    // Initialized in subclass
    protected ArrayList<TradeDto> tradeDtoList;

    /**
     * Initializes non-fragment user interface.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();
        if(activity != null)
        {
            this.pseService = new FacadePSEPlannerService(activity);
            this.formatService = new TradePlanFormatService(activity);
            this.compositeDisposable = new CompositeDisposable();
        }

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
    protected void updateListView(final List<T> list, final String lastUpdated)
    {
        Activity activity = getActivity();
        LogManager.debug(CLASS_NAME, "updateListView", "Updating list view. adapter = " + getArrayAdapter().getClass().getSimpleName());

        if(list != null && !list.isEmpty() && activity != null)
        {
            getArrayAdapter().update(list);
            lastUpdatedTextView.setText(activity.getString(R.string.last_updated, lastUpdated));
            searchListener.setListAdapater(getListAdapter());

            LogManager.debug(CLASS_NAME, "updateListView", "Updating list view. list size = " + list.size());
        }
    }

    /**
     * Should only be called in onCreate(). Blocks and executes on the main thread.
     */
    protected ArrayList<TradeDto> initTradePlanListFromDatabase()
    {
        return this.pseService.getTradePlanListFromDatabase().blockingGet();
    }

    /**
     * A Fragment may continue to exist after its Views are destroyed, you need to call .unbind() from a Fragment to release the reference to the Views (and allow the associated
     * memory to be reclaimed).
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        if(this.unbinder != null)
        {
            this.unbinder.unbind();
        }
    }

    /**
     * Cleanup RxJava observables.
     */
    @Override
    public void onDestroy()
    {
        if(!this.compositeDisposable.isDisposed())
        {
            this.compositeDisposable.dispose();
        }

        super.onDestroy();
    }

    /**
     * Returns the filterable list adapter. Even though it is unchecked, we are sure of the type because the parameter passed to setListAdapter() is from getArrayAdapter(List).
     *
     * @return FilterableArrayAdapter
     */
    @Override
    @SuppressWarnings("unchecked")
    public FilterableArrayAdapter<T> getListAdapter()
    {
        return (FilterableArrayAdapter<T>) super.getListAdapter();
    }

    public AbstractListFragment<T> setSearchListener(SearchOnQueryTextListener searchListener)
    {
        this.searchListener = searchListener;
        return this;
    }

    /**
     * Returns the ArrayAdapter that will be used to populate the ListFragment.
     *
     * @return ArrayAdapter
     */
    protected abstract FilterableArrayAdapter<T> getArrayAdapter();

    /**
     * Updates the list of this fragment list by getting the latest data through http request.
     *
     * @param doAfterSubscribe the action that will be executed after executing this observable
     */
    public abstract void updateListFromWeb(Action doAfterSubscribe);

    /**
     * Updates the list of this fragment list by getting the latest data from the database.
     */
    public abstract void updateListFromDatabase();
}
