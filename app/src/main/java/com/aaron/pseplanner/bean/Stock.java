package com.aaron.pseplanner.bean;

/**
 * Created by Aaron on 6/29/2017.
 */
public interface Stock<T extends Stock>
{
    String getSymbol();

    T setSymbol(String symbol);
}
