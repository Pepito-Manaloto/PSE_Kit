package com.aaron.pseplanner.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Aaron on 3/21/2017.
 */
@Entity
public class TradeEntry
{
    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "SYMBOL")
    @NotNull
    private String tradeSymbol;

    @NotNull
    private String entryPrice;

    private long shares;

    @NotNull
    private String percentWeight;

    private int order;

    @Generated(hash = 1533414364)
    public TradeEntry(Long id, @NotNull String tradeSymbol, @NotNull String entryPrice, long shares,
            @NotNull String percentWeight, int order)
    {
        this.id = id;
        this.tradeSymbol = tradeSymbol;
        this.entryPrice = entryPrice;
        this.shares = shares;
        this.percentWeight = percentWeight;
        this.order = order;
    }

    @Generated(hash = 1778071108)
    public TradeEntry()
    {
    }

    public Long getId()
    {
        return id;
    }

    public TradeEntry setId(Long id)
    {
        this.id = id;
        return this;
    }

    public String getTradeSymbol()
    {
        return tradeSymbol;
    }

    public TradeEntry setTradeSymbol(@NotNull String tradeSymbol)
    {
        this.tradeSymbol = tradeSymbol;
        return this;
    }

    public String getEntryPrice()
    {
        return entryPrice;
    }

    public TradeEntry setEntryPrice(@NotNull String entryPrice)
    {
        this.entryPrice = entryPrice;
        return this;
    }

    public long getShares()
    {
        return shares;
    }

    public TradeEntry setShares(long shares)
    {
        this.shares = shares;
        return this;
    }

    public String getPercentWeight()
    {
        return percentWeight;
    }

    public TradeEntry setPercentWeight(@NotNull String percentWeight)
    {
        this.percentWeight = percentWeight;
        return this;
    }

    public int getOrder()
    {
        return order;
    }

    public TradeEntry setOrder(int order)
    {
        this.order = order;
        return this;
    }
}
