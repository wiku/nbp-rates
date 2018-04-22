package com.wiku.nbp;

import com.wiku.nbp.NBPRateFetcherException;

import java.math.BigDecimal;

public interface RateFetcher
{
    BigDecimal fetchRateForDay( String symbol, String dateString ) throws NBPRateFetcherException;

    BigDecimal fetchRateForPreviousWorkingDay( String symbol, String dateString ) throws NBPRateFetcherException;
}
