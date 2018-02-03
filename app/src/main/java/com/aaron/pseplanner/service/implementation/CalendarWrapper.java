package com.aaron.pseplanner.service.implementation;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Aaron on 29/01/2018.
 * Wrapper class that creates Calendar object, so that it can be easily testable.
 */
public class CalendarWrapper
{
    private static final CalendarWrapper INSTANCE = new CalendarWrapper();

    private CalendarWrapper()
    {
    }

    public static CalendarWrapper newInstance()
    {
        return INSTANCE;
    }

    public Calendar newCalendar(TimeZone timezone)
    {
        return Calendar.getInstance(timezone);
    }
}