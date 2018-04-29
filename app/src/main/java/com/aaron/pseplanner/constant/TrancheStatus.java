package com.aaron.pseplanner.constant;

public enum TrancheStatus
{
    Executed, Pending;

    public static String getTrancheStatus(boolean executed)
    {
        if(executed)
        {
            return Executed.toString();
        }

        return Pending.toString();
    }
}
