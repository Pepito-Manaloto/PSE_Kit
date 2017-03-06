package com.aaron.pseplanner.service;

import com.aaron.pseplanner.response.PSE.ResponsePSEStock;
import com.aaron.pseplanner.response.PSE.ResponsePSEStockInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Aaron on 2/26/2017.
 */

public interface PSEService
{
    String BASE_URL = "http:/www.pse.com.ph/";

    /**
     * Retrieves all stocks with volume, indicator, % change close, last traded price, security alias, and security symbol.
     *
     * @param method query parameter to specify which method to use (getSecuritiesAndIndicesForPublic)
     * @param ajax   query parameter to specify the type of request (true or false)
     * @return {@code Call<List<ResponsePSEStock>>} list of response stock
     */
    @GET("stockMarket/home.html")
    public Call<List<ResponsePSEStock>> getAllStocks(@Query("method") String method, @Query("ajax") boolean ajax);

    /**
     * Retrieves all stocks with security status, listing date, symbol, security name, company id, company name, and security id.
     *
     * @param method query parameter to specify which method to use (getListedRecords)
     * @param ajax   query parameter to specify the type of request (true or false)
     * @return {@code Call<List<ResponsePSEStockInfo>>} list of response stock info
     */
    @GET("stockMarket/companyInfoSecurityProfile.html")
    public Call<List<ResponsePSEStockInfo>> getAllStocksInfo(@Query("method") String method, @Query("ajax") boolean ajax);

    /**
     * Retrieves all stocks with security status, company id, symbol, company name, security id, and security name.
     *
     * @param method query parameter to specify which method to use (findSecurityOrCompany)
     * @param query  the stock symbol to get
     * @param ajax   query parameter to specify the type of request (true or false)
     * @return {@code Call<List<ResponsePSEStockInfo>>} the response stock info
     */
    @GET("stockMarket/home.html")
    public Call<ResponsePSEStockInfo> getAllStocks(@Query("method") String method, @Query("query") String query, @Query("ajax") boolean ajax);

    /**
     * Retrieves a stock with low, high, 52 week high, 52 week low, change close, last traded date, last traded price, % change close, previous, open, symbol, average price, and total volume.
     *
     * @param method   query parameter to specify which method to use (fetchHeaderData)
     * @param symbol   query parameter the stock symbol to get
     * @param security query parameter the stock's security number
     * @param ajax     query parameter to specify the type of request (true or false)
     * @return {@code Call<ResponsePSEStock>} the response stock
     */
    @GET("stockMarket/companyInfo.html")
    public Call<ResponsePSEStock> getStock(@Query("method") String method, @Query("company") String symbol, @Query("security") int security, @Query("ajax") boolean ajax);
}
