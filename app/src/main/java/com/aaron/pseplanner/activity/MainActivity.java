package com.aaron.pseplanner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.IntentRequestCode;
import com.aaron.pseplanner.fragment.AbstractListFragment;
import com.aaron.pseplanner.fragment.CalculatorTabsFragment;
import com.aaron.pseplanner.fragment.SettingsFragment;
import com.aaron.pseplanner.fragment.TickerListFragment;
import com.aaron.pseplanner.fragment.TradePlanListFragment;
import com.aaron.pseplanner.listener.SearchOnQueryTextListener;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PSEPlannerService;
import com.aaron.pseplanner.service.implementation.FacadePSEPlannerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * The main activity, contains Navigation items in a Drawer. Contains fragments: trade plan list, calculator, ticker, and settings.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    public static final String CLASS_NAME = MainActivity.class.getSimpleName();
    private final AtomicBoolean isUpdating = new AtomicBoolean(false);

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Menu toolbarMenu;
    private AbstractListFragment selectedListFragment;
    private ArrayList<TickerDto> tickerDtoList;
    private ArrayList<TradeDto> tradeDtoList;
    private PSEPlannerService pseService;
    private boolean isReturningResultHomeView;
    private CompositeDisposable compositeDisposable;
    private SearchOnQueryTextListener searchListener;

    // TODO: https://www.youtube.com/watch?v=ZWYOy8E4jWo
    // https://stackoverflow.com/questions/38605090/rxjava-timer-that-repeats-forever-and-can-be-restarted-and-stopped-at-anytime/38607323
    /**
     * Initializes the navigation drawer. Adds the current fragment in the fragment_container, uses TradePlanList as default fragment to inflate.
     *
     * @param savedInstanceState this Bundle is unused in this method.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LogManager.debug(CLASS_NAME, "onCreate", "");

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(this.toolbar);

        initializeDrawer();

        this.navigationView.setNavigationItemSelectedListener(this);

        this.pseService = new FacadePSEPlannerService(this);
        this.searchListener = new SearchOnQueryTextListener();
        this.tickerDtoList = new ArrayList<>();

        this.compositeDisposable = new CompositeDisposable();

        this.tradeDtoList = this.pseService.getTradePlanListFromDatabase().blockingGet();
        Collections.sort(tradeDtoList);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null)
        {
            this.selectedListFragment = TradePlanListFragment.newInstance(this.tradeDtoList).setSearchListener(searchListener);
            fm.beginTransaction().add(R.id.fragment_container, this.selectedListFragment).commit();
        }

        initTickerDtoList();
    }

    private void initializeDrawer()
    {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, this.drawer, this.toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        this.drawer.removeDrawerListener(toggle);
        this.drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Init ticker dto list data.
     */
    private void initTickerDtoList()
    {
        if(this.tickerDtoList == null || this.tickerDtoList.isEmpty())
        {
            LogManager.debug(CLASS_NAME, "initTickerDtoList", "TickerList is still empty, getting values from database.");

            // Check if exists in database
            Disposable disposable = initTickerListObservable().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(initTickerListObserver());

            this.compositeDisposable.add(disposable);
        }
    }

    /**
     * Returns a Single observable that retrieves ticker list from the database then web if not found in database.
     *
     * @return {@code Single<ArrayList<TickerDto>>}
     */
    private Single<ArrayList<TickerDto>> initTickerListObservable()
    {
        Single<ArrayList<TickerDto>> getAllTickerFromDatabase = pseService.getTickerListFromDatabase()
                .subscribeOn(Schedulers.io());
        Single<ArrayList<TickerDto>> getAllTickerFromWeb = pseService.getAllTickerList()
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Pair<List<TickerDto>, Date>, SingleSource<? extends ArrayList<TickerDto>>>()
                {
                    @Override
                    public SingleSource<? extends ArrayList<TickerDto>> apply(Pair<List<TickerDto>, Date> pair)
                    {
                        ArrayList<TickerDto> tickerDtoList = new ArrayList<>(pair.first);
                        Date lastUpdated = pair.second;

                        pseService.insertTickerList(tickerDtoList, lastUpdated);
                        LogManager.debug(CLASS_NAME, "initTickerDtoList", "Retrieved from Web API and saved to database, count: " + tickerDtoList.size());
                        return Single.just(tickerDtoList);
                    }
                });

        return Single.concat(getAllTickerFromDatabase, getAllTickerFromWeb)
                .first(tickerDtoList);
    }

    /**
     * Returns an observer that observes init ticker list observable. OnSuccess: Sets the hasTradePlan flag of each ticker in the list and updates the fragment.
     * OnError: Logs error.
     *
     * @return {@code DisposableSingleObserver<ArrayList<TickerDto>>}
     */
    private DisposableSingleObserver<ArrayList<TickerDto>> initTickerListObserver()
    {
        return new DisposableSingleObserver<ArrayList<TickerDto>>()
        {
            @Override
            public void onSuccess(ArrayList<TickerDto> tickerDtos)
            {
                if(!tickerDtos.isEmpty())
                {
                    Set<String> tradeDtoSymbols = pseService.getTradeSymbolsFromTradeDtos(tradeDtoList);

                    // Set hasTradePlan, because it is not tracked in the database.
                    tickerDtoList = pseService.setTickerDtoListHasTradePlan(tickerDtos, tradeDtoSymbols);

                    if(selectedListFragment instanceof TickerListFragment)
                    {
                        LogManager.debug(CLASS_NAME, "initTickerDtoList.onSuccess", "Updating TickerListFragment.");
                        selectedListFragment.updateListFromDatabase();
                    }
                }
            }

            @Override
            public void onError(Throwable e)
            {
                LogManager.error(CLASS_NAME, "initTickerDtoList.onError", "Failed to initialize ticker list.", e);
            }
        };
    }

    /**
     * Cleanup.
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
     * Update selected fragment here instead in onActivityResult() to avoid "IllegalStateException: Can not perform this action after onSaveInstanceState".
     */
    @Override
    public void onPostResume()
    {
        super.onPostResume();
        LogManager.debug(CLASS_NAME, "onPostResume", "");

        if(this.isReturningResultHomeView)
        {
            setDefaultHomeView();
            this.navigationView.setCheckedItem(R.id.nav_trade_plan);
            this.isReturningResultHomeView = false;
        }
    }

    /**
     * Receives the result data from the previous fragment. Updates the application's state depending on the data received.
     *
     * @param requestCode the request code that determines the previous activity
     * @param resultCode the result of the previous activity or fragment
     * @param data the data that are returned from the previous activity or fragment
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != Activity.RESULT_OK)
        {
            return;
        }

        LogManager.debug(CLASS_NAME, "onActivityResult", "requestCode=" + requestCode + " resultCode=" + resultCode);

        if(IntentRequestCode.CREATE_TRADE_PLAN.code() == requestCode)
        {
            loadCreateTradePlanResultData(data);
        }
        else if(IntentRequestCode.VIEW_TRADE_PLAN.code() == requestCode)
        {
            loadViewTradePlanResultData(data);
        }
    }

    private void loadCreateTradePlanResultData(Intent data)
    {
        if(data.hasExtra(DataKey.EXTRA_TICKER.toString()))
        {
            if(tickerDtoList == null || tickerDtoList.isEmpty())
            {
                tickerDtoList = pseService.getTickerListFromDatabase().blockingGet();
            }

            TickerDto addedTickerDto = data.getParcelableExtra(DataKey.EXTRA_TICKER.toString());

            int addedTickerIndex = this.tickerDtoList.indexOf(addedTickerDto);

            // Replace ticker dto
            if(addedTickerIndex != -1)
            {
                this.tickerDtoList.remove(addedTickerDto);
                this.tickerDtoList.add(addedTickerIndex, addedTickerDto);
            }
            else
            {
                this.tickerDtoList.add(addedTickerDto);
            }

            LogManager.debug(CLASS_NAME, "loadCreateTradePlanResultData", "Extra Ticker: " + addedTickerDto);
        }

        if(data.hasExtra(DataKey.EXTRA_TRADE.toString()))
        {
            TradeDto addedTradeDto = data.getParcelableExtra(DataKey.EXTRA_TRADE.toString());

            if(!this.tradeDtoList.contains(addedTradeDto))
            {
                this.tradeDtoList.add(addedTradeDto);
                this.isReturningResultHomeView = true;
                Collections.sort(this.tradeDtoList);
            }

            LogManager.debug(CLASS_NAME, "loadCreateTradePlanResultData", "Extra Trade: " + addedTradeDto);
        }
    }

    private void loadViewTradePlanResultData(Intent data)
    {
        if(data.hasExtra(DataKey.EXTRA_TRADE.toString()))
        {
            TradeDto removedTradeDto = data.getParcelableExtra(DataKey.EXTRA_TRADE.toString());

            if(this.tradeDtoList.contains(removedTradeDto))
            {
                this.tradeDtoList.remove(removedTradeDto);

                for(TickerDto dto : this.tickerDtoList)
                {
                    if(removedTradeDto.getSymbol().equals(dto.getSymbol()))
                    {
                        dto.setHasTradePlan(false);
                    }
                }

                this.isReturningResultHomeView = true;
                LogManager.debug(CLASS_NAME, "onActivityResult", "Extra Trade removed: " + removedTradeDto);
            }
        }
        else if(data.hasExtra(DataKey.EXTRA_TRADE_LIST.toString()))
        {
            this.tradeDtoList = data.getParcelableArrayListExtra(DataKey.EXTRA_TRADE_LIST.toString());
            selectedListFragment.getListAdapter().update(tradeDtoList);
            LogManager.debug(CLASS_NAME, "onActivityResult", "Extra Trade list updated.");
        }
    }

    /**
     * Initializes the toolbar(search bar and refresh button).
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_options, menu);
        this.toolbarMenu = menu;

        // Initializes the search bar.
        MenuItem myActionMenuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(searchListener);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This method is called when a user selects an item in the menu bar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_search:
            {
                return true;
            }
            case R.id.menu_refresh:
            {
                if(!isUpdating.get())
                {
                    executeRefreshTicker(item);
                }
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    /**
     * Closes the navigation drawer if it is open.
     */
    @Override
    public void onBackPressed()
    {
        if(this.drawer.isDrawerOpen(GravityCompat.START))
        {
            this.drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    /**
     * Sets the action of each option in the navigation drawer.
     *
     * @param item the selected drawer option
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        // Handle navigation view item clicks here.
        switch(item.getItemId())
        {
            case R.id.nav_trade_plan:
            {
                setDefaultHomeView();
                break;
            }
            case R.id.nav_ticker:
            {
                this.selectedListFragment = TickerListFragment.newInstance(this.tickerDtoList, this.tradeDtoList).setSearchListener(searchListener);
                updateFragmentContainer(this.selectedListFragment);
                this.showToolbarMenuItems();
                this.toolbar.setTitle(R.string.nav_ticker);
                break;
            }
            case R.id.nav_calculator:
            {
                updateFragmentContainer(new CalculatorTabsFragment());
                this.hideToolbarMenuItems();
                this.toolbar.setTitle(R.string.nav_calculator);
                break;
            }
            case R.id.nav_settings:
            {
                updateFragmentContainer(new SettingsFragment());
                this.hideToolbarMenuItems();
                this.toolbar.setTitle(R.string.nav_settings);
                break;
            }
            default:
            {
                LogManager.warn(CLASS_NAME, "onNavigationItemSelected", "Unknown menu item.");
            }
        }

        this.drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Sets the default home view, which is the trade plan list fragment.
     */
    private void setDefaultHomeView()
    {
        this.selectedListFragment = TradePlanListFragment.newInstance(this.tradeDtoList).setSearchListener(searchListener);
        updateFragmentContainer(this.selectedListFragment);
        this.showToolbarMenuItems();
        this.toolbar.setTitle(R.string.app_name);
    }

    /**
     * Replaces the current fragment inside the drawer layout.
     *
     * @param newFragment the new fragment to show
     */
    protected void updateFragmentContainer(Fragment newFragment)
    {
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction().replace(R.id.fragment_container, newFragment).commit();
    }

    /**
     * Shows the toolbar menu in the view.
     */
    protected void showToolbarMenuItems()
    {
        if(this.toolbarMenu != null && this.toolbarMenu.size() > 0)
        {
            setMenuItemsVisibility(this.toolbarMenu, true);
        }
    }

    /**
     * Hides the toolbar menu in the view.
     */
    protected void hideToolbarMenuItems()
    {
        if(this.toolbarMenu != null && this.toolbarMenu.size() > 0)
        {
            setMenuItemsVisibility(this.toolbarMenu, false);
        }
    }

    /**
     * Sets the visibility of the items in the given menu.
     *
     * @param menu the menu to alter
     * @param visible if true shows each item of the menu, else hides all items
     */
    protected void setMenuItemsVisibility(Menu menu, boolean visible)
    {
        int size = menu.size();
        for(int i = 0; i < size; i++)
        {
            MenuItem item = menu.getItem(i);
            item.setVisible(visible);
        }
    }

    /**
     * Starts the rotating animation of the refresh menu.
     */
    protected void startRefreshAnimation(MenuItem item)
    {
        LogManager.debug(CLASS_NAME, "startRefreshAnimation", "");

        ImageView refreshImage = (ImageView) LayoutInflater.from(this).inflate(R.layout.imageview_refresh, null);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        rotation.setRepeatCount(Animation.INFINITE);
        refreshImage.startAnimation(rotation);

        item.setActionView(refreshImage);
    }

    /**
     * Starts the refresh button animation then retrieves data from PSE.
     *
     * @param item the refresh button menu item
     */
    protected void executeRefreshTicker(MenuItem item)
    {
        startRefreshAnimation(item);

        Action updateFragmentListFromWebAfterSubscribe = updateFragmentListFromWebAfterSubscribe();
        Disposable subscription = Completable.fromCallable(updateFragmentListFromWebObservable(updateFragmentListFromWebAfterSubscribe))
                .subscribeOn(Schedulers.io())
                .subscribe();

        isUpdating.set(true);
        LogManager.debug(CLASS_NAME, "executeRefreshTicker", "Executed!");

        this.compositeDisposable.add(subscription);
    }

    /**
     * Returns a callable that updates the list of the selected fragment list.
     *
     * @param doAfterSubscribe the action that will be executed after executing this observable
     * @return {@code Callable<Void>}
     */
    private Callable<Void> updateFragmentListFromWebObservable(final Action doAfterSubscribe)
    {
        return new Callable<Void>()
        {
            @Override
            public Void call()
            {
                selectedListFragment.updateListFromWeb(doAfterSubscribe);

                return null;
            }
        };
    }

    /**
     * Stops refresh animation and update isUpdating flag to false.
     *
     * @return Action
     */
    private Action updateFragmentListFromWebAfterSubscribe()
    {
        return new Action()
        {
            @Override
            public void run()
            {
                stopRefreshAnimation();
                isUpdating.set(false);
            }
        };
    }

    /**
     * Stops the rotating animation of the refresh menu.
     */
    public void stopRefreshAnimation()
    {
        LogManager.debug(CLASS_NAME, "stopRefreshAnimation", "");

        // Get our refresh item from the menu
        MenuItem refreshMenu = this.toolbarMenu.findItem(R.id.menu_refresh);
        if(refreshMenu.getActionView() != null)
        {
            // Remove the animation.
            refreshMenu.getActionView().clearAnimation();
            refreshMenu.setActionView(null);
        }
    }
}
