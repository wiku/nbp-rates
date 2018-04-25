package com.wiku.nbp.application;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RateFetcher
{
    BigDecimal fetchRateForDay( String symbol, LocalDate date ) throws RateFetcherException;
}
