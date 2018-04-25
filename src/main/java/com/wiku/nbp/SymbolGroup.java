package com.wiku.nbp;

import java.math.BigDecimal;

public interface SymbolGroup
{
    boolean belongs(String symbol);
    String getUrl(String date);
    String getUrl(String startDate, String endDate);
    Class<?> getReponseClass();
    BigDecimal getRate();
}
