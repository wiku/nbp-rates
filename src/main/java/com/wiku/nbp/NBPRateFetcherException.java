package com.wiku.nbp;

import com.wiku.rest.client.RestClientException;

public class NBPRateFetcherException extends Exception
{
    public NBPRateFetcherException( String message )
    {
        super(message);
    }

    public NBPRateFetcherException( String message, Exception e )
    {
        super(message,e);
    }
}
