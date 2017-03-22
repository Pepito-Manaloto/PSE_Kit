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
    private long id;

    @Property(nameInDb = "SYMBOL")
    @NotNull
    private String tradeSymbol;

    @NotNull
    private String entryPrice;

    private long shares;

    @NotNull
    private String percentWeight;

    private int order;

    @Generated(hash = 1082169048)
    public TradeEntry(long id, @NotNull String tradeSymbol,
            @NotNull String entryPrice, long shares, @NotNull String percentWeight,
            int order) {
        this.id = id;
        this.tradeSymbol = tradeSymbol;
        this.entryPrice = entryPrice;
        this.shares = shares;
        this.percentWeight = percentWeight;
        this.order = order;
    }

    @Generated(hash = 1778071108)
    public TradeEntry() {
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getTradeSymbol()
    {
        return tradeSymbol;
    }

    public void setTradeSymbol(@NotNull String tradeSymbol)
    {
        this.tradeSymbol = tradeSymbol;
    }

    public String getEntryPrice()
    {
        return entryPrice;
    }

    public void setEntryPrice(@NotNull String entryPrice)
    {
        this.entryPrice = entryPrice;
    }

    public long getShares()
    {
        return shares;
    }

    public void setShares(long shares)
    {
        this.shares = shares;
    }

    public String getPercentWeight()
    {
        return percentWeight;
    }

    public void setPercentWeight(@NotNull String percentWeight)
    {
        this.percentWeight = percentWeight;
    }

    public int getOrder()
    {
        return order;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }
}
