package com.aaron.pseplanner.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.aaron.pseplanner.R;

/**
 * Created by Aaron on 2/18/2017.
 */

public class TradePlanActivity extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_plan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_create_trade_plan);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * This method is called when a user selects an item in the menu bar. Home button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                // Finish activity with no result to send back to home
                finish();
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
