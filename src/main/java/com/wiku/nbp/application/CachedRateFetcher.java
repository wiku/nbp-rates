package com.wiku.nbp.application;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

/**
 * Decorator for RateFetcher, caches results for a given combination of symbol and date
 */
@RequiredArgsConstructor
public class CachedRateFetcher implements RateFetcher
{

    @Data private class CacheKey
    {
        private final String symbol;
        private final LocalDate date;
    }

    private final NBPRateFetcher fetcher;

    private LoadingCache<CacheKey, BigDecimal> ratesForDate = CacheBuilder.newBuilder()
            .build(new CacheLoader<CacheKey, BigDecimal>()
            {
                @Override public BigDecimal load( CacheKey key ) throws Exception
                {
                    return fetcher.fetchRateForDay(key.getSymbol(), key.getDate());
                }
            });


    @Override public BigDecimal fetchRateForDay( String symbol, LocalDate date ) throws RateFetcherException
    {
        try
        {
            return ratesForDate.get(new CacheKey(symbol, date));
        }
        catch( ExecutionException e )
        {
            throw new RateFetcherException("Error fetching rates: ", e);
        }
    }

}
