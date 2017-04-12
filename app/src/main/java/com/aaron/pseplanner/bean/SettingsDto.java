package com.aaron.pseplanner.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aaron.asuncion on 4/12/2017.
 */

public class SettingsDto implements Parcelable
{
    private boolean autoRefresh;
    private int refreshInterval;
    private boolean notifyStopLoss;
    private boolean notifyTargetPrice;
    private boolean notiftyTimeStop;
    private boolean notifySoundEffect;

    public SettingsDto()
    {
    }

    public SettingsDto(boolean autoRefresh, int refreshInterval, boolean notifyStopLoss, boolean notifyTargetPrice, boolean notiftyTimeStop, boolean notifySoundEffect)
    {
        this.autoRefresh = autoRefresh;
        this.refreshInterval = refreshInterval;
        this.notifyStopLoss = notifyStopLoss;
        this.notifyTargetPrice = notifyTargetPrice;
        this.notiftyTimeStop = notiftyTimeStop;
        this.notifySoundEffect = notifySoundEffect;
    }

    public boolean isAutoRefresh()
    {
        return autoRefresh;
    }

    public void setAutoRefresh(boolean autoRefresh)
    {
        this.autoRefresh = autoRefresh;
    }

    public int getRefreshInterval()
    {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval)
    {
        this.refreshInterval = refreshInterval;
    }

    public boolean isNotifyStopLoss()
    {
        return notifyStopLoss;
    }

    public void setNotifyStopLoss(boolean notifyStopLoss)
    {
        this.notifyStopLoss = notifyStopLoss;
    }

    public boolean isNotifyTargetPrice()
    {
        return notifyTargetPrice;
    }

    public void setNotifyTargetPrice(boolean notifyTargetPrice)
    {
        this.notifyTargetPrice = notifyTargetPrice;
    }

    public boolean isNotiftyTimeStop()
    {
        return notiftyTimeStop;
    }

    public void setNotiftyTimeStop(boolean notiftyTimeStop)
    {
        this.notiftyTimeStop = notiftyTimeStop;
    }

    public boolean isNotifySoundEffect()
    {
        return notifySoundEffect;
    }

    public void setNotifySoundEffect(boolean notifySoundEffect)
    {
        this.notifySoundEffect = notifySoundEffect;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof SettingsDto))
        {
            return false;
        }
        SettingsDto that = (SettingsDto) o;
        return isAutoRefresh() == that.isAutoRefresh() && getRefreshInterval() == that.getRefreshInterval() && isNotifyStopLoss() == that.isNotifyStopLoss() && isNotifyTargetPrice() == that.isNotifyTargetPrice() && isNotiftyTimeStop() == that.isNotiftyTimeStop() && isNotifySoundEffect() == that.isNotifySoundEffect();
    }

    @Override
    public int hashCode()
    {
        int result = (isAutoRefresh() ? 1 : 0);
        result = 31 * result + getRefreshInterval();
        result = 31 * result + (isNotifyStopLoss() ? 1 : 0);
        result = 31 * result + (isNotifyTargetPrice() ? 1 : 0);
        result = 31 * result + (isNotiftyTimeStop() ? 1 : 0);
        result = 31 * result + (isNotifySoundEffect() ? 1 : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "SettingsDto{" + "autoRefresh=" + autoRefresh + ", refreshInterval=" + refreshInterval + ", notifyStopLoss=" + notifyStopLoss + ", notifyTargetPrice=" + notifyTargetPrice + ", notiftyTimeStop=" + notiftyTimeStop + ", notifySoundEffect=" + notifySoundEffect + '}';
    }

    protected SettingsDto(Parcel in)
    {
        this.autoRefresh = in.readByte() != 0;
        this.refreshInterval = in.readInt();
        this.notifyStopLoss = in.readByte() != 0;
        this.notifyTargetPrice = in.readByte() != 0;
        this.notiftyTimeStop = in.readByte() != 0;
        this.notifySoundEffect = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeByte((byte) (this.autoRefresh ? 1 : 0));
        dest.writeInt(this.refreshInterval);
        dest.writeByte((byte) (this.notifyStopLoss ? 1 : 0));
        dest.writeByte((byte) (this.notifyTargetPrice ? 1 : 0));
        dest.writeByte((byte) (this.notiftyTimeStop ? 1 : 0));
        dest.writeByte((byte) (this.notifySoundEffect ? 1 : 0));
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<SettingsDto> CREATOR = new Creator<SettingsDto>()
    {
        @Override
        public SettingsDto createFromParcel(Parcel in)
        {
            return new SettingsDto(in);
        }

        @Override
        public SettingsDto[] newArray(int size)
        {
            return new SettingsDto[size];
        }
    };
}
