package com.aaron.pseplanner.app;

import android.app.Application;

import com.aaron.pseplanner.entity.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

import static org.mockito.Mockito.mock;

/**
 * Created by Aaron on 18/01/2018.
 */

public class TestApplication extends Application implements DaoSessionCreator
{
    private DaoSession daoSession;

    @Override
    public void onCreate()
    {
        super.onCreate();

        initDaoSession();
        debugOn();
    }

    private void initDaoSession()
    {
        this.daoSession = mock(DaoSession.class);
    }

    public static void debugOn()
    {
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    @Override
    public DaoSession getDaoSession()
    {
        return daoSession;
    }
}
