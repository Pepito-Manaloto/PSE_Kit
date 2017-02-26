package com.aaron.pseplanner.service;

import com.aaron.pseplanner.response.ResponsePSEStock;
import com.aaron.pseplanner.response.ResponsePhisixStock;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Aaron on 2/26/2017.
 */

public interface PhisixService
{
    String BASE_URL = "http://phisix-api.appspot.com/";

    /**
     * Retrieves all stocks with .
     *
     * @return {@code Call<List<ResponsePhisixStock>>} list of response stock
     */
    @GET("stocks.json")
    public Call<List<ResponsePhisixStock>> getAllStocks();

    /**
     * Retrieves a stock with .
     *
     * @param symbol path parameter of the stock to retrieve
     * @return {@code Call<ResponsePhisixStock>} the response stock
     */
    @GET("stocks/{symbol}.json")
    public Call<ResponsePhisixStock> getAllStocks(@Path("symbol") String symbol);
}
