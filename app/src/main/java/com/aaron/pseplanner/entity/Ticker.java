package com.aaron.pseplanner.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;

/**
 * Created by Aaron on 3/21/2017.
 */
@Entity(indexes = { @Index(value = "symbol ASC", unique = true) })
public class Ticker
{
    @Id(autoincrement = true)
    private Long id;

    @Unique
    @NotNull
    private String symbol;

    @NotNull
    private String name;

    private long volume;

    @NotNull
    private String currentPrice;

    @NotNull
    private String change;

    @NotNull
    private String percentChange;

    @NotNull
    private Date dateUpdate;

    @Generated(hash = 616569974)
    public Ticker(Long id, @NotNull String symbol, @NotNull String name, long volume, @NotNull String currentPrice,
            @NotNull String change, @NotNull String percentChange, @NotNull Date dateUpdate)
    {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.volume = volume;
        this.currentPrice = currentPrice;
        this.change = change;
        this.percentChange = percentChange;
        this.dateUpdate = dateUpdate;
    }

    @Generated(hash = 1358381819)
    public Ticker()
    {
    }

    public Long getId()
    {
        return id;
    }

    public Ticker setId(Long id)
    {
        this.id = id;
        return this;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public Ticker setSymbol(@NotNull String symbol)
    {
        this.symbol = symbol;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public Ticker setName(@NotNull String name)
    {
        this.name = name;
        return this;
    }

    public long getVolume()
    {
        return volume;
    }

    public Ticker setVolume(long volume)
    {
        this.volume = volume;
        return this;
    }

    public String getCurrentPrice()
    {
        return currentPrice;
    }

    public Ticker setCurrentPrice(@NotNull String currentPrice)
    {
        this.currentPrice = currentPrice;
        return this;
    }

    public String getChange()
    {
        return change;
    }

    public Ticker setChange(@NotNull String change)
    {
        this.change = change;
        return this;
    }

    public String getPercentChange()
    {
        return percentChange;
    }

    public Ticker setPercentChange(@NotNull String percentChange)
    {
        this.percentChange = percentChange;
        return this;
    }

    public Date getDateUpdate()
    {
        return dateUpdate;
    }

    public Ticker setDateUpdate(@NotNull Date dateUpdate)
    {
        this.dateUpdate = dateUpdate;
        return this;
    }
}
