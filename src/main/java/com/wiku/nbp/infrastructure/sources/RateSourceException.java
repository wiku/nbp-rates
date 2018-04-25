package com.wiku.nbp.infrastructure.sources;

public class RateSourceException extends Exception
{
    public RateSourceException(String message, Throwable t)
    {
        super(message,t);
    }
}
