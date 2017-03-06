package com.aaron.pseplanner.response.Phisix;

import com.aaron.pseplanner.service.PSEPlannerService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by Aaron on 2/26/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsePhisixStockWrapper
{
    private List<ResponsePhisixStock> responsePhisixStocksList;
    private Date dateUpdated;

    public List<ResponsePhisixStock> getResponsePhisixStocksList()
    {
        return responsePhisixStocksList;
    }

    @JsonProperty("stock")
    public void setResponsePhisixStocksList(List<ResponsePhisixStock> responsePhisixStocksList)
    {
        this.responsePhisixStocksList = responsePhisixStocksList;
    }

    public Date getDateUpdated()
    {
        return dateUpdated;
    }

    @JsonProperty("as_of")
    public void setDateUpdated(Date dateUpdated)
    {
        this.dateUpdated = dateUpdated;
    }

    /**
     * Returns the first stock in the list.
     */
    public ResponsePhisixStock getResponseStock()
    {
        if(this.responsePhisixStocksList != null && !this.responsePhisixStocksList.isEmpty())
        {
            return this.responsePhisixStocksList.get(0);
        }

        return null;
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

        ResponsePhisixStockWrapper that = (ResponsePhisixStockWrapper) o;

        return dateUpdated.equals(that.dateUpdated) && responsePhisixStocksList.equals(that.responsePhisixStocksList);
    }

    @Override
    public int hashCode()
    {
        int result = responsePhisixStocksList.hashCode();
        result = 31 * result + dateUpdated.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "ResponsePSEStockInfo{" + "responsePhisixStocksList=" + responsePhisixStocksList + ", dateUpdated=" + dateUpdated + '}';
    }
}
