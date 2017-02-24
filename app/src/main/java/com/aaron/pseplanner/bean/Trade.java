package com.aaron.pseplanner.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
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
    private BigDecimal currentPrice;
    private BigDecimal averagePrice;
    private long totalShares;
    private BigDecimal totalAmount;
    private BigDecimal priceToBreakEven;
    private BigDecimal targetPrice;
    private BigDecimal gainLoss;
    private BigDecimal gainLossPercent;
    private long gainToTarget;
    private BigDecimal lossToStopLoss;
    private BigDecimal stopLoss;
    private Date stopDate;
    private int daysToStopDate;
    private BigDecimal riskReward;
    private long capital;
    private BigDecimal percentCapital;
    private List<TradeEntry> tradeEntries;

    public Trade()
    {
    }

    public Trade(String symbol, Date entryDate, int holdingPeriod, double currentPrice, double averagePrice, long totalShares, double totalAmount, double priceToBreakEven, double targetPrice, double gainLoss, double gainLossPercent, long gainToTarget, double stopLoss, double lossToStopLoss, Date stopDate, int daysToStopDate, double riskReward, long capital, double percentCapital, List<TradeEntry> tradeEntries)
    {
        this.symbol = symbol;
        this.entryDate = entryDate;
        this.holdingPeriod = holdingPeriod;
        this.currentPrice = BigDecimal.valueOf(currentPrice);
        this.averagePrice = BigDecimal.valueOf(averagePrice);
        this.totalShares = totalShares;
        this.totalAmount = BigDecimal.valueOf(totalAmount);
        this.priceToBreakEven = BigDecimal.valueOf(priceToBreakEven);
        this.targetPrice = BigDecimal.valueOf(targetPrice);
        this.gainLoss = BigDecimal.valueOf(gainLoss);
        this.gainLossPercent = BigDecimal.valueOf(gainLossPercent);
        this.gainToTarget = gainToTarget;
        this.stopLoss = BigDecimal.valueOf(stopLoss);
        this.lossToStopLoss = BigDecimal.valueOf(lossToStopLoss);
        this.stopDate = stopDate;
        this.daysToStopDate = daysToStopDate;
        this.riskReward = BigDecimal.valueOf(riskReward);
        this.capital = capital;
        this.percentCapital = BigDecimal.valueOf(percentCapital);
        this.tradeEntries = tradeEntries;
    }

    public Trade(String symbol, Date entryDate, int holdingPeriod, String currentPrice, String averagePrice, long totalShares, String totalAmount, String priceToBreakEven, String targetPrice, String gainLoss, String gainLossPercent, long gainToTarget, String stopLoss, String lossToStopLoss, Date stopDate, int daysToStopDate, String riskReward, long capital, String percentCapital, List<TradeEntry> tradeEntries)
    {
        this.symbol = symbol;
        this.entryDate = entryDate;
        this.holdingPeriod = holdingPeriod;
        this.currentPrice = new BigDecimal(currentPrice);
        this.averagePrice = new BigDecimal(averagePrice);
        this.totalShares = totalShares;
        this.totalAmount = new BigDecimal(totalAmount);
        this.priceToBreakEven = new BigDecimal(priceToBreakEven);
        this.targetPrice = new BigDecimal(targetPrice);
        this.gainLoss = new BigDecimal(gainLoss);
        this.gainLossPercent = new BigDecimal(gainLossPercent);
        this.gainToTarget = gainToTarget;
        this.stopLoss = new BigDecimal(stopLoss);
        this.lossToStopLoss = new BigDecimal(lossToStopLoss);
        this.stopDate = stopDate;
        this.daysToStopDate = daysToStopDate;
        this.riskReward = new BigDecimal(riskReward);
        this.capital = capital;
        this.percentCapital = new BigDecimal(percentCapital);
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

        return currentPrice.equals(trade.currentPrice) && averagePrice.equals(trade.averagePrice) &&
                totalShares == trade.totalShares && totalAmount.equals(trade.totalAmount) && priceToBreakEven.equals(trade.priceToBreakEven) &&
                targetPrice.equals(trade.targetPrice) && gainLoss.equals(trade.gainLoss) && gainLossPercent.equals(trade.gainLossPercent) &&
                gainToTarget == trade.gainToTarget && stopLoss.equals(trade.stopLoss) && lossToStopLoss.equals(trade.lossToStopLoss) &&
                daysToStopDate == trade.daysToStopDate && riskReward.equals(trade.riskReward) && capital == trade.capital &&
                percentCapital.equals(trade.percentCapital) && entryDate.equals(trade.entryDate) && holdingPeriod == trade.holdingPeriod &&
                symbol.equals(trade.symbol) && stopDate.equals(trade.stopDate) && tradeEntries.equals(trade.tradeEntries);
    }

    @Override
    public int hashCode()
    {
        int result = getEntryDate().hashCode();
        result = 31 * result + getHoldingPeriod();
        result = 31 * result + getSymbol().hashCode();
        result = 31 * result + getCurrentPrice().hashCode();
        result = 31 * result + getAveragePrice().hashCode();
        result = 31 * result + (int) (getTotalShares() ^ (getTotalShares() >>> 32));
        result = 31 * result + getTotalAmount().hashCode();
        result = 31 * result + getPriceToBreakEven().hashCode();
        result = 31 * result + getTargetPrice().hashCode();
        result = 31 * result + getGainLoss().hashCode();
        result = 31 * result + getGainLossPercent().hashCode();
        result = 31 * result + (int) (getGainToTarget() ^ (getGainToTarget() >>> 32));
        result = 31 * result + getLossToStopLoss().hashCode();
        result = 31 * result + getStopLoss().hashCode();
        result = 31 * result + getStopDate().hashCode();
        result = 31 * result + getDaysToStopDate();
        result = 31 * result + getRiskReward().hashCode();
        result = 31 * result + (int) (getCapital() ^ (getCapital() >>> 32));
        result = 31 * result + getPercentCapital().hashCode();
        result = 31 * result + getTradeEntries().hashCode();
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

    public BigDecimal getCurrentPrice()
    {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice)
    {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getAveragePrice()
    {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice)
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

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPriceToBreakEven()
    {
        return priceToBreakEven;
    }

    public void setPriceToBreakEven(BigDecimal priceToBreakEven)
    {
        this.priceToBreakEven = priceToBreakEven;
    }

    public BigDecimal getTargetPrice()
    {
        return targetPrice;
    }

    public void setTargetPrice(BigDecimal targetPrice)
    {
        this.targetPrice = targetPrice;
    }

    public BigDecimal getGainLossPercent()
    {
        return gainLossPercent;
    }

    public void setGainLossPercent(BigDecimal gainLossPercent)
    {
        this.gainLossPercent = gainLossPercent;
    }

    public BigDecimal getGainLoss()
    {
        return gainLoss;
    }

    public void setGainLoss(BigDecimal gainLoss)
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

    public BigDecimal getStopLoss()
    {
        return stopLoss;
    }

    public void setStopLoss(BigDecimal stopLoss)
    {
        this.stopLoss = stopLoss;
    }

    public BigDecimal getLossToStopLoss()
    {
        return lossToStopLoss;
    }

    public void setLossToStopLoss(BigDecimal lossToStopLoss)
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

    public BigDecimal getRiskReward()
    {
        return riskReward;
    }

    public void setRiskReward(BigDecimal riskReward)
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

    public BigDecimal getPercentCapital()
    {
        return percentCapital;
    }

    public void setPercentCapital(BigDecimal percentCapital)
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
        dest.writeString(this.currentPrice.toPlainString());
        dest.writeString(this.averagePrice.toPlainString());
        dest.writeLong(this.totalShares);
        dest.writeString(this.totalAmount.toPlainString());
        dest.writeString(this.priceToBreakEven.toPlainString());
        dest.writeString(this.targetPrice.toPlainString());
        dest.writeString(this.gainLoss.toPlainString());
        dest.writeString(this.gainLossPercent.toPlainString());
        dest.writeLong(this.gainToTarget);
        dest.writeString(this.stopLoss.toPlainString());
        dest.writeString(this.lossToStopLoss.toPlainString());
        dest.writeLong(this.stopDate != null ? this.stopDate.getTime() : -1);
        dest.writeInt(this.daysToStopDate);
        dest.writeString(this.riskReward.toPlainString());
        dest.writeLong(this.capital);
        dest.writeString(this.percentCapital.toPlainString());
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
        this.currentPrice = new BigDecimal(in.readString());
        this.averagePrice = new BigDecimal(in.readString());
        this.totalShares = in.readLong();
        this.totalAmount = new BigDecimal(in.readString());
        this.priceToBreakEven = new BigDecimal(in.readString());
        this.targetPrice = new BigDecimal(in.readString());
        this.gainLoss = new BigDecimal(in.readString());
        this.gainLossPercent = new BigDecimal(in.readString());
        this.gainToTarget = in.readLong();
        this.stopLoss = new BigDecimal(in.readString());
        this.lossToStopLoss = new BigDecimal(in.readString());
        long tmpStopDate = in.readLong();
        this.stopDate = tmpStopDate == -1 ? null : new Date(tmpStopDate);
        this.daysToStopDate = in.readInt();
        this.riskReward = new BigDecimal(in.readString());
        this.capital = in.readLong();
        this.percentCapital = new BigDecimal(in.readString());
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
