package com.wiku.nbp.infrastructure.sources.gold;

import com.wiku.nbp.domain.Rate;
import com.wiku.nbp.infrastructure.sources.RateSource;
import com.wiku.nbp.infrastructure.sources.RateSourceException;
import com.wiku.rest.client.RestClient;
import com.wiku.rest.client.RestClientException;
import lombok.Data;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Data public class GoldRateSource implements RateSource
{
    private static final int MAX_DAYS_WITH_NO_DATA = 7;

    private final String xauUrl = "http://api.nbp.pl/api/cenyzlota";
    private final RestClient restClient;

    @Override public List<Rate> getRates( String symbol, LocalDate date ) throws RateSourceException
    {
        try
        {
            GoldPrice[] goldPrices = restClient.get(String.format("%s/%s/%s",
                    xauUrl,
                    date.minusDays(MAX_DAYS_WITH_NO_DATA),
                    date), GoldPrice[].class);

            return Arrays.stream(goldPrices)
                    .map(goldPrice -> Rate.from(symbol, goldPrice.getDateString(), goldPrice.getPrice()))
                    .collect(Collectors.toList());
        }
        catch( RestClientException e )
        {
            throw new RateSourceException("Failed request to fetch rate for symbol=" + symbol + " date=" + date, e);
        }
    }
}
