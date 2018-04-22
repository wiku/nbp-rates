package com.wiku;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class AppOptions
{
    private final String inputFile;
    private final boolean fullOutput;
    private final boolean fetchForPreviousDay;
}
