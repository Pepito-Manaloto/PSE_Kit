package com.aaron.pseplanner.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.math.BigDecimal;

/**
 * Created by aaron.asuncion on 12/8/2016.
 * Represents a PSE stock.
 */
public class TickerDto implements Stock, Parcelable, Comparable<TickerDto>
{
    private Long id;

    private String symbol;
    private String name;
    private long volume;
    private BigDecimal currentPrice;
    private BigDecimal change;
    private BigDecimal percentChange;
    private boolean hasTradePlan;

    public TickerDto()
    {
    }

    public TickerDto(String symbol)
    {
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }

        if(!(o instanceof TickerDto))
        {
            return false;
        }

        TickerDto tickerDto = (TickerDto) o;

        return getVolume() == tickerDto.getVolume() && getCurrentPrice().equals(tickerDto.getCurrentPrice()) && getChange().equals(tickerDto.getChange())
                && getPercentChange().equals(tickerDto.getPercentChange()) && getSymbol().equals(tickerDto.getSymbol())
                && getName().equals(tickerDto.getName());
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
        return "TickerDto{" + "symbol='" + symbol + '\'' + ", name='" + name + '\'' + ", volume=" + volume + ", currentPrice=" + currentPrice + ", change="
                + change + ", percentChange=" + percentChange + ", hasTradePlan=" + hasTradePlan + '}';
    }

    @Override
    public String getSymbol()
    {
        return symbol;
    }

    @Override
    public TickerDto setSymbol(String symbol)
    {
        this.symbol = symbol;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public TickerDto setName(String name)
    {
        this.name = name;
        return this;
    }

    public long getVolume()
    {
        return volume;
    }

    public TickerDto setVolume(long volume)
    {
        this.volume = volume;
        return this;
    }

    public BigDecimal getCurrentPrice()
    {
        return currentPrice;
    }

    public TickerDto setCurrentPrice(BigDecimal currentPrice)
    {
        this.currentPrice = currentPrice;
        return this;
    }

    public BigDecimal getChange()
    {
        return change;
    }

    public TickerDto setChange(BigDecimal change)
    {
        this.change = change;
        return this;
    }

    public BigDecimal getPercentChange()
    {
        return percentChange;
    }

    public TickerDto setPercentChange(BigDecimal percentChange)
    {
        this.percentChange = percentChange;
        return this;
    }

    public boolean isHasTradePlan()
    {
        return hasTradePlan;
    }

    public TickerDto setHasTradePlan(boolean hasTradePlan)
    {
        this.hasTradePlan = hasTradePlan;
        return this;
    }

    public Long getId()
    {
        return id;
    }

    public TickerDto setId(Long id)
    {
        this.id = id;
        return this;
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
        dest.writeByte((byte) (this.hasTradePlan ? 1 : 0));
    }

    protected TickerDto(Parcel in)
    {
        this.symbol = in.readString();
        this.name = in.readString();
        this.volume = in.readLong();
        this.currentPrice = new BigDecimal(in.readString());
        this.change = new BigDecimal(in.readString());
        this.percentChange = new BigDecimal(in.readString());
        this.hasTradePlan = in.readByte() != 0;
    }

    public static final Parcelable.Creator<TickerDto> CREATOR = new Parcelable.Creator<TickerDto>()
    {
        @Override
        public TickerDto createFromParcel(Parcel source)
        {
            return new TickerDto(source);
        }

        @Override
        public TickerDto[] newArray(int size)
        {
            return new TickerDto[size];
        }
    };

    @Override
    public int compareTo(@NonNull TickerDto t)
    {
        return this.symbol.compareTo(t.getSymbol());
    }
}
