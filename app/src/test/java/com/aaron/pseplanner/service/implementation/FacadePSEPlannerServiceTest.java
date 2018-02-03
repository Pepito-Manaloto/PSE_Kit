package com.aaron.pseplanner.service.implementation;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.aaron.pseplanner.app.TestApplication;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;
import com.aaron.pseplanner.bean.TradeEntryDto;
import com.aaron.pseplanner.constant.PSEPlannerPreference;
import com.aaron.pseplanner.entity.DaoSession;
import com.aaron.pseplanner.entity.Ticker;
import com.aaron.pseplanner.entity.TickerDao;
import com.aaron.pseplanner.entity.Trade;
import com.aaron.pseplanner.entity.TradeDao;
import com.aaron.pseplanner.entity.TradeEntry;
import com.aaron.pseplanner.entity.TradeEntryDao;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.HttpClient;
import com.aaron.pseplanner.test.utils.DayOfWeek;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;

import static com.aaron.pseplanner.test.utils.UnitTestUtils.newDate;
import static com.aaron.pseplanner.test.utils.UnitTestUtils.newDateTime;
import static com.aaron.pseplanner.test.utils.UnitTestUtils.newTime;
import static com.aaron.pseplanner.test.utils.UnitTestUtils.randomSecondOrMinute;
import static com.aaron.pseplanner.test.utils.UnitTestUtils.randomSecondOrMinuteMax29;
import static com.aaron.pseplanner.test.utils.UnitTestUtils.randomSecondOrMinuteMin30;
import static com.aaron.pseplanner.test.utils.UnitTestUtils.setPrivateField;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Aaron on 06/01/2018.
 */

public class FacadePSEPlannerServiceTest extends AbstractHttpClientTest
{
    private static final String LAST_UPDATED_PREFERENCE = "last-updated-preference";
    private static final Date LAST_UPDATED = newDateTime(2017, 5, 27, 5, 32, 48);
    private static final String formattedLastUpdated = "May 27, Saturday 05:32:48 AM";
    private static final String epochLastUpdated = "January 01, Thursday 08:00:00 AM";

    @Mock
    private TickerDao tickerDao;

    @Mock
    private TradeDao tradeDao;

    @Mock
    private TradeEntryDao tradeEntryDao;

    @Mock
    private HttpClient httpClient;

    @Mock
    private CalendarWrapper calendarWrapper;

    private FacadePSEPlannerService service;

    @Before
    public void init() throws Exception
    {
        DaoSession daoSession = ((TestApplication) getContext()).getDaoSession();
        when(daoSession.getTickerDao()).thenReturn(tickerDao);
        when(daoSession.getTradeDao()).thenReturn(tradeDao);
        when(daoSession.getTradeEntryDao()).thenReturn(tradeEntryDao);

        service = new FacadePSEPlannerService(getContext());
        setPrivateField(service, "calendarWrapper", calendarWrapper);
        setPrivateField(service, "phisixHttpClient", httpClient);
    }

    @Test
    public void givenLastUpdated_whenGetLastUpdated_thenLastUpdatedShouldBeReturned()
    {
        givenLastUpdated(LAST_UPDATED_PREFERENCE);

        String lastUpdated = whenGetLastUpdated(LAST_UPDATED_PREFERENCE);

        thenLastUpdatedShouldBeReturned(lastUpdated);
    }

    @Test
    public void givenLastUpdatedFromOtherPreference_whenGetLastUpdated_thenEmptyLastUpdatedShouldBeReturned()
    {
        String otherPreference = "other";
        givenLastUpdated(otherPreference);

        String lastUpdated = whenGetLastUpdated(LAST_UPDATED_PREFERENCE);

        thenEpochLastUpdatedShouldBeReturned(lastUpdated);
    }

    @Test
    public void givenNothing_whenGetLastUpdated_thenEmptyLastUpdatedShouldBeReturned()
    {
        String lastUpdated = whenGetLastUpdated("");

        thenEpochLastUpdatedShouldBeReturned(lastUpdated);
    }

    @Test
    public void givenTickerDtoList_whenInsertTickerList_thenTickerDtoListConvertedToTickerSetAndIsInsertedAndLastUpdatedRefreshed()
    {
        List<String> nameList = Arrays.asList("Alsons Consolidated Resource", "Calata Corporation", "Eagle Cement Corporation", "Philippine National Bank",
                "Wilcon Depot, Inc.");
        List<String> symbolList = Arrays.asList("ACR", "CAL", "EAGLE", "PNB", "WLCON");
        List<Double> percentChangeList = Arrays.asList(13.89, -29.12, 0.10, 3.93, 37.05);
        List<Long> volumeList = Arrays.asList(1_719_000L, 380_000L, 153_400L, 19_800L, 730_000L);
        List<Double> amountList = Arrays.asList(1.35, 2.31, 14.44, 57.15, 8.03);

        List<TickerDto> tickerDtoList = givenTickerDtoList(nameList, symbolList, percentChangeList, volumeList, amountList);

        Set<Ticker> tickerSet = whenInsertTickerList(tickerDtoList, LAST_UPDATED);

        thenTickerDtoListConvertedToTickerSetAndIsInsertedAndLastUpdatedRefreshed(tickerDtoList, tickerSet, getLastUpdatedTickerPreference());
    }

    @Test
    public void givenEmptyList_whenInsertTickerList_thenNothingIsInsertedAndLastUpdatedNotRefreshed()
    {
        Set<Ticker> tickerSet = whenInsertTickerList(Collections.<TickerDto> emptyList(), LAST_UPDATED);

        thenNothingIsInsertedAndLastUpdatedNotRefreshed(tickerSet, getLastUpdatedTickerPreference());
    }

    @Test
    public void givenTickerDtoList_whenUpdateTickerList_thenTickerDtoListConvertedToTickerSetAndIsInsertedAndLastUpdatedRefreshed()
    {
        List<String> nameList = Arrays.asList("Alsons Consolidated Resource", "Calata Corporation", "Eagle Cement Corporation", "Philippine National Bank",
                "Wilcon Depot, Inc.");
        List<String> symbolList = Arrays.asList("ACR", "CAL", "EAGLE", "PNB", "WLCON");
        List<Double> percentChangeList = Arrays.asList(13.89, -29.12, 0.10, 3.93, 37.05);
        List<Long> volumeList = Arrays.asList(1_719_000L, 380_000L, 153_400L, 19_800L, 730_000L);
        List<Double> amountList = Arrays.asList(1.35, 2.31, 14.44, 57.15, 8.03);

        List<TickerDto> tickerDtoList = givenTickerDtoList(nameList, symbolList, percentChangeList, volumeList, amountList);

        Set<Ticker> tickerSet = whenUpdateTickerList(tickerDtoList, LAST_UPDATED);

        thenTickerDtoListConvertedToTickerSetAndIsInsertedAndLastUpdatedRefreshed(tickerDtoList, tickerSet, getLastUpdatedTickerPreference());
    }

    @Test
    public void givenEmptyList_whenUpdateTickerList_thenNothingIsInsertedAndLastUpdatedNotRefreshed()
    {
        Set<Ticker> tickerSet = whenUpdateTickerList(Collections.<TickerDto> emptyList(), LAST_UPDATED);

        thenNothingIsInsertedAndLastUpdatedNotRefreshed(tickerSet, getLastUpdatedTickerPreference());
    }

    @Test
    public void givenTradeDtoList_whenGetTradeSymbolsFromTradeDtos_thenTradeSymbolsFromTradeDtoListShouldBeReturned()
    {
        // Note values are random and does not reflect correct computation
        List<Long> id = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        List<String> symbol = Arrays.asList("ACR", "CHP", "HOUSE", "PCOR", "VUL");
        List<Date> entryDate = Arrays.asList(newDate(2018, 5, 13), newDate(2018, 6, 15),
                newDate(2018, 8, 19), newDate(2018, 10, 25), newDate(2018, 12, 7));
        List<Integer> holdingPeriod = Arrays.asList(5, 17, 28, 31, 71);
        List<String> currentPrice = Arrays.asList("1.31", "4.64", "6.29", "9.1", "0.82");
        List<String> averagePrice = Arrays.asList("1.32", "4.68", "6.56", "9.21", "0.828");
        List<Long> totalShares = Arrays.asList(3_000_000L, 1_454_000L, 420_000L, 51_000L, 8_900_000L);
        List<String> totalAmount = Arrays.asList("12345", "5678", "7942", "7526", "2299");
        List<String> priceToBreakEven = Arrays.asList("1.33", "4.75", "6.66", "9.31", "0.84");
        List<String> targetPrice = Arrays.asList("1.43", "5.75", "7.66", "11.31", "0.94");
        List<String> gainLoss = Arrays.asList("8785676", "-5684674", "252342", "-45465", "5464564");
        List<String> gainLossPercent = Arrays.asList("-235", "74.75", "68.66", "-97.31", "60.84");
        List<String> gainToTarget = Arrays.asList("7685457", "-7857754", "23246", "878768", "-789696");
        List<String> stopLoss = Arrays.asList("1.13", "4.35", "6.16", "9.0", "0.80");
        List<String> lossToStopLoss = Arrays.asList("653435", "763452", "635435", "67587", "6787868");
        List<Date> stopDate = Arrays.asList(newDate(2018, 6, 13), newDate(2018, 7, 15),
                newDate(2018, 9, 19), newDate(2018, 11, 25), newDate(2019, 1, 7));
        List<Integer> daysToStopDate = Arrays.asList(30, 30, 30, 30, 30);
        List<String> riskReward = Arrays.asList("3.43", "2", "4.5", "2.64", "1.9");
        List<Long> capital = Arrays.asList(10_000_000L, 80_000_000L, 5_000_000L, 3_000_000L, 14_000_000L);
        List<String> percentCapital = Arrays.asList("30", "5.85", "8.67", "84.05", "100");
        List<List<TradeEntryDto>> tradeEntries = createTradeEntryDtos(symbol, currentPrice, totalShares, gainLossPercent);

        Collection<TradeDto> tradeDtoList = givenTradeDtoList(id, symbol, entryDate, holdingPeriod, currentPrice, averagePrice, totalShares,
                totalAmount, priceToBreakEven, targetPrice, gainLoss, gainLossPercent, gainToTarget, stopLoss, lossToStopLoss, stopDate,
                daysToStopDate, riskReward, capital, percentCapital, tradeEntries);

        Set<String> tradeSymbols = whenGetTradeSymbolsFromTradeDtos(tradeDtoList);

        thenTradeSymbolsFromTradeDtoListShouldBeReturned(tradeSymbols, tradeDtoList);
    }

    @Test
    public void givenTickerListDtoAndTradeSymbols_whenSetTickerDtoListHasTradePlan_thenTickerListFoundInTradeSymbolsShouldHaveHasTradePlanSetToTrue()
    {
        List<String> nameList = Arrays.asList("Alsons Consolidated Resource", "Calata Corporation", "Eagle Cement Corporation", "Philippine National Bank",
                "Wilcon Depot, Inc.");
        List<String> symbolList = Arrays.asList("ACR", "CAL", "EAGLE", "PNB", "WLCON");
        List<Double> percentChangeList = Arrays.asList(13.89, -29.12, 0.10, 3.93, 37.05);
        List<Long> volumeList = Arrays.asList(1_719_000L, 380_000L, 153_400L, 19_800L, 730_000L);
        List<Double> amountList = Arrays.asList(1.35, 2.31, 14.44, 57.15, 8.03);

        ArrayList<TickerDto> tickerDtoList = (ArrayList<TickerDto>) givenTickerDtoList(nameList, symbolList, percentChangeList, volumeList, amountList);
        Set<String> tradeSymbols = givenTradeSymbols("CAL", "PNB", "WLCON");

        ArrayList<TickerDto> updatedTickerDtoList = whenSetTickerDtoListHasTradePlan(tickerDtoList, tradeSymbols);

        thenTickerListFoundInTradeSymbolsShouldHaveHasTradePlanSetToTrue(updatedTickerDtoList, tradeSymbols);
    }

    @Test
    public void givenMaxShortTickerCount_whenIsTickerListSavedInDatabase_thenTrueShouldBeReturned()
    {
        givenTickerCount(Short.MAX_VALUE);

        boolean isSaved = whensTickerListSavedInDatabase();

        assertTrue(isSaved);
    }

    @Test
    public void givenTenTickerCount_whenIsTickerListSavedInDatabase_thenFalseShouldBeReturned()
    {
        givenTickerCount(10);

        boolean isSaved = whensTickerListSavedInDatabase();

        assertFalse(isSaved);
    }

    @Test
    public void givenZeroTickerCount_whenIsTickerListSavedInDatabase_thenFalseShouldBeReturned()
    {
        givenTickerCount(0);

        boolean isSaved = whensTickerListSavedInDatabase();

        assertFalse(isSaved);
    }

    @Test
    public void whenGetExpectedMinimumTotalStocks_thenShouldReturnEqualToExpectedMinimumTotalStocks()
    {
        int total = service.getExpectedMinimumTotalStocks();
        assertEquals(243, total);
    }

    @Test
    public void givenTradeDto_whenInsertTradePlan_thenTradeDtoConvertedToTradeAndIsInsertedAndLastUpdatedRefreshed()
    {
        String symbol = "PIP";
        List<String> entryPrice = Arrays.asList("3.1", "3.3", "3.5");
        List<Long> shares = Arrays.asList(3_000_000L, 1_454_000L, 420_000L);
        List<String> percentWeight = Arrays.asList("33.34", "33.33", "33.33");
        List<TradeEntryDto> tradeEntries = createTradeEntryDto(symbol, entryPrice, shares, percentWeight);
        TradeDto tradeDto = givenTradeDto(9L, symbol, newDate(2018, 7, 19), 20, "3.3", "3.309", 26_300L,
                "73123", "3.35", "3.98", "-15354", "-9.24", "5456", "3.21", "0",
                newDate(2018, 10, 19), 39, "2.98", 10_200_100, "42.38", tradeEntries);

        Trade trade = whenInsertTradePlan(tradeDto);

        thenTradeDtoConvertedToTradeAndIsInsertedAndLastUpdatedRefreshed(tradeDto, trade, getLastUpdatedTradePreference());
    }

    @Test
    public void givenNullTradeDto_whenInsertTradePlan_thenIllegalArgumentExceptionShouldBeThrown()
    {
        expectingExceptionWillBeThrownWithMessage(IllegalArgumentException.class, "TradeDto must not be null.");

        whenInsertTradePlan(null);
    }

    @Test
    public void givenTradeDto_whenUpdateTradePlan_thenTradeDtoIsUpdatedAndLastUpdatedRefreshed()
    {
        String symbol = "PIP";
        List<String> entryPrice = Arrays.asList("3.1", "3.3", "3.5");
        List<Long> shares = Arrays.asList(3_000_000L, 1_454_000L, 420_000L);
        List<String> percentWeight = Arrays.asList("33.34", "33.33", "33.33");
        List<TradeEntryDto> tradeEntries = createTradeEntryDto(symbol, entryPrice, shares, percentWeight);
        TradeDto tradeDto = givenTradeDto(9L, symbol, newDate(2018, 7, 19), 20, "3.3", "3.309", 26_300L,
                "73123", "3.35", "3.98", "-15354", "-9.24", "5456", "3.21", "0",
                newDate(2018, 10, 19), 39, "2.98", 10_200_100, "42.38", tradeEntries);

        whenUpdateTradePlan(tradeDto);

        thenTradeDtoIsUpdatedAndLastUpdatedRefreshed(tradeDto, getLastUpdatedTradePreference());
    }

    @Test
    public void givenNullTradeDto_whenUpdateTradePlan_thenIllegalArgumentExceptionShouldBeThrown()
    {
        expectingExceptionWillBeThrownWithMessage(IllegalArgumentException.class, "TradeDto must not be null.");

        whenUpdateTradePlan(null);
    }

    @Test
    public void givenTradeDto_whenDeleteTradePlan_thenTradeDtoIsDeleted()
    {
        String symbol = "PIP";
        List<String> entryPrice = Arrays.asList("3.1", "3.3", "3.5");
        List<Long> shares = Arrays.asList(3_000_000L, 1_454_000L, 420_000L);
        List<String> percentWeight = Arrays.asList("33.34", "33.33", "33.33");
        List<TradeEntryDto> tradeEntries = createTradeEntryDto(symbol, entryPrice, shares, percentWeight);
        TradeDto tradeDto = givenTradeDto(9L, symbol, newDate(2018, 7, 19), 20, "3.3", "3.309", 26_300L,
                "73123", "3.35", "3.98", "-15354", "-9.24", "5456", "3.21", "0",
                newDate(2018, 10, 19), 39, "2.98", 10_200_100, "42.38", tradeEntries);

        String deletedTrade = whenDeleteTradePlan(tradeDto);

        thenTradeDtoIsDeleted(deletedTrade);
    }

    @Test
    public void givenNullTradeDto_whenDeleteTradePlan_thenIllegalArgumentExceptionShouldBeThrown()
    {
        expectingExceptionWillBeThrownWithMessage(IllegalArgumentException.class, "TradeDto must not be null.");

        whenDeleteTradePlan(null);
    }

    @Test
    public void givenWeekdayAfterNineTwentyNineAMAndBeforeLunchBreak_whenIsMarketOpen_thenMarketShouldReturnTrue()
    {
        boolean isMarketOpen;
        for(int minute = 30; minute < 60; minute++)
        {
            givenWeekday(9, minute);
            isMarketOpen = whenIsMarketOpen();
            assertTrue("9:" + minute, isMarketOpen);
        }

        for(int hour = 10; hour < 12; hour++)
        {
            for(int minute = 0; minute < 60; minute++)
            {
                givenWeekday(hour, minute);
                isMarketOpen = whenIsMarketOpen();
                assertTrue(hour + ":" + minute, isMarketOpen);
            }
        }
    }

    @Test
    public void givenWeekdayAfterLunchBreakAndBeforeThreeThirty_whenIsMarketOpen_thenShouldReturnTrue()
    {
        boolean isMarketOpen;
        for(int minute = 30; minute < 60; minute++)
        {
            givenWeekday(13, minute);
            isMarketOpen = whenIsMarketOpen();
            assertTrue("13:" + minute, isMarketOpen);
        }

        for(int minute = 0; minute < 60; minute++)
        {
            givenWeekday(14, minute);
            isMarketOpen = whenIsMarketOpen();
            assertTrue("14:" + minute, isMarketOpen);
        }

        for(int minute = 0; minute < 30; minute++)
        {
            givenWeekday(15, minute);
            isMarketOpen = whenIsMarketOpen();
            assertTrue("15:" + minute, isMarketOpen);
        }
    }

    @Test
    public void givenWeekdayBeforeNineThirtyAM_whenIsMarketOpen_thenShouldReturnFalse()
    {
        boolean isMarketOpen;
        for(int hour = 0; hour < 9; hour++)
        {
            for(int minute = 0; minute < 60; minute++)
            {
                givenWeekday(hour, minute);
                isMarketOpen = whenIsMarketOpen();
                assertFalse(hour + ":" + minute, isMarketOpen);
            }
        }

        for(int minute = 0; minute < 30; minute++)
        {
            givenWeekday(9, minute);
            isMarketOpen = whenIsMarketOpen();
            assertFalse("9:" + minute, isMarketOpen);
        }
    }

    @Test
    public void givenWeekdayLunchBreak_whenIsMarketOpen_thenShouldReturnFalse()
    {
        boolean isMarketOpen;
        for(int minute = 0; minute < 60; minute++)
        {
            givenWeekday(12, minute);
            isMarketOpen = whenIsMarketOpen();
            assertFalse("12:" + minute, isMarketOpen);
        }

        for(int minute = 0; minute < 30; minute++)
        {
            givenWeekday(13, minute);
            isMarketOpen = whenIsMarketOpen();
            assertFalse("13:" + minute, isMarketOpen);
        }
    }

    @Test
    public void givenWeekdayAfterThreeTwentyNinePM_whenIsMarketOpen_thenShouldReturnFalse()
    {
        boolean isMarketOpen;
        for(int minute = 30; minute < 60; minute++)
        {
            givenWeekday(15, minute);
            isMarketOpen = whenIsMarketOpen();
            assertFalse("15:" + minute, isMarketOpen);
        }

        for(int hour = 16; hour < 24; hour++)
        {
            for(int minute = 0; minute < 60; minute++)
            {
                givenWeekday(hour, minute);
                isMarketOpen = whenIsMarketOpen();
                assertFalse(hour + ":" + minute, isMarketOpen);
            }
        }
    }

    @Test
    public void givenWeekend_whenIsMarketOpen_thenShouldReturnFalse()
    {
        givenWeekend();
        boolean isMarketOpen = whenIsMarketOpen();
        assertFalse(isMarketOpen);
    }

    @Test
    public void givenWeekdayAndLastUpdatedIsMarketClose_whenIsUpToDate_thenShouldReturnTrue()
    {
        PSEPlannerPreference preference = PSEPlannerPreference.LAST_UPDATED_TICKER;
        boolean isUpToDate;

        for(DayOfWeek weekday : DayOfWeek.weekDays())
        {
            // 3pm
            givenWeekday(weekday, 15, randomSecondOrMinuteMin30());
            givenLastUpdated(preference.toString(), newTime(15, 30, 0, weekday).getTime());
            isUpToDate = whenIsUpToDate(preference);
            assertTrue(isUpToDate);

            // 4pm to 11pm
            for(int hour = 16; hour < 24; hour++)
            {
                givenWeekday(weekday, hour, randomSecondOrMinute());
                givenLastUpdated(preference.toString(), newTime(15, 30, 0, weekday).getTime());
                isUpToDate = whenIsUpToDate(preference);
                assertTrue(isUpToDate);
            }
        }
    }

    @Test
    public void givenWeekendAndLastUpdatedIsFridayMarketClose_whenIsUpToDate_thenShouldReturnTrue()
    {
        PSEPlannerPreference preference = PSEPlannerPreference.LAST_UPDATED_TICKER;
        boolean isUpToDate;

        for(DayOfWeek weekEnd : DayOfWeek.weekEnds())
        {
            givenWeekend(weekEnd);
            givenLastUpdated(preference.toString(), newTime(15, 30, randomSecondOrMinute(), DayOfWeek.FRIDAY).getTime());
            isUpToDate = whenIsUpToDate(preference);
            assertTrue(weekEnd.toString(), isUpToDate);
        }
    }

    @Test
    public void givenWeekdayAndLastUpdatedBeforeMarketClose_whenIsUpToDate_thenShouldReturnFalse()
    {
        PSEPlannerPreference preference = PSEPlannerPreference.LAST_UPDATED_TICKER;
        boolean isUpToDate;

        for(DayOfWeek weekday : DayOfWeek.weekDays())
        {
            // 10am to 2pm
            for(int hour = 10; hour < 15; hour++)
            {
                givenWeekday(weekday, hour, randomSecondOrMinute());
                givenLastUpdated(preference.toString(), newTime(10, 30, 25, weekday).getTime());
                isUpToDate = whenIsUpToDate(preference);
                assertFalse(isUpToDate);
            }

            // 3pm
            givenWeekday(weekday, 15, randomSecondOrMinuteMin30());
            givenLastUpdated(preference.toString(), newTime(15, 15, 0, weekday).getTime());
            isUpToDate = whenIsUpToDate(preference);
            assertFalse(isUpToDate);
        }
    }

    @Test
    public void givenWeekendAndLastUpdatedIsThursdayMarketClose_whenIsUpToDate_thenShouldReturnFalse()
    {
        PSEPlannerPreference preference = PSEPlannerPreference.LAST_UPDATED_TICKER;
        boolean isUpToDate;

        for(DayOfWeek weekEnd : DayOfWeek.weekEnds())
        {
            // 9am to 2pm
            for(int hour = 9; hour < 15; hour++)
            {
                givenWeekend(weekEnd);
                givenLastUpdated(preference.toString(), newTime(15, 30, randomSecondOrMinute(), DayOfWeek.THURSDAY).getTime());
                isUpToDate = whenIsUpToDate(preference);
                assertFalse(isUpToDate);
            }

            // 3pm
            givenWeekend(weekEnd);
            givenLastUpdated(preference.toString(), newTime(15, 30, randomSecondOrMinute(), DayOfWeek.THURSDAY).getTime());
            isUpToDate = whenIsUpToDate(preference);
            assertFalse(isUpToDate);
        }
    }

    @Test
    public void givenWeekendAndLastUpdatedIsFridayBeforeMarketClose_whenIsUpToDate_thenShouldReturnFalse()
    {
        PSEPlannerPreference preference = PSEPlannerPreference.LAST_UPDATED_TICKER;
        boolean isUpToDate;

        for(DayOfWeek weekEnd : DayOfWeek.weekEnds())
        {
            // 9am to 2pm
            for(int hour = 9; hour < 15; hour++)
            {
                givenWeekend(weekEnd);
                givenLastUpdated(preference.toString(), newTime(hour, randomSecondOrMinute(), randomSecondOrMinute(), DayOfWeek.FRIDAY).getTime());
                isUpToDate = whenIsUpToDate(preference);
                assertFalse(isUpToDate);
            }

            // 3pm
            givenWeekend(weekEnd);
            givenLastUpdated(preference.toString(), newTime(15, randomSecondOrMinuteMax29(), randomSecondOrMinute(), DayOfWeek.FRIDAY).getTime());
            isUpToDate = whenIsUpToDate(preference);
            assertFalse(isUpToDate);
        }
    }

    @Test
    public void givenTickerListInDatabase_whenGetTickerListFromDatabase_thenShouldReturnConvertedTickerDtoList()
    {
        List<String> nameList = Arrays.asList("Alsons Consolidated Resource", "Calata Corporation", "Eagle Cement Corporation", "Philippine National Bank",
                "Wilcon Depot, Inc.");
        List<String> symbolList = Arrays.asList("ACR", "CAL", "EAGLE", "PNB", "WLCON");
        List<String> changeList = Arrays.asList("13422.89", "-24789.12", "76790.10", "33.93", "387.05");
        List<String> percentChangeList = Arrays.asList("13.89", "-29.12", "0.10", "3.93", "37.05");
        List<Long> volumeList = Arrays.asList(1_719_000L, 380_000L, 153_400L, 19_800L, 730_000L);
        List<String> currentPriceList = Arrays.asList("1.35", "2.31", "14.44", "57.15", "8.03");

        ArrayList<Ticker> tickerList = givenTickerList(symbolList, nameList, volumeList, currentPriceList, changeList, percentChangeList);
        givenTickerListInDatabase(tickerList);

        TestObserver<ArrayList<TickerDto>> result = whenGetTickerListFromDatabase();

        thenShouldReturnConvertedTickerDtoList(result, tickerList);
    }

    @Test
    public void givenEmptyDatabase_whenGetTickerListFromDatabase_thenShouldReturnConvertedTickerDtoList()
    {
        List<Ticker> tickerList = Collections.emptyList();
        givenTickerListInDatabase(tickerList);

        TestObserver<ArrayList<TickerDto>> result = whenGetTickerListFromDatabase();

        thenShouldReturnConvertedTickerDtoList(result, tickerList);
    }

    @Test
    public void givenTradePlanListInDatabase_whenGetTradePlanListFromDatabase_thenShouldReturnConvertedTradePlanDtoList()
    {
        // Note values are random and does not reflect correct computation
        List<Long> id = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        List<String> symbol = Arrays.asList("ACR", "CHP", "HOUSE", "PCOR", "VUL");
        List<Date> entryDate = Arrays.asList(newDate(2018, 5, 13), newDate(2018, 6, 15),
                newDate(2018, 8, 19), newDate(2018, 10, 25), newDate(2018, 12, 7));
        List<Integer> holdingPeriod = Arrays.asList(5, 17, 28, 31, 71);
        List<String> currentPrice = Arrays.asList("1.31", "4.64", "6.29", "9.1", "0.82");
        List<String> averagePrice = Arrays.asList("1.32", "4.68", "6.56", "9.21", "0.828");
        List<Long> totalShares = Arrays.asList(3_000_000L, 1_454_000L, 420_000L, 51_000L, 8_900_000L);
        List<String> totalAmount = Arrays.asList("12345", "5678", "7942", "7526", "2299");
        List<String> priceToBreakEven = Arrays.asList("1.33", "4.75", "6.66", "9.31", "0.84");
        List<String> targetPrice = Arrays.asList("1.43", "5.75", "7.66", "11.31", "0.94");
        List<String> gainLoss = Arrays.asList("8785676", "-5684674", "252342", "-45465", "5464564");
        List<String> gainLossPercent = Arrays.asList("-235", "74.75", "68.66", "-97.31", "60.84");
        List<String> gainToTarget = Arrays.asList("7685457", "-7857754", "23246", "878768", "-789696");
        List<String> stopLoss = Arrays.asList("1.13", "4.35", "6.16", "9.0", "0.80");
        List<String> lossToStopLoss = Arrays.asList("653435", "763452", "635435", "67587", "6787868");
        List<Date> stopDate = Arrays.asList(newDate(2018, 6, 13), newDate(2018, 7, 15),
                newDate(2018, 9, 19), newDate(2018, 11, 25), newDate(2019, 1, 7));
        List<Integer> daysToStopDate = Arrays.asList(30, 30, 30, 30, 30);
        List<String> riskReward = Arrays.asList("3.43", "2", "4.5", "2.64", "1.9");
        List<Long> capital = Arrays.asList(10_000_000L, 80_000_000L, 5_000_000L, 3_000_000L, 14_000_000L);
        List<String> percentCapital = Arrays.asList("30", "5.85", "8.67", "84.05", "100");
        List<List<TradeEntry>> tradeEntries = createTradeEntryList(symbol, currentPrice, totalShares, gainLossPercent);

        ArrayList<Trade> tradePlanList = givenTradePlanList(id, symbol, entryDate, holdingPeriod, currentPrice, averagePrice, totalShares,
                totalAmount, priceToBreakEven, targetPrice, gainLoss, gainLossPercent, gainToTarget, stopLoss, lossToStopLoss, stopDate,
                daysToStopDate, riskReward, capital, percentCapital, tradeEntries);
        givenTradePlanListInDatabase(tradePlanList);

        TestObserver<ArrayList<TradeDto>> testObserver = whenGetTradePlanListFromDatabase();

        thenShouldReturnConvertedTradePlanDtoList(testObserver, tradePlanList);
    }

    @Test
    public void givenEmptyDatabase_whenGetTradePlanListFromDatabase_thenShouldReturnConvertedTradePlanDtoList()
    {
        List<Trade> tradePlanList = Collections.emptyList();
        givenTradePlanListInDatabase(tradePlanList);

        TestObserver<ArrayList<TradeDto>> testObserver = whenGetTradePlanListFromDatabase();

        thenShouldReturnConvertedTradePlanDtoList(testObserver, tradePlanList);
    }

    /**
     * Ticker is not checked in this test, because it is already tested in
     * @see PhisixHttpClientTest
     */
    @Test
    public void givenLastUpdated_whenGetTicker_thenLastUpdatedRefreshed()
    {
        givenLastUpdated(getLastUpdatedTickerPreference(), LAST_UPDATED.getTime());

        whenGetTicker();

        thenLastUpdatedRefreshed(getLastUpdatedTickerPreference(), new Date());
    }

    /**
     * Ticker list is not checked in this test, because it is already tested in
     * @see PhisixHttpClientTest
     */
    @Test
    public void givenLastUpdated_whenGetTickerList_thenLastUpdatedRefreshed()
    {
        givenLastUpdated(getLastUpdatedTickerPreference(), LAST_UPDATED.getTime());

        whenGetTickerList();

        thenLastUpdatedRefreshed(getLastUpdatedTickerPreference(), new Date());
    }

    /**
     * Ticker list is not checked in this test, because it is already tested in
     * @see PhisixHttpClientTest
     */
    @Test
    public void givenLastUpdated_whenGetAllTickerList_thenLastUpdatedRefreshed()
    {
        givenLastUpdated(getLastUpdatedTickerPreference(), LAST_UPDATED.getTime());

        whenGetAllTickerList();

        thenLastUpdatedRefreshed(getLastUpdatedTickerPreference(), new Date());
    }

    /**
     * Only looks at the second parameter to determine the number of TradeDto.
     */
    private Collection<TradeDto> givenTradeDtoList(List<Long> id, List<String> symbol, List<Date> entryDate, List<Integer> holdingPeriod,
            List<String> currentPrice, List<String> averagePrice, List<Long> totalShares, List<String> totalAmount, List<String> priceToBreakEven,
            List<String> targetPrice, List<String> gainLoss, List<String> gainLossPercent, List<String> gainToTarget, List<String> stopLoss,
            List<String> lossToStopLoss, List<Date> stopDate, List<Integer> daysToStopDate, List<String> riskReward, List<Long> capital,
            List<String> percentCapital, List<List<TradeEntryDto>> tradeEntries)
    {
        int size = symbol.size();
        Collection<TradeDto> tradeDtos = new ArrayList<>(size);

        for(int i = 0; i < size; i++)
        {
            tradeDtos.add(givenTradeDto(id.get(i), symbol.get(i), entryDate.get(i), holdingPeriod.get(i), currentPrice.get(i), averagePrice.get(i),
                    totalShares.get(i), totalAmount.get(i), priceToBreakEven.get(i), targetPrice.get(i), gainLoss.get(i), gainLossPercent.get(i),
                    gainToTarget.get(i), stopLoss.get(i), lossToStopLoss.get(i), stopDate.get(i), daysToStopDate.get(i), riskReward.get(i),
                    capital.get(i), percentCapital.get(i), tradeEntries.get(i)));
        }

        return tradeDtos;
    }

    private TradeDto givenTradeDto(Long id, String symbol, Date entryDate, int holdingPeriod, String currentPrice, String averagePrice, long totalShares,
            String totalAmount, String priceToBreakEven, String targetPrice, String gainLoss, String gainLossPercent, String gainToTarget, String stopLoss,
            String lossToStopLoss, Date stopDate, int daysToStopDate, String riskReward, long capital, String percentCapital, List<TradeEntryDto> tradeEntries)
    {
        return new TradeDto(id, symbol, entryDate, holdingPeriod, currentPrice, averagePrice, totalShares, totalAmount, priceToBreakEven, targetPrice,
                gainLoss, gainLossPercent, gainToTarget, stopLoss, lossToStopLoss, stopDate, daysToStopDate, riskReward, capital, percentCapital, tradeEntries);
    }

    private List<TradeEntryDto> createTradeEntryDto(String symbol, List<String> entryPrice, List<Long> shares, List<String> percentWeight)
    {
        int size = entryPrice.size();
        List<TradeEntryDto> list = new ArrayList<>(size);
        for(int i = 0; i < size; i++)
        {
            list.add(new TradeEntryDto(symbol, entryPrice.get(i), shares.get(i), percentWeight.get(i)));
        }

        return list;
    }

    private List<List<TradeEntryDto>> createTradeEntryDtos(List<String> symbolList, List<String> entryPriceList, List<Long> sharesList,
            List<String> percentWeightList)
    {
        int size = symbolList.size();
        List<List<TradeEntryDto>> listOfList = new ArrayList<>(size);

        for(String symbol : symbolList)
        {
            List<TradeEntryDto> list = createTradeEntryDto(symbol, entryPriceList, sharesList, percentWeightList);
            listOfList.add(list);
        }

        return listOfList;
    }

    private void givenLastUpdated(String preference)
    {
        givenLastUpdated(preference, LAST_UPDATED.getTime());
    }

    private void givenLastUpdated(String preference, long time)
    {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(preference, time);
        editor.apply();
    }

    private SharedPreferences getSharedPreferences()
    {
        return getContext().getSharedPreferences(PSEPlannerPreference.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private List<TickerDto> givenTickerDtoList(List<String> nameList, List<String> symbolList, List<Double> percentChangeList, List<Long> volumeList,
            List<Double> amountList)
    {
        int size = nameList.size();
        List<TickerDto> list = new ArrayList<>(size);

        for(int i = 0; i < size; i++)
        {
            list.add(createTickerDto(symbolList.get(i), nameList.get(i), volumeList.get(i), amountList.get(i), percentChangeList.get(i)));
        }

        return list;
    }

    private void givenTickerCount(long maxValue)
    {
        when(tickerDao.count()).thenReturn(maxValue);
    }

    private Set<String> givenTradeSymbols(String... symbols)
    {
        Set<String> tradeSymbols = new HashSet<>();
        tradeSymbols.addAll(Arrays.asList(symbols));

        return tradeSymbols;
    }

    private Calendar createMockedCalendar(DayOfWeek day, int hour, int minute)
    {
        Calendar calendar = createMockedCalendar(day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        when(calendar.get(Calendar.HOUR_OF_DAY)).thenReturn(hour);
        when(calendar.get(Calendar.MINUTE)).thenReturn(minute);

        return calendar;
    }

    private void givenWeekday(DayOfWeek day, int hour, int minute)
    {
        Calendar mockedCalendar = createMockedCalendar(day, hour, minute);
        when(calendarWrapper.newCalendar(FormatService.MANILA_TIMEZONE)).thenReturn(mockedCalendar);
    }

    private void givenWeekday(int hour, int minute)
    {
        givenWeekday(DayOfWeek.MONDAY, hour, minute);
    }

    private Calendar createMockedCalendar(DayOfWeek day)
    {
        Calendar calendar = spy(Calendar.getInstance(FormatService.MANILA_TIMEZONE));
        calendar.set(Calendar.DAY_OF_WEEK, day.getValue());
        when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(day.getValue());

        return calendar;
    }

    // For Sundays, adjust to next Sunday instead of previous, because Sunday's value is 0 instead of 7. (Treats as previous Sundays of the week)
    private void adjustCalendarAfterTodayOnSundays(Calendar calendar)
    {
        Calendar now = Calendar.getInstance(FormatService.MANILA_TIMEZONE);
        boolean nowAfterGivenDate = now.after(calendar);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        boolean givenCalendarIsSunday = dayOfWeek == Calendar.SUNDAY;

        if(nowAfterGivenDate && givenCalendarIsSunday)
        {
            calendar.set(Calendar.WEEK_OF_YEAR, now.get(Calendar.WEEK_OF_YEAR) + 1);
        }
    }

    private void givenWeekend(DayOfWeek day)
    {
        Calendar mockedCalendar = createMockedCalendar(day);
        adjustCalendarAfterTodayOnSundays(mockedCalendar);
        when(calendarWrapper.newCalendar(FormatService.MANILA_TIMEZONE)).thenReturn(mockedCalendar);
    }

    private void givenWeekend()
    {
        givenWeekend(DayOfWeek.SATURDAY);
    }

    public ArrayList<Ticker> givenTickerList(List<String> symbolList, List<String> nameList, List<Long> volumeList, List<String> currentPriceList,
            List<String> changeList, List<String> percentChangeList)
    {
        int size = nameList.size();
        ArrayList<Ticker> list = new ArrayList<>(size);

        for(int i = 0; i < size; i++)
        {
            list.add(createTicker(i, symbolList.get(i), nameList.get(i), volumeList.get(i), currentPriceList.get(i), changeList.get(i),
                    percentChangeList.get(i)));
        }

        return list;
    }

    public void givenTickerListInDatabase(List<Ticker> tickerList)
    {
        QueryBuilder<?> queryBuilder = mock(QueryBuilder.class);
        QueryBuilder<?> queryBuilderOrderAsc = mock(QueryBuilder.class);

        Mockito.<QueryBuilder<?>> when(tickerDao.queryBuilder()).thenReturn(queryBuilder);
        Mockito.<QueryBuilder<?>> when(queryBuilder.orderAsc(TickerDao.Properties.Symbol)).thenReturn(queryBuilderOrderAsc);
        Mockito.<List<?>> when(queryBuilderOrderAsc.list()).thenReturn(tickerList);
    }

    private Ticker createTicker(int id, String symbol, String name, Long volume, String currentPrice, String change, String percentChange)
    {
        Date randomDate = newDate(RandomUtils.nextInt(2000, 2020), RandomUtils.nextInt(1, 13), RandomUtils.nextInt(1, 31));
        return new Ticker((long) id, symbol, name, volume, currentPrice, change, percentChange, randomDate);
    }

    public void givenTradePlanListInDatabase(List<Trade> tradePlanList)
    {
        when(tradeDao.loadAll()).thenReturn(tradePlanList);
    }

    private ArrayList<Trade> givenTradePlanList(List<Long> id, List<String> symbol, List<Date> entryDate, List<Integer> holdingPeriod,
            List<String> currentPrice, List<String> averagePrice, List<Long> totalShares, List<String> totalAmount, List<String> priceToBreakEven,
            List<String> targetPrice, List<String> gainLoss, List<String> gainLossPercent, List<String> gainToTarget, List<String> stopLoss,
            List<String> lossToStopLoss, List<Date> stopDate, List<Integer> daysToStopDate, List<String> riskReward, List<Long> capital,
            List<String> percentCapital, List<List<TradeEntry>> tradeEntries)
    {
        int size = symbol.size();
        ArrayList<Trade> tradePlanList = new ArrayList<>(size);

        for(int i = 0; i < size; i++)
        {
            tradePlanList.add(givenTradePlan(id.get(i), symbol.get(i), entryDate.get(i), holdingPeriod.get(i), currentPrice.get(i), averagePrice.get(i),
                    totalShares.get(i), totalAmount.get(i), priceToBreakEven.get(i), targetPrice.get(i), gainLoss.get(i), gainLossPercent.get(i),
                    gainToTarget.get(i), stopLoss.get(i), lossToStopLoss.get(i), stopDate.get(i), daysToStopDate.get(i), riskReward.get(i),
                    capital.get(i), percentCapital.get(i), tradeEntries.get(i)));
        }

        return tradePlanList;
    }

    private Trade givenTradePlan(Long id, String symbol, Date entryDate, int holdingPeriod, String currentPrice, String averagePrice, long totalShares,
            String totalAmount, String priceToBreakEven, String targetPrice, String gainLoss, String gainLossPercent, String gainToTarget, String stopLoss,
            String lossToStopLoss, Date stopDate, int daysToStopDate, String riskReward, long capital, String percentCapital, List<TradeEntry> tradeEntries)
    {
        Trade trade = new Trade(id, entryDate, holdingPeriod, symbol, currentPrice, averagePrice, totalShares, totalAmount, priceToBreakEven, targetPrice,
                gainLoss, gainLossPercent, gainToTarget, lossToStopLoss, stopLoss, stopDate, daysToStopDate, riskReward, capital, percentCapital);
        trade.setTradeEntriesTransient(tradeEntries);

        return trade;
    }

    private List<TradeEntry> createTradeEntry(String symbol, List<String> entryPrice, List<Long> shares, List<String> percentWeight)
    {
        int size = entryPrice.size();
        List<TradeEntry> list = new ArrayList<>(size);
        for(int i = 0; i < size; i++)
        {
            list.add(new TradeEntry((long) i, symbol, entryPrice.get(i), shares.get(i), percentWeight.get(i), i));
        }

        return list;
    }

    private List<List<TradeEntry>> createTradeEntryList(List<String> symbolList, List<String> entryPriceList, List<Long> sharesList,
            List<String> percentWeightList)
    {
        int size = symbolList.size();
        List<List<TradeEntry>> listOfList = new ArrayList<>(size);

        for(String symbol : symbolList)
        {
            List<TradeEntry> list = createTradeEntry(symbol, entryPriceList, sharesList, percentWeightList);
            listOfList.add(list);
        }

        return listOfList;
    }

    private String whenGetLastUpdated(String preference)
    {
        return service.getLastUpdated(preference);
    }

    private Set<Ticker> whenInsertTickerList(List<TickerDto> tickerDtoList, Date lastUpdated)
    {
        return service.insertTickerList(tickerDtoList, lastUpdated);
    }

    private Set<Ticker> whenUpdateTickerList(List<TickerDto> tickerDtoList, Date lastUpdated)
    {
        return service.updateTickerList(tickerDtoList, lastUpdated);
    }

    private Set<String> whenGetTradeSymbolsFromTradeDtos(Collection<TradeDto> tradeDtoList)
    {
        return service.getTradeSymbolsFromTradeDtos(tradeDtoList);
    }

    private boolean whensTickerListSavedInDatabase()
    {
        return service.isTickerListSavedInDatabase();
    }

    private ArrayList<TickerDto> whenSetTickerDtoListHasTradePlan(ArrayList<TickerDto> tickerDtoList, Set<String> tradeSymbols)
    {
        return service.setTickerDtoListHasTradePlan(tickerDtoList, tradeSymbols);
    }

    private Trade whenInsertTradePlan(TradeDto tradeDto)
    {
        return service.insertTradePlan(tradeDto);
    }

    private void whenUpdateTradePlan(TradeDto tradeDto)
    {
        mockTradeDaoWhereQuery();
        QueryBuilder queryBuilderWhereTradeEntry = mockTradeEntryDaoWhereQuery();
        when(queryBuilderWhereTradeEntry.buildDelete()).thenReturn(mock(DeleteQuery.class));

        AbstractDaoSession abstractDaoSession = mockInvokeDaoSessionRunInTx();
        when(tradeDao.getSession()).thenReturn(abstractDaoSession);

        // Trade is not returned, because Database transactions are mocked
        service.updateTradePlan(tradeDto);
    }

    private void whenGetTicker()
    {
        Pair<TickerDto, Date> pair = new Pair<>(new TickerDto(), new Date());
        Single<Pair<TickerDto, Date>> returnValue = Single.just(pair);
        when(httpClient.getTicker(anyString())).thenReturn(returnValue);

        service.getTicker(RandomStringUtils.randomAlphabetic(4)).subscribe();
    }

    private void whenGetTickerList()
    {
        Pair<List<TickerDto>, Date> pair = new Pair<>(Collections.<TickerDto> emptyList(), new Date());
        Single<Pair<List<TickerDto>, Date>> returnValue = Single.just(pair);
        when(httpClient.getTickerList(anyCollectionOf(String.class))).thenReturn(returnValue);

        service.getTickerList(Collections.<String>emptyList()).subscribe();
    }

    private void whenGetAllTickerList()
    {
        Pair<List<TickerDto>, Date> pair = new Pair<>(Collections.<TickerDto> emptyList(), new Date());
        Single<Pair<List<TickerDto>, Date>> returnValue = Single.just(pair);
        when(httpClient.getAllTickerList()).thenReturn(returnValue);

        service.getAllTickerList().subscribe();
    }

    private AbstractDaoSession mockInvokeDaoSessionRunInTx()
    {
        AbstractDaoSession abstractDaoSession = mock(AbstractDaoSession.class);
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Runnable runnable = invocation.getArgumentAt(0, Runnable.class);
                runnable.run();
                return null;
            }
        }).when(abstractDaoSession).runInTx(any(Runnable.class));

        return abstractDaoSession;
    }

    private String whenDeleteTradePlan(TradeDto tradeDto)
    {
        QueryBuilder queryBuilderWhereTrade = mockTradeDaoWhereQuery();
        when(queryBuilderWhereTrade.buildDelete()).thenReturn(mock(DeleteQuery.class));

        QueryBuilder queryBuilderWhereTradeEntry = mockTradeEntryDaoWhereQuery();
        when(queryBuilderWhereTradeEntry.buildDelete()).thenReturn(mock(DeleteQuery.class));

        return service.deleteTradePlan(tradeDto);
    }

    private TestObserver<ArrayList<TickerDto>> whenGetTickerListFromDatabase()
    {
        TestObserver<ArrayList<TickerDto>> testObserver = new TestObserver<>();
        service.getTickerListFromDatabase().subscribe(testObserver);

        return testObserver;
    }

    private QueryBuilder<?> mockTradeDaoWhereQuery()
    {
        QueryBuilder<?> queryBuilderTrade = mock(QueryBuilder.class);
        QueryBuilder<?> queryBuilderWhereTrade = mock(QueryBuilder.class);
        Mockito.<QueryBuilder<?>> when(tradeDao.queryBuilder()).thenReturn(queryBuilderTrade);
        Mockito.<QueryBuilder<?>> when(queryBuilderTrade.where(any(WhereCondition.class), Matchers.<WhereCondition> anyVararg()))
                .thenReturn(queryBuilderWhereTrade);

        return queryBuilderWhereTrade;
    }

    private QueryBuilder<?> mockTradeEntryDaoWhereQuery()
    {
        QueryBuilder<?> queryBuilderTradeEntry = mock(QueryBuilder.class);
        QueryBuilder<?> queryBuilderWhereTradeEntry = mock(QueryBuilder.class);
        Mockito.<QueryBuilder<?>> when(tradeEntryDao.queryBuilder()).thenReturn(queryBuilderTradeEntry);
        Mockito.<QueryBuilder<?>> when(queryBuilderTradeEntry.where(any(WhereCondition.class), Matchers.<WhereCondition> anyVararg()))
                .thenReturn(queryBuilderWhereTradeEntry);

        return queryBuilderWhereTradeEntry;
    }

    private boolean whenIsMarketOpen()
    {
        return service.isMarketOpen();
    }

    private boolean whenIsUpToDate(PSEPlannerPreference preference)
    {
        return service.isUpToDate(preference);
    }

    private TestObserver<ArrayList<TradeDto>> whenGetTradePlanListFromDatabase()
    {
        TestObserver<ArrayList<TradeDto>> testObserver = new TestObserver<>();
        service.getTradePlanListFromDatabase().subscribe(testObserver);

        return testObserver;
    }

    private void thenLastUpdatedShouldBeReturned(String lastUpdated)
    {
        assertEquals(formattedLastUpdated, lastUpdated);
    }

    private void thenEpochLastUpdatedShouldBeReturned(String lastUpdated)
    {
        assertEquals(epochLastUpdated, lastUpdated);
    }

    private void thenTickerDtoListConvertedToTickerSet(List<TickerDto> tickerDtoList, Set<Ticker> tickerSet)
    {
        boolean found = false;
        for(TickerDto dto : tickerDtoList)
        {
            for(Ticker ticker : tickerSet)
            {
                found = dto.getSymbol().equals(ticker.getSymbol());
                if(found)
                {
                    assertEquals("Failed on " + ticker.getSymbol(), dto.getName(), ticker.getName());
                    assertEquals("Failed on " + ticker.getSymbol(), dto.getCurrentPrice().toString(), ticker.getCurrentPrice());
                    assertEquals("Failed on " + ticker.getSymbol(), dto.getChange().toString(), ticker.getChange());
                    assertEquals("Failed on " + ticker.getSymbol(), dto.getPercentChange().toString(), ticker.getPercentChange());
                    assertEquals("Failed on " + ticker.getSymbol(), dto.getVolume(), ticker.getVolume());
                    break;
                }
            }

            assertTrue(dto.getSymbol() + " is not found in Ticker Set.", found);

            found = false;
        }
    }

    private void thenTickerDtoListConvertedToTickerSetAndIsInsertedAndLastUpdatedRefreshed(List<TickerDto> tickerDtoList, Set<Ticker> tickerSet,
            String preference)
    {
        thenTickerDtoListConvertedToTickerSet(tickerDtoList, tickerSet);

        verify(tickerDao, times(1)).insertOrReplaceInTx(tickerSet);

        thenLastUpdatedRefreshed(preference);
    }

    private void thenLastUpdatedRefreshed(String lastUpdatedPreference, Date expected)
    {
        long lastUpdatedActual = getSharedPreferences().getLong(lastUpdatedPreference, 0);
        assertEquals(millisToMinutesEpoch(expected.getTime()), millisToMinutesEpoch(lastUpdatedActual));
    }

    private long millisToMinutesEpoch(long time)
    {
        String timeStr = String.valueOf(time);

        int millisToMinutesPlaces = 4;
        String millisAndSecondsTruncated = timeStr.substring(0, timeStr.length() - millisToMinutesPlaces).concat("0000");

        return Long.valueOf(millisAndSecondsTruncated);
    }

    private void thenLastUpdatedRefreshed(String lastUpdatedPreference)
    {
        long lastUpdatedActual = getSharedPreferences().getLong(lastUpdatedPreference, 0);
        assertEquals(LAST_UPDATED.getTime(), lastUpdatedActual);
    }

    private void thenNothingIsInsertedAndLastUpdatedNotRefreshed(Set<Ticker> tickerSet, String preference)
    {
        assertTrue(tickerSet.isEmpty());

        verify(tickerDao, times(0)).insertOrReplaceInTx(anyCollectionOf(Ticker.class));

        thenLastUpdatedNotRefreshed(preference);
    }

    private void thenLastUpdatedNotRefreshed(String lastUpdatedPreference)
    {
        long lastUpdated = getSharedPreferences().getLong(lastUpdatedPreference, 0);
        assertEquals(0, lastUpdated);
    }

    private void thenTradeSymbolsFromTradeDtoListShouldBeReturned(Set<String> tradeSymbols, Collection<TradeDto> tradeDtoList)
    {
        for(TradeDto dto : tradeDtoList)
        {
            assertTrue(tradeSymbols.contains(dto.getSymbol()));
        }
    }

    private void thenTickerListFoundInTradeSymbolsShouldHaveHasTradePlanSetToTrue(ArrayList<TickerDto> tickerDtoList, Set<String> tradeSymbols)
    {
        for(TickerDto dto : tickerDtoList)
        {
            if(tradeSymbols.contains(dto.getSymbol()))
            {
                assertTrue(dto.isHasTradePlan());
            }
            else
            {
                assertFalse(dto.isHasTradePlan());
            }
        }
    }

    private void thenTradeDtoConvertedToTradeAndIsInsertedAndLastUpdatedRefreshed(TradeDto tradeDto, Trade trade, String preference)
    {
        thenTradeDtoEqualsToTrade(tradeDto, trade);

        verify(tradeDao, times(1)).insert(trade);
        verify(tradeEntryDao, times(1)).insertInTx(anyListOf(TradeEntry.class));

        thenLastUpdatedRefreshed(preference, new Date());
    }

    private void thenTradeDtoIsUpdatedAndLastUpdatedRefreshed(TradeDto tradeDto, String preference)
    {
        verify(tradeDao.queryBuilder().where(TradeDao.Properties.Symbol.eq(tradeDto.getSymbol())), times(1)).unique();
        verify(tradeDao.getSession(), times(1)).runInTx(any(Runnable.class));
        verify(tradeDao, times(1)).update(any(Trade.class));
        verify(tradeEntryDao.queryBuilder().where(TradeEntryDao.Properties.TradeSymbol.eq(tradeDto.getSymbol())).buildDelete(), times(1))
                .executeDeleteWithoutDetachingEntities();
        verify(tradeEntryDao, times(1)).insertInTx(anyCollectionOf(TradeEntry.class));

        thenLastUpdatedRefreshed(preference, new Date());
    }

    private void thenTradeDtoIsDeleted(String deletedTrade)
    {
        verify(tradeDao.queryBuilder().where(TradeDao.Properties.Symbol.eq(deletedTrade)).buildDelete(), times(1))
                .executeDeleteWithoutDetachingEntities();
        verify(tradeEntryDao.queryBuilder().where(TradeEntryDao.Properties.TradeSymbol.eq(deletedTrade)).buildDelete(), times(1))
                .executeDeleteWithoutDetachingEntities();
    }

    private void thenTradeDtoEqualsToTrade(TradeDto tradeDto, Trade trade)
    {
        assertEquals(tradeDto.getId(), trade.getId());
        assertEquals(tradeDto.getSymbol(), trade.getSymbol());
        assertEquals(tradeDto.getEntryDate(), trade.getEntryDate());
        assertEquals(tradeDto.getHoldingPeriod(), trade.getHoldingPeriod());
        assertEquals(tradeDto.getCurrentPrice().toPlainString(), trade.getCurrentPrice());
        assertEquals(tradeDto.getAveragePrice().toPlainString(), trade.getAveragePrice());
        assertEquals(tradeDto.getTotalShares(), trade.getTotalShares());
        assertEquals(tradeDto.getTotalAmount().toPlainString(), trade.getTotalAmount());
        assertEquals(tradeDto.getPriceToBreakEven().toPlainString(), trade.getPriceToBreakEven());
        assertEquals(tradeDto.getTargetPrice().toPlainString(), trade.getTargetPrice());
        assertEquals(tradeDto.getPriceToBreakEven().toPlainString(), trade.getPriceToBreakEven());
        assertEquals(tradeDto.getTargetPrice().toPlainString(), trade.getTargetPrice());
        assertEquals(tradeDto.getGainLoss().toPlainString(), trade.getGainLoss());
        assertEquals(tradeDto.getGainLossPercent().toPlainString(), trade.getGainLossPercent());
        assertEquals(tradeDto.getGainToTarget().toPlainString(), trade.getGainToTarget());
        assertEquals(tradeDto.getLossToStopLoss().toPlainString(), trade.getLossToStopLoss());
        assertEquals(tradeDto.getStopLoss().toPlainString(), trade.getStopLoss());
        assertEquals(tradeDto.getStopDate(), trade.getStopDate());
        assertEquals(tradeDto.getDaysToStopDate(), trade.getDaysToStopDate());
        assertEquals(tradeDto.getRiskReward().toPlainString(), trade.getRiskReward());
        assertEquals(tradeDto.getCapital(), trade.getCapital());
        assertEquals(tradeDto.getPercentCapital().toPlainString(), trade.getPercentCapital());

        thenTradeEntriesDtoConvertedToTradeEntries(tradeDto.getTradeEntries(), trade.getTradeEntries());
    }

    public void thenTradeEntriesDtoConvertedToTradeEntries(List<TradeEntryDto> tradeEntryDtos, List<TradeEntry> tradeEntries)
    {
        int expectedSize = tradeEntryDtos.size();
        assertEquals(expectedSize, tradeEntries.size());

        for(int i = 0; i < expectedSize; i++)
        {
            assertEquals(tradeEntryDtos.get(i).getSymbol(), tradeEntries.get(i).getTradeSymbol());
            assertEquals(tradeEntryDtos.get(i).getEntryPrice().toPlainString(), tradeEntries.get(i).getEntryPrice());
            assertEquals(tradeEntryDtos.get(i).getShares(), tradeEntries.get(i).getShares());
            assertEquals(tradeEntryDtos.get(i).getPercentWeight().toPlainString(), tradeEntries.get(i).getPercentWeight());
        }
    }

    private void thenShouldReturnConvertedTickerDtoList(TestObserver<ArrayList<TickerDto>> result, final List<Ticker> tickerList)
    {
        result.assertSubscribed();
        result.assertValue(new Predicate<ArrayList<TickerDto>>()
        {
            @Override
            public boolean test(ArrayList<TickerDto> tickerDtos) throws Exception
            {
                int size = tickerList.size();
                assertEquals(size, tickerDtos.size());
                for(int i = 0; i < size; i++)
                {
                    assertEquals(tickerList.get(i).getId(), tickerDtos.get(i).getId());
                    assertEquals(tickerList.get(i).getName(), tickerDtos.get(i).getName());
                    assertEquals(tickerList.get(i).getSymbol(), tickerDtos.get(i).getSymbol());
                    assertEquals(tickerList.get(i).getVolume(), tickerDtos.get(i).getVolume());
                    assertEquals(tickerList.get(i).getCurrentPrice(), tickerDtos.get(i).getCurrentPrice().toPlainString());
                    assertEquals(tickerList.get(i).getChange(), tickerDtos.get(i).getChange().toPlainString());
                    assertEquals(tickerList.get(i).getPercentChange(), tickerDtos.get(i).getPercentChange().toPlainString());
                }

                return true;
            }
        });
    }

    private void thenShouldReturnConvertedTradePlanDtoList(TestObserver<ArrayList<TradeDto>> result, final List<Trade> tradeList)
    {
        result.assertSubscribed();
        result.assertValue(new Predicate<ArrayList<TradeDto>>()
        {
            @Override
            public boolean test(ArrayList<TradeDto> tradeDtos) throws Exception
            {
                int size = tradeList.size();
                assertEquals(size, tradeDtos.size());
                for(int i = 0; i < size; i++)
                {
                    thenTradeDtoEqualsToTrade(tradeDtos.get(i), tradeList.get(i));
                }

                return true;
            }
        });
    }

    public String getLastUpdatedTickerPreference()
    {
        return PSEPlannerPreference.LAST_UPDATED_TICKER.toString();
    }

    public String getLastUpdatedTradePreference()
    {
        return PSEPlannerPreference.LAST_UPDATED_TRADE_PLAN.toString();
    }
}