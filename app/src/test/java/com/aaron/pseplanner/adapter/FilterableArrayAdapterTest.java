package com.aaron.pseplanner.adapter;

import com.aaron.pseplanner.RobolectricTest;
import com.aaron.pseplanner.bean.Stock;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.bean.TradeDto;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Aaron on 06/01/2018.
 */

public class FilterableArrayAdapterTest extends RobolectricTest
{
    private FilterableArrayAdapter adapter;
    private ArrayList<Stock> actualList;
    private ArrayList<Stock> tempList;

    @Before
    public void beforeTest()
    {
        FilterableArrayAdapter realAdapter = new FilterableArrayAdapter<Stock>(getContext(), 0, actualList)
        {
            @Override
            protected ArrayList<Stock> getActualList()
            {
                return actualList;
            }

            @Override
            protected ArrayList<Stock> getTempList()
            {
                return tempList;
            }
        };

        // Spy to be able to verify some method calls
        adapter = spy(realAdapter);
    }

    @Test
    public void givenAListOfStocksAndASearchQuery_whenFilter_thenTheListOfStocksShouldOnlyIncludeStocksMatchingTheSearchQuery()
    {
        givenAListOfStocks("ACR", "APX", "BCOR", "ECP", "FGEN", "JOH", "NI", "NIKL", "PIP", "PHA", "PHEN", "PHES", "PNB", "SLI", "SM", "X");
        String searchQuery = "ph";

        whenFilter(searchQuery);

        ArrayList<String> stocksFiltered = new ArrayList<>(Arrays.asList("PHA", "PHEN", "PHES"));
        thenTheListOfStocksShouldOnlyContainStocksMatchingTheSearchQuery(stocksFiltered);
        verify(adapter, times(1)).notifyDataSetChanged();
    }

    @Test
    public void givenAListOfStocksAndEmptySearchQuery_whenFilter_thenTheListOfStocksShouldIncludeAllStocks()
    {
        ArrayList<String> stocks = new ArrayList<>(
                Arrays.asList("ACR", "APX", "BCOR", "ECP", "FGEN", "JOH", "NI", "NIKL", "PIP", "PHA", "PHEN", "PHES", "PNB", "SLI", "SM", "X"));

        String[] stocksArray = new String[stocks.size()];
        stocksArray = stocks.toArray(stocksArray);

        givenAListOfStocks(stocksArray);
        String searchQuery = "";

        whenFilter(searchQuery);

        thenTheListOfStocksShouldOnlyContainStocksMatchingTheSearchQuery(stocks);
        verify(adapter, times(1)).notifyDataSetChanged();
    }

    private void givenAListOfStocks(String... listOfStockSymbols)
    {
        actualList = new ArrayList<>();
        tempList = new ArrayList<>();

        for(String symbol : listOfStockSymbols)
        {
            Stock stock = createStockTypeRandomly(symbol);

            actualList.add(stock);
            tempList.add(stock);
        }
    }

    private Stock createStockTypeRandomly(String symbol)
    {
        int randomNumber = RandomUtils.nextInt(1, 7);
        boolean isEven = randomNumber % 2 == 0;

        Stock stock;
        if(isEven)
        {
            stock = new TickerDto();
        }
        else
        {
            stock = new TradeDto();
        }

        stock.setSymbol(symbol);

        return stock;
    }

    private void whenFilter(String searchQuery)
    {
        adapter.filter(searchQuery);
    }

    private void thenTheListOfStocksShouldOnlyContainStocksMatchingTheSearchQuery(ArrayList<String> stocksFiltered)
    {
        ArrayList<String> actualListSymbols = new ArrayList<>();
        for(Stock stock : actualList)
        {
            actualListSymbols.add(stock.getSymbol());
        }

        assertEquals(stocksFiltered, actualListSymbols);
    }
}
