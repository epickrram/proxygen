package com.aitusoftware.proxygen.message;

import com.aitusoftware.proxygen.common.MethodDescriptor;
import com.aitusoftware.proxygen.common.ParameterDescriptor;

final class Fixtures
{
    static final MethodDescriptor[] METHODS = new MethodDescriptor[]
            {
                    new MethodDescriptor(0, "orderId", new ParameterDescriptor[0],
                            new ParameterDescriptor(null, long.class, "long")),
                    new MethodDescriptor(0, "getQuantity", new ParameterDescriptor[0],
                            new ParameterDescriptor(null, double.class, "double")),
                    new MethodDescriptor(0, "price", new ParameterDescriptor[0],
                            new ParameterDescriptor(null, double.class, "double")),
                    new MethodDescriptor(0, "getDescriptor", new ParameterDescriptor[0],
                            new ParameterDescriptor(null, CharSequence.class, "java.lang.CharSequence")),
            };
}
