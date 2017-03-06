package com.aaron.pseplanner.exception;

/**
 * Created by Aaron on 3/5/2017.
 */

public class HttpRequestException extends Exception
{
    private int statusCode;

    public HttpRequestException(int statusCode)
    {
        this.statusCode = statusCode;
    }

    public HttpRequestException(String message, int statusCode)
    {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpRequestException(String message, Throwable cause, int statusCode)
    {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public HttpRequestException(Throwable cause, int statusCode)
    {
        super(cause);
        this.statusCode = statusCode;
    }

    public HttpRequestException(String message)
    {
        super(message);
    }

    public HttpRequestException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public HttpRequestException(Throwable cause)
    {
        super(cause);
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
    }
}
