package com.aaron.pseplanner.bean;

import java.math.BigDecimal;

/**
 * Created by aaron.asuncion on 12/29/2016.
 * Current boardlot of PSE.
 *
 * @see <a href="https://www.colfinancial.com/ape/Final2/home/online_trading.asp">https://www.colfinancial.com/ape/Final2/home/online_trading.asp</a>
 */
public enum BoardLot
{
    ONE_HUNDREDTH_OF_A_CENT(0.0001, 0.0099, 1_000_000, 0.0001),
    ONE_CENT(0.01, 0.049, 100_000, 0.001),
    FIVE_CENTS(0.05, 0.249, 10_000, 0.001),
    TWENTY_FIVE_CENTS(0.25, 0.495, 10_000, 0.005),
    FIFTY_CENTS(0.50, 4.99, 1_000, 0.01),
    FIVE(5.00, 9.99, 100, 0.01),
    TEN(10.00, 19.98, 100, 0.02),
    TWENTY(20.00, 49.95, 100, 0.05),
    FIFTY(50.00, 99.95, 10, 0.05),
    ONE_HUNDRED(100, 199.9, 10, 0.10),
    TWO_HUNDRED(200, 499.8, 10, 0.20),
    FIVE_HUNDRED(500, 999.5, 10, 0.50),
    ONE_THOUSAND(1000, 1999, 5, 1.00),
    TWO_THOUSAND(2000, 4998, 5, 2.00),
    FIVE_THOUSAND(5000, Long.MAX_VALUE, 5, 5.00);

    private BigDecimal lowerRange;
    private BigDecimal upperRange;
    private int minimumShares;
    private BigDecimal priceFluctuation;

    BoardLot(double lowerRange, double upperRange, int minimumShares, double priceFluctuation)
    {
        this.lowerRange = BigDecimal.valueOf(lowerRange);
        this.upperRange = BigDecimal.valueOf(upperRange);
        this.minimumShares = minimumShares;
        this.priceFluctuation = BigDecimal.valueOf(priceFluctuation);
    }

    /**
     * Check if the number of price has a correct number of shares with regards to the board lot.
     * Note: This does not take into consideration odd lot or shares that are bought in different board lots.
     *
     * @param price  the stock price
     * @param shares the total shares
     * @return true if the price and shares are correct with regards to the board lot
     */
    public static boolean isValidBoardLot(BigDecimal price, long shares)
    {
        boolean sharesLessThanHighestBoardlotMinimumShares = shares < FIVE_THOUSAND.getMinimumShares();
        boolean priceIsNullOrLessThanEqualToZero = price == null || price.signum() <= 0;

        if(priceIsNullOrLessThanEqualToZero || sharesLessThanHighestBoardlotMinimumShares)
        {
            return false;
        }

        for(BoardLot bl : BoardLot.values())
        {
            if(bl.priceWithinRange(price))
            {
                boolean sharesIsValidMinimumShares = shares % bl.getMinimumShares() == 0;
                boolean priceIsValidFluctuation = price.remainder(bl.getPriceFluctuation()).compareTo(BigDecimal.ZERO) == 0;

                return sharesIsValidMinimumShares && priceIsValidFluctuation;
            }
        }

        return false;
    }

    /**
     * Checks if the given price is within the range of this boardlot.
     *
     * @param price the price to check
     * @return true if within range, else false
     */
    public boolean priceWithinRange(BigDecimal price)
    {
        return price != null && price.compareTo(getLowerRange()) >= 0 && price.compareTo(getUpperRange()) <= 0;
    }

    public BigDecimal getLowerRange()
    {
        return lowerRange;
    }

    public BigDecimal getUpperRange()
    {
        return upperRange;
    }

    public int getMinimumShares()
    {
        return minimumShares;
    }

    public BigDecimal getPriceFluctuation()
    {
        return priceFluctuation;
    }
}
