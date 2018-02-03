package com.aaron.pseplanner.test.utils;

import java.util.Calendar;

/**
 * Created by Aaron on 30/01/2018.
 */

public enum DayOfWeek
{
    MONDAY(Calendar.MONDAY),
    TUESDAY(Calendar.TUESDAY),
    WEDNESDAY(Calendar.WEDNESDAY),
    THURSDAY(Calendar.THURSDAY),
    FRIDAY(Calendar.FRIDAY),
    SATURDAY(Calendar.SATURDAY),
    SUNDAY(Calendar.SUNDAY);

    private static final DayOfWeek[] WEEK_DAYS = new DayOfWeek[]{MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY};
    private static final DayOfWeek[] WEEK_ENDS = new DayOfWeek[]{SATURDAY, SUNDAY};

    private int value;

    DayOfWeek(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static DayOfWeek[] weekDays()
    {
        return WEEK_DAYS;
    }

    public static DayOfWeek[] weekEnds()
    {
        return WEEK_ENDS;
    }
}
