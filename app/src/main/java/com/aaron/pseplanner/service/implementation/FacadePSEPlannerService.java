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
import com.aaron.pseplanner.entity.TradeEntryDao;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.service.CalculatorService;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.HttpClient;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PSEPlannerService;
import com.aaron.pseplanner.service.SettingsService;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
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
    private CalculatorService calculatorService;

    private SharedPreferences sharedPreferences;
    private StockDao stockDao;
    private TradeDao tradeDao;
    private TradeEntryDao tradeEntryDao;

    public FacadePSEPlannerService(@NonNull Activity activity)
    {
        this.settingsService = new DefaultSettingsService(activity);
        SettingsDto settings = this.settingsService.getSettings();
        long timeout;
        String proxyHost = null;
        int proxyPort = 0;
        if(settings != null)
        {
            // Deduct by 1 so that it will not overlap the next request
            timeout = (settings.getRefreshInterval() > 0 ? settings.getRefreshInterval() : DEFAUT_TIMEOUT) - 1;

            proxyHost = settings.getProxyHost();
            proxyPort = settings.getProxyPort();
        }
        else
        {
            timeout = DEFAUT_TIMEOUT - 1;
        }

        if(StringUtils.isNotBlank(proxyHost) && proxyPort > 0)
        {
            this.phisixHttpClient = new PhisixHttpClient(timeout, timeout, timeout, proxyHost, proxyPort);
            this.pseHttpClient = new PSEHttpClient(timeout, timeout, timeout, proxyHost, proxyPort);
        }
        else
        {
            this.phisixHttpClient = new PhisixHttpClient(timeout, timeout, timeout);
            this.pseHttpClient = new PSEHttpClient(timeout, timeout, timeout);
        }

        this.formatService = new DefaultFormatService(activity);
        this.sharedPreferences = activity.getSharedPreferences(PSEPlannerPreference.class.getSimpleName(), Context.MODE_PRIVATE);
        this.calculatorService = new DefaultCalculatorService();

        // get the note DAO
        DaoSession daoSession = ((PSEPlannerApplication) activity.getApplication()).getDaoSession();
        this.stockDao = daoSession.getStockDao();
        this.tradeDao = daoSession.getTradeDao();
        this.tradeEntryDao = daoSession.getTradeEntryDao();
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
     *
     * @param preference the shared preference key, determines which last updated date will be retrieved
     * @return String the last updated formatted
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
    public Set<String> getTradeSymbolsFromTradeDtos(Collection<TradeDto> tradeDtos)
    {
        Set<String> tradeDtoSymbols = new HashSet<>();
        for(TradeDto dto : tradeDtos)
        {
            tradeDtoSymbols.add(dto.getSymbol());
        }

        return tradeDtoSymbols;
    }

    @Override
    public void setTickerDtoListHasTradePlan(Collection<TickerDto> tickerDtoList, Set<String> tradeDtoSymbols)
    {
        for(TickerDto dto : tickerDtoList)
        {
            if(tradeDtoSymbols.contains(dto.getSymbol()))
            {
                dto.setHasTradePlan(true);
            }
        }
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
        List<TradeEntry> tradeEntries = this.fromTradeEntryDtoToTradeEntry(tradeDto.getTradeEntries());

        this.tradeDao.insert(trade);
        this.tradeEntryDao.insertInTx(tradeEntries);

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
        // TODO: update trade entries

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
        trade.setGainToTarget(tradeDto.getGainToTarget().toPlainString());
        trade.setGainLoss(tradeDto.getGainLoss().toPlainString());
        trade.setGainLossPercent(tradeDto.getGainLossPercent().toPlainString());
        trade.setLossToStopLoss(tradeDto.getLossToStopLoss().toPlainString());
        trade.setStopLoss(tradeDto.getStopLoss().toPlainString());
        trade.setStopDate(tradeDto.getStopDate());
        trade.setPriceToBreakEven(tradeDto.getPriceToBreakEven().toPlainString());
        trade.setRiskReward(tradeDto.getRiskReward().toPlainString());

        return trade;
    }

    private List<TradeEntry> fromTradeEntryDtoToTradeEntry(List<TradeEntryDto> tradeEntries)
    {
        List<TradeEntry> tradeEntryList = new ArrayList<>(tradeEntries.size());

        int order = 0;
        for(TradeEntryDto dto : tradeEntries)
        {
            TradeEntry entry = new TradeEntry();
            entry.setTradeSymbol(dto.getSymbol());
            entry.setShares(dto.getShares());
            entry.setEntryPrice(dto.getEntryPrice().toPlainString());
            entry.setPercentWeight(dto.getPercentWeight().toPlainString());
            entry.setOrder(order);
            order++;

            tradeEntryList.add(entry);
        }

        return tradeEntryList;
    }

    @Override
    public ArrayList<TradeDto> getTradePlanListFromDatabase()
    {
        List<Trade> tradePlanList = this.tradeDao.loadAll();
        ArrayList<TradeDto> tradePlanDtoList = new ArrayList<>(tradePlanList.size());

        for(Trade trade : tradePlanList)
        {
            List<TradeEntry> tradeEntryList = trade.getTradeEntries();

            List<TradeEntryDto> tradeEntryDtos = new ArrayList<>(tradeEntryList.size());
            for(TradeEntry tradeEntry : tradeEntryList)
            {
                tradeEntryDtos.add(new TradeEntryDto(tradeEntry.getTradeSymbol(), tradeEntry.getEntryPrice(), tradeEntry.getShares(), tradeEntry.getPercentWeight()));
            }

            tradePlanDtoList.add(new TradeDto(trade.getSymbol(), trade.getEntryDate(), trade.getHoldingPeriod(), trade.getCurrentPrice(), trade.getAveragePrice(), trade.getTotalShares(), trade.getTotalAmount(), trade.getPriceToBreakEven(), trade.getTargetPrice(), trade.getGainLoss(), trade.getGainLossPercent(), trade.getGainToTarget(), trade.getStopLoss(), trade.getLossToStopLoss(), trade.getStopDate(), trade.getDaysToStopDate(), trade.getRiskReward(), trade.getCapital(), trade.getPercentCapital(), tradeEntryDtos));
        }

        LogManager.debug(CLASS_NAME, "getTradePlanListFromDatabase", "Retrieved: count = " + tradePlanDtoList.size());

        return tradePlanDtoList;
    }

    private boolean isWeekEnd(Calendar calendar)
    {
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    private boolean isTradingHours(Calendar calendar)
    {
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteOfHour = calendar.get(Calendar.MINUTE);

        boolean morningOpeningHour = hourOfDay == 9 && minuteOfHour >= 30;
        boolean morningHour = (morningOpeningHour || hourOfDay >= 10) && hourOfDay <= 12;
        boolean afternoonOpeningHour = hourOfDay == 13 && minuteOfHour >= 30;
        boolean afternoonClosingHour = hourOfDay == 15 && minuteOfHour <= 20;
        boolean afternoonHour = afternoonOpeningHour || hourOfDay == 14 || afternoonClosingHour;

        // Hour of day is between 9:30AM and 12PM AND between 1:30PM and 3:30PM
        return morningHour && afternoonHour;
    }

    /**
     * Checks if the market is open.
     * Monday to Friday, 9:30AM - 12:00PM and 1:30PM - 3:20PM
     *
     * @return true is market is open, else false
     */
    @Override
    public boolean isMarketOpen()
    {
        Calendar cal = Calendar.getInstance(FormatService.MANILA_TIMEZONE);

        boolean isWeekday = !isWeekEnd(cal);
        boolean isTradingHours = isTradingHours(cal);


        LogManager.debug(CLASS_NAME, "isMarketOpen", "isWeekday:" + isWeekday + " && isTradingHours:" + isTradingHours);

        return isWeekday && isTradingHours;
    }

    /**
     * Checks if the lastUpdated is up date with respect to the current time.
     *
     * @param preference the type of lastUpdated, either ticker or trade plan
     * @return true if up to date, else false
     */
    @Override
    public boolean isUpToDate(PSEPlannerPreference preference)
    {
        Calendar lastUpdated = Calendar.getInstance(FormatService.MANILA_TIMEZONE);
        lastUpdated.setTime(this.getLastUpdatedDate(preference.toString()));
        Calendar now = Calendar.getInstance(FormatService.MANILA_TIMEZONE);

        boolean isWeekEnd = isWeekEnd(now);
        boolean isWeekDay = !isWeekEnd;
        boolean lastUpdateEndOfHour = (int) lastUpdated.get(Calendar.HOUR_OF_DAY) == 15 && (int) lastUpdated.get(Calendar.MINUTE) == 20;
        boolean lastUpdateEndOfWeek = lastUpdated.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && lastUpdateEndOfHour;

        int daysDifference = this.calculatorService.getDaysBetween(lastUpdated.getTime(), now.getTime());

        LogManager.debug(CLASS_NAME, "isUpToDate", "(daysDifference:" + daysDifference + " < 3 && lastUpdateEndOfWeek:" + lastUpdateEndOfWeek + " && isWeekEnd:" + isWeekEnd + ")" + " || (daysDifference:" + daysDifference + " == 0 && lastUpdateEndOfHour:" + lastUpdateEndOfHour + " && isWeekDay:" + isWeekDay + ")");

        // (Days difference is just 2 days(sat and sun) AND lastUpdated is on Friday 3:20PM AND today is a weekend) OR
        // (There is no difference in days AND lastUpdated is 3:20PM AND today is a weekday)
        return (daysDifference < 3 && lastUpdateEndOfWeek && isWeekEnd) || (daysDifference == 0 && lastUpdateEndOfHour && isWeekDay);
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