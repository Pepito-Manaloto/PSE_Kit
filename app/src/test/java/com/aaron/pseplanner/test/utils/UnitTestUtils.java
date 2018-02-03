package com.aaron.pseplanner.test.utils;

import org.apache.commons.lang3.RandomUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Aaron on 03/12/2017.
 */

public final class UnitTestUtils
{
    public static <T> void setPrivateField(T object, String fieldName, Object value) throws Exception
    {
        Field field = setPrivateFieldAccessible(object, fieldName);
        field.set(object, value);
    }

    public static <T> Object getPrivateField(T object, String fieldName) throws Exception
    {
        Field field = setPrivateFieldAccessible(object, fieldName);
        return field.get(object);
    }

    public static <T> Field setPrivateFieldAccessible(T object, String fieldName) throws Exception
    {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        return field;
    }

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

    public static Date newTime(int hour, int minute, int second, DayOfWeek dayOfWeek)
    {
        GregorianCalendar date = (GregorianCalendar) GregorianCalendar.getInstance();
        date.set(Calendar.DAY_OF_WEEK, dayOfWeek.getValue());
        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, minute);
        date.set(Calendar.SECOND, second);

        return date.getTime();
    }

    public static int randomSecondOrMinute()
    {
        return RandomUtils.nextInt(0, 60);
    }

    public static int randomSecondOrMinuteMin30()
    {
        return RandomUtils.nextInt(30, 60);
    }

    public static int randomSecondOrMinuteMax29()
    {
        return RandomUtils.nextInt(0, 30);
    }
}
