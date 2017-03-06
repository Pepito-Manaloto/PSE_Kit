package com.aaron.pseplanner.service;

import com.aaron.pseplanner.response.Phisix.ResponsePhisixStockWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Aaron on 2/26/2017.
 */

public interface PhisixService
{
    String BASE_URL = "http://phisix-api.appspot.com/";

    /**
     * Retrieves all stocks with dateUpdated, name, currency, amount, percentChange, volume, and symbol.
     *
     * @return {@code Call<ResponsePhisixStockWrapper>} list of response stock
     */
    @GET("stocks.json")
    public Call<ResponsePhisixStockWrapper> getStocksList();

    /**
     * Retrieves a stock with dateUpdated, name, currency, amount, percentChange, volume, and symbol.
     *
     * @param symbol path parameter of the stock to retrieve
     * @return {@code Call<ResponsePhisixStockWrapper>} the response stock
     */
    @GET("stocks/{symbol}.json")
    public Call<ResponsePhisixStockWrapper> getStocksList(@Path("symbol") String symbol);
}
