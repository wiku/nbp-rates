package com.wiku.nbp.infrastructure.sources;

import com.wiku.nbp.infrastructure.sources.currency.CurrencyRateSource;
import com.wiku.nbp.infrastructure.sources.gold.GoldRateSource;
import com.wiku.rest.client.RestClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor public class RateResourceFactory
{

    private final RestClient client;

    public RateSource getRateFetcher(String symbol)
    {
        if(isGold(symbol))
        {
            return new GoldRateSource(client);
        }
        else {
            return new CurrencyRateSource(client);
        }
    }

    private boolean isGold( String symbol )
    {
        return symbol.equals("XAU");
    }
}
