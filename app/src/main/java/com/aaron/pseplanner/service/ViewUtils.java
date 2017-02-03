package com.aaron.pseplanner.service;

import static com.aaron.pseplanner.constant.Constants.LOG_TAG;

import android.text.InputFilter;
import android.util.Log;

import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * Created by aaron.asuncion on 2/3/2017.
 */

public final class ViewUtils
{
    private ViewUtils()
    {
        // Prevent initialization
    }

    /**
     * Retrieves the edit text's android:maxLength.
     *
     * @param filters     the InputFilters
     * @param callerClass the caller class name
     * @return int the EditText max length
     */
    public static int getEditTextMaxLength(InputFilter[] filters, String callerClass)
    {
        for(InputFilter filter : filters)
        {
            if(filter instanceof InputFilter.LengthFilter)
            {
                if(android.os.Build.VERSION.SDK_INT >= 21)
                {
                    return ((InputFilter.LengthFilter) filter).getMax();
                }else
                {
                    try
                    {
                        return (int) FieldUtils.readField(filter, "mMax", true);
                    }
                    catch(IllegalAccessException e)
                    {
                        Log.e(LOG_TAG, callerClass + ": getEditTextMaxLength. Error retrieving EditText's maxLength.", e);
                    }
                }
            }
        }

        return 0;
    }
}
