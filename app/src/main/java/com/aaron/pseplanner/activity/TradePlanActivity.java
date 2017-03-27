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
import com.aaron.pseplanner.bean.TradeDto;
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
    private ArrayList<TradeDto> tradeDtoPlanList;
    private TradeDto selectedTradeDtoPlan;

    /**
     * Inflates the UI.
     *
     * @param savedInstanceState this Bundle is unused in this method.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LogManager.debug(CLASS_NAME, "onCreate", "");

        setContentView(R.layout.activity_trade_plan);

        if(savedInstanceState != null)
        {
            this.tradeDtoPlanList = savedInstanceState.getParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString());
            this.selectedTradeDtoPlan = savedInstanceState.getParcelable(DataKey.EXTRA_TRADE.toString());
        }
        else
        {
            this.tradeDtoPlanList = getIntent().getParcelableArrayListExtra(DataKey.EXTRA_TRADE_LIST.toString());
            this.selectedTradeDtoPlan = getIntent().getParcelableExtra(DataKey.EXTRA_TRADE.toString());
        }

        LogManager.debug(CLASS_NAME, "onCreate", "selected=" + (this.selectedTradeDtoPlan == null ? null : this.selectedTradeDtoPlan.toString()));

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
        FragmentPagerAdapter pagerAdapter = new TradePlanPagerAdapter(getSupportFragmentManager(), this.tradeDtoPlanList, this.tradeDtoPlanList.size());

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(this.tradeDtoPlanList.indexOf(this.selectedTradeDtoPlan));
    }

    /**
     * Saves current state in memory, when this activity is temporarily destroyed.
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(this.tradeDtoPlanList != null && !this.tradeDtoPlanList.isEmpty())
        {
            outState.putParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString(), this.tradeDtoPlanList);
        }

        if(this.selectedTradeDtoPlan != null)
        {
            outState.putParcelable(DataKey.EXTRA_TRADE.toString(), this.selectedTradeDtoPlan);
            LogManager.debug(CLASS_NAME, "onSaveInstanceState", "Trade: " + this.selectedTradeDtoPlan);
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

        if(IntentRequestCode.UPDATE_TRADE_PLAN.code() == requestCode && data.hasExtra(DataKey.EXTRA_TRADE.toString()))
        {
            this.selectedTradeDtoPlan = data.getParcelableExtra(DataKey.EXTRA_TRADE.toString());
        }
    }
}
