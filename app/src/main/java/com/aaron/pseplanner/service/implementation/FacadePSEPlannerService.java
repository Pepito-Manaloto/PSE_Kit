package com.aaron.pseplanner.service.implementation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.aaron.pseplanner.app.PSEPlannerApplication;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.constant.PSEPlannerPreference;
import com.aaron.pseplanner.entity.DaoSession;
import com.aaron.pseplanner.entity.Stock;
import com.aaron.pseplanner.entity.StockDao;
import com.aaron.pseplanner.entity.StockDao.Properties;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.HttpClient;
import com.aaron.pseplanner.service.PSEPlannerService;

import org.greenrobot.greendao.query.Query;

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
    private static final Date EPOCH_DATE = new Date(0);
    private HttpClient phisixHttpClient;
    private HttpClient pseHttpClient;
    private FormatService formatService;

    private SharedPreferences sharedPreferences;
    private StockDao stockDao;

    public FacadePSEPlannerService(@NonNull Activity activity)
    {
        this.phisixHttpClient = new PhisixHttpClient(DEFAUT_TIMEOUT, DEFAUT_TIMEOUT, DEFAUT_TIMEOUT);
        this.pseHttpClient = new PSEHttpClient(DEFAUT_TIMEOUT, DEFAUT_TIMEOUT, DEFAUT_TIMEOUT);
        this.formatService = new DefaultFormatService(activity);
        this.sharedPreferences = activity.getSharedPreferences(PSEPlannerPreference.class.getSimpleName(), Context.MODE_PRIVATE);

        // get the note DAO
        DaoSession daoSession = ((PSEPlannerApplication) activity.getApplication()).getDaoSession();
        this.stockDao = daoSession.getStockDao();
    }

    public FacadePSEPlannerService(@NonNull Activity activity, long connectionTimeout, long readTimeout, long pingInterval)
    {
        this.phisixHttpClient = new PhisixHttpClient(connectionTimeout, readTimeout, pingInterval);
        this.pseHttpClient = new PSEHttpClient(connectionTimeout, readTimeout, pingInterval);
        this.formatService = new DefaultFormatService(activity);
        this.sharedPreferences = activity.getSharedPreferences(PSEPlannerPreference.class.getSimpleName(), Context.MODE_PRIVATE);

        // get the note DAO
        DaoSession daoSession = ((PSEPlannerApplication) activity.getApplication()).getDaoSession();
        this.stockDao = daoSession.getStockDao();
    }

    /**
     * Returns the datetime of when the last http request occurs. Gets the cached lastUpdated first if not null, else retrieve from database.
     * Pattern: MMMM dd, EEEE hh:mm:ss a
     * Timezone: Manila, Philippines
     */
    @Override
    public String getLastUpdated(String preference)
    {
        Date lastUpdated = getLastUpdatedDate(preference);

        return this.formatService.formatLastUpdated(lastUpdated);
    }

    /**
     * Insert/Update the given TickerDto as Stock entity in the database.
     *
     * @param tickerDtoList the list of TickerDto
     * @return true if successful, else false
     */
    @Override
    public boolean saveTickerList(List<TickerDto> tickerDtoList)
    {
        Date lastUpdated = this.getLastUpdatedDate(PSEPlannerPreference.LAST_UPDATED_TICKER.toString());
        Set<Stock> stockList = new HashSet<>(tickerDtoList.size());

        // Convert each TickerDto to Stock and store in a Set
        for(TickerDto dto : tickerDtoList)
        {
            Stock stock = this.fromTickerDtoToStock(dto, lastUpdated);
            stockList.add(stock);
        }

        // Bulk update
        this.stockDao.updateInTx(stockList);
        this.updateLastUpdated(lastUpdated);

        return true;
    }

    /**
     * Retrieves the Stock list from the database and converts to TickerDto list.
     *
     * @return {@code List<TickerDto>}
     */
    @Override
    public List<TickerDto> getTickerListFromDatabase()
    {
        List<Stock> stockList = this.stockDao.loadAll();
        List<TickerDto> tickerDtoList = new ArrayList<>(stockList.size());

        for(Stock stock : stockList)
        {
            tickerDtoList.add(new TickerDto(stock.getSymbol(), stock.getName(), stock.getVolume(), stock.getCurrentPrice(), stock.getChange(), stock.getPercentChange()));
        }

        return tickerDtoList;
    }

    @Override
    public Pair<TickerDto, Date> getTicker(String symbol) throws HttpRequestException
    {
        Pair<TickerDto, Date> pair = this.phisixHttpClient.getTicker(symbol);

        //        TickerDto dto = pair.first;

        //        Stock updatedStock = this.stockDao.queryBuilder().where(Properties.Symbol.eq(symbol)).unique();
        //        updatedStock = this.fromTickerDtoToStock(updatedStock, dto, pair.second);
        //
        //        this.stockDao.update(updatedStock);
        //        this.updateLastUpdated(pair.second);
        updateLastUpdated(pair.second);

        return pair;
    }

    @Override
    public Pair<List<TickerDto>, Date> getAllTickerList() throws HttpRequestException
    {
        Pair<List<TickerDto>, Date> pair = this.phisixHttpClient.getAllTickerList();

        //this.updateStockListFromTickerDtoList(pair.first, pair.second);
        updateLastUpdated(pair.second);

        return pair;
    }

    @Override
    public Pair<List<TickerDto>, Date> getTickerList(Collection<String> symbols) throws HttpRequestException
    {
        Pair<List<TickerDto>, Date> pair = this.phisixHttpClient.getTickerList(symbols);

        //this.updateStockListFromTickerDtoList(pair.first, pair.second);
        updateLastUpdated(pair.second);

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
     * Updates the list of Stock that corresponds the list of TickerDto.
     *
     * @param tickerDtoList the list of stocks to update
     * @param lastUpdated   the current datetime
     */
    @Deprecated
    private void updateStockListFromTickerDtoList(List<TickerDto> tickerDtoList, Date lastUpdated)
    {
        List<Stock> stockList = new ArrayList<>(tickerDtoList.size());
        Query<Stock> query = this.stockDao.queryBuilder().where(Properties.Symbol.eq("")).build();

        // Retrieve each stock from the database
        for(TickerDto dto : tickerDtoList)
        {
            query.setParameter(0, dto.getSymbol());
            Stock updatedStock = query.unique();
            updatedStock = this.fromTickerDtoToStock(updatedStock, dto, lastUpdated);

            stockList.add(updatedStock);
        }

        this.stockDao.updateInTx(stockList);
        this.updateLastUpdated(lastUpdated);
    }

    /**
     * Updates the last updated in the shared preferences.
     *
     * @param now the current datetime
     */
    private void updateLastUpdated(Date now)
    {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putLong(PSEPlannerPreference.LAST_UPDATED_TICKER.toString(), now.getTime());
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