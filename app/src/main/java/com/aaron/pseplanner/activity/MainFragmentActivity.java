package com.aaron.pseplanner.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.aaron.pseplanner.fragment.CalculatorFragment;
import com.aaron.pseplanner.fragment.HomeFragment;
import com.aaron.pseplanner.fragment.SettingsFragment;
import com.aaron.pseplanner.fragment.TickerFragment;

import java.lang.reflect.Field;

/**
 * Abstract super class that creates a single fragment in the fragment container.
 */
public abstract class MainFragmentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    public static final String LOG_MARKER = "MainFragmentActivity";
    private Menu toolbarMenu;
    private Toolbar toolbar;

    /**
     * Adds the single fragment, returned from the abstract method createFragment(), into the fragment container.
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.removeDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null)
        {
            fragment = this.createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_options, menu);
        this.toolbarMenu = menu;

        initializeSearchBar(menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This method is called when a user selects an item in the menu bar. Opens
     * the fragment of selected item.
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

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        // Handle navigation view item clicks here.
        switch(item.getItemId())
        {
            case R.id.nav_home:
            {
                updateFragmentContainer(new HomeFragment());
                this.inflateToolbarMenuItems();
                this.toolbar.setTitle(R.string.nav_home);
                break;
            }
            case R.id.nav_ticker:
            {
                updateFragmentContainer(new TickerFragment());
                this.inflateToolbarMenuItems();
                this.toolbar.setTitle(R.string.nav_ticker);
                break;
            }
            case R.id.nav_calculator:
            {
                updateFragmentContainer(new CalculatorFragment());
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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

    protected void updateFragmentContainer(Fragment newFragment)
    {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment_container, newFragment).commit();
    }

    protected void inflateToolbarMenuItems()
    {
        if(this.toolbarMenu != null && this.toolbarMenu.size() <= 0)
        {
            getMenuInflater().inflate(R.menu.toolbar_options, this.toolbarMenu);
        }
    }

    protected void removeToolbarMenuItems()
    {
        if(this.toolbarMenu != null && this.toolbarMenu.size() > 0)
        {
            this.toolbarMenu.clear();
        }
    }

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
                Log.d(LOG_MARKER, "onQueryTextSubmit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                Log.d(LOG_MARKER, "onQueryTextChange: " + s);
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
            Log.e(LOG_MARKER, "Error setting custom search cursor.", e);
        }
    }

    protected void executeRefreshTicker(MenuItem item)
    {
        // Do animation start
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView refreshImage = (ImageView) inflater.inflate(R.layout.imageview_refresh, null);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        rotation.setRepeatCount(Animation.INFINITE);
        refreshImage.startAnimation(rotation);

        item.setActionView(refreshImage);

        UpdateTickerTask tickerUpdater = new UpdateTickerTask(this);
        tickerUpdater.execute();
    }

    /**
     * To be implemented by an activity that has a single fragment. Returns a fragment that will be added to the fragment container.
     *
     * @return the fragment that will be added to the fragment container of the implementing Activity class
     */
    protected abstract Fragment createFragment();
}
