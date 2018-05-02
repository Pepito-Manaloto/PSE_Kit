package com.aaron.pseplanner.service.implementation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.aaron.pseplanner.app.DaoSessionCreator;
import com.aaron.pseplanner.bean.SettingsDto;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.constant.PSEPlannerPreference;
import com.aaron.pseplanner.entity.DaoSession;
import com.aaron.pseplanner.entity.Ticker;
import com.aaron.pseplanner.entity.TickerDao;
import com.aaron.pseplanner.entity.Trade;
import com.aaron.pseplanner.entity.TradeDao;
import com.aaron.pseplanner.entity.TradeEntry;
import com.aaron.pseplanner.entity.TradeEntryDao;
import com.aaron.pseplanner.service.CalculatorService;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.HttpClient;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PSEPlannerService;
import com.aaron.pseplanner.service.SettingsService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Function;

import static com.aaron.pseplanner.service.BeanEntityUtils.fromTickerDtoToTicker;
import static com.aaron.pseplanner.service.BeanEntityUtils.fromTickerListToTickerDtoList;
import static com.aaron.pseplanner.service.BeanEntityUtils.fromTradeDtoToTrade;
import static com.aaron.pseplanner.service.BeanEntityUtils.fromTradeEntryDtoListToTradeEntryList;
import static com.aaron.pseplanner.service.BeanEntityUtils.fromTradeListToTradeDtoList;

/**
 * Created by aaron.asuncion on 1/31/2017.
 */
public class FacadePSEPlannerService implements PSEPlannerService
{
    public static final String CLASS_NAME = FacadePSEPlannerService.class.getSimpleName();
    private static final Date EPOCH_DATE = new Date(0);

    private static final int MORNING_OPENING_HOUR = 9;
    private static final int MORNING_OPENING_MINUTE = 30;
    private static final int LUNCH_HOUR = 12;
    private static final int AFTERNOON_OPENING_HOUR = 13;
    private static final int AFTERNOON_OPENING_MINUTE = 30;
    private static final int AFTERNOON_CLOSING_HOUR = 15;
    private static final int AFTERNOON_CLOSING_MINUTE = 30;

    private HttpClient phisixHttpClient;
    //private HttpClient pseHttpClient;
    private FormatService formatService;
    private SettingsService settingsService;
    private CalculatorService calculatorService;

    private CalendarWrapper calendarWrapper;
    private SharedPreferences sharedPreferences;
    private TickerDao tickerDao;
    private TradeDao tradeDao;
    private TradeEntryDao tradeEntryDao;

    public FacadePSEPlannerService(@NonNull Context context)
    {
        this.settingsService = new DefaultSettingsService(context);
        SettingsDto settings = this.settingsService.getSettings();
        initHttpClient(settings);

        this.formatService = new TradePlanFormatService(context);
        this.sharedPreferences = context.getSharedPreferences(PSEPlannerPreference.class.getSimpleName(), Context.MODE_PRIVATE);
        this.calculatorService = new DefaultCalculatorService();
        this.calendarWrapper = CalendarWrapper.newInstance();

        initDAO(context);
    }

    public FacadePSEPlannerService(@NonNull Activity activity, long connectionTimeout, long readTimeout, long pingInterval)
    {
        this.settingsService = new DefaultSettingsService(activity);
        SettingsDto settings = this.settingsService.getSettings();
        initHttpClient(connectionTimeout, readTimeout, pingInterval, settings.getProxyHost(), settings.getProxyPort());

        this.formatService = new TradePlanFormatService(activity);
        this.sharedPreferences = activity.getSharedPreferences(PSEPlannerPreference.class.getSimpleName(), Context.MODE_PRIVATE);
        this.calendarWrapper = CalendarWrapper.newInstance();

        initDAO(activity);
    }

    private void initHttpClient(SettingsDto settings)
    {
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

        initHttpClient(timeout, timeout, timeout, proxyHost, proxyPort);
    }

    private void initHttpClient(long connectionTimeout, long readTimeout, long pingInterval, String proxyHost, int proxyPort)
    {
        if(StringUtils.isNotBlank(proxyHost) && proxyPort > 0)
        {
            this.phisixHttpClient = new PhisixHttpClient(connectionTimeout, readTimeout, pingInterval, proxyHost, proxyPort);
            //this.pseHttpClient = new PSEHttpClient(connectionTimeout, readTimeout, pingInterval, proxyHost, proxyPort);
        }
        else
        {
            this.phisixHttpClient = new PhisixHttpClient(connectionTimeout, readTimeout, pingInterval);
            //this.pseHttpClient = new PSEHttpClient(connectionTimeout, readTimeout, pingInterval);
        }
    }

    private void initDAO(Context context)
    {
        DaoSession daoSession = ((DaoSessionCreator) context.getApplicationContext()).getDaoSession();
        this.tickerDao = daoSession.getTickerDao();
        this.tradeDao = daoSession.getTradeDao();
        this.tradeEntryDao = daoSession.getTradeEntryDao();
    }

    /**
     * Returns the datetime of when the last http request occurs. Gets the cached lastUpdated first if not null, else retrieve from database. Pattern: MMMM dd,
     * EEEE hh:mm:ss a Timezone: Manila, Philippines
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
     * Inserts the given TickerDto list as Ticker entity in the database.
     *
     * @param tickerDtoList the list of TickerDto
     * @param lastUpdated the last updated
     *
     * @return the inserted Ticker list
     */
    @Override
    public Set<Ticker> insertTickerList(List<TickerDto> tickerDtoList, Date lastUpdated)
    {
        Set<Ticker> tickerSet = insertUpdateTickerList(tickerDtoList, lastUpdated);

        LogManager.debug(CLASS_NAME, "insertTickerList", "Inserted: count = " + tickerSet.size());

        return tickerSet;
    }

    /**
     * Updates the given TickerDto list as Ticker entity in the database.
     *
     * @param tickerDtoList the list of TickerDto
     * @param lastUpdated the last updated
     *
     * @return the updated Ticker list
     */
    @Override
    public Set<Ticker> updateTickerList(List<TickerDto> tickerDtoList, Date lastUpdated)
    {
        Set<Ticker> tickerSet = insertUpdateTickerList(tickerDtoList, lastUpdated);

        LogManager.debug(CLASS_NAME, "updateTickerList", "Updated: count = " + tickerSet.size());

        return tickerSet;
    }

    private Set<Ticker> insertUpdateTickerList(List<TickerDto> tickerDtoList, Date lastUpdated)
    {
        if(tickerDtoList == null || tickerDtoList.isEmpty())
        {
            return Collections.emptySet();
        }

        Set<Ticker> tickerSet = this.getStockListAndUpdateLastUpdated(tickerDtoList, lastUpdated);

        // Bulk update
        this.tickerDao.insertOrReplaceInTx(tickerSet);

        return tickerSet;
    }

    private Set<Ticker> getStockListAndUpdateLastUpdated(List<TickerDto> tickerDtoList, Date lastUpdated)
    {
        Set<Ticker> tickerList = fromTickerDtoListToTickerList(tickerDtoList, lastUpdated);

        this.updateLastUpdatedSharedPreference(lastUpdated, PSEPlannerPreference.LAST_UPDATED_TICKER);
        LogManager.debug(CLASS_NAME, "getStockListAndUpdateLastUpdated", "Last updated = " + lastUpdated.toString());

        return tickerList;
    }

    private Set<Ticker> fromTickerDtoListToTickerList(List<TickerDto> tickerDtoList, Date lastUpdated)
    {
        Set<Ticker> tickerList = new HashSet<>(tickerDtoList.size());
        for(TickerDto dto : tickerDtoList)
        {
            Ticker ticker = fromTickerDtoToTicker(dto, lastUpdated);
            tickerList.add(ticker);
        }

        return tickerList;
    }

    /**
     * Retrieves the Ticker list from the database and converts to TickerDto list.
     *
     * @return {@code List<TickerDto>}
     */
    @Override
    public Single<ArrayList<TickerDto>> getTickerListFromDatabase()
    {
        return Single.fromCallable(new Callable<List<Ticker>>()
        {
            @Override
            public List<Ticker> call()
            {
                return tickerDao.queryBuilder().orderAsc(TickerDao.Properties.Symbol).list();
            }
        })
        .map(new Function<List<Ticker>, ArrayList<TickerDto>>()
        {
            @Override
            public ArrayList<TickerDto> apply(List<Ticker> tickerList)
            {
                ArrayList<TickerDto> tickerDtoList = fromTickerListToTickerDtoList(tickerList);

                LogManager.debug(CLASS_NAME, "getTickerListFromDatabase", "Retrieved: count = " + tickerDtoList.size());

                return tickerDtoList;
            }
        });
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
    public ArrayList<TickerDto> setTickerDtoListHasTradePlan(ArrayList<TickerDto> tickerDtoList, Set<String> tradeDtoSymbols)
    {
        for(TickerDto dto : tickerDtoList)
        {
            if(tradeDtoSymbols.contains(dto.getSymbol()))
            {
                dto.setHasTradePlan(true);
            }
        }

        return tickerDtoList;
    }

    @Override
    public boolean isTickerListSavedInDatabase()
    {
        long count = this.tickerDao.count();

        LogManager.debug(CLASS_NAME, "isTickerListSavedInDatabase", "Retrieved: count = " + count);

        return count >= getExpectedMinimumTotalStocks();
    }

    @Override
    public int getExpectedMinimumTotalStocks()
    {
        // TODO: Need to determine current total stocks dynamically
        return 243;
    }

    /**
     * Inserts the given TradeDto as Trade entity in the database.
     *
     * @param tradeDto the TradeDto
     *
     * @throws IllegalArgumentException if tradeDto is null
     * @return the inserted Trade
     */
    @Override
    public Trade insertTradePlan(final TradeDto tradeDto) throws IllegalArgumentException
    {
        if(tradeDto == null)
        {
            throw new IllegalArgumentException("TradeDto must not be null.");
        }

        updateLastUpdatedSharedPreferenceNow(PSEPlannerPreference.LAST_UPDATED_TRADE_PLAN);

        Trade trade = fromTradeDtoToTrade(tradeDto);
        this.tradeDao.insert(trade);

        List<TradeEntry> tradeEntries = fromTradeEntryDtoListToTradeEntryList(tradeDto.getTradeEntries());
        trade.setTradeEntriesTransient(tradeEntries);
        this.tradeEntryDao.insertInTx(tradeEntries);

        LogManager.debug(CLASS_NAME, "insertTradePlan", "Inserted: " + trade);

        return trade;
    }

    /**
     * Updates the given TradeDto list as Trade entity in the database.
     *
     * @param tradeDto the tradeDto
     *
     * @throws IllegalArgumentException if tradeDto is null
     * @return the updated Trade
     */
    @Override
    public Trade updateTradePlan(final TradeDto tradeDto) throws IllegalArgumentException
    {
        if(tradeDto == null)
        {
            throw new IllegalArgumentException("TradeDto must not be null.");
        }

        updateLastUpdatedSharedPreferenceNow(PSEPlannerPreference.LAST_UPDATED_TRADE_PLAN);

        final Trade trade = this.tradeDao.queryBuilder().where(TradeDao.Properties.Symbol.eq(tradeDto.getSymbol())).unique();
        final List<TradeEntry> tradeEntries = fromTradeEntryDtoListToTradeEntryList(tradeDto.getTradeEntries());

        // Execute delete and update/insert in one transaction
        this.tradeDao.getSession().runInTx(new Runnable()
        {
            @Override
            public void run()
            {
                // DELETE FROM TRADE_ENTRY WHERE tradeSymbol = tradeDto.symbol
                tradeEntryDao.queryBuilder().where(TradeEntryDao.Properties.TradeSymbol.eq(tradeDto.getSymbol()))
                        .buildDelete().executeDeleteWithoutDetachingEntities();
                tradeDao.update(trade);
                tradeEntryDao.insertInTx(tradeEntries);
            }
        });

        LogManager.debug(CLASS_NAME, "updateTradePlan", "Updated: " + trade);
        return trade;
    }

    /**
     * Delete the trade plan in the database.
     *
     * @param tradeDto the trade plan to delete
     *
     * @throws IllegalArgumentException if tradeDto is null
     * @return true if successful, else false
     */
    @Override
    public String deleteTradePlan(final TradeDto tradeDto) throws IllegalArgumentException
    {
        if(tradeDto == null)
        {
            throw new IllegalArgumentException("TradeDto must not be null.");
        }

        String symbol = tradeDto.getSymbol();

        // DELETE FROM TRADE WHERE symbol = tradeDto.symbol
        this.tradeDao.queryBuilder().where(TradeDao.Properties.Symbol.eq(symbol))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        // DELETE FROM TRADE_ENTRY WHERE tradeSymbol = tradeDto.symbol
        this.tradeEntryDao.queryBuilder().where(TradeEntryDao.Properties.TradeSymbol.eq(symbol))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        LogManager.debug(CLASS_NAME, "deleteTradePlan", "Deleted: " + symbol);

        return symbol;
    }

    @Override
    public Single<ArrayList<TradeDto>> getTradePlanListFromDatabase()
    {
        return Single.fromCallable(new Callable<List<Trade>>()
        {
            @Override
            public List<Trade> call()
            {
                return tradeDao.loadAll();
            }
        })
        .map(new Function<List<Trade>, ArrayList<TradeDto>>()
        {
            @Override
            public ArrayList<TradeDto> apply(List<Trade> tradePlanList)
            {
                ArrayList<TradeDto> tradePlanDtoList = fromTradeListToTradeDtoList(tradePlanList);

                LogManager.debug(CLASS_NAME, "getTradePlanListFromDatabase", "Retrieved: count = " + tradePlanDtoList.size());

                return tradePlanDtoList;
            }
        });
    }

    /**
     * Checks if the market is open. Monday to Friday, 9:30AM - 12:00PM and 1:30PM - 3:20PM
     *
     * @return true is market is open, else false
     */
    @Override
    public boolean isMarketOpen()
    {
        Calendar cal = calendarWrapper.newCalendar(FormatService.MANILA_TIMEZONE);

        boolean isWeekday = !isWeekEnd(cal);
        boolean isTradingHours = isTradingHours(cal);

        LogManager.debug(CLASS_NAME, "isMarketOpen", "isWeekday:" + isWeekday + " && isTradingHours:" + isTradingHours);

        return isWeekday && isTradingHours;
    }

    private boolean isWeekEnd(Calendar calendar)
    {
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    private boolean isTradingHours(Calendar calendar)
    {
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        boolean morningHour = isMorningHour(hourOfDay, minutes);
        boolean afternoonHour = isAfternoonHour(hourOfDay, minutes);

        // Hour of day is between 9:30AM and 12PM AND between 1:30PM and 3:30PM
        return morningHour || afternoonHour;
    }

    private boolean isMorningHour(int hourOfDay, int minutes)
    {
        boolean morningOpeningHour = hourOfDay == MORNING_OPENING_HOUR && minutes >= MORNING_OPENING_MINUTE;
        boolean morningBetweenHour = hourOfDay > MORNING_OPENING_HOUR && hourOfDay < LUNCH_HOUR;

        return morningOpeningHour || morningBetweenHour;
    }

    private boolean isAfternoonHour(int hourOfDay, int minutes)
    {
        boolean afternoonOpeningHour = hourOfDay == AFTERNOON_OPENING_HOUR && minutes >= AFTERNOON_OPENING_MINUTE;
        boolean afternoonBetweenHour = hourOfDay > AFTERNOON_OPENING_HOUR && hourOfDay < AFTERNOON_CLOSING_HOUR;
        boolean afternoonClosingHour = hourOfDay == AFTERNOON_CLOSING_HOUR && minutes < AFTERNOON_CLOSING_MINUTE;

        return afternoonOpeningHour || afternoonBetweenHour || afternoonClosingHour;
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
        // Use wrapper here to be able to mock current time
        Calendar now = calendarWrapper.newCalendar(FormatService.MANILA_TIMEZONE);

        boolean isWeekEnd = isWeekEnd(now);
        boolean isWeekDay = !isWeekEnd;
        boolean lastUpdateEndOfHour = lastUpdated.get(Calendar.HOUR_OF_DAY) == AFTERNOON_CLOSING_HOUR
                && lastUpdated.get(Calendar.MINUTE) >= AFTERNOON_CLOSING_MINUTE;
        boolean lastUpdateEndOfWeek = lastUpdated.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && lastUpdateEndOfHour;

        int daysDifference = this.calculatorService.getDaysBetween(lastUpdated.getTime(), now.getTime());

        LogManager.debug(CLASS_NAME, "isUpToDate",
                "(daysDifference:" + daysDifference + " < 3 && lastUpdateEndOfWeek:" + lastUpdateEndOfWeek + " && isWeekEnd:" + isWeekEnd + ")" +
                        " || (daysDifference:" + daysDifference + " == 0 && lastUpdateEndOfHour:" + lastUpdateEndOfHour + " && isWeekDay:" + isWeekDay + ")");

        // Days difference is just 2 days(sat and sun) AND lastUpdated is on Friday 3:30PM AND today is a weekend
        boolean lastUpdatedJustBeforeWeekend = daysDifference < 3 && lastUpdateEndOfWeek && isWeekEnd;
        // There is no difference in days AND lastUpdated is 3:30PM AND today is a weekday
        boolean lastUpdatedEndOfDayOnAWeekday = daysDifference == 0 && lastUpdateEndOfHour && isWeekDay;

        return lastUpdatedJustBeforeWeekend || lastUpdatedEndOfDayOnAWeekday;
    }

    @Override
    public Single<Pair<TickerDto, Date>> getTicker(String symbol)
    {
        return this.phisixHttpClient.getTicker(symbol).map(new Function<Pair<TickerDto, Date>, Pair<TickerDto, Date>>()
        {
            @Override
            public Pair<TickerDto, Date> apply(Pair<TickerDto, Date> pair)
            {
                updateLastUpdatedSharedPreference(pair.second, PSEPlannerPreference.LAST_UPDATED_TICKER);
                return pair;
            }
        });
    }

    @Override
    public Single<Pair<List<TickerDto>, Date>> getAllTickerList()
    {
        return this.phisixHttpClient.getAllTickerList().map(new Function<Pair<List<TickerDto>, Date>, Pair<List<TickerDto>, Date>>()
        {
            @Override
            public Pair<List<TickerDto>, Date> apply(Pair<List<TickerDto>, Date> pair)
            {
                updateLastUpdatedSharedPreference(pair.second, PSEPlannerPreference.LAST_UPDATED_TICKER);
                return pair;
            }
        });
    }

    @Override
    public Single<Pair<List<TickerDto>, Date>> getTickerList(Collection<String> symbols)
    {
        return this.phisixHttpClient.getTickerList(symbols).map(new Function<Pair<List<TickerDto>, Date>, Pair<List<TickerDto>, Date>>()
        {
            @Override
            public Pair<List<TickerDto>, Date> apply(Pair<List<TickerDto>, Date> pair)
            {
                updateLastUpdatedSharedPreference(pair.second, PSEPlannerPreference.LAST_UPDATED_TICKER);
                return pair;
            }
        });
    }

    /**
     * Updates the last updated in the shared preferences with the current datetime.
     */
    private void updateLastUpdatedSharedPreferenceNow(PSEPlannerPreference preference)
    {
        Date now = new Date();
        updateLastUpdatedSharedPreference(now, preference);
    }

    /**
     * Updates the last updated in the shared preferences.
     *
     * @param now the current datetime
     */
    private void updateLastUpdatedSharedPreference(Date now, PSEPlannerPreference preference)
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
            lastUpdated = EPOCH_DATE;
        }

        return lastUpdated;
    }
}