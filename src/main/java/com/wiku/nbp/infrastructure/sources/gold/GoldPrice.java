package com.wiku.nbp.infrastructure.sources.gold;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoldPrice
{
    @JsonProperty(value="data")
    private String dateString;

    @JsonProperty(value="cena")
    private String price;
}
