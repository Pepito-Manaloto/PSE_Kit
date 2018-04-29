package com.aaron.pseplanner.test.utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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

    private static final List<DayOfWeek> WEEK_DAYS = Collections.unmodifiableList(Arrays.asList(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY));
    private static final List<DayOfWeek> WEEK_ENDS = Collections.unmodifiableList(Arrays.asList(SATURDAY, SUNDAY));

    private int value;

    DayOfWeek(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static List<DayOfWeek> weekDays()
    {
        return WEEK_DAYS;
    }

    public static List<DayOfWeek> weekEnds()
    {
        return WEEK_ENDS;
    }
}
