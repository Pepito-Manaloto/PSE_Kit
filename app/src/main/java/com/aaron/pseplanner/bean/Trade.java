package com.aaron.pseplanner.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * Created by aaron.asuncion on 12/20/2016.
 * Represents a trading plan.
 */
public class Trade implements Parcelable
{
    private Date entryDate;
    private int holdingPeriod;
    private String symbol;
    private double currentPrice;
    private double averagePrice;
    private long totalShares;
    private double totalAmount;
    private double priceToBreakEven;
    private double targetPrice;
    private double gainLoss;
    private double gainLossPercent;
    private long gainToTarget;
    private double lossToStopLoss;
    private double stopLoss;
    private Date stopDate;
    private int daysToStopDate;
    private double riskReward;
    private long capital;
    private double percentCapital;
    private List<TradeEntry> tradeEntries;

    public Trade()
    {
    }

    public Trade(String symbol, Date entryDate, int holdingPeriod, double currentPrice, double averagePrice, long totalShares, double totalAmount, double priceToBreakEven, double targetPrice, double gainLoss, double gainLossPercent, long gainToTarget, double stopLoss, double lossToStopLoss, Date stopDate, int daysToStopDate, double riskReward, long capital, double percentCapital, List<TradeEntry> tradeEntries)
    {
        this.symbol = symbol;
        this.lossToStopLoss = lossToStopLoss;
        this.entryDate = entryDate;
        this.holdingPeriod = holdingPeriod;

        this.currentPrice = currentPrice;
        this.averagePrice = averagePrice;
        this.totalShares = totalShares;
        this.totalAmount = totalAmount;
        this.priceToBreakEven = priceToBreakEven;
        this.targetPrice = targetPrice;
        this.gainLoss = gainLoss;
        this.gainLossPercent = gainLossPercent;
        this.gainToTarget = gainToTarget;
        this.stopLoss = stopLoss;
        this.stopDate = stopDate;
        this.daysToStopDate = daysToStopDate;
        this.riskReward = riskReward;
        this.capital = capital;
        this.percentCapital = percentCapital;
        this.tradeEntries = tradeEntries;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }

        if(!(o instanceof Trade))
        {
            return false;
        }

        Trade trade = (Trade) o;

        return Double.compare(trade.currentPrice, currentPrice) == 0 && Double.compare(trade.averagePrice, averagePrice) == 0 &&
                totalShares == trade.totalShares && totalAmount == trade.totalAmount && Double.compare(trade.priceToBreakEven, priceToBreakEven) == 0 &&
                Double.compare(trade.targetPrice, targetPrice) == 0 && gainLoss == trade.gainLoss && gainLossPercent == trade.gainLossPercent &&
                gainToTarget == trade.gainToTarget && Double.compare(trade.stopLoss, stopLoss) == 0 && lossToStopLoss == trade.lossToStopLoss &&
                daysToStopDate == trade.daysToStopDate && Double.compare(trade.riskReward, riskReward) == 0 && capital == trade.capital &&
                percentCapital == trade.percentCapital && entryDate.equals(trade.entryDate) && holdingPeriod == trade.holdingPeriod && symbol.equals(trade.symbol) &&
                stopDate.equals(trade.stopDate) && tradeEntries.equals(trade.tradeEntries);
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = entryDate.hashCode();
        result = 31 * result + holdingPeriod;
        result = 31 * result + symbol.hashCode();
        temp = Double.doubleToLongBits(currentPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(averagePrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (totalShares ^ (totalShares >>> 32));
        temp = Double.doubleToLongBits(totalAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(priceToBreakEven);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(targetPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(gainLoss);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(gainLossPercent);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (gainToTarget ^ (gainToTarget >>> 32));
        temp = Double.doubleToLongBits(lossToStopLoss);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(stopLoss);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + stopDate.hashCode();
        result = 31 * result + daysToStopDate;
        temp = Double.doubleToLongBits(riskReward);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (capital ^ (capital >>> 32));
        temp = Double.doubleToLongBits(percentCapital);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + tradeEntries.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "Trade{" +
                "entryDate=" + entryDate +
                ", holdingPeriod=" + holdingPeriod +
                ", symbol='" + symbol + '\'' +
                ", currentPrice=" + currentPrice +
                ", averagePrice=" + averagePrice +
                ", totalShares=" + totalShares +
                ", totalAmount=" + totalAmount +
                ", priceToBreakEven=" + priceToBreakEven +
                ", targetPrice=" + targetPrice +
                ", gainLoss=" + gainLoss +
                ", gainLossPercent=" + gainLossPercent +
                ", gainToTarget=" + gainToTarget +
                ", stopLoss=" + stopLoss +
                ", lossToStopLoss=" + lossToStopLoss +
                ", stopDate=" + stopDate +
                ", daysToStopDate=" + daysToStopDate +
                ", riskReward=" + riskReward +
                ", capital=" + capital +
                ", percentCapital=" + percentCapital +
                ", tradeEntries=" + tradeEntries +
                '}';
    }

    public Date getEntryDate()
    {
        return entryDate;
    }

    public void setEntryDate(Date entryDate)
    {
        this.entryDate = entryDate;
    }

    public int getHoldingPeriod()
    {
        return holdingPeriod;
    }

    public void setHoldingPeriod(int holdingPeriod)
    {
        this.holdingPeriod = holdingPeriod;
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

    public double getAveragePrice()
    {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice)
    {
        this.averagePrice = averagePrice;
    }

    public long getTotalShares()
    {
        return totalShares;
    }

    public void setTotalShares(long totalShares)
    {
        this.totalShares = totalShares;
    }

    public double getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public double getPriceToBreakEven()
    {
        return priceToBreakEven;
    }

    public void setPriceToBreakEven(double priceToBreakEven)
    {
        this.priceToBreakEven = priceToBreakEven;
    }

    public double getTargetPrice()
    {
        return targetPrice;
    }

    public void setTargetPrice(double targetPrice)
    {
        this.targetPrice = targetPrice;
    }

    public double getGainLossPercent()
    {
        return gainLossPercent;
    }

    public void setGainLossPercent(double gainLossPercent)
    {
        this.gainLossPercent = gainLossPercent;
    }

    public double getGainLoss()
    {
        return gainLoss;
    }

    public void setGainLoss(double gainLoss)
    {
        this.gainLoss = gainLoss;
    }

    public long getGainToTarget()
    {
        return gainToTarget;
    }

    public void setGainToTarget(long gainToTarget)
    {
        this.gainToTarget = gainToTarget;
    }

    public double getStopLoss()
    {
        return stopLoss;
    }

    public void setStopLoss(double stopLoss)
    {
        this.stopLoss = stopLoss;
    }

    public double getLossToStopLoss()
    {
        return lossToStopLoss;
    }

    public void setLossToStopLoss(double lossToStopLoss)
    {
        this.lossToStopLoss = lossToStopLoss;
    }

    public Date getStopDate()
    {
        return stopDate;
    }

    public void setStopDate(Date stopDate)
    {
        this.stopDate = stopDate;
    }

    public int getDaysToStopDate()
    {
        return daysToStopDate;
    }

    public void setDaysToStopDate(int daysToStopDate)
    {
        this.daysToStopDate = daysToStopDate;
    }

    public double getRiskReward()
    {
        return riskReward;
    }

    public void setRiskReward(double riskReward)
    {
        this.riskReward = riskReward;
    }

    public long getCapital()
    {
        return capital;
    }

    public void setCapital(long capital)
    {
        this.capital = capital;
    }

    public double getPercentCapital()
    {
        return percentCapital;
    }

    public void setPercentCapital(double percentCapital)
    {
        this.percentCapital = percentCapital;
    }

    public List<TradeEntry> getTradeEntries()
    {
        return tradeEntries;
    }

    public void setTradeEntries(List<TradeEntry> tradeEntries)
    {
        this.tradeEntries = tradeEntries;
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
     * Flatten this Trade object in to a Parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(this.entryDate != null ? this.entryDate.getTime() : -1);
        dest.writeInt(this.holdingPeriod);
        dest.writeString(this.symbol);
        dest.writeDouble(this.currentPrice);
        dest.writeDouble(this.averagePrice);
        dest.writeLong(this.totalShares);
        dest.writeDouble(this.totalAmount);
        dest.writeDouble(this.priceToBreakEven);
        dest.writeDouble(this.targetPrice);
        dest.writeDouble(this.gainLoss);
        dest.writeDouble(this.gainLossPercent);
        dest.writeLong(this.gainToTarget);
        dest.writeDouble(this.stopLoss);
        dest.writeDouble(this.lossToStopLoss);
        dest.writeLong(this.stopDate != null ? this.stopDate.getTime() : -1);
        dest.writeInt(this.daysToStopDate);
        dest.writeDouble(this.riskReward);
        dest.writeLong(this.capital);
        dest.writeDouble(this.percentCapital);
        dest.writeTypedList(this.tradeEntries);
    }

    /**
     * Constructor that will be called in creating the parcel.
     * Note: Reading the parcel should be the same order as writing the parcel!
     */
    private Trade(Parcel in)
    {
        long tmpEntryDate = in.readLong();
        this.entryDate = tmpEntryDate == -1 ? null : new Date(tmpEntryDate);
        this.holdingPeriod = in.readInt();
        this.symbol = in.readString();
        this.currentPrice = in.readDouble();
        this.averagePrice = in.readDouble();
        this.totalShares = in.readLong();
        this.totalAmount = in.readDouble();
        this.priceToBreakEven = in.readDouble();
        this.targetPrice = in.readDouble();
        this.gainLoss = in.readDouble();
        this.gainLossPercent = in.readDouble();
        this.gainToTarget = in.readLong();
        this.stopLoss = in.readDouble();
        this.lossToStopLoss = in.readDouble();
        long tmpStopDate = in.readLong();
        this.stopDate = tmpStopDate == -1 ? null : new Date(tmpStopDate);
        this.daysToStopDate = in.readInt();
        this.riskReward = in.readDouble();
        this.capital = in.readLong();
        this.percentCapital = in.readDouble();
        this.tradeEntries = in.createTypedArrayList(TradeEntry.CREATOR);
    }

    /**
     * Generates instances of your Parcelable class from a Parcel.
     */
    public static final Parcelable.Creator<Trade> CREATOR = new Parcelable.Creator<Trade>()
    {
        @Override
        public Trade createFromParcel(Parcel source)
        {
            return new Trade(source);
        }

        @Override
        public Trade[] newArray(int size)
        {
            return new Trade[size];
        }
    };
}
