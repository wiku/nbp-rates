package com.wiku.nbp.infrastructure.sources.currency;

import com.wiku.nbp.domain.Rate;
import com.wiku.nbp.infrastructure.sources.RateSource;
import com.wiku.nbp.infrastructure.sources.RateSourceException;
import com.wiku.rest.client.RestClient;
import com.wiku.rest.client.RestClientException;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data public class CurrencyRateSource implements RateSource
{

    private static final int MAX_DAYS_WITH_NO_DATA = 7;
    private final String url = "http://api.nbp.pl/api/exchangerates/rates/A";

    private final RestClient restClient;

    @Override public List<Rate> getRates( String symbol, LocalDate date ) throws RateSourceException
    {

        try
        {
            CurrencyRates response = restClient.get(String.format("%s/%s/%s/%s",
                    url,
                    symbol,
                    date.minusDays(MAX_DAYS_WITH_NO_DATA),
                    date), CurrencyRates.class);

            return response.getRates()
                    .stream()
                    .map(nbpRate -> Rate.from(symbol, nbpRate.getEffectiveDate(), nbpRate.getMid()))
                    .collect(Collectors.toList());
        }
        catch( RestClientException e )
        {
            throw new RateSourceException("Failed to fetch rates for symbol=" + symbol + ", date=" + date, e);
        }
    }
}
