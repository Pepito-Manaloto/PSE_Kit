package com.aaron.pseplanner;

import android.content.Context;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

/**
 * Created by Aaron on 02/01/2018.
 */
@RunWith(RobolectricTestRunner.class)
public abstract class RobolectricTest
{
    protected Context getContext()
    {
        return RuntimeEnvironment.application.getApplicationContext();
    }
}
