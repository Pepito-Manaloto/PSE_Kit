package com.aaron.pseplanner.entity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import java.util.List;

/**
 * Created by Aaron on 3/21/2017.
 */
@Entity(indexes = {@Index(value = "symbol", unique = true)})
public class Trade
{
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private Date entryDate;

    @NotNull
    private int holdingPeriod;

    @Unique
    @NotNull
    private String symbol;

    @NotNull
    private String currentPrice;

    @NotNull
    private String averagePrice;

    private long totalShares;

    @NotNull
    private String totalAmount;

    @NotNull
    private String priceToBreakEven;

    @NotNull
    private String targetPrice;

    @NotNull
    private String gainLoss;

    @NotNull
    private String gainLossPercent;

    @NotNull
    private String gainToTarget;

    @NotNull
    private String lossToStopLoss;

    @NotNull
    private String stopLoss;

    @NotNull
    private Date stopDate;

    private int daysToStopDate;

    @NotNull
    private String riskReward;

    private long capital;

    @NotNull
    private String percentCapital;

    @ToMany(joinProperties = {@JoinProperty(name = "symbol", referencedName = "tradeSymbol")})
    @OrderBy("order ASC")
    private List<TradeEntry> tradeEntries;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 295783675)
    private transient TradeDao myDao;

    @Generated(hash = 1506989549)
    public Trade(Long id, @NotNull Date entryDate, int holdingPeriod, @NotNull String symbol,
            @NotNull String currentPrice, @NotNull String averagePrice, long totalShares,
            @NotNull String totalAmount, @NotNull String priceToBreakEven,
            @NotNull String targetPrice, @NotNull String gainLoss,
            @NotNull String gainLossPercent, @NotNull String gainToTarget,
            @NotNull String lossToStopLoss, @NotNull String stopLoss, @NotNull Date stopDate,
            int daysToStopDate, @NotNull String riskReward, long capital,
            @NotNull String percentCapital) {
        this.id = id;
        this.entryDate = entryDate;
        this.holdingPeriod = holdingPeriod;
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.averagePrice = averagePrice;
        this.totalShares = totalShares;
        this.totalAmount = totalAmount;
        this.priceToBreakEven = priceToBreakEven;
        this.targetPrice = targetPrice;
        this.gainLoss = gainLoss;
        this.gainLossPercent = gainLossPercent;
        this.gainToTarget = gainToTarget;
        this.lossToStopLoss = lossToStopLoss;
        this.stopLoss = stopLoss;
        this.stopDate = stopDate;
        this.daysToStopDate = daysToStopDate;
        this.riskReward = riskReward;
        this.capital = capital;
        this.percentCapital = percentCapital;
    }

    @Generated(hash = 1773414334)
    public Trade() {
    }

    public Long getId()
    {
        return this.id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Date getEntryDate()
    {
        return this.entryDate;
    }

    public void setEntryDate(@NotNull Date entryDate)
    {
        this.entryDate = entryDate;
    }

    public int getHoldingPeriod()
    {
        return this.holdingPeriod;
    }

    public void setHoldingPeriod(int holdingPeriod)
    {
        this.holdingPeriod = holdingPeriod;
    }

    public String getSymbol()
    {
        return this.symbol;
    }

    public void setSymbol(@NotNull String symbol)
    {
        this.symbol = symbol;
    }

    public String getCurrentPrice()
    {
        return this.currentPrice;
    }

    public void setCurrentPrice(@NotNull String currentPrice)
    {
        this.currentPrice = currentPrice;
    }

    public String getAveragePrice()
    {
        return this.averagePrice;
    }

    public void setAveragePrice(@NotNull String averagePrice)
    {
        this.averagePrice = averagePrice;
    }

    public long getTotalShares()
    {
        return this.totalShares;
    }

    public void setTotalShares(long totalShares)
    {
        this.totalShares = totalShares;
    }

    public String getTotalAmount()
    {
        return this.totalAmount;
    }

    public void setTotalAmount(@NotNull String totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public String getPriceToBreakEven()
    {
        return this.priceToBreakEven;
    }

    public void setPriceToBreakEven(@NotNull String priceToBreakEven)
    {
        this.priceToBreakEven = priceToBreakEven;
    }

    public String getTargetPrice()
    {
        return this.targetPrice;
    }

    public void setTargetPrice(@NotNull String targetPrice)
    {
        this.targetPrice = targetPrice;
    }

    public String getGainLoss()
    {
        return this.gainLoss;
    }

    public void setGainLoss(@NotNull String gainLoss)
    {
        this.gainLoss = gainLoss;
    }

    public String getGainLossPercent()
    {
        return this.gainLossPercent;
    }

    public void setGainLossPercent(@NotNull String gainLossPercent)
    {
        this.gainLossPercent = gainLossPercent;
    }

    public String getGainToTarget()
    {
        return this.gainToTarget;
    }

    public void setGainToTarget(String gainToTarget)
    {
        this.gainToTarget = gainToTarget;
    }

    public String getLossToStopLoss()
    {
        return this.lossToStopLoss;
    }

    public void setLossToStopLoss(@NotNull String lossToStopLoss)
    {
        this.lossToStopLoss = lossToStopLoss;
    }

    public String getStopLoss()
    {
        return this.stopLoss;
    }

    public void setStopLoss(@NotNull String stopLoss)
    {
        this.stopLoss = stopLoss;
    }

    public Date getStopDate()
    {
        return this.stopDate;
    }

    public void setStopDate(@NotNull Date stopDate)
    {
        this.stopDate = stopDate;
    }

    public int getDaysToStopDate()
    {
        return this.daysToStopDate;
    }

    public void setDaysToStopDate(int daysToStopDate)
    {
        this.daysToStopDate = daysToStopDate;
    }

    public String getRiskReward()
    {
        return this.riskReward;
    }

    public void setRiskReward(@NotNull String riskReward)
    {
        this.riskReward = riskReward;
    }

    public long getCapital()
    {
        return this.capital;
    }

    public void setCapital(long capital)
    {
        this.capital = capital;
    }

    public String getPercentCapital()
    {
        return this.percentCapital;
    }

    public void setPercentCapital(@NotNull String percentCapital)
    {
        this.percentCapital = percentCapital;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1790216817)
    public List<TradeEntry> getTradeEntries() {
        if (tradeEntries == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TradeEntryDao targetDao = daoSession.getTradeEntryDao();
            List<TradeEntry> tradeEntriesNew = targetDao._queryTrade_TradeEntries(symbol);
            synchronized (this) {
                if (tradeEntries == null) {
                    tradeEntries = tradeEntriesNew;
                }
            }
        }
        return tradeEntries;
    }

    public void setTradeEntriesTransient(List<TradeEntry> tradeEntries)
    {
        this.tradeEntries = tradeEntries;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1183466938)
    public synchronized void resetTradeEntries() {
        tradeEntries = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 281372810)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTradeDao() : null;
    }
}
