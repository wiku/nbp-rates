package com.wiku.nbp.application;

import com.wiku.nbp.domain.Rate;
import com.wiku.nbp.infrastructure.sources.RateSourceFactory;
import com.wiku.nbp.infrastructure.sources.RateSource;
import com.wiku.nbp.infrastructure.sources.RateSourceException;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Data public class NBPRateFetcher implements RateFetcher
{
    public static final int MAX_DAYS_WITH_NO_DATA = 7;
    private final String url = "http://api.nbp.pl/api/exchangerates/rates/A";
    private final String xauUrl = "http://api.nbp.pl/api/cenyzlota";

    private final RateSourceFactory rateSourceFactory;

    /**
     * Fetches NBP average currency rate for a given date using the official NBP API:
     * http://api.nbp.pl/api/exchangerates/rates/A
     * If rate is not found for some reason (eg. given day is not a working day), tries to fetch the rate for
     * previous working day.
     *
     * @param symbol     symbol of the currency eg. EUR, USD
     * @param date date of transaction
     * @return currency rate for a given symbol
     * @throws RateFetcherException when rate was not found or any kind of parsing/rest client exception occured
     */
    @Override public BigDecimal fetchRateForDay( String symbol, LocalDate date ) throws RateFetcherException
    {
        RateSource rateSource = rateSourceFactory.getRateFetcher(symbol);

        try
        {
            List<Rate> rates = rateSource.getRates(symbol, date);
            Optional<BigDecimal> rate = findLastRate(date, rates);
            return rate.orElseThrow(() -> new RateFetcherException(
                    "Failed to fetch rates for date " + date + " among rates present in the response: " + rates));
        }
        catch( RateSourceException e )
        {
            throw new RateFetcherException("Failed to fetch rates for date " + date, e);
        }
    }


    private Optional<BigDecimal> findLastRate( LocalDate date, List<Rate> rates )
    {
        for( int daysBefore = 0; daysBefore <= MAX_DAYS_WITH_NO_DATA; daysBefore++ )
        {
            LocalDate previousDate = date.minusDays(daysBefore);
            Optional<BigDecimal> exchangeRate = findRateForDay(rates, previousDate);

            if( exchangeRate.isPresent() )
            {
                return exchangeRate;
            }
        }
        return Optional.empty();
    }

    private Optional<BigDecimal> findRateForDay( List<Rate> rates, LocalDate oneDayEarlier )
    {
        return rates.stream()
                .filter(rate -> rate.getDate().equals(oneDayEarlier))
                .map(Rate::getPrice)
                .findFirst();
    }

}
