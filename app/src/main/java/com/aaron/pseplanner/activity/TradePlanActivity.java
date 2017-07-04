package com.aaron.pseplanner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.adapter.TradePlanPagerAdapter;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.IntentRequestCode;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PSEPlannerService;
import com.aaron.pseplanner.service.implementation.FacadePSEPlannerService;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by Aaron on 2/18/2017.
 */

public class TradePlanActivity extends AppCompatActivity
{
    public static final String CLASS_NAME = TradePlanActivity.class.getSimpleName();
    private ArrayList<TradeDto> tradeDtoPlanList;
    private TradeDto selectedTradeDtoPlan;
    private FragmentStatePagerAdapter pagerAdapter;
    private boolean tradePlanListUpdated;

    /**
     * Inflates the UI.
     *
     * @param savedInstanceState stores the current state: tradeDtoPlanList and selectedTradeDtoPlan
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LogManager.debug(CLASS_NAME, "onCreate", "");

        setContentView(R.layout.activity_trade_plan);
        ButterKnife.bind(this);

        if(savedInstanceState != null)
        {
            this.tradeDtoPlanList = savedInstanceState.getParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString());
            this.selectedTradeDtoPlan = savedInstanceState.getParcelable(DataKey.EXTRA_TRADE.toString());
        }
        else
        {
            // Check if coming from TickerListFragment
            TickerDto ticker = getIntent().getParcelableExtra(DataKey.EXTRA_TICKER.toString());
            if(ticker != null)
            {
                PSEPlannerService pseService = new FacadePSEPlannerService(this);
                this.tradeDtoPlanList = pseService.getTradePlanListFromDatabase();

                for(TradeDto dto : this.tradeDtoPlanList)
                {
                    if(ticker.getSymbol().equals(dto.getSymbol()))
                    {
                        this.selectedTradeDtoPlan = dto;
                        break;
                    }
                }
            }
            else
            {
                this.tradeDtoPlanList = getIntent().getParcelableArrayListExtra(DataKey.EXTRA_TRADE_LIST.toString());
                this.selectedTradeDtoPlan = getIntent().getParcelableExtra(DataKey.EXTRA_TRADE.toString());
            }

        }

        LogManager.debug(CLASS_NAME, "onCreate", "selected=" + (this.selectedTradeDtoPlan == null ? null : this.selectedTradeDtoPlan.toString()));

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        toolbar.setTitle(R.string.title_trade_plan);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ViewPager viewPager = ButterKnife.findById(this, R.id.view_pager);
        this.pagerAdapter = new TradePlanPagerAdapter(getSupportFragmentManager(), this.tradeDtoPlanList, this.tradeDtoPlanList.size());

        viewPager.setAdapter(this.pagerAdapter);
        viewPager.setCurrentItem(this.tradeDtoPlanList.indexOf(this.selectedTradeDtoPlan));

        this.tradePlanListUpdated = false;
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

        LogManager.debug(CLASS_NAME, "onActivityResult", "requestCode=" + requestCode + " resultCode=" + resultCode);

        if(IntentRequestCode.UPDATE_TRADE_PLAN.code() == requestCode && data.hasExtra(DataKey.EXTRA_TRADE.toString()))
        {
            int index = this.tradeDtoPlanList.indexOf(this.selectedTradeDtoPlan);

            this.selectedTradeDtoPlan = data.getParcelableExtra(DataKey.EXTRA_TRADE.toString());
            LogManager.debug(CLASS_NAME, "onActivityResult", "Extra Trade: " + this.selectedTradeDtoPlan);

            this.tradeDtoPlanList.set(index, this.selectedTradeDtoPlan);
            this.pagerAdapter.notifyDataSetChanged();
            this.tradePlanListUpdated = true;
        }
    }

    /**
     * Sets the trade plan list as activity result to MainActivity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                this.setActivityResult();
                return super.onOptionsItemSelected(item);
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    /**
     * Sets the trade plan list as activity result to MainActivity.
     */
    @Override
    public void onBackPressed()
    {
        this.setActivityResult();
        super.onBackPressed();
    }

    private void setActivityResult()
    {
        if(this.tradePlanListUpdated)
        {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(DataKey.EXTRA_TRADE_LIST.toString(), this.tradeDtoPlanList);
            setResult(RESULT_OK, intent);
        }
    }
}
