package com.wiku.nbp.application;

import com.wiku.rest.client.RestClientException;

public class RateFetcherException extends Exception
{
    public RateFetcherException( String message )
    {
        super(message);
    }

    public RateFetcherException( String message, Exception e )
    {
        super(message,e);
    }
}
