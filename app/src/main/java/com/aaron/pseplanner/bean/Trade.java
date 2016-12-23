package com.aaron.pseplanner.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * Created by aaron.asuncion on 12/20/2016.
 */

public class Trade implements Parcelable
{
    private Date dateEntry;
    private Date dateHeld;
    private String symbol;
    private double currentPrice;
    private double averagePrice;
    private long totalShares;
    private double totalAmount;
    private double priceToBreakEven;
    private double targetPrice;
    private long gainToTarget;
    private double stopLoss;
    private long lossToStopLoss;
    private Date timeStop;
    private int daysToTimeStop;
    private double riskReward;
    private long capital;
    private int percentCapital;
    private List<TradeEntry> tradeEntries;

    public Trade()
    {
    }

    public Trade(long lossToStopLoss, Date dateEntry, Date dateHeld, String symbol, double currentPrice, double averagePrice, long totalShares, double totalAmount, double priceToBreakEven, double targetPrice, long gainToTarget, double stopLoss, Date timeStop, int daysToTimeStop, double riskReward, long capital, int percentCapital, List<TradeEntry> tradeEntries)
    {
        this.lossToStopLoss = lossToStopLoss;
        this.dateEntry = dateEntry;
        this.dateHeld = dateHeld;
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.averagePrice = averagePrice;
        this.totalShares = totalShares;
        this.totalAmount = totalAmount;
        this.priceToBreakEven = priceToBreakEven;
        this.targetPrice = targetPrice;
        this.gainToTarget = gainToTarget;
        this.stopLoss = stopLoss;
        this.timeStop = timeStop;
        this.daysToTimeStop = daysToTimeStop;
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
                Double.compare(trade.targetPrice, targetPrice) == 0 && gainToTarget == trade.gainToTarget &&
                Double.compare(trade.stopLoss, stopLoss) == 0 && lossToStopLoss == trade.lossToStopLoss && daysToTimeStop == trade.daysToTimeStop &&
                Double.compare(trade.riskReward, riskReward) == 0 && capital == trade.capital && percentCapital == trade.percentCapital &&
                dateEntry.equals(trade.dateEntry) && dateHeld.equals(trade.dateHeld) && symbol.equals(trade.symbol) && timeStop.equals(trade.timeStop) &&
                tradeEntries.equals(trade.tradeEntries);
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = dateEntry.hashCode();
        result = 31 * result + dateHeld.hashCode();
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
        result = 31 * result + (int) (gainToTarget ^ (gainToTarget >>> 32));
        temp = Double.doubleToLongBits(stopLoss);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (lossToStopLoss ^ (lossToStopLoss >>> 32));
        result = 31 * result + timeStop.hashCode();
        result = 31 * result + daysToTimeStop;
        temp = Double.doubleToLongBits(riskReward);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (capital ^ (capital >>> 32));
        result = 31 * result + percentCapital;
        result = 31 * result + tradeEntries.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "Trade{" +
                "dateEntry=" + dateEntry +
                ", dateHeld=" + dateHeld +
                ", symbol='" + symbol + '\'' +
                ", currentPrice=" + currentPrice +
                ", averagePrice=" + averagePrice +
                ", totalShares=" + totalShares +
                ", totalAmount=" + totalAmount +
                ", priceToBreakEven=" + priceToBreakEven +
                ", targetPrice=" + targetPrice +
                ", gainToTarget=" + gainToTarget +
                ", stopLoss=" + stopLoss +
                ", lossToStopLoss=" + lossToStopLoss +
                ", timeStop=" + timeStop +
                ", daysToTimeStop=" + daysToTimeStop +
                ", riskReward=" + riskReward +
                ", capital=" + capital +
                ", percentCapital=" + percentCapital +
                ", tradeEntries=" + tradeEntries +
                '}';
    }

    public Date getDateEntry()
    {
        return dateEntry;
    }

    public void setDateEntry(Date dateEntry)
    {
        this.dateEntry = dateEntry;
    }

    public Date getDateHeld()
    {
        return dateHeld;
    }

    public void setDateHeld(Date dateHeld)
    {
        this.dateHeld = dateHeld;
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

    public long getLossToStopLoss()
    {
        return lossToStopLoss;
    }

    public void setLossToStopLoss(long lossToStopLoss)
    {
        this.lossToStopLoss = lossToStopLoss;
    }

    public Date getTimeStop()
    {
        return timeStop;
    }

    public void setTimeStop(Date timeStop)
    {
        this.timeStop = timeStop;
    }

    public int getDaysToTimeStop()
    {
        return daysToTimeStop;
    }

    public void setDaysToTimeStop(int daysToTimeStop)
    {
        this.daysToTimeStop = daysToTimeStop;
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

    public int getPercentCapital()
    {
        return percentCapital;
    }

    public void setPercentCapital(int percentCapital)
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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(this.dateEntry != null ? this.dateEntry.getTime() : -1);
        dest.writeLong(this.dateHeld != null ? this.dateHeld.getTime() : -1);
        dest.writeString(this.symbol);
        dest.writeDouble(this.currentPrice);
        dest.writeDouble(this.averagePrice);
        dest.writeLong(this.totalShares);
        dest.writeDouble(this.totalAmount);
        dest.writeDouble(this.priceToBreakEven);
        dest.writeDouble(this.targetPrice);
        dest.writeLong(this.gainToTarget);
        dest.writeDouble(this.stopLoss);
        dest.writeLong(this.lossToStopLoss);
        dest.writeLong(this.timeStop != null ? this.timeStop.getTime() : -1);
        dest.writeInt(this.daysToTimeStop);
        dest.writeDouble(this.riskReward);
        dest.writeLong(this.capital);
        dest.writeInt(this.percentCapital);
        dest.writeTypedList(this.tradeEntries);
    }

    protected Trade(Parcel in)
    {
        long tmpDateEntry = in.readLong();
        this.dateEntry = tmpDateEntry == -1 ? null : new Date(tmpDateEntry);
        long tmpDateHeld = in.readLong();
        this.dateHeld = tmpDateHeld == -1 ? null : new Date(tmpDateHeld);
        this.symbol = in.readString();
        this.currentPrice = in.readDouble();
        this.averagePrice = in.readDouble();
        this.totalShares = in.readLong();
        this.totalAmount = in.readDouble();
        this.priceToBreakEven = in.readDouble();
        this.targetPrice = in.readDouble();
        this.gainToTarget = in.readLong();
        this.stopLoss = in.readDouble();
        this.lossToStopLoss = in.readLong();
        long tmpTimeStop = in.readLong();
        this.timeStop = tmpTimeStop == -1 ? null : new Date(tmpTimeStop);
        this.daysToTimeStop = in.readInt();
        this.riskReward = in.readDouble();
        this.capital = in.readLong();
        this.percentCapital = in.readInt();
        this.tradeEntries = in.createTypedArrayList(TradeEntry.CREATOR);
    }

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
