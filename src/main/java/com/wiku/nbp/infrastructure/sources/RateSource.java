package com.wiku.nbp.infrastructure.sources;

import com.wiku.nbp.domain.Rate;

import java.time.LocalDate;
import java.util.List;

public interface RateSource
{
    public List<Rate> getRates( String symbol, LocalDate date ) throws RateSourceException;
}
