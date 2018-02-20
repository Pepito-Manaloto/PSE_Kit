package com.aaron.pseplanner.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by aaron.asuncion on 12/20/2016.
 * Represents a trading plan.
 */
public class TradeDto implements Stock, Parcelable, Comparable<TradeDto>
{
    private Long id;

    private Date datePlanned;
    private int daysSincePlanned;
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
    private BigDecimal gainToTarget;
    private BigDecimal lossToStopLoss;
    private BigDecimal stopLoss;
    private Date stopDate;
    private int daysToStopDate;
    private BigDecimal riskReward;
    private long capital;
    private BigDecimal percentCapital;
    private List<TradeEntryDto> tradeEntries;

    public TradeDto()
    {
    }

    public TradeDto(String symbol)
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

        if(!(o instanceof TradeDto))
        {
            return false;
        }

        TradeDto tradeDto = (TradeDto) o;

        return currentPrice.equals(tradeDto.currentPrice) && averagePrice.equals(tradeDto.averagePrice) && totalShares == tradeDto.totalShares
                && totalAmount.equals(tradeDto.totalAmount) && priceToBreakEven.equals(tradeDto.priceToBreakEven) && targetPrice.equals(tradeDto.targetPrice)
                && gainLoss.equals(tradeDto.gainLoss) && gainLossPercent.equals(tradeDto.gainLossPercent) && gainToTarget.equals(tradeDto.gainToTarget)
                && stopLoss.equals(tradeDto.stopLoss) && lossToStopLoss.equals(tradeDto.lossToStopLoss) && daysToStopDate == tradeDto.daysToStopDate
                && riskReward.equals(tradeDto.riskReward) && capital == tradeDto.capital && percentCapital.equals(tradeDto.percentCapital)
                && datePlanned.equals(tradeDto.datePlanned) && daysSincePlanned == tradeDto.daysSincePlanned && symbol.equals(tradeDto.symbol)
                && ((entryDate == null && tradeDto.entryDate == null) || (entryDate != null && entryDate.equals(tradeDto.entryDate)))
                && holdingPeriod == tradeDto.holdingPeriod && stopDate.equals(tradeDto.stopDate) && tradeEntries.equals(tradeDto.tradeEntries);
    }

    @Override
    public int hashCode()
    {
        int result = getDatePlanned().hashCode();
        result = 31 * result + getDaysSincePlanned();

        if(getEntryDate() != null)
        {
            result = 31 * result + getEntryDate().hashCode();
        }

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
        result = 31 * result + getGainToTarget().hashCode();
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
        return "TradeDto{" +
                "id=" + id +
                ", datePlanned=" + datePlanned +
                ", daysSincePlanned=" + daysSincePlanned +
                ", entryDate=" + entryDate +
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
                ", lossToStopLoss=" + lossToStopLoss +
                ", stopLoss=" + stopLoss +
                ", stopDate=" + stopDate +
                ", daysToStopDate=" + daysToStopDate +
                ", riskReward=" + riskReward +
                ", capital=" + capital +
                ", percentCapital=" + percentCapital +
                ", tradeEntries=" + tradeEntries +
                '}';
    }

    public Long getId()
    {
        return id;
    }

    public TradeDto setId(Long id)
    {
        this.id = id;
        return this;
    }

    public Date getDatePlanned()
    {
        return datePlanned;
    }

    public TradeDto setDatePlanned(Date datePlanned)
    {
        this.datePlanned = datePlanned;
        return this;
    }

    public int getDaysSincePlanned()
    {
        return daysSincePlanned;
    }

    public TradeDto setDaysSincePlanned(int daysSincePlanned)
    {
        this.daysSincePlanned = daysSincePlanned;
        return this;
    }

    public Date getEntryDate()
    {
        return entryDate;
    }

    public TradeDto setEntryDate(Date entryDate)
    {
        this.entryDate = entryDate;
        return this;
    }

    public int getHoldingPeriod()
    {
        return holdingPeriod;
    }

    public TradeDto setHoldingPeriod(int holdingPeriod)
    {
        this.holdingPeriod = holdingPeriod;
        return this;
    }

    @Override
    public String getSymbol()
    {
        return symbol;
    }

    @Override
    public TradeDto setSymbol(String symbol)
    {
        this.symbol = symbol;
        return this;
    }

    public BigDecimal getCurrentPrice()
    {
        return currentPrice;
    }

    public TradeDto setCurrentPrice(BigDecimal currentPrice)
    {
        this.currentPrice = currentPrice;
        return this;
    }

    public BigDecimal getAveragePrice()
    {
        return averagePrice;
    }

    public TradeDto setAveragePrice(BigDecimal averagePrice)
    {
        this.averagePrice = averagePrice;
        return this;
    }

    public long getTotalShares()
    {
        return totalShares;
    }

    public TradeDto setTotalShares(long totalShares)
    {
        this.totalShares = totalShares;
        return this;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public TradeDto setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
        return this;
    }

    public BigDecimal getPriceToBreakEven()
    {
        return priceToBreakEven;
    }

    public TradeDto setPriceToBreakEven(BigDecimal priceToBreakEven)
    {
        this.priceToBreakEven = priceToBreakEven;
        return this;
    }

    public BigDecimal getTargetPrice()
    {
        return targetPrice;
    }

    public TradeDto setTargetPrice(BigDecimal targetPrice)
    {
        this.targetPrice = targetPrice;
        return this;
    }

    public BigDecimal getGainLossPercent()
    {
        return gainLossPercent;
    }

    public TradeDto setGainLossPercent(BigDecimal gainLossPercent)
    {
        this.gainLossPercent = gainLossPercent;
        return this;
    }

    public BigDecimal getGainLoss()
    {
        return gainLoss;
    }

    public TradeDto setGainLoss(BigDecimal gainLoss)
    {
        this.gainLoss = gainLoss;
        return this;
    }

    public BigDecimal getGainToTarget()
    {
        return gainToTarget;
    }

    public TradeDto setGainToTarget(BigDecimal gainToTarget)
    {
        this.gainToTarget = gainToTarget;
        return this;
    }

    public BigDecimal getStopLoss()
    {
        return stopLoss;
    }

    public TradeDto setStopLoss(BigDecimal stopLoss)
    {
        this.stopLoss = stopLoss;
        return this;
    }

    public BigDecimal getLossToStopLoss()
    {
        return lossToStopLoss;
    }

    public TradeDto setLossToStopLoss(BigDecimal lossToStopLoss)
    {
        this.lossToStopLoss = lossToStopLoss;
        return this;
    }

    public Date getStopDate()
    {
        return stopDate;
    }

    public TradeDto setStopDate(Date stopDate)
    {
        this.stopDate = stopDate;
        return this;
    }

    public int getDaysToStopDate()
    {
        return daysToStopDate;
    }

    public TradeDto setDaysToStopDate(int daysToStopDate)
    {
        this.daysToStopDate = daysToStopDate;
        return this;
    }

    public BigDecimal getRiskReward()
    {
        return riskReward;
    }

    public TradeDto setRiskReward(BigDecimal riskReward)
    {
        this.riskReward = riskReward;
        return this;
    }

    public long getCapital()
    {
        return capital;
    }

    public TradeDto setCapital(long capital)
    {
        this.capital = capital;
        return this;
    }

    public BigDecimal getPercentCapital()
    {
        return percentCapital;
    }

    public TradeDto setPercentCapital(BigDecimal percentCapital)
    {
        this.percentCapital = percentCapital;
        return this;
    }

    public List<TradeEntryDto> getTradeEntries()
    {
        return tradeEntries;
    }

    public TradeDto setTradeEntries(List<TradeEntryDto> tradeEntries)
    {
        this.tradeEntries = tradeEntries;
        return this;
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
     * Flatten this TradeDto object in to a Parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(this.datePlanned != null ? this.datePlanned.getTime() : -1);
        dest.writeInt(this.daysSincePlanned);
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
        dest.writeString(this.gainToTarget.toPlainString());
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
    private TradeDto(Parcel in)
    {
        long tmpDatePlanned = in.readLong();
        this.datePlanned = tmpDatePlanned == -1 ? null : new Date(tmpDatePlanned);
        this.daysSincePlanned = in.readInt();
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
        this.gainToTarget = new BigDecimal(in.readString());
        this.stopLoss = new BigDecimal(in.readString());
        this.lossToStopLoss = new BigDecimal(in.readString());
        long tmpStopDate = in.readLong();
        this.stopDate = tmpStopDate == -1 ? null : new Date(tmpStopDate);
        this.daysToStopDate = in.readInt();
        this.riskReward = new BigDecimal(in.readString());
        this.capital = in.readLong();
        this.percentCapital = new BigDecimal(in.readString());
        this.tradeEntries = in.createTypedArrayList(TradeEntryDto.CREATOR);
    }

    /**
     * Generates instances of your Parcelable class from a Parcel.
     */
    public static final Parcelable.Creator<TradeDto> CREATOR = new Parcelable.Creator<TradeDto>()
    {
        @Override
        public TradeDto createFromParcel(Parcel source)
        {
            return new TradeDto(source);
        }

        @Override
        public TradeDto[] newArray(int size)
        {
            return new TradeDto[size];
        }
    };

    @Override
    public int compareTo(@NonNull TradeDto t)
    {
        return this.symbol.compareTo(t.symbol);
    }
}
