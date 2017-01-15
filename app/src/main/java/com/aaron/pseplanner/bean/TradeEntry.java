package com.aaron.pseplanner.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aaron.asuncion on 12/20/2016.
 * Represents a trade(buy).
 */
public class TradeEntry implements Parcelable
{
    private String symbol;
    private double entryPrice;
    private long shares;
    private int percentWeight;

    public TradeEntry()
    {
    }

    public TradeEntry(int percentWeight, String symbol, double entryPrice, long shares)
    {
        this.percentWeight = percentWeight;
        this.symbol = symbol;
        this.entryPrice = entryPrice;
        this.shares = shares;
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

        return Double.compare(that.entryPrice, entryPrice) == 0 && shares == that.shares &&
                percentWeight == that.percentWeight && symbol.equals(that.symbol);
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = symbol.hashCode();
        temp = Double.doubleToLongBits(entryPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (shares ^ (shares >>> 32));
        result = 31 * result + percentWeight;
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

    public int getPercentWeight()
    {
        return percentWeight;
    }

    public void setPercentWeight(int percentWeight)
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

    public double getEntryPrice()
    {
        return entryPrice;
    }

    public void setEntryPrice(double entryPrice)
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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.symbol);
        dest.writeDouble(this.entryPrice);
        dest.writeLong(this.shares);
        dest.writeInt(this.percentWeight);
    }

    protected TradeEntry(Parcel in)
    {
        this.symbol = in.readString();
        this.entryPrice = in.readDouble();
        this.shares = in.readLong();
        this.percentWeight = in.readInt();
    }

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
