package com.aitusoftware.proxygen.message;

import com.aitusoftware.proxygen.common.Constants;

public enum MessageClassnames
{
    INSTANCE;

    public static String toBuilder(final String interfaceName)
    {
        return interfaceName + Constants.MESSAGE_BUILDER_SUFFIX;
    }

    public static String toFlyweight(final String interfaceName)
    {
        return interfaceName + Constants.MESSAGE_FLYWEIGHT_SUFFIX;
    }

    public static String toSerialiser(final String interfaceName)
    {
        return interfaceName + Constants.MESSAGE_SERIALISER_SUFFIX;
    }
}
