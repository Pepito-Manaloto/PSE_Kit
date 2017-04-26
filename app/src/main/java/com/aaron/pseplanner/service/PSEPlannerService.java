package com.aaron.pseplanner.service;

import android.icu.text.TimeZoneNames;

import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by aaron.asuncion on 1/31/2017.
 */

public interface PSEPlannerService extends HttpClient
{
    /**
     * Returns the datetime of when the last http request occurs.
     * Pattern: MMMM dd, EEEE hh:mm:ss a
     * Timezone: Manila, Philippines
     *
     * @param preference the shared preference key, determines which last updated date will be retrieved
     * @return String the last updated formatted date
     */
    String getLastUpdated(String preference);

    /**
     * Inserts the list ticker dto in the database.
     */
    boolean insertTickerList(List<TickerDto> tickerDtoList);

    /**
     * Updates the list of ticker dto in the database.
     */
    boolean updateTickerList(List<TickerDto> tickerDtoList);

    /**
     * Retrieves the list of tickers from the database.
     *
     * @return {@code List<TickerDto>} the list of ticker dto
     */
    ArrayList<TickerDto> getTickerListFromDatabase();

    /**
     * Returns true if the ticker list is already in the database.
     *
     * @return true if exist, else false
     */
    boolean isTickerListSavedInDatabase();

    /**
     * Returns the expected minimum total stocks in PSE.
     *
     * @return the minimum total stocks
     */
    int getExpectedMinimumTotalStocks();

    /**
     * Inserts the trade dto in the database.
     */
    boolean insertTradePlan(TradeDto tradeDto);

    /**
     * Updates the list of trade dto in the database.
     */
    boolean updateTradePlan(TradeDto tradeDtoFirst, TradeDto... tradeDtos);

    /**
     * Retrieves the list of trade plans from the database.
     *
     * @return {@code List<TradeDto>} the list of trade plan dto
     */
    ArrayList<TradeDto> getTradePlanListFromDatabase();
}
