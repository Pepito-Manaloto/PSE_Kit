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

import static com.aaron.pseplanner.test.utils.BeanEntityBuilderTestUtils.*;
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
        List<TickerDto> tickerDtoList = givenTickerDtoList();

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
        List<TickerDto> tickerDtoList = givenTickerDtoList();

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
        Collection<TradeDto> tradeDtoList = givenTradeDtoList();

        Set<String> tradeSymbols = whenGetTradeSymbolsFromTradeDtos(tradeDtoList);

        thenTradeSymbolsFromTradeDtoListShouldBeReturned(tradeSymbols, tradeDtoList);
    }

    @Test
    public void givenTickerListDtoAndTradeSymbols_whenSetTickerDtoListHasTradePlan_thenTickerListFoundInTradeSymbolsShouldHaveHasTradePlanSetToTrue()
    {
        ArrayList<TickerDto> tickerDtoList = (ArrayList<TickerDto>) givenTickerDtoList();
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
        List<TradeEntryDto> tradeEntries = givenTradeEntryDto(symbol);
        TradeDto tradeDto = givenTradeDto(symbol, tradeEntries);

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
        List<TradeEntryDto> tradeEntries = givenTradeEntryDto(symbol);
        TradeDto tradeDto = givenTradeDto(symbol, tradeEntries);

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
        List<TradeEntryDto> tradeEntries = givenTradeEntryDto(symbol);
        TradeDto tradeDto = givenTradeDto(symbol, tradeEntries);

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
        ArrayList<Ticker> tickerList = givenTickerList();
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
        List<String> symbol = Arrays.asList("ACR", "CHP", "HOUSE", "PCOR", "VUL");
        List<List<TradeEntry>> tradeEntries = givenTradeEntryList(symbol);

        ArrayList<Trade> tradePlanList = givenTradeList(symbol, tradeEntries);
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

    public void givenTickerListInDatabase(List<Ticker> tickerList)
    {
        QueryBuilder<?> queryBuilder = mock(QueryBuilder.class);
        QueryBuilder<?> queryBuilderOrderAsc = mock(QueryBuilder.class);

        Mockito.<QueryBuilder<?>> when(tickerDao.queryBuilder()).thenReturn(queryBuilder);
        Mockito.<QueryBuilder<?>> when(queryBuilder.orderAsc(TickerDao.Properties.Symbol)).thenReturn(queryBuilderOrderAsc);
        Mockito.<List<?>> when(queryBuilderOrderAsc.list()).thenReturn(tickerList);
    }

    public void givenTradePlanListInDatabase(List<Trade> tradePlanList)
    {
        when(tradeDao.loadAll()).thenReturn(tradePlanList);
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

        service.getTickerList(Collections.<String> emptyList()).subscribe();
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