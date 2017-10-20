package com.aitusoftware.proxygen.publisher;

import com.aitusoftware.proxygen.common.Constants;

public enum ProxyClassnames
{
    INSTANCE;

    public static String toPublisher(final String interfaceName)
    {
        return interfaceName + Constants.PROXYGEN_PUBLISHER_SUFFIX;
    }

    public static String toSubscriber(final String interfaceName)
    {
        return interfaceName + Constants.PROXYGEN_SUBSCRIBER_SUFFIX;
    }
}
