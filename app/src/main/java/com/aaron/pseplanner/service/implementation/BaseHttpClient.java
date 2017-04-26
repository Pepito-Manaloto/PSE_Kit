package com.aaron.pseplanner.service.implementation;

import android.os.Parcelable;

import com.aaron.pseplanner.service.HttpClient;

import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Aaron on 2/26/2017.
 */

public abstract class BaseHttpClient implements HttpClient
{
    protected static Retrofit retrofit;

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
     * @param readTimeout       read timeout in seconds  for new connections
     * @param pingInterval      interval in seconds between web socket pings initiated by this client. Use this to automatically send web socket ping frames until either the web socket fails or it is closed.
     */
    public void setRetrofit(long connectionTimeout, long readTimeout, long pingInterval)
    {
        OkHttpClient client = new OkHttpClient.Builder()
                                                .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
                                                .readTimeout(readTimeout, TimeUnit.SECONDS)
                                                .pingInterval(pingInterval, TimeUnit.SECONDS)
                                                .build();

        retrofit = new Retrofit.Builder()
                                .baseUrl(getBaseURL())
                                .client(client).addConverterFactory(JacksonConverterFactory.create())
                                .build();
    }

    /**
     * Sets the retrofit http client with proxy
     *
     * @param connectionTimeout connect timeout in seconds for new connections
     * @param readTimeout       read timeout in seconds  for new connections
     * @param pingInterval      interval in seconds between web socket pings initiated by this client. Use this to automatically send web socket ping frames until either the web socket fails or it is closed.
     * @param proxyHost         the proxy host name
     * @param proxyPort         the proxy port number
     */
    public void setRetrofit(long connectionTimeout, long readTimeout, long pingInterval, String proxyHost, int proxyPort)
    {
        OkHttpClient client = new OkHttpClient.Builder()
                                                .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
                                                .readTimeout(readTimeout, TimeUnit.SECONDS)
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

    /**
     * Helper class for asynchronous requests.
     */
    protected static class CallbackResult<T extends Parcelable>
    {
        private List<T> responseList;
        private Date lastUpdated;
        private String errorMessage;
        private int errorCode;

        /**
         * Returns true if all async calls are successful.
         */
        public boolean isSuccessful()
        {
            return StringUtils.isBlank(errorMessage) || errorCode == 0;
        }

        /**
         * Returns the number of async calls with response.
         */
        public int responseSize()
        {
            return responseList.size();
        }

        /**
         * Returns the response list of the async calls.
         */
        public List<T> getResponseList()
        {
            return responseList;
        }

        /**
         * Adds an async response to the response list.
         */
        public void addResponseToList(T response)
        {
            if(this.responseList == null)
            {
                this.responseList = new ArrayList<>();
            }

            this.responseList.add(response);
        }

        public void setResponseList(List<T> responseList)
        {
            this.responseList = responseList;
        }

        public Date getLastUpdated()
        {
            return lastUpdated;
        }

        public void setLastUpdated(Date lastUpdated)
        {
            this.lastUpdated = lastUpdated;
        }

        public String getErrorMessage()
        {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage)
        {
            this.errorMessage = errorMessage;
        }

        public int getErrorCode()
        {
            return errorCode;
        }

        public void setErrorCode(int errorCode)
        {
            this.errorCode = errorCode;
        }
    }
}