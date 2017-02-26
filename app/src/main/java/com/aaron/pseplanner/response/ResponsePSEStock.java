package com.aaron.pseplanner.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Aaron on 2/26/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsePSEStock
{
    private String totalVolume;
    private String indicator;
    private String percentChangeClose;
    private String lastTradedPrice;
    private String securityAlias;
    private String indicatorImg;
    private String securitySymbol;

    public String getTotalVolume()
    {
        return totalVolume;
    }

    public void setTotalVolume(String totalVolume)
    {
        this.totalVolume = totalVolume;
    }

    public String getIndicator()
    {
        return indicator;
    }

    public void setIndicator(String indicator)
    {
        this.indicator = indicator;
    }

    public String getPercentChangeClose()
    {
        return percentChangeClose;
    }

    public void setPercentChangeClose(String percentChangeClose)
    {
        this.percentChangeClose = percentChangeClose;
    }

    public String getLastTradedPrice()
    {
        return lastTradedPrice;
    }

    public void setLastTradedPrice(String lastTradedPrice)
    {
        this.lastTradedPrice = lastTradedPrice;
    }

    public String getSecurityAlias()
    {
        return securityAlias;
    }

    public void setSecurityAlias(String securityAlias)
    {
        this.securityAlias = securityAlias;
    }

    public String getIndicatorImg()
    {
        return indicatorImg;
    }

    public void setIndicatorImg(String indicatorImg)
    {
        this.indicatorImg = indicatorImg;
    }

    public String getSecuritySymbol()
    {
        return securitySymbol;
    }

    public void setSecuritySymbol(String securitySymbol)
    {
        this.securitySymbol = securitySymbol;
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

        ResponsePSEStock that = (ResponsePSEStock) o;

        return totalVolume.equals(that.totalVolume) && indicator.equals(that.indicator) && percentChangeClose.equals(that.percentChangeClose) &&
                lastTradedPrice.equals(that.lastTradedPrice) && securityAlias.equals(that.securityAlias) && indicatorImg.equals(that.indicatorImg) &&
                securitySymbol.equals(that.securitySymbol);
    }

    @Override
    public int hashCode()
    {
        int result = totalVolume.hashCode();
        result = 31 * result + indicator.hashCode();
        result = 31 * result + percentChangeClose.hashCode();
        result = 31 * result + lastTradedPrice.hashCode();
        result = 31 * result + securityAlias.hashCode();
        result = 31 * result + indicatorImg.hashCode();
        result = 31 * result + securitySymbol.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "ResponsePSEStock{" +
                "totalVolume='" + totalVolume + '\'' +
                ", indicator='" + indicator + '\'' +
                ", percentChangeClose='" + percentChangeClose + '\'' +
                ", lastTradedPrice='" + lastTradedPrice + '\'' +
                ", securityAlias='" + securityAlias + '\'' +
                ", indicatorImg='" + indicatorImg + '\'' +
                ", securitySymbol='" + securitySymbol + '\'' +
                '}';
    }
}
