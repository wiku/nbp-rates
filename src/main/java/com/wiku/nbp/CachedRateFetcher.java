package com.wiku.nbp;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

public class CachedRateFetcher implements RateFetcher
{

    private final NBPRateFetcher fetcher;


    private LoadingCache<CacheKey,BigDecimal> ratesForDate = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, BigDecimal>()
    {
        @Override public BigDecimal load( CacheKey key ) throws Exception
        {
            return fetcher.fetchRateForDay(key.getSymbol(), key.getDate());
        }
    });

    private LoadingCache<CacheKey,BigDecimal> ratesForPreviousWorkingDate = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, BigDecimal>()
    {
        @Override public BigDecimal load( CacheKey key ) throws Exception
        {
            return fetcher.fetchRateForPreviousWorkingDay(key.getSymbol(), key.getDate());
        }
    });

    public CachedRateFetcher(NBPRateFetcher fetcher)
    {
        this.fetcher =fetcher;
    }

    @Override public BigDecimal fetchRateForDay( String symbol, String dateString ) throws NBPRateFetcherException
    {
        try
        {
            return ratesForDate.get(new CacheKey(symbol,dateString));
        }
        catch( ExecutionException e )
        {
            throw new NBPRateFetcherException("Error fetching rates: ", e);
        }
    }

    @Override public BigDecimal fetchRateForPreviousWorkingDay( String symbol, String dateString ) throws
            NBPRateFetcherException
    {
        try
        {
            return ratesForPreviousWorkingDate.get(new CacheKey(symbol,dateString));
        }
        catch( ExecutionException e )
        {
            throw new NBPRateFetcherException("Error fetching rates: ", e);
        }
    }

    @Data
    private class CacheKey
    {
        private final String symbol;
        private final String date;
    }
}
