package com.aaron.pseplanner.app;

import android.app.Application;

import com.aaron.pseplanner.entity.DaoMaster;
import com.aaron.pseplanner.entity.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Aaron on 3/22/2017.
 */

public class PSEPlannerApplication extends Application
{
    /**
     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
     */
    public static boolean ENCRYPTED = false;

    public static final String DATABASE_NAME = "pse-planner-db";
    public static final String DATABASE_ENCRYPTED_NAME = DATABASE_NAME + "-encrypted";

    private DaoSession daoSession;

    @Override
    public void onCreate()
    {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? DATABASE_ENCRYPTED_NAME : DATABASE_NAME);
        Database database = ENCRYPTED ? helper.getEncryptedWritableDb("") : helper.getWritableDb();
        daoSession = new DaoMaster(database).newSession();
    }

    public DaoSession getDaoSession()
    {
        return daoSession;
    }
}
