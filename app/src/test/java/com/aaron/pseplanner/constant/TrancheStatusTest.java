package com.aaron.pseplanner.constant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TrancheStatusTest
{
    @Test
    public void givenExecutedTranche_whenGetTrancheStatus_thenShouldReturnExecuted()
    {
        assertEquals(TrancheStatus.Executed.toString(), TrancheStatus.getTrancheStatus(true));
    }

    @Test
    public void givenNotExecutedTranche_whenGetTrancheStatus_thenShouldReturnPending()
    {
        assertEquals(TrancheStatus.Pending.toString(), TrancheStatus.getTrancheStatus(false));
    }
}
