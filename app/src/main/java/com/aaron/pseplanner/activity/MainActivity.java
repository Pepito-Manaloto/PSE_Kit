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
import com.aaron.pseplanner.async.UpdateTickerTask;
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

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * The main activity, contains Navigation items in a Drawer. Contains fragments: trade plan list, calculator, ticker, and settings.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    public static final String CLASS_NAME = MainActivity.class.getSimpleName();

    private DrawerLayout drawer;
    private Menu toolbarMenu;
    private Toolbar toolbar;
    private AbstractListFragment selectedListFragment;
    private ArrayList<TickerDto> tickerDtoList;
    private ArrayList<TradeDto> tradeDtoList;

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
        setContentView(R.layout.activity_main);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);

        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, this.drawer, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.removeDrawerListener(toggle);
        this.drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.tickerDtoList = new ArrayList<>();
        this.tradeDtoList = new ArrayList<>();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null)
        {
            this.selectedListFragment = new TradePlanListFragment();
            fm.beginTransaction().add(R.id.fragment_container, this.selectedListFragment).commit();
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

        LogManager.debug(CLASS_NAME, "onActivityResult", "requestCode=" + requestCode + " resultCode=" + resultCode + " dataKeys=" + data.getExtras().keySet());

        if(IntentRequestCode.CREATE_TRADE_PLAN.code() == requestCode && data.hasExtra(DataKey.EXTRA_TICKER.toString()))
        {
            TickerDto addedTickerDto = data.getParcelableExtra(DataKey.EXTRA_TICKER.toString());

            if(!this.tickerDtoList.contains(addedTickerDto))
            {
                this.tickerDtoList.add(addedTickerDto);
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
                executeRefreshTicker(item);
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
                if(getIntent().getExtras() != null)
                {
                    this.tradeDtoList = getIntent().getParcelableArrayListExtra(DataKey.EXTRA_TRADE_LIST.toString());
                }
                else
                {
                    this.tradeDtoList = new ArrayList<>();
                }

                this.selectedListFragment = TradePlanListFragment.newInstance(this.tradeDtoList);
                updateFragmentContainer(this.selectedListFragment);
                this.inflateToolbarMenuItems();
                this.toolbar.setTitle(R.string.app_name);
                break;
            }
            case R.id.nav_ticker:
            {
                if(getIntent().getExtras() != null)
                {
                    this.tickerDtoList = getIntent().getParcelableArrayListExtra(DataKey.EXTRA_TICKER_LIST.toString());
                }
                else
                {
                    this.tickerDtoList = new ArrayList<>();
                }

                this.selectedListFragment = TickerListFragment.newInstance(this.tickerDtoList);
                updateFragmentContainer(this.selectedListFragment);
                this.inflateToolbarMenuItems();
                this.toolbar.setTitle(R.string.nav_ticker);
                break;
            }
            case R.id.nav_calculator:
            {
                updateFragmentContainer(new CalculatorTabsFragment());
                this.removeToolbarMenuItems();
                this.toolbar.setTitle(R.string.nav_calculator);
                break;
            }
            case R.id.nav_settings:
            {
                updateFragmentContainer(new SettingsFragment());
                this.removeToolbarMenuItems();
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
     * Inflates the toolbar menu in the view.
     */
    protected void inflateToolbarMenuItems()
    {
        if(this.toolbarMenu != null && this.toolbarMenu.size() <= 0)
        {
            getMenuInflater().inflate(R.menu.toolbar_options, this.toolbarMenu);
        }
    }

    /**
     * Removes the toolbar menu in the view.
     */
    protected void removeToolbarMenuItems()
    {
        if(this.toolbarMenu != null && this.toolbarMenu.size() > 0)
        {
            this.toolbarMenu.clear();
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
     * Executes the UpdateTicker async task. Starts the refresh button animation then retrieves data from PSE.
     *
     * @param item the refresh button menu item
     */
    protected void executeRefreshTicker(MenuItem item)
    {
        // Do animation start
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView refreshImage = (ImageView) inflater.inflate(R.layout.imageview_refresh, null);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        rotation.setRepeatCount(Animation.INFINITE);
        refreshImage.startAnimation(rotation);

        item.setActionView(refreshImage);

        UpdateTickerTask tickerUpdater = new UpdateTickerTask(this, this.selectedListFragment);
        tickerUpdater.execute();
    }
}
