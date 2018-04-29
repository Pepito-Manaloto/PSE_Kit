package com.aaron.pseplanner.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by aaron.asuncion on 12/20/2016.
 * Represents a trade(buy).
 */
public class TradeEntryDto implements Parcelable
{
    private String symbol;
    private BigDecimal entryPrice;
    private long shares;
    private BigDecimal percentWeight;
    private boolean executed;

    public TradeEntryDto()
    {
    }

    public TradeEntryDto(String symbol, BigDecimal entryPrice, long shares, BigDecimal percentWeight, boolean executed)
    {
        this.symbol = symbol;
        this.entryPrice = entryPrice;
        this.shares = shares;
        this.percentWeight = percentWeight;
        this.executed = executed;
    }

    public TradeEntryDto(String symbol, double entryPrice, long shares, double percentWeight, boolean executed)
    {
        this.symbol = symbol;
        this.entryPrice = BigDecimal.valueOf(entryPrice);
        this.shares = shares;
        this.percentWeight = BigDecimal.valueOf(percentWeight);
        this.executed = executed;
    }

    public TradeEntryDto(String symbol, String entryPrice, long shares, String percentWeight, boolean executed)
    {
        this.symbol = symbol;
        this.entryPrice = new BigDecimal(entryPrice);
        this.shares = shares;
        this.percentWeight = new BigDecimal(percentWeight);
        this.executed = executed;
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

        TradeEntryDto that = (TradeEntryDto) o;

        return new EqualsBuilder()
                .append(shares, that.shares)
                .append(executed, that.executed)
                .append(symbol, that.symbol)
                .append(entryPrice, that.entryPrice)
                .append(percentWeight, that.percentWeight)
                .isEquals();
    }

    @Override
    public int hashCode()
    {

        return Objects.hash(symbol, entryPrice, shares, percentWeight, executed);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("symbol", symbol)
                .append("entryPrice", entryPrice)
                .append("shares", shares)
                .append("percentWeight", percentWeight)
                .append("executed", executed)
                .toString();
    }

    public BigDecimal getPercentWeight()
    {
        return percentWeight;
    }

    public TradeEntryDto setPercentWeight(BigDecimal percentWeight)
    {
        this.percentWeight = percentWeight;
        return this;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public TradeEntryDto setSymbol(String symbol)
    {
        this.symbol = symbol;
        return this;
    }

    public BigDecimal getEntryPrice()
    {
        return entryPrice;
    }

    public TradeEntryDto setEntryPrice(BigDecimal entryPrice)
    {
        this.entryPrice = entryPrice;
        return this;
    }

    public long getShares()
    {
        return shares;
    }

    public TradeEntryDto setShares(long shares)
    {
        this.shares = shares;
        return this;
    }

    public boolean isExecuted()
    {
        return executed;
    }

    public TradeEntryDto setExecuted(boolean executed)
    {
        this.executed = executed;
        return this;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable instance's marshaled representation.
     */
    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     * Flatten this TradeEntryDto object in to a Parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.symbol);
        dest.writeString(this.entryPrice.toPlainString());
        dest.writeLong(this.shares);
        dest.writeString(this.percentWeight.toPlainString());
        dest.writeByte((byte) (executed ? 1 : 0));
    }

    /**
     * Constructor that will be called in creating the parcel.
     */
    private TradeEntryDto(Parcel in)
    {
        this.symbol = in.readString();
        this.entryPrice = new BigDecimal(in.readString());
        this.shares = in.readLong();
        this.percentWeight = new BigDecimal(in.readString());
        this.executed = in.readByte() == 1;
    }

    /**
     * Generates instances of your Parcelable class from a Parcel.
     */
    public static final Parcelable.Creator<TradeEntryDto> CREATOR = new Parcelable.Creator<TradeEntryDto>()
    {
        @Override
        public TradeEntryDto createFromParcel(Parcel source)
        {
            return new TradeEntryDto(source);
        }

        @Override
        public TradeEntryDto[] newArray(int size)
        {
            return new TradeEntryDto[size];
        }
    };
}
