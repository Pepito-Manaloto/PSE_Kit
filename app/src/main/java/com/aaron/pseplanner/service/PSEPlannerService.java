package com.aaron.pseplanner.service;

import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.constant.PSEPlannerPreference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import io.reactivex.Single;

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
     * @return String the last updated formatted
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
     * @return {@code Single<ArrayList<TickerDto>>} the Single observable list of ticker dto
     */
    Single<ArrayList<TickerDto>> getTickerListFromDatabase();

    /**
     * Converts the collections of tradeDtos to a set of trade symbols.
     *
     * @param tradeDtos the collection of trade symbols
     * @return {@code Set<String>} the set of stock symbols
     */
    Set<String> getTradeSymbolsFromTradeDtos(Collection<TradeDto> tradeDtos);

    /**
     * Sets each TickerDto's hasTradePlan to true if there is a corresponding TradePlan.
     *
     * @param tickerDtoList   the tickerDtoList to transform
     * @param tradeDtoSymbols the TradePlan symbols used in checking if a tickerDto hasTradePlan
     *
     * @return ArrayList<TickerDto> updated ticker dto list
     */
    ArrayList<TickerDto> setTickerDtoListHasTradePlan(ArrayList<TickerDto> tickerDtoList, Set<String> tradeDtoSymbols);

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
    boolean updateTradePlan(TradeDto tradeDtoFirst);

    /**
     * Deletes the trade dto in the database.
     */
    boolean deleteTradePlan(TradeDto tradeDto);

    /**
     * Retrieves the list of trade plans from the database.
     *
     * @return {@code Single<ArrayList<TradeDto>>} the list of trade plan dto
     */
    Single<ArrayList<TradeDto>> getTradePlanListFromDatabase();

    /**
     * Checks if the market is open.
     * Monday to Friday, 9:30AM - 12:00PM and 1:30PM - 3:20PM
     *
     * @return true is market is open, else false
     */
    boolean isMarketOpen();

    /**
     * Checks if the lastUpdated is up date with respect to the current time.
     *
     * @param preference the type of lastUpdated, either ticker or trade plan
     * @return true if up to date, else false
     */
    boolean isUpToDate(PSEPlannerPreference preference);
}
