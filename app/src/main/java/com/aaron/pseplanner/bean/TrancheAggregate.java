package com.aaron.pseplanner.bean;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Objects;

public class TrancheAggregate
{
    private boolean atLeastOneExecuted;
    private BigDecimal averagePrice;
    private long totalShares;
    private BigDecimal realAveragePrice;
    private long realTotalShares;
    private BigDecimal totalWeight;

    public boolean isAtLeastOneExecuted()
    {
        return atLeastOneExecuted;
    }

    public TrancheAggregate setAtLeastOneExecuted(boolean atLeastOneExecuted)
    {
        this.atLeastOneExecuted = atLeastOneExecuted;
        return this;
    }

    public BigDecimal getAveragePrice()
    {
        return averagePrice;
    }

    public TrancheAggregate setAveragePrice(BigDecimal averagePrice)
    {
        this.averagePrice = averagePrice;
        return this;
    }

    public long getTotalShares()
    {
        return totalShares;
    }

    public TrancheAggregate setTotalShares(long totalShares)
    {
        this.totalShares = totalShares;
        return this;
    }

    public BigDecimal getRealAveragePrice()
    {
        return realAveragePrice;
    }

    public TrancheAggregate setRealAveragePrice(BigDecimal realAveragePrice)
    {
        this.realAveragePrice = realAveragePrice;
        return this;
    }

    public long getRealTotalShares()
    {
        return realTotalShares;
    }

    public TrancheAggregate setRealTotalShares(long realTotalShares)
    {
        this.realTotalShares = realTotalShares;
        return this;
    }

    public BigDecimal getTotalWeight()
    {
        return totalWeight;
    }

    public TrancheAggregate setTotalWeight(BigDecimal totalWeight)
    {
        this.totalWeight = totalWeight;
        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }

        if(o == null || getClass() != o.getClass())
        {
            return false;
        }

        TrancheAggregate that = (TrancheAggregate) o;

        return new EqualsBuilder()
                .append(atLeastOneExecuted, that.atLeastOneExecuted)
                .append(totalShares, that.totalShares)
                .append(realTotalShares, that.realTotalShares)
                .append(averagePrice, that.averagePrice)
                .append(realAveragePrice, that.realAveragePrice)
                .append(totalWeight, that.totalWeight)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(atLeastOneExecuted, averagePrice, totalShares, realAveragePrice, realTotalShares, totalWeight);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("atLeastOneExecuted", atLeastOneExecuted)
                .append("averagePrice", averagePrice)
                .append("totalShares", totalShares)
                .append("realAveragePrice", realAveragePrice)
                .append("realTotalShares", realTotalShares)
                .append("totalWeight", totalWeight)
                .toString();
    }
}
