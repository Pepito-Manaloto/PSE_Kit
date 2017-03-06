package com.aaron.pseplanner.response.Phisix;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Aaron on 3/5/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class ResponsePrice
{
    private String currency;
    private double amount;

    String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
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

        ResponsePrice that = (ResponsePrice) o;

        return currency.equals(that.currency) && Double.compare(that.amount, amount) == 0;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = currency.hashCode();
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString()
    {
        return "ResponsePrice{" + "currency='" + currency + '\'' + ", amount=" + amount + '}';
    }
}
