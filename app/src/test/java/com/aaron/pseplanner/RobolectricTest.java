package com.aaron.pseplanner;

import android.content.Context;

import com.aaron.pseplanner.app.TestApplication;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by Aaron on 02/01/2018.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class)
public abstract class RobolectricTest
{
    protected Context getContext()
    {
        return RuntimeEnvironment.application.getApplicationContext();
    }
}
