package com.wiku.nbp.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Rate
{
    private final String symbol;
    private final LocalDate date;
    private final BigDecimal price;

    public static Rate from(String symbol, String isoDateString, String price)
    {
        return new Rate(symbol, LocalDate.parse(isoDateString), new BigDecimal(price));
    }
}
