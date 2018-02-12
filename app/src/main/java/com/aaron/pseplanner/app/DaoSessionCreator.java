package com.aaron.pseplanner.app;

import com.aaron.pseplanner.entity.DaoSession;

/**
 * Created by Aaron on 18/01/2018.
 *
 * Any class that is able to create a Green DaoSession.
 */
public interface DaoSessionCreator
{
    DaoSession getDaoSession();
}
