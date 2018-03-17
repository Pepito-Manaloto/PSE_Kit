package com.aaron.pseplanner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
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
import com.aaron.pseplanner.service.CalculatorService;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PSEPlannerService;
import com.aaron.pseplanner.service.implementation.DefaultCalculatorService;
import com.aaron.pseplanner.service.implementation.FacadePSEPlannerService;

import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Aaron on 2/18/2017.
 */

public class TradePlanActivity extends AppCompatActivity
{
    public static final String CLASS_NAME = TradePlanActivity.class.getSimpleName();
    private ArrayList<TradeDto> tradeDtoPlanList;
    private TradeDto selectedTradeDtoPlan;
    private FragmentStatePagerAdapter pagerAdapter;
    private CalculatorService calculatorService;
    private boolean tradePlanListUpdated;
    private CompositeDisposable compositeDisposable;

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

        this.calculatorService = new DefaultCalculatorService();
        this.compositeDisposable = new CompositeDisposable();

        initializeTradePlanAndList(savedInstanceState);

        String selectedTradePlanString = this.selectedTradeDtoPlan == null ? null : this.selectedTradeDtoPlan.toString();
        LogManager.debug(CLASS_NAME, "onCreate", "selected=" + selectedTradePlanString);

        initializeToolbarAndActionBar();

        this.pagerAdapter = new TradePlanPagerAdapter(getSupportFragmentManager(), this.tradeDtoPlanList, this.tradeDtoPlanList.size());
        setUpViewPager(this.pagerAdapter);

        this.tradePlanListUpdated = false;
    }

    private void initializeTradePlanAndList(Bundle savedInstanceState)
    {
        if(savedInstanceState != null)
        {
            setSelectedTradePlanAndListFromBundle(savedInstanceState);
        }
        else
        {
            final TickerDto ticker = getIntent().getParcelableExtra(DataKey.EXTRA_TICKER.toString());
            boolean isPreviousIntentFromTickerListFragment = ticker != null;

            if(isPreviousIntentFromTickerListFragment)
            {
                setSelectedTradePlanAndListFromDatabase(ticker);
            }
            else
            {
                setSelectedTradePlanAndListFromIntent(getIntent());
            }
        }
    }

    private void setSelectedTradePlanAndListFromBundle(Bundle savedInstanceState)
    {
        this.tradeDtoPlanList = savedInstanceState.getParcelableArrayList(DataKey.EXTRA_TRADE_LIST.toString());
        this.selectedTradeDtoPlan = savedInstanceState.getParcelable(DataKey.EXTRA_TRADE.toString());
    }

    private void setSelectedTradePlanAndListFromDatabase(TickerDto ticker)
    {
        PSEPlannerService pseService = new FacadePSEPlannerService(this);

        Date now = new Date();
        this.tradeDtoPlanList = pseService.getTradePlanListFromDatabase().blockingGet();
        for(TradeDto dto : this.tradeDtoPlanList)
        {
            setTradeDtoDaysField(dto, now);
            if(ticker.getSymbol().equals(dto.getSymbol()))
            {
                this.selectedTradeDtoPlan = dto;
                break;
            }
        }
    }

    /**
     * Set the daysToStopDate, holdingPeriod, and daysSincePlanned based on the current date (not last updated).
     */
    private void setTradeDtoDaysField(TradeDto tradeDto, Date now)
    {
        tradeDto.setDaysToStopDate(calculatorService.getDaysBetween(now, tradeDto.getStopDate()));
        if(tradeDto.getEntryDate() != null)
        {
            tradeDto.setHoldingPeriod(calculatorService.getDaysBetween(now, tradeDto.getEntryDate()));
        }
        else
        {
            tradeDto.setHoldingPeriod(0);
        }
        tradeDto.setDaysSincePlanned(calculatorService.getDaysBetween(now, tradeDto.getDatePlanned()));
    }

    private void setSelectedTradePlanAndListFromIntent(Intent intent)
    {
        this.tradeDtoPlanList = intent.getParcelableArrayListExtra(DataKey.EXTRA_TRADE_LIST.toString());
        this.selectedTradeDtoPlan = intent.getParcelableExtra(DataKey.EXTRA_TRADE.toString());
    }

    private void initializeToolbarAndActionBar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_trade_plan);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void setUpViewPager(PagerAdapter pagerAdapter)
    {
        int indexOfSelectedTradePlan = getIndexOfTradeDtoInTradeDtoList(this.selectedTradeDtoPlan);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(indexOfSelectedTradePlan);
    }

    /**
     * Saves current state in memory, when this activity is temporarily destroyed.
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        boolean tradeDtoPlanListNotEmpty = this.tradeDtoPlanList != null && !this.tradeDtoPlanList.isEmpty();
        if(tradeDtoPlanListNotEmpty)
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

        boolean requestFromUpdateTradePlan = IntentRequestCode.UPDATE_TRADE_PLAN.code() == requestCode;
        boolean requestHasExtraTrade = data.hasExtra(DataKey.EXTRA_TRADE.toString());

        if(requestFromUpdateTradePlan && requestHasExtraTrade)
        {
            int index = getIndexOfTradeDtoInTradeDtoList(this.selectedTradeDtoPlan);
            TradeDto newlySelectedTradeDtoPlan = data.getParcelableExtra(DataKey.EXTRA_TRADE.toString());

            updateSelectedTradeDtoPlanAndList(newlySelectedTradeDtoPlan, index);
            notifyChangeAndSetUpdated();
        }
    }

    private int getIndexOfTradeDtoInTradeDtoList(TradeDto tradeDto)
    {
        return tradeDtoPlanList.indexOf(tradeDto);
    }

    private void updateSelectedTradeDtoPlanAndList(TradeDto newlySelectedTradeDtoPlan, int index)
    {
        LogManager.debug(CLASS_NAME, "setNewSelectedTradeDtoPlanInList", "Extra Trade: " + newlySelectedTradeDtoPlan);
        this.selectedTradeDtoPlan = newlySelectedTradeDtoPlan;
        this.tradeDtoPlanList.set(index, newlySelectedTradeDtoPlan);
    }

    private void notifyChangeAndSetUpdated()
    {
        this.pagerAdapter.notifyDataSetChanged();
        this.tradePlanListUpdated = true;
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
                this.setActivityResultWithExtraTradeDtoPlanList();
                return super.onOptionsItemSelected(item);
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
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
     * Sets the trade plan list as activity result to MainActivity.
     */
    @Override
    public void onBackPressed()
    {
        this.setActivityResultWithExtraTradeDtoPlanList();
        super.onBackPressed();
    }

    private void setActivityResultWithExtraTradeDtoPlanList()
    {
        if(this.tradePlanListUpdated)
        {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(DataKey.EXTRA_TRADE_LIST.toString(), this.tradeDtoPlanList);
            setResult(RESULT_OK, intent);
        }
    }
}
