package com.aaron.pseplanner.service.implementation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.aaron.pseplanner.app.PSEPlannerApplication;
import com.aaron.pseplanner.bean.SettingsDto;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.bean.TradeEntryDto;
import com.aaron.pseplanner.constant.PSEPlannerPreference;
import com.aaron.pseplanner.entity.DaoSession;
import com.aaron.pseplanner.entity.Stock;
import com.aaron.pseplanner.entity.StockDao;
import com.aaron.pseplanner.entity.Trade;
import com.aaron.pseplanner.entity.TradeDao;
import com.aaron.pseplanner.entity.TradeEntry;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.HttpClient;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PSEPlannerService;
import com.aaron.pseplanner.service.SettingsService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by aaron.asuncion on 1/31/2017.
 */

public class FacadePSEPlannerService implements PSEPlannerService
{
    public static final String CLASS_NAME = FacadePSEPlannerService.class.getSimpleName();
    private static final Date EPOCH_DATE = new Date(0);

    private HttpClient phisixHttpClient;
    private HttpClient pseHttpClient;
    private FormatService formatService;
    private SettingsService settingsService;

    private SharedPreferences sharedPreferences;
    private StockDao stockDao;
    private TradeDao tradeDao;

    public FacadePSEPlannerService(@NonNull Activity activity)
    {
        this.settingsService = new DefaultSettingsService(activity);
        SettingsDto settings = this.settingsService.getSettings();
        // Deduct by 1 so that it will not overlap the next request
        long timeout = (settings.getRefreshInterval() > 0 ? settings.getRefreshInterval() : DEFAUT_TIMEOUT) - 1;

        if(StringUtils.isNotBlank(settings.getProxyHost()) && settings.getProxyPort() > 0)
        {
            this.phisixHttpClient = new PhisixHttpClient(timeout, timeout, timeout, settings.getProxyHost(), settings.getProxyPort());
            this.pseHttpClient = new PSEHttpClient(timeout, timeout, timeout, settings.getProxyHost(), settings.getProxyPort());
        }
        else
        {
            this.phisixHttpClient = new PhisixHttpClient(timeout, timeout, timeout);
            this.pseHttpClient = new PSEHttpClient(timeout, timeout, timeout);
        }

        this.formatService = new DefaultFormatService(activity);
        this.sharedPreferences = activity.getSharedPreferences(PSEPlannerPreference.class.getSimpleName(), Context.MODE_PRIVATE);

        // get the note DAO
        DaoSession daoSession = ((PSEPlannerApplication) activity.getApplication()).getDaoSession();
        this.stockDao = daoSession.getStockDao();
        this.tradeDao = daoSession.getTradeDao();
    }

    public FacadePSEPlannerService(@NonNull Activity activity, long connectionTimeout, long readTimeout, long pingInterval)
    {
        this.settingsService = new DefaultSettingsService(activity);
        SettingsDto settings = this.settingsService.getSettings();
        if(StringUtils.isNotBlank(settings.getProxyHost()) && settings.getProxyPort() > 0)
        {
            this.phisixHttpClient = new PhisixHttpClient(connectionTimeout, readTimeout, pingInterval, settings.getProxyHost(), settings.getProxyPort());
            this.pseHttpClient = new PSEHttpClient(connectionTimeout, readTimeout, pingInterval, settings.getProxyHost(), settings.getProxyPort());
        }
        else
        {
            this.phisixHttpClient = new PhisixHttpClient(connectionTimeout, readTimeout, pingInterval);
            this.pseHttpClient = new PSEHttpClient(connectionTimeout, readTimeout, pingInterval);
        }

        this.formatService = new DefaultFormatService(activity);
        this.sharedPreferences = activity.getSharedPreferences(PSEPlannerPreference.class.getSimpleName(), Context.MODE_PRIVATE);

        // get the note DAO
        DaoSession daoSession = ((PSEPlannerApplication) activity.getApplication()).getDaoSession();
        this.stockDao = daoSession.getStockDao();
        this.tradeDao = daoSession.getTradeDao();
    }

    /**
     * Returns the datetime of when the last http request occurs. Gets the cached lastUpdated first if not null, else retrieve from database.
     * Pattern: MMMM dd, EEEE hh:mm:ss a
     * Timezone: Manila, Philippines
     */
    @Override
    public String getLastUpdated(String preference)
    {
        Date lastUpdated = this.getLastUpdatedDate(preference);

        LogManager.debug(CLASS_NAME, "getLastUpdated", lastUpdated.toString());

        return this.formatService.formatLastUpdated(lastUpdated);
    }

    /**
     * Inserts the given TickerDto list as Stock entity in the database.
     *
     * @param tickerDtoList the list of TickerDto
     * @return true if successful, else false
     */
    @Override
    public boolean insertTickerList(List<TickerDto> tickerDtoList)
    {
        Set<Stock> stockList = this.getStockListAndUpdateLastUpdated(tickerDtoList);

        // Bulk insert
        this.stockDao.insertOrReplaceInTx(stockList);

        LogManager.debug(CLASS_NAME, "insertTickerList", "Inserted: count = " + stockList.size());

        return true;
    }

    /**
     * Updates the given TickerDto list as Stock entity in the database.
     *
     * @param tickerDtoList the list of TickerDto
     * @return true if successful, else false
     */
    @Override
    public boolean updateTickerList(List<TickerDto> tickerDtoList)
    {
        Set<Stock> stockList = this.getStockListAndUpdateLastUpdated(tickerDtoList);

        // Bulk update
        this.stockDao.updateInTx(stockList);

        LogManager.debug(CLASS_NAME, "updateTickerList", "Updated: count = " + stockList.size());

        return true;
    }

    private Set<Stock> getStockListAndUpdateLastUpdated(List<TickerDto> tickerDtoList)
    {
        Date lastUpdated = this.getLastUpdatedDate(PSEPlannerPreference.LAST_UPDATED_TICKER.toString());
        Set<Stock> stockList = new HashSet<>(tickerDtoList.size());

        // Convert each TickerDto to Stock and store in a Set
        for(TickerDto dto : tickerDtoList)
        {
            Stock stock = this.fromTickerDtoToStock(dto, lastUpdated);
            stockList.add(stock);
        }

        this.updateLastUpdated(lastUpdated, PSEPlannerPreference.LAST_UPDATED_TICKER);
        LogManager.debug(CLASS_NAME, "getStockListAndUpdateLastUpdated", "Last updated = " + lastUpdated.toString());

        return stockList;
    }

    /**
     * Retrieves the Stock list from the database and converts to TickerDto list.
     *
     * @return {@code List<TickerDto>}
     */
    @Override
    public ArrayList<TickerDto> getTickerListFromDatabase()
    {
        List<Stock> stockList = this.stockDao.queryBuilder().orderAsc(StockDao.Properties.Symbol).list();
        ArrayList<TickerDto> tickerDtoList = new ArrayList<>(stockList.size());

        for(Stock stock : stockList)
        {
            tickerDtoList.add(new TickerDto(stock.getSymbol(), stock.getName(), stock.getVolume(), stock.getCurrentPrice(), stock.getChange(), stock.getPercentChange()));
        }

        LogManager.debug(CLASS_NAME, "getTickerListFromDatabase", "Retrieved: count = " + tickerDtoList.size());

        return tickerDtoList;
    }

    @Override
    public boolean isTickerListSavedInDatabase()
    {
        long count = this.stockDao.count();

        LogManager.debug(CLASS_NAME, "isTickerListSavedInDatabase", "Retrieved: count = " + count);

        return count > getExpectedMinimumTotalStocks();
    }

    @Override
    public int getExpectedMinimumTotalStocks()
    {
        // TODO: Need to determine current total stocks
        return 200;
    }

    /**
     * Inserts the given TradeDto as Trade entity in the database.
     *
     * @param tradeDto the TradeDto
     * @return true if successful, else false
     */
    @Override
    public boolean insertTradePlan(TradeDto tradeDto)
    {
        Date now = new Date();
        this.updateLastUpdated(now, PSEPlannerPreference.LAST_UPDATED_TRADE_PLAN);
        Trade trade = this.fromTradeDtoToTrade(tradeDto, now);

        this.tradeDao.insert(trade);

        LogManager.debug(CLASS_NAME, "insertTradePlan", "Inserted: dto = " + tradeDto);

        return true;
    }


    /**
     * Updates the given TradeDto list as Trade entity in the database.
     *
     * @param tradeDtoFirst the first tradeDto, ensures this method will not be called with an empty vararg
     * @param tradeDtos     the list of TradeDto
     * @return true if successful, else false
     */
    @Override
    public boolean updateTradePlan(TradeDto tradeDtoFirst, TradeDto... tradeDtos)
    {
        Date now = new Date();
        Set<Trade> tradeList = new HashSet<>(tradeDtos.length);
        tradeList.add(this.fromTradeDtoToTrade(tradeDtoFirst, now));

        for(TradeDto dto : tradeDtos)
        {
            Trade trade = this.fromTradeDtoToTrade(dto, now);
            tradeList.add(trade);
        }

        this.tradeDao.updateInTx(tradeList);

        LogManager.debug(CLASS_NAME, "updateTradePlan", "Updated: count = " + tradeList.size());

        return true;
    }

    private Trade fromTradeDtoToTrade(TradeDto tradeDto, Date now)
    {
        Trade trade = new Trade();

        return this.fromTradeDtoToTrade(trade, tradeDto, now);
    }

    private Trade fromTradeDtoToTrade(Trade trade, TradeDto tradeDto, Date now)
    {
        trade.setSymbol(tradeDto.getSymbol());
        trade.setCurrentPrice(tradeDto.getCurrentPrice().toPlainString());
        trade.setAveragePrice(tradeDto.getAveragePrice().toPlainString());
        trade.setPercentCapital(tradeDto.getPercentCapital().toPlainString());
        trade.setCapital(tradeDto.getCapital());
        trade.setTotalAmount(tradeDto.getTotalAmount().toPlainString());
        trade.setTotalShares(tradeDto.getTotalShares());
        trade.setEntryDate(tradeDto.getEntryDate());
        trade.setDaysToStopDate(tradeDto.getDaysToStopDate());
        trade.setHoldingPeriod(tradeDto.getHoldingPeriod());
        trade.setTargetPrice(tradeDto.getTargetPrice().toPlainString());
        trade.setGainToTarget(tradeDto.getGainToTarget());
        trade.setGainLoss(tradeDto.getGainLoss().toPlainString());
        trade.setGainLossPercent(tradeDto.getGainLossPercent().toPlainString());
        trade.setLossToStopLoss(tradeDto.getLossToStopLoss().toPlainString());
        trade.setStopLoss(tradeDto.getStopLoss().toPlainString());
        trade.setStopDate(tradeDto.getStopDate());
        trade.setPriceToBreakEven(tradeDto.getPriceToBreakEven().toPlainString());
        trade.setRiskReward(tradeDto.getRiskReward().toPlainString());

        return trade;
    }

    @Override
    public ArrayList<TradeDto> getTradePlanListFromDatabase()
    {
        List<Trade> tradePlanList = this.tradeDao.loadAll();
        ArrayList<TradeDto> tradePlanDtoList = new ArrayList<>(tradePlanList.size());

        for(Trade trade : tradePlanList)
        {
            List<TradeEntryDto> tradeEntryDtos = new ArrayList<>(trade.getTradeEntries().size());
            for(TradeEntry tradeEntry : trade.getTradeEntries())
            {
                tradeEntryDtos.add(new TradeEntryDto(tradeEntry.getTradeSymbol(), tradeEntry.getEntryPrice(), tradeEntry.getShares(), tradeEntry.getPercentWeight()));
            }

            tradePlanDtoList.add(new TradeDto(trade.getSymbol(), trade.getEntryDate(), trade.getHoldingPeriod(), trade.getCurrentPrice(), trade.getAveragePrice(), trade.getTotalShares(), trade.getTotalAmount(), trade.getPriceToBreakEven(), trade.getTargetPrice(), trade.getGainLoss(), trade.getGainLossPercent(), trade.getGainToTarget(), trade.getStopLoss(), trade.getLossToStopLoss(), trade.getStopDate(), trade.getDaysToStopDate(), trade.getRiskReward(), trade.getCapital(), trade.getPercentCapital(), tradeEntryDtos));
        }

        LogManager.debug(CLASS_NAME, "getTradePlanListFromDatabase", "Retrieved: count = " + tradePlanDtoList.size());

        return tradePlanDtoList;
    }

    @Override
    public Pair<TickerDto, Date> getTicker(String symbol) throws HttpRequestException
    {
        Pair<TickerDto, Date> pair = this.phisixHttpClient.getTicker(symbol);
        updateLastUpdated(pair.second, PSEPlannerPreference.LAST_UPDATED_TICKER);

        return pair;
    }

    @Override
    public Pair<List<TickerDto>, Date> getAllTickerList() throws HttpRequestException
    {
        Pair<List<TickerDto>, Date> pair = this.phisixHttpClient.getAllTickerList();
        updateLastUpdated(pair.second, PSEPlannerPreference.LAST_UPDATED_TICKER);

        return pair;
    }

    @Override
    public Pair<List<TickerDto>, Date> getTickerList(Collection<String> symbols) throws HttpRequestException
    {
        Pair<List<TickerDto>, Date> pair = this.phisixHttpClient.getTickerList(symbols);
        updateLastUpdated(pair.second, PSEPlannerPreference.LAST_UPDATED_TICKER);

        return pair;
    }

    /**
     * Converts TickerDto to Stock entity.
     *
     * @param dto the dto to convert
     * @param now the current datetime
     * @return Stock the converted entity
     */
    private Stock fromTickerDtoToStock(TickerDto dto, Date now)
    {
        Stock stock = new Stock();

        return this.fromTickerDtoToStock(stock, dto, now);
    }

    /**
     * Replaces the values of the Stock entity with the TickerDto.
     *
     * @param stock the stock to replace values
     * @param dto   the dto to get the values from
     * @param now   the current datetime
     * @return Stock the passed stock with its properties replaced
     */
    private Stock fromTickerDtoToStock(Stock stock, TickerDto dto, Date now)
    {
        stock.setSymbol(dto.getSymbol());
        stock.setName(dto.getName());
        stock.setVolume(dto.getVolume());
        stock.setCurrentPrice(dto.getCurrentPrice().toPlainString());
        stock.setChange(dto.getChange().toPlainString());
        stock.setPercentChange(dto.getPercentChange().toPlainString());
        stock.setDateUpdate(now);

        return stock;
    }

    /**
     * Updates the last updated in the shared preferences.
     *
     * @param now the current datetime
     */
    private void updateLastUpdated(Date now, PSEPlannerPreference preference)
    {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putLong(preference.toString(), now.getTime());
        editor.apply();
    }

    /**
     * Retrieves the last updated from the shared preference, if it does not exist then return Epoch Date.
     *
     * @return Date the last updated date
     */
    private Date getLastUpdatedDate(String preference)
    {
        Date lastUpdated;
        if(this.sharedPreferences.contains(preference))
        {
            lastUpdated = new Date(this.sharedPreferences.getLong(preference, 0)); // We are sure that this preference exists, 0 will never be returned
        }
        else
        {
            lastUpdated = EPOCH_DATE; // Shouldn't happen because shared preference is set in onCreate of MainActivity
        }

        return lastUpdated;
    }
}