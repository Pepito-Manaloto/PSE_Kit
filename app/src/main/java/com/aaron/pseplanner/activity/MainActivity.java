package com.aaron.pseplanner.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.async.InitTickerListTask;
import com.aaron.pseplanner.async.UpdateFragmentListTask;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.IntentRequestCode;
import com.aaron.pseplanner.fragment.AbstractListFragment;
import com.aaron.pseplanner.fragment.CalculatorTabsFragment;
import com.aaron.pseplanner.fragment.SettingsFragment;
import com.aaron.pseplanner.fragment.TickerListFragment;
import com.aaron.pseplanner.fragment.TradePlanListFragment;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PSEPlannerService;
import com.aaron.pseplanner.service.implementation.FacadePSEPlannerService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The main activity, contains Navigation items in a Drawer. Contains fragments: trade plan list, calculator, ticker, and settings.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    public static final String CLASS_NAME = MainActivity.class.getSimpleName();
    private final AtomicBoolean isUpdating = new AtomicBoolean(false);

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Menu toolbarMenu;
    private Toolbar toolbar;
    private AbstractListFragment selectedListFragment;
    private ArrayList<TickerDto> tickerDtoList;
    private ArrayList<TradeDto> tradeDtoList;
    private PSEPlannerService pseService;
    private boolean isReturningResultHomeView;

    /**
     * Initializes the navigation drawer.
     * Adds the current fragment in the fragment_container, uses TradePlanList as default fragment to inflate.
     *
     * @param savedInstanceState this Bundle is unused in this method.
     */
    @Override

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LogManager.debug(CLASS_NAME, "onCreate", "");

        setContentView(R.layout.activity_main);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);

        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, this.drawer, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.removeDrawerListener(toggle);
        this.drawer.addDrawerListener(toggle);
        toggle.syncState();

        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.navigationView.setNavigationItemSelectedListener(this);

        this.pseService = new FacadePSEPlannerService(this);
        this.tickerDtoList = new ArrayList<>();
        this.tradeDtoList = this.pseService.getTradePlanListFromDatabase();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null)
        {
            this.selectedListFragment = new TradePlanListFragment();
            fm.beginTransaction().add(R.id.fragment_container, this.selectedListFragment).commit();
        }

        this.initTickerDtoList(this.tradeDtoList);
    }

    /**
     * Init ticker dto list data.
     */
    private void initTickerDtoList(ArrayList<TradeDto> tradeDtoList)
    {
        if(this.tickerDtoList.isEmpty())
        {
            LogManager.debug(CLASS_NAME, "initTickerDtoList", "TickerList is empty, getting values from intent extras.");

            if(this.tickerDtoList == null || this.tickerDtoList.isEmpty())
            {
                LogManager.debug(CLASS_NAME, "initTickerDtoList", "TickerList is still empty, getting values from database.");
                // Check if exists in database
                this.tickerDtoList = (ArrayList<TickerDto>) this.pseService.getTickerListFromDatabase();

                if(this.tickerDtoList.size() < this.pseService.getExpectedMinimumTotalStocks())
                {
                    LogManager.debug(CLASS_NAME, "initTickerDtoList", "TickerList is still empty, getting values from web api asynchronously.");
                    // Does not exists in both intent extra and database, then retrieve from web api.
                    new InitTickerListTask(this, this.pseService, tradeDtoList).execute();
                }
                else
                {
                    this.pseService.setTickerDtoListHasTradePlan(this.tickerDtoList, this.pseService.getTradeSymbolsFromTradeDtos(tradeDtoList));
                }
            }
        }
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
     * Receives the result data from the previous fragment. Updates the
     * application's state depending on the data received.
     *
     * @param requestCode the request code that determines the previous activity
     * @param resultCode  the result of the previous activity or fragment
     * @param data        the data that are returned from the previous activity or fragment
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
            if(data.hasExtra(DataKey.EXTRA_TICKER.toString()))
            {
                TickerDto addedTickerDto = data.getParcelableExtra(DataKey.EXTRA_TICKER.toString());

                int addedTickerIndex = this.tickerDtoList.indexOf(addedTickerDto);
                // Replace ticker dto
                if(addedTickerIndex != -1)
                {
                    this.tickerDtoList.remove(addedTickerDto);
                }

                LogManager.debug(CLASS_NAME, "onActivityResult", "Extra Ticker: " + addedTickerDto);
                this.tickerDtoList.add(addedTickerIndex, addedTickerDto);
            }

            if(data.hasExtra(DataKey.EXTRA_TRADE.toString()))
            {
                TradeDto addedTradeDto = data.getParcelableExtra(DataKey.EXTRA_TRADE.toString());

                if(!this.tradeDtoList.contains(addedTradeDto))
                {
                    this.tradeDtoList.add(addedTradeDto);
                    this.isReturningResultHomeView = true;
                }

                LogManager.debug(CLASS_NAME, "onActivityResult", "Extra Trade: " + addedTradeDto);
            }
        }
        else if(IntentRequestCode.VIEW_TRADE_PLAN.code() == requestCode)
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

        initializeSearchBar(menu);

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
                this.selectedListFragment = TickerListFragment.newInstance(this.tickerDtoList, this.tradeDtoList);
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
        }

        this.drawer.closeDrawer(GravityCompat.START);
        return true;
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
     * @param menu    the menu to alter
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
     * Initializes the search bar.
     */
    protected void initializeSearchBar(Menu menu)
    {
        MenuItem myActionMenuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                LogManager.debug(CLASS_NAME, "onQueryTextSubmit", query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                LogManager.debug(CLASS_NAME, "onQueryTextChange", s);
                return false;
            }
        });

        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try
        {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.search_cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        }
        catch(Exception e)
        {
            LogManager.error(CLASS_NAME, "initializeSearchBar", "Error setting custom search cursor.", e);
        }
    }

    /**
     * Stops the rotating animation of the refresh menu.
     */
    protected void startRefreshAnimation(MenuItem item)
    {
        LogManager.debug(CLASS_NAME, "startRefreshAnimation", "");

        // Do animation start
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView refreshImage = (ImageView) inflater.inflate(R.layout.imageview_refresh, null);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        rotation.setRepeatCount(Animation.INFINITE);
        refreshImage.startAnimation(rotation);

        item.setActionView(refreshImage);
    }

    /**
     * Executes the UpdateTicker async task. Starts the refresh button animation then retrieves data from PSE.
     *
     * @param item the refresh button menu item
     */
    protected void executeRefreshTicker(MenuItem item)
    {
        startRefreshAnimation(item);

        UpdateFragmentListTask fragmentListUpdater = new UpdateFragmentListTask(this, this.selectedListFragment, this.isUpdating);
        fragmentListUpdater.execute();

        isUpdating.set(true);

        LogManager.debug(CLASS_NAME, "executeRefreshTicker", "Executed!");
    }

    public AbstractListFragment getSelectedListFragment()
    {
        return this.selectedListFragment;
    }

    public void setTickerDtoList(ArrayList<TickerDto> tickerDtoList)
    {
        this.tickerDtoList = tickerDtoList;
    }

    public void setTradeDtoList(ArrayList<TradeDto> tradeDtoList)
    {
        this.tradeDtoList = tradeDtoList;
    }

    /**
     * Sets the default home view, which is the trade plan list fragment.
     */
    private void setDefaultHomeView()
    {
        this.selectedListFragment = TradePlanListFragment.newInstance(this.tradeDtoList);
        updateFragmentContainer(this.selectedListFragment);
        this.showToolbarMenuItems();
        this.toolbar.setTitle(R.string.app_name);
    }
}
