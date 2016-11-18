package com.aaron.pseplanner.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.fragment.CalculatorFragment;
import com.aaron.pseplanner.fragment.HomeFragment;
import com.aaron.pseplanner.fragment.SettingsFragment;
import com.aaron.pseplanner.fragment.TickerFragment;

/**
 * Abstract super class that creates a single fragment in the fragment container.
 */
public abstract class MainFragmentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private Menu toolbarMenu;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }else
        {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        switch(item.getItemId())
        {
            case R.id.nav_home:
            {
                updateFragmentContainer(new HomeFragment());
                this.inflateToolbarMenuItems();
                break;
            }
            case R.id.nav_ticker:
            {
                updateFragmentContainer(new TickerFragment());
                this.inflateToolbarMenuItems();
                break;
            }
            case R.id.nav_calculator:
            {
                updateFragmentContainer(new CalculatorFragment());
                this.removeToolbarMenuItems();
                break;
            }
            case R.id.nav_settings:
            {
                updateFragmentContainer(new SettingsFragment());
                this.removeToolbarMenuItems();
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    /**
     * To be implemented by an activity that has a single fragment. Returns a fragment that will be added to the fragment container.
     *
     * @return the fragment that will be added to the fragment container of the implementing Activity class
     */
    protected abstract Fragment createFragment();
}
