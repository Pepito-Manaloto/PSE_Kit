package com.aaron.pseplanner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Aaron on 03/12/2017.
 */

public final class UnitTestUtils
{
    public static void setFinalStatic(Field field, Object newValue) throws Exception
    {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    public static Date newDate(int year, int month, int day)
    {
        return newDateTime(year, month, day, 0, 0, 0);
    }

    public static Date newDate(int year, int month, int day, TimeZone timezone)
    {
        return newDateTime(year, month, day, 0, 0, 0, timezone);
    }

    public static Date newDateTime(int year, int month, int day, int hour, int minute, int second)
    {
        return new GregorianCalendar(year, month - 1, day, hour, minute, second).getTime();
    }

    public static Date newDateTime(int year, int month, int day, int hour, int minute, int second, TimeZone timezone)
    {
        GregorianCalendar date = (GregorianCalendar) GregorianCalendar.getInstance(timezone);
        date.set(year, month - 1, day, hour, minute, second);
        return date.getTime();
    }
}
