package com.aaron.pseplanner.constant;

/**
 * Created by aaron.asuncion on 2/1/2017.
 */

public enum IntentRequestCode
{
    HOME(1),
    CREATE_TRADE_PLAN(2),
    UPDATE_TRADE_PLAN(3),
    VIEW_TRADE_PLAN(4);

    private int code;

    IntentRequestCode(int code)
    {
        this.code = code;
    }

    public int code()
    {
        return this.code;
    }
}
