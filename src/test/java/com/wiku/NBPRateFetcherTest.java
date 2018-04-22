package com.wiku;

import com.wiku.nbp.CachedRateFetcher;
import com.wiku.nbp.NBPRateFetcher;
import com.wiku.nbp.NBPRateFetcherException;
import com.wiku.nbp.RateFetcher;
import com.wiku.rest.client.RestClient;
import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;

public class NBPRateFetcherTest
{
    private RestClient client = new RestClient();
    private RateFetcher nbpRateFetcher = new CachedRateFetcher(new NBPRateFetcher(client));

    @Test
    public void canFetchUSDRateForDayOfTheWeek() throws NBPRateFetcherException
    {
        BigDecimal rate = nbpRateFetcher.fetchRateForDay( "USD", "2018-04-19");
        assertEquals(new BigDecimal("3.3693"), rate);
    }

    @Test
    public void canFetchUSDRateForWeekendDate() throws NBPRateFetcherException
    {
        BigDecimal rate = nbpRateFetcher.fetchRateForDay( "USD", "2018-04-15");
        assertEquals(new BigDecimal("3.3862"), rate);
    }


    @Test
    public void canFetchPreviousEURRateForDayOfWeek() throws NBPRateFetcherException
    {
        BigDecimal rate = nbpRateFetcher.fetchRateForPreviousWorkingDay( "EUR", "2018-04-19");
        assertEquals(new BigDecimal("4.1677"), rate);
    }

    @Test
    public void canFetchPreviousEURRateForSpecialDay() throws NBPRateFetcherException
    {
        BigDecimal rate = nbpRateFetcher.fetchRateForPreviousWorkingDay( "USD", "2016-06-05");
        assertEquals(new BigDecimal("3.9384"), rate);
    }


    @Test
    public void canFetchPreviousEURRateForDayForMonday() throws NBPRateFetcherException
    {
        BigDecimal rate = nbpRateFetcher.fetchRateForPreviousWorkingDay( "EUR", "2018-04-16");
        assertEquals(new BigDecimal("4.1769"), rate);
    }
}
