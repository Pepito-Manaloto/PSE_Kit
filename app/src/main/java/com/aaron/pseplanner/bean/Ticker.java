package com.aaron.pseplanner.bean;

/**
 * Created by aaron.asuncion on 12/8/2016.
 */

public class Ticker
{
    private String stock;
    private double price;
    private double change;
    private double percentChange;

    public Ticker()
    {
    }

    public Ticker(String stock, double price, double change, double percentChange)
    {
        this.stock = stock;
        this.price = price;
        this.change = change;
        this.percentChange = percentChange;
    }

    public String getStock()
    {
        return stock;
    }

    public void setStock(String stock)
    {
        this.stock = stock;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
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

        return Double.compare(ticker.getPrice(), getPrice()) == 0 &&
                Double.compare(ticker.getChange(), getChange()) == 0 &&
                Double.compare(ticker.getPercentChange(), getPercentChange()) == 0 &&
                getStock().equals(ticker.getStock());
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = getStock().hashCode();
        temp = Double.doubleToLongBits(getPrice());
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
                "stock='" + stock + '\'' +
                ", price=" + price +
                ", change=" + change +
                ", percentChange=" + percentChange +
                '}';
    }
}
