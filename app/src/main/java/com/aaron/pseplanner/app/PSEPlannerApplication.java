package com.aaron.pseplanner.app;

import android.app.Application;
import android.os.Build;

import com.aaron.pseplanner.entity.DaoMaster;
import com.aaron.pseplanner.entity.DaoSession;
import com.aaron.pseplanner.service.LogManager;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by Aaron on 3/22/2017. Initializes LeakCanary and GreenDao
 */
public class PSEPlannerApplication extends Application
{
    /**
     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
     */
    public static boolean ENCRYPTED = false;

    public static final String DATABASE_NAME = "pse-planner-db";
    public static final String DATABASE_ENCRYPTED_NAME = DATABASE_NAME + "-encrypted";

    public static final String CLASS_NAME = PSEPlannerApplication.class.getSimpleName();

    private DaoSession daoSession;

    @Override
    public void onCreate()
    {
        super.onCreate();

        if(!isRoboUnitTest())
        {
            if(LeakCanary.isInAnalyzerProcess(this))
            {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);

            // Access in Google Chrome url via -> chrome://inspect
            Stetho.initializeWithDefaults(this);
        }

        // Normal app init code...
        initDaoSession();

        /*
         * uncomment for debugging purposes only debugOn();
         */
    }

    private void initDaoSession()
    {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? DATABASE_ENCRYPTED_NAME : DATABASE_NAME);
        Database database = ENCRYPTED ? helper.getEncryptedWritableDb("") : helper.getWritableDb();
        daoSession = new DaoMaster(database).newSession();

        LogManager.debug(CLASS_NAME, "onCreate", "Initialized GreenDao database and session.");
    }

    public static void debugOn()
    {
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public static boolean isRoboUnitTest()
    {
        return "robolectric".equals(Build.FINGERPRINT);
    }

    public DaoSession getDaoSession()
    {
        return daoSession;
    }
}
