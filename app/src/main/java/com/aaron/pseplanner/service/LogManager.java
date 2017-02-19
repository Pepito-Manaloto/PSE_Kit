package com.aaron.pseplanner.service;

import android.util.Log;

/**
 * Created by Aaron on 2/19/2017.
 * Delegates logging to android.util.Log, and do some extra work. (TODO: No extra work as of now)
 */
public final class LogManager
{
    private static final String LOG_TAG = "PSE_PLANNER";

    private LogManager()
    {
        // Prevent initialization
    }

    public static void debug(String className, String methodName, String message)
    {
        Log.d(LOG_TAG, className + ": " + methodName + ". " + message);
    }

    public static void debug(String className, String methodName, String message, Throwable e)
    {
        Log.d(LOG_TAG, className + ": " + methodName + ". " + message, e);
    }

    public static void info(String className, String methodName, String message)
    {
        Log.i(LOG_TAG, className + ": " + methodName + ". " + message);
    }

    public static void info(String className, String methodName, String message, Throwable e)
    {
        Log.i(LOG_TAG, className + ": " + methodName + ". " + message, e);
    }

    public static void warn(String className, String methodName, String message)
    {
        Log.w(LOG_TAG, className + ": " + methodName + ". " + message);
    }

    public static void warn(String className, String methodName, String message, Throwable e)
    {
        Log.w(LOG_TAG, className + ": " + methodName + ". " + message, e);
    }

    public static void error(String className, String methodName, String message)
    {
        Log.e(LOG_TAG, className + ": " + methodName + ". " + message);
    }

    public static void error(String className, String methodName, String message, Throwable e)
    {
        Log.e(LOG_TAG, className + ": " + methodName + ". " + message, e);
    }

    public static void fatal(String className, String methodName, String message)
    {
        Log.wtf(LOG_TAG, className + ": " + methodName + ". " + message);
    }

    public static void fatal(String className, String methodName, String message, Throwable e)
    {
        Log.wtf(LOG_TAG, className + ": " + methodName + ". " + message, e);
    }
}
