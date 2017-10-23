package com.aitusoftware.proxygen.message;

import com.aitusoftware.proxygen.common.MethodDescriptor;
import com.aitusoftware.proxygen.common.ParameterDescriptor;

final class Fixtures
{
    static final MethodDescriptor[] METHODS = new MethodDescriptor[]
            {
                    new MethodDescriptor(0, "orderId", new ParameterDescriptor[0],
                            new ParameterDescriptor("orderId", long.class, "long")),
                    new MethodDescriptor(0, "getQuantity", new ParameterDescriptor[0],
                            new ParameterDescriptor("getQuantity", double.class, "double")),
                    new MethodDescriptor(0, "price", new ParameterDescriptor[0],
                            new ParameterDescriptor("price", double.class, "double")),
                    new MethodDescriptor(0, "getDescriptor", new ParameterDescriptor[0],
                            new ParameterDescriptor("getDescriptor", CharSequence.class, "java.lang.CharSequence")),
                    new MethodDescriptor(0, "getDescriptor2", new ParameterDescriptor[0],
                            new ParameterDescriptor("getDescriptor2", CharSequence.class, "java.lang.CharSequence")),
            };
}
