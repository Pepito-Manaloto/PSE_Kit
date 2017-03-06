package com.aaron.pseplanner.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

/**
 * Created by aaron.asuncion on 12/8/2016.
 * Represents a PSE stock.
 */
public class Ticker implements Parcelable
{
    private String symbol;
    private String name;
    private long volume;
    private BigDecimal currentPrice;
    private BigDecimal change;
    private BigDecimal percentChange;

    public Ticker()
    {
    }

    public Ticker(String symbol, String name, long volume, BigDecimal currentPrice, BigDecimal change, BigDecimal percentChange)
    {
        this.symbol = symbol;
        this.name = name;
        this.volume = volume;
        this.currentPrice = currentPrice;
        this.change = change;
        this.percentChange = percentChange;
    }

    public Ticker(String symbol, String name, long volume, double currentPrice, double change, double percentChange)
    {
        this.symbol = symbol;
        this.name = name;
        this.volume = volume;
        this.currentPrice = BigDecimal.valueOf(currentPrice);
        this.change = BigDecimal.valueOf(change);
        this.percentChange = BigDecimal.valueOf(percentChange);
    }

    public Ticker(String symbol, String name, long volume, String currentPrice, String change, String percentChange)
    {
        this.symbol = symbol;
        this.name = name;
        this.volume = volume;
        this.currentPrice = new BigDecimal(currentPrice);
        this.change = new BigDecimal(change);
        this.percentChange = new BigDecimal(percentChange);
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }

        if(!(o instanceof Ticker))
        {
            return false;
        }

        Ticker ticker = (Ticker) o;

        return getVolume() == ticker.getVolume() && getCurrentPrice().equals(ticker.getCurrentPrice()) &&
                getChange().equals(ticker.getChange()) && getPercentChange().equals(ticker.getPercentChange()) &&
                getSymbol().equals(ticker.getSymbol()) && getName().equals(ticker.getName());
    }

    @Override
    public int hashCode()
    {
        int result = getSymbol().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + (int) (getVolume() ^ (getVolume() >>> 32));
        result = 31 * result + getCurrentPrice().hashCode();
        result = 31 * result + getChange().hashCode();
        result = 31 * result + getPercentChange().hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "Ticker{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", volume=" + volume +
                ", currentPrice=" + currentPrice +
                ", change=" + change +
                ", percentChange=" + percentChange +
                '}';
    }

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getVolume()
    {
        return volume;
    }

    public void setVolume(long volume)
    {
        this.volume = volume;
    }

    public BigDecimal getCurrentPrice()
    {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice)
    {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getChange()
    {
        return change;
    }

    public void setChange(BigDecimal change)
    {
        this.change = change;
    }

    public BigDecimal getPercentChange()
    {
        return percentChange;
    }

    public void setPercentChange(BigDecimal percentChange)
    {
        this.percentChange = percentChange;
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
        dest.writeString(this.name);
        dest.writeLong(this.volume);
        dest.writeString(this.currentPrice.toPlainString());
        dest.writeString(this.change.toPlainString());
        dest.writeString(this.percentChange.toPlainString());
    }

    protected Ticker(Parcel in)
    {
        this.symbol = in.readString();
        this.name = in.readString();
        this.volume = in.readLong();
        this.currentPrice = new BigDecimal(in.readString());
        this.change = new BigDecimal(in.readString());
        this.percentChange = new BigDecimal(in.readString());
    }

    public static final Parcelable.Creator<Ticker> CREATOR = new Parcelable.Creator<Ticker>()
    {
        @Override
        public Ticker createFromParcel(Parcel source)
        {
            return new Ticker(source);
        }

        @Override
        public Ticker[] newArray(int size)
        {
            return new Ticker[size];
        }
    };
}
