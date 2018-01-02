package com.aaron.pseplanner.service.implementation;

import com.aaron.pseplanner.service.HttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Aaron on 2/26/2017.
 */

public abstract class BaseHttpClient implements HttpClient
{
    protected static Retrofit retrofit;

    public BaseHttpClient()
    {
        setRetrofit(DEFAUT_TIMEOUT, DEFAUT_TIMEOUT, DEFAUT_TIMEOUT);
    }

    public BaseHttpClient(long connectionTimeout, long readTimeout, long pingInterval)
    {
        setRetrofit(connectionTimeout, readTimeout, pingInterval);
    }

    public BaseHttpClient(long connectionTimeout, long readTimeout, long pingInterval, String proxyHost, int proxyPort)
    {
        setRetrofit(connectionTimeout, readTimeout, pingInterval, proxyHost, proxyPort);
    }

    /**
     * Sets the retrofit http client.
     *
     * @param connectionTimeout connect timeout in seconds for new connections
     * @param readTimeout       read timeout in seconds for new connections
     * @param pingInterval      interval in seconds between web socket pings initiated by this client. Use this to automatically send web socket ping frames until either the web socket fails or it is closed.
     */
    private void setRetrofit(long connectionTimeout, long readTimeout, long pingInterval)
    {
        OkHttpClient client = new OkHttpClient.Builder()
                                                .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
                                                .readTimeout(readTimeout, TimeUnit.SECONDS)
                                                .writeTimeout(readTimeout, TimeUnit.SECONDS)
                                                .pingInterval(pingInterval, TimeUnit.SECONDS)
                                                .build();

        retrofit = new Retrofit.Builder()
                                .baseUrl(getBaseURL())
                                .client(client).addConverterFactory(JacksonConverterFactory.create())
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .build();
    }

    /**
     * Sets the retrofit http client with proxy
     *
     * @param connectionTimeout connect timeout in seconds for new connections
     * @param readTimeout       read timeout in seconds for new connections
     * @param pingInterval      interval in seconds between web socket pings initiated by this client. Use this to automatically send web socket ping frames until either the web socket fails or it is closed.
     * @param proxyHost         the proxy host name
     * @param proxyPort         the proxy port number
     */
    private void setRetrofit(long connectionTimeout, long readTimeout, long pingInterval, String proxyHost, int proxyPort)
    {
        OkHttpClient client = new OkHttpClient.Builder()
                                                .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
                                                .readTimeout(readTimeout, TimeUnit.SECONDS)
                                                .writeTimeout(readTimeout, TimeUnit.SECONDS)
                                                .pingInterval(pingInterval, TimeUnit.SECONDS)
                                                .proxy(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyHost, proxyPort)))
                                                .build();

        retrofit = new Retrofit.Builder()
                                .baseUrl(getBaseURL())
                                .client(client)
                                .addConverterFactory(JacksonConverterFactory.create())
                                .build();
    }

    /**
     * The base URL used by the implementing class.
     *
     * @return String the base URL
     */
    protected abstract String getBaseURL();
}