package com.aitusoftware.proxygen.publisher;

public final class NetAddress
{
    private final String host;
    private final int port;

    public NetAddress(final String host, final int port)
    {
        this.host = host;
        this.port = port;
    }

    String getHost()
    {
        return host;
    }

    int getPort()
    {
        return port;
    }
}
