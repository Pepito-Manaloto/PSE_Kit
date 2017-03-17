package com.aaron.pseplanner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.adapter.TradePlanPagerAdapter;
import com.aaron.pseplanner.bean.Trade;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.IntentRequestCode;
import com.aaron.pseplanner.service.LogManager;

import java.util.ArrayList;

/**
 * Created by Aaron on 2/18/2017.
 */

public class TradePlanActivity extends AppCompatActivity
{
    public static final String CLASS_NAME = TradePlanActivity.class.getSimpleName();
    private ArrayList<Trade> tradePlanList;
    private Trade selectedTradePlan;

    /**
     * Inflates the UI.
     *
     * @param savedInstanceState this Bundle is unused in this method.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_plan);

        if(savedInstanceState != null)
        {
            this.tradePlanList = savedInstanceState.getParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString());
            this.selectedTradePlan = savedInstanceState.getParcelable(DataKey.EXTRA_TRADE.toString());
        }
        else
        {
            this.tradePlanList = getIntent().getParcelableArrayListExtra(DataKey.EXTRA_TRADE_LIST.toString());
            this.selectedTradePlan = getIntent().getParcelableExtra(DataKey.EXTRA_TRADE.toString());
        }

        LogManager.debug(CLASS_NAME, "onCreate", "selected=" + (this.selectedTradePlan == null ? null : this.selectedTradePlan.toString()));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_trade_plan);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        FragmentPagerAdapter pagerAdapter = new TradePlanPagerAdapter(getSupportFragmentManager(), this.tradePlanList, this.tradePlanList.size());

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(this.tradePlanList.indexOf(this.selectedTradePlan));
    }

    /**
     * Saves current state in memory, when this activity is temporarily destroyed.
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString(), this.tradePlanList);
        outState.putParcelable(DataKey.EXTRA_TRADE.toString(), this.selectedTradePlan);

        LogManager.debug(CLASS_NAME, "onSaveInstanceState", "");
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

        if(IntentRequestCode.UPDATE_TRADE_PLAN.code() == requestCode && data.hasExtra(DataKey.EXTRA_TRADE.toString()))
        {
            this.selectedTradePlan = data.getParcelableExtra(DataKey.EXTRA_TRADE.toString());
        }
    }

}
