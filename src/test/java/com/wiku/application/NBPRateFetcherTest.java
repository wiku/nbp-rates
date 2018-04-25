package com.wiku.application;

import com.wiku.nbp.application.CachedRateFetcher;
import com.wiku.nbp.application.NBPRateFetcher;
import com.wiku.nbp.application.RateFetcherException;
import com.wiku.nbp.application.RateFetcher;
import com.wiku.nbp.infrastructure.sources.RateResourceFactory;
import com.wiku.rest.client.RestClient;
import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class NBPRateFetcherTest
{
    private RestClient client = new RestClient();
    private RateFetcher nbpRateFetcher = new CachedRateFetcher(new NBPRateFetcher(new RateResourceFactory(client)));

    @Test
    public void canFetchUSDRateForDayOfTheWeek() throws RateFetcherException
    {
        BigDecimal rate = nbpRateFetcher.fetchRateForDay( "USD", LocalDate.parse("2018-04-19"));
        assertEquals(new BigDecimal("3.3693"), rate);
    }

    @Test
    public void canFetchUSDRateForWeekendDate() throws RateFetcherException
    {
        BigDecimal rate = nbpRateFetcher.fetchRateForDay( "USD", LocalDate.parse("2018-04-15"));
        assertEquals(new BigDecimal("3.3862"), rate);
    }


    @Test
    public void canFetchEURRateForDayOfWeek() throws RateFetcherException
    {
        BigDecimal rate = nbpRateFetcher.fetchRateForDay( "EUR", LocalDate.parse("2018-04-18"));
        assertEquals(new BigDecimal("4.1677"), rate);
    }

    @Test
    public void canFetchEURRateForSpecialDay() throws RateFetcherException
    {
        BigDecimal rate = nbpRateFetcher.fetchRateForDay( "USD", LocalDate.parse("2016-06-04"));
        assertEquals(new BigDecimal("3.9384"), rate);
    }


    @Test
    public void canFetchEURForSunday() throws RateFetcherException
    {
        BigDecimal rate = nbpRateFetcher.fetchRateForDay( "EUR", LocalDate.parse("2018-04-15"));
        assertEquals(new BigDecimal("4.1769"), rate);
    }
    @Test
    public void canFetchPriceOfGold() throws RateFetcherException
    {
        BigDecimal rate = nbpRateFetcher.fetchRateForDay( "XAU", LocalDate.parse("2018-04-24"));
        assertEquals(new BigDecimal("145.44"), rate);
    }

    @Test
    public void canFetchPriceOfGoldBeforeWeekend() throws RateFetcherException
    {
        BigDecimal rate = nbpRateFetcher.fetchRateForDay( "XAU", LocalDate.parse("2018-04-22"));
        assertEquals(new BigDecimal("146.09"), rate);
    }
}
