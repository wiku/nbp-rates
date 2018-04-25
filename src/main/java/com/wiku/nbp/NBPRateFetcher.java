package com.wiku.nbp;

import com.wiku.nbp.json.GoldPrice;
import com.wiku.nbp.json.NBPRate;
import com.wiku.nbp.json.NBPRatesResponse;
import com.wiku.rest.client.ResourceNotFoundRestClientException;
import com.wiku.rest.client.RestClient;
import com.wiku.rest.client.RestClientException;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Data public class NBPRateFetcher implements RateFetcher
{
    public static final int MAX_DAYS_WITH_NO_DATA = 7;
    private final String url = "http://api.nbp.pl/api/exchangerates/rates/A";
    private final String xauUrl = "http://api.nbp.pl/api/cenyzlota";

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
            BigDecimal rate = tryFetchRateForDate(symbol, dateString);
            return rate;
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
        return fetchRateForDay(symbol, getStringForEarlierDate(dateString, 1));
    }

    private BigDecimal tryFetchRateForDate( String symbol, String dateString ) throws
            NBPRateFetcherException,
            RestClientException
    {
        try
        {
            if( isGold(symbol) )
            {
                GoldPrice[] goldPrices = client.get(String.format("%s/%s", xauUrl, dateString), GoldPrice[].class);
                System.out.println(goldPrices[0].getPrice());
                return new BigDecimal(goldPrices[0].getPrice());
            }
            else
            {
                NBPRate rate = client.get(String.format("%s/%s/%s", url, symbol, dateString), NBPRatesResponse.class)
                        .getRates()
                        .get(0);
                return new BigDecimal(rate.getMid());
            }

        }
        catch( ResourceNotFoundRestClientException e )
        {
            return getPreviousWorkingDayRate(symbol, dateString);
        }
    }

    private boolean isGold( String symbol )
    {
        return symbol.equals("XAU");
    }

    private BigDecimal getPreviousWorkingDayRate( String symbol, String dateString ) throws
            RestClientException,
            NBPRateFetcherException
    {

        Optional<BigDecimal> rate = Optional.empty();
        if( isGold(symbol) )
        {
            GoldPrice[] response = client.get(String.format("%s/%s/%s",
                    xauUrl,
                    getStringForEarlierDate(dateString, MAX_DAYS_WITH_NO_DATA),
                    dateString),
                    GoldPrice[].class);

            rate = findLastRate(dateString, Arrays.asList(response),
                    goldPrice -> new BigDecimal(goldPrice.getPrice()),
                    goldPrice -> goldPrice.getDateString());

            return rate.orElseThrow(() -> new NBPRateFetcherException(
                    "Failed to fetch rates for date " + dateString + " among rates present in the response: " + response));
        }
        else
        {

            NBPRatesResponse response = client.get(String.format("%s/%s/%s/%s",
                    url,
                    symbol,
                    getStringForEarlierDate(dateString, MAX_DAYS_WITH_NO_DATA),
                    dateString),
                    NBPRatesResponse.class);

            rate = findLastRate(dateString,
                    response.getRates(),
                    nbpRate -> new BigDecimal(nbpRate.getMid()),
                    nbpRate -> nbpRate.getEffectiveDate());

            return rate.orElseThrow(() -> new NBPRateFetcherException(
                    "Failed to fetch rates for date " + dateString + " among rates present in the response: " + response));
        }


    }

    private <T> Optional<BigDecimal> findLastRate( String dateString,
            List<T> rates,
            Function<T, BigDecimal> rateExtractor,
            Function<T, String> dateExtractor )
    {
        for( int daysBefore = 1; daysBefore <= MAX_DAYS_WITH_NO_DATA; daysBefore++ )
        {
            String previousDate = getStringForEarlierDate(dateString, daysBefore);
            Optional<BigDecimal> exchangeRate = findRateForDay(rates, previousDate, rateExtractor, dateExtractor);
            if( exchangeRate.isPresent() )
            {
                return exchangeRate;
            }
        }
        return Optional.empty();
    }

    private <T> Optional<BigDecimal> findRateForDay( List<T> rates,
            String oneDayEarlier,
            Function<T, BigDecimal> rateExtractor,
            Function<T, String> dateExtractor )
    {
        return rates.stream()
                .filter(rate -> dateExtractor.apply(rate).equals(oneDayEarlier))
                .map(rateExtractor)
                .findFirst();
    }

    private String getStringForEarlierDate( String dateString, int daysBefore )
    {
        LocalDate date = LocalDate.parse(dateString);
        return date.minusDays(daysBefore).toString();
    }

}
