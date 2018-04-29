package com.aaron.pseplanner.bean;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Objects;

public class Tranche
{
    private int order;
    private boolean executed;
    private BigDecimal price;
    private long shares;
    private BigDecimal weight;

    public Tranche()
    {

    }

    public Tranche(int order, boolean executed, BigDecimal price, long shares, BigDecimal weight)
    {
        this.order = order;
        this.executed = executed;
        this.price = price;
        this.shares = shares;
        this.weight = weight;
    }

    public int getOrder()
    {
        return order;
    }

    public Tranche setOrder(int order)
    {
        this.order = order;
        return this;
    }

    public boolean isExecuted()
    {
        return executed;
    }

    public Tranche setExecuted(boolean executed)
    {
        this.executed = executed;
        return this;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public Tranche setPrice(BigDecimal price)
    {
        this.price = price;
        return this;
    }

    public long getShares()
    {
        return shares;
    }

    public Tranche setShares(long shares)
    {
        this.shares = shares;
        return this;
    }

    public BigDecimal getWeight()
    {
        return weight;
    }

    public Tranche setWeight(BigDecimal weight)
    {
        this.weight = weight;
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

        Tranche tranche = (Tranche) o;

        return new EqualsBuilder()
                .append(order, tranche.order)
                .append(executed, tranche.executed)
                .append(shares, tranche.shares)
                .append(price, tranche.price)
                .append(weight, tranche.weight)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(order, executed, price, shares, weight);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("order", order)
                .append("executed", executed)
                .append("price", price)
                .append("shares", shares)
                .append("weight", weight)
                .toString();
    }
}
