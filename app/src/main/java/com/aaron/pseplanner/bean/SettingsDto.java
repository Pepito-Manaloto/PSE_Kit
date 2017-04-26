package com.aaron.pseplanner.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

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
    private String proxyHost;
    private int proxyPort;

    public SettingsDto()
    {
    }

    public SettingsDto(boolean autoRefresh, int refreshInterval, boolean notifyStopLoss, boolean notifyTargetPrice, boolean notiftyTimeStop, boolean notifySoundEffect, String proxyHost, int proxyPort)
    {
        this.autoRefresh = autoRefresh;
        this.refreshInterval = refreshInterval;
        this.notifyStopLoss = notifyStopLoss;
        this.notifyTargetPrice = notifyTargetPrice;
        this.notiftyTimeStop = notiftyTimeStop;
        this.notifySoundEffect = notifySoundEffect;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
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

    public String getProxyHost()
    {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost)
    {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort()
    {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort)
    {
        this.proxyPort = proxyPort;
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
        return isAutoRefresh() == that.isAutoRefresh() && getRefreshInterval() == that.getRefreshInterval() && isNotifyStopLoss() == that.isNotifyStopLoss() && isNotifyTargetPrice() == that.isNotifyTargetPrice() && isNotiftyTimeStop() == that.isNotiftyTimeStop() && isNotifySoundEffect() == that.isNotifySoundEffect() && getProxyPort() == that.getProxyPort() && getProxyHost().equals(that.getProxyHost());
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
        result = 31 * result + (getProxyHost() != null ? getProxyHost().hashCode() : 0);
        result = 31 * result + getProxyPort();
        return result;
    }

    @Override
    public String toString()
    {
        return "SettingsDto{" + "autoRefresh=" + autoRefresh + ", refreshInterval=" + refreshInterval + ", notifyStopLoss=" + notifyStopLoss + ", notifyTargetPrice=" + notifyTargetPrice + ", notiftyTimeStop=" + notiftyTimeStop + ", notifySoundEffect=" + notifySoundEffect + ", proxyHost='" + proxyHost + '\'' + ", proxyPort=" + proxyPort + '}';
    }

    protected SettingsDto(Parcel in)
    {
        this.autoRefresh = in.readByte() != 0;
        this.refreshInterval = in.readInt();
        this.notifyStopLoss = in.readByte() != 0;
        this.notifyTargetPrice = in.readByte() != 0;
        this.notiftyTimeStop = in.readByte() != 0;
        this.notifySoundEffect = in.readByte() != 0;
        this.proxyHost = in.readString();
        this.proxyPort = in.readInt();
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
        dest.writeString(this.proxyHost);
        dest.writeInt(this.proxyPort);
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
