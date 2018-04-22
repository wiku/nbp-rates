package com.wiku.nbp;

import com.wiku.nbp.json.NBPRate;
import com.wiku.nbp.json.NBPRatesResponse;
import com.wiku.rest.client.ResourceNotFoundRestClientException;
import com.wiku.rest.client.RestClient;
import com.wiku.rest.client.RestClientException;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Data public class NBPRateFetcher implements RateFetcher
{
    public static final int MAX_DAYS_WITH_NO_QUOTES = 7;
    private final String url = "http://api.nbp.pl/api/exchangerates/rates/A";
    private final RestClient client;

    /**
     * Fetches NBP average currency rate for a given date using the official NBP API:
     * http://api.nbp.pl/api/exchangerates/rates/A
     * If rate is not found for some reason (eg. given day is not a working day), tries to fetch the rate for
     * previous working day.
     *
     * @param symbol     symbol of the currency eg. EUR, USD
     * @param dateString date in ISO format (YYYY-MM-DD)
     * @return currency rate for a given symbol
     * @throws NBPRateFetcherException when rate was not found or any kind of parsing/rest client exception occured
     */
    @Override public BigDecimal fetchRateForDay( String symbol, String dateString ) throws NBPRateFetcherException
    {
        try
        {
            NBPRate rate = tryFetchRateForDate(symbol, dateString);
            BigDecimal decimalRate = new BigDecimal(rate.getMid());
            return decimalRate;
        }
        catch( RestClientException e )
        {

            throw new NBPRateFetcherException(
                    "Failed to fetch rates for date " + dateString + " due to RestClientException:", e);
        }
    }

    /**
     * Fetches NBP average currency rate for last working day preceeding the given date. Uses the official NBP API:
     * http://api.nbp.pl/api/exchangerates/rates
     *
     * @param symbol     symbol of the currency eg. EUR, USD
     * @param dateString date in ISO format (YYYY-MM-DD)
     * @return currency rate for a given symbol
     * @throws NBPRateFetcherException - when rate was not found or any kind of parsing/rest client exception occured
     */
    @Override public BigDecimal fetchRateForPreviousWorkingDay( String symbol, String dateString ) throws
            NBPRateFetcherException
    {
        return fetchRateForDay(symbol, daysBefore(dateString, 1));
    }

    private NBPRate tryFetchRateForDate( String symbol, String dateString ) throws
            NBPRateFetcherException,
            RestClientException
    {
        try
        {
            return client.get(String.format("%s/%s/%s", url, symbol, dateString), NBPRatesResponse.class)
                    .getRates()
                    .get(0);
        }
        catch( ResourceNotFoundRestClientException e )
        {
            return getPreviousWorkingDayRate(symbol, dateString);
        }
    }

    private NBPRate getPreviousWorkingDayRate( String symbol, String dateString ) throws
            RestClientException,
            NBPRateFetcherException
    {
        NBPRatesResponse response = client.get(getUriForLastNDays(symbol, dateString, MAX_DAYS_WITH_NO_QUOTES),
                NBPRatesResponse.class);

        Optional<NBPRate> rate = findLastRate(dateString, response.getRates());

        return rate.orElseThrow(() -> new NBPRateFetcherException(
                "Failed to fetch rates for date " + dateString + " among rates present in the response: " + response));
    }

    private String getUriForLastNDays( String symbol, String dateString, int lastNDays )
    {
        return String.format("%s/%s/%s/%s", url, symbol, daysBefore(dateString, lastNDays), dateString);
    }

    private Optional<NBPRate> findLastRate( String dateString, List<NBPRate> rates )
    {
        String date = dateString;
        for( int i = 0; i < MAX_DAYS_WITH_NO_QUOTES; i++ )
        {
            String oneDayEarlier = daysBefore(date, 1);
            Optional<NBPRate> rate = findRateForDay(rates, oneDayEarlier);
            if( rate.isPresent() )
            {
                return rate;
            }
            date = oneDayEarlier;
        }
        return Optional.empty();
    }

    private Optional<NBPRate> findRateForDay( List<NBPRate> rates, String oneDayEarlier )
    {
        return rates.stream().filter(rate -> rate.getEffectiveDate().equals(oneDayEarlier)).findFirst();
    }

    private String daysBefore( String dateString, int daysBefore )
    {
        LocalDate date = LocalDate.parse(dateString);
        return date.minusDays(daysBefore).toString();
    }

}
