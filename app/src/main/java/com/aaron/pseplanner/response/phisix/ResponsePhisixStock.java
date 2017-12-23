package com.aaron.pseplanner.response.phisix;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Aaron on 3/5/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsePhisixStock
{
    private String name;
    private String currency;
    private double amount;
    private double percentChange;
    private long volume;
    private String symbol;

    /**
     * Setter.
     */
    @JsonCreator
    public ResponsePhisixStock(@JsonProperty("name") String name, @JsonProperty("price") ResponsePrice price,
            @JsonProperty("percent_change") double percentChange,
            @JsonProperty("volume") long volume, @JsonProperty("symbol") String symbol)
    {
        this.name = name;
        this.currency = price.getCurrency();
        this.amount = price.getAmount();
        this.percentChange = percentChange;
        this.volume = volume;
        this.symbol = symbol;
    }

    public String getName()
    {
        return name;
    }

    public String getCurrency()
    {
        return currency;
    }

    public double getAmount()
    {
        return amount;
    }

    public double getPercentChange()
    {
        return percentChange;
    }

    public long getVolume()
    {
        return volume;
    }

    public String getSymbol()
    {
        return symbol;
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

        ResponsePhisixStock that = (ResponsePhisixStock) o;

        return Double.compare(that.amount, amount) == 0 && Double.compare(that.percentChange, percentChange) == 0 &&
                volume == that.volume && name.equals(that.name) && currency.equals(that.currency) && symbol.equals(that.symbol);
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = name.hashCode();
        result = 31 * result + currency.hashCode();
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(percentChange);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (volume ^ (volume >>> 32));
        result = 31 * result + symbol.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "ResponsePhisixStock{" + "name='" + name + '\'' + ", currency='" + currency + '\'' + ", amount=" + amount + ", percentChange=" + percentChange
                + ", volume=" + volume + ", symbol='" + symbol + '\'' + '}';
    }
}
