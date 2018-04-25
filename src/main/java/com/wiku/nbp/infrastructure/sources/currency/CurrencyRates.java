package com.wiku.nbp.infrastructure.sources.currency;

import lombok.Data;

import java.util.List;

@Data
public class CurrencyRates
{
    String table;
    String currency;
    String code;
    List<CurrencyRate> rates;
}
