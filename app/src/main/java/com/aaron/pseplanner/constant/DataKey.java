package com.aaron.pseplanner.constant;

/**
 * Created by aaron.asuncion on 2/2/2017.
 * Holds all the keys used in Bundle and Intent-extra.
 */
public enum DataKey
{
    EXTRA_ID("com.aaron.pseplanner.id"),
    EXTRA_TICKER("com.aaron.pseplanner.ticker"),
    EXTRA_TRADE("com.aaron.pseplanner.trade"),
    EXTRA_TRADE_LIST("com.aaron.pseplanner.trade_list"),
    EXTRA_DATE("com.aaron.pseplanner.date");

    private String value;

    DataKey(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
