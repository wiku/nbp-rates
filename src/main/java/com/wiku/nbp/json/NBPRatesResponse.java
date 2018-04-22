package com.wiku.nbp.json;

import lombok.Data;

import java.util.List;

@Data
public class NBPRatesResponse
{
    String table;
    String currency;
    String code;
    List<NBPRate> rates;
}
