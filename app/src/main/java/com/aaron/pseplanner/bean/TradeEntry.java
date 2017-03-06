package com.aaron.pseplanner.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

/**
 * Created by aaron.asuncion on 12/20/2016.
 * Represents a trade(buy).
 */
public class TradeEntry implements Parcelable
{
    private String symbol;
    private BigDecimal entryPrice;
    private long shares;
    private BigDecimal percentWeight;

    public TradeEntry()
    {
    }

    public TradeEntry(String symbol, BigDecimal entryPrice, long shares, BigDecimal percentWeight)
    {
        this.symbol = symbol;
        this.entryPrice = entryPrice;
        this.shares = shares;
        this.percentWeight = percentWeight;
    }

    public TradeEntry(String symbol, double entryPrice, long shares, double percentWeight)
    {
        this.symbol = symbol;
        this.entryPrice = BigDecimal.valueOf(entryPrice);
        this.shares = shares;
        this.percentWeight = BigDecimal.valueOf(percentWeight);
    }

    public TradeEntry(String symbol, String entryPrice, long shares, String percentWeight)
    {
        this.symbol = symbol;
        this.entryPrice = new BigDecimal(entryPrice);
        this.shares = shares;
        this.percentWeight = new BigDecimal(percentWeight);
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof TradeEntry))
        {
            return false;
        }

        TradeEntry that = (TradeEntry) o;

        return entryPrice.equals(that.entryPrice) && shares == that.shares &&
                percentWeight.equals(that.percentWeight) && symbol.equals(that.symbol);
    }

    @Override
    public int hashCode()
    {
        int result = getSymbol().hashCode();
        result = 31 * result + getEntryPrice().hashCode();
        result = 31 * result + (int) (getShares() ^ (getShares() >>> 32));
        result = 31 * result + getPercentWeight().hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "TradeEntry{" +
                "symbol='" + symbol + '\'' +
                ", entryPrice=" + entryPrice +
                ", shares=" + shares +
                ", percentWeight=" + percentWeight +
                '}';
    }

    public BigDecimal getPercentWeight()
    {
        return percentWeight;
    }

    public void setPercentWeight(BigDecimal percentWeight)
    {
        this.percentWeight = percentWeight;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public BigDecimal getEntryPrice()
    {
        return entryPrice;
    }

    public void setEntryPrice(BigDecimal entryPrice)
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

    /**
     * Describe the kinds of special objects contained in this Parcelable instance's marshaled representation.
     */
    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     * Flatten this TradeEntry object in to a Parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.symbol);
        dest.writeString(this.entryPrice.toPlainString());
        dest.writeLong(this.shares);
        dest.writeString(this.percentWeight.toPlainString());
    }

    /**
     * Constructor that will be called in creating the parcel.
     */
    private TradeEntry(Parcel in)
    {
        this.symbol = in.readString();
        this.entryPrice = new BigDecimal(in.readString());
        this.shares = in.readLong();
        this.percentWeight = new BigDecimal(in.readString());
    }

    /**
     * Generates instances of your Parcelable class from a Parcel.
     */
    public static final Parcelable.Creator<TradeEntry> CREATOR = new Parcelable.Creator<TradeEntry>()
    {
        @Override
        public TradeEntry createFromParcel(Parcel source)
        {
            return new TradeEntry(source);
        }

        @Override
        public TradeEntry[] newArray(int size)
        {
            return new TradeEntry[size];
        }
    };
}
