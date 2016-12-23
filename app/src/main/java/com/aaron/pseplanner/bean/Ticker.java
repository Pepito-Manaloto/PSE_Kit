package com.aaron.pseplanner.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aaron.asuncion on 12/8/2016.
 */

public class Ticker implements Parcelable
{
    private String symbol;
    private String name;
    private long volume;
    private double previous;
    private double low;
    private double high;
    private double currentPrice;
    private double averagePrice;
    private double yearLow;
    private double yearHigh;
    private double change;
    private double percentChange;

    public Ticker()
    {
    }

    public Ticker(String symbol, double currentPrice, double change, double percentChange)
    {
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.change = change;
        this.percentChange = percentChange;
    }

    public Ticker(String symbol, String name, long volume, double previous, double low, double high, double currentPrice, double averagePrice, double yearLow, double yearHigh, double change, double percentChange)
    {
        this.symbol = symbol;
        this.name = name;
        this.volume = volume;
        this.previous = previous;
        this.low = low;
        this.high = high;
        this.currentPrice = currentPrice;
        this.averagePrice = averagePrice;
        this.yearLow = yearLow;
        this.yearHigh = yearHigh;
        this.change = change;
        this.percentChange = percentChange;
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

        return getVolume() == ticker.getVolume() && Double.compare(ticker.getPrevious(), getPrevious()) == 0 &&
                Double.compare(ticker.getLow(), getLow()) == 0 && Double.compare(ticker.getHigh(), getHigh()) == 0 &&
                Double.compare(ticker.getCurrentPrice(), getCurrentPrice()) == 0 && Double.compare(ticker.getAveragePrice(), getAveragePrice()) == 0 &&
                Double.compare(ticker.getYearLow(), getYearLow()) == 0 && Double.compare(ticker.getYearHigh(), getYearHigh()) == 0 &&
                Double.compare(ticker.getChange(), getChange()) == 0 && Double.compare(ticker.getPercentChange(), getPercentChange()) == 0 &&
                getSymbol().equals(ticker.getSymbol()) && getName().equals(ticker.getName());
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = getSymbol().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + (int) (getVolume() ^ (getVolume() >>> 32));
        temp = Double.doubleToLongBits(getPrevious());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getLow());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getHigh());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getCurrentPrice());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getAveragePrice());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getYearLow());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getYearHigh());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getChange());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getPercentChange());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString()
    {
        return "Ticker{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", volume=" + volume +
                ", previous=" + previous +
                ", low=" + low +
                ", high=" + high +
                ", currentPrice=" + currentPrice +
                ", averagePrice=" + averagePrice +
                ", yearLow=" + yearLow +
                ", yearHigh=" + yearHigh +
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

    public double getCurrentPrice()
    {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice)
    {
        this.currentPrice = currentPrice;
    }

    public double getChange()
    {
        return change;
    }

    public void setChange(double change)
    {
        this.change = change;
    }

    public double getPercentChange()
    {
        return percentChange;
    }

    public void setPercentChange(double percentChange)
    {
        this.percentChange = percentChange;
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

    public double getPrevious()
    {
        return previous;
    }

    public void setPrevious(double previous)
    {
        this.previous = previous;
    }

    public double getLow()
    {
        return low;
    }

    public void setLow(double low)
    {
        this.low = low;
    }

    public double getHigh()
    {
        return high;
    }

    public void setHigh(double high)
    {
        this.high = high;
    }

    public double getAveragePrice()
    {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice)
    {
        this.averagePrice = averagePrice;
    }

    public double getYearLow()
    {
        return yearLow;
    }

    public void setYearLow(double yearLow)
    {
        this.yearLow = yearLow;
    }

    public double getYearHigh()
    {
        return yearHigh;
    }

    public void setYearHigh(double yearHigh)
    {
        this.yearHigh = yearHigh;
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
        dest.writeDouble(this.previous);
        dest.writeDouble(this.low);
        dest.writeDouble(this.high);
        dest.writeDouble(this.currentPrice);
        dest.writeDouble(this.averagePrice);
        dest.writeDouble(this.yearLow);
        dest.writeDouble(this.yearHigh);
        dest.writeDouble(this.change);
        dest.writeDouble(this.percentChange);
    }

    protected Ticker(Parcel in)
    {
        this.symbol = in.readString();
        this.name = in.readString();
        this.volume = in.readLong();
        this.previous = in.readDouble();
        this.low = in.readDouble();
        this.high = in.readDouble();
        this.currentPrice = in.readDouble();
        this.averagePrice = in.readDouble();
        this.yearLow = in.readDouble();
        this.yearHigh = in.readDouble();
        this.change = in.readDouble();
        this.percentChange = in.readDouble();
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
