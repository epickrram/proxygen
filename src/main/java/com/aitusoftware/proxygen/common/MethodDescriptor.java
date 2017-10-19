package com.aitusoftware.proxygen.common;

import java.util.Arrays;

public final class MethodDescriptor
{
    private final int index;
    private final String name;
    private final ParameterDescriptor[] parameterTypes;
    private final ParameterDescriptor returnType;

    public MethodDescriptor(final int index, final String name, final ParameterDescriptor[] parameterTypes)
    {
        this(index, name, parameterTypes, null);
    }

    public MethodDescriptor(final int index, final String name,
                            final ParameterDescriptor[] parameterTypes,
                            final ParameterDescriptor returnType)
    {
        this.index = index;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    int getIndex()
    {
        return index;
    }

    public String getName()
    {
        return name;
    }

    public ParameterDescriptor[] getParameterTypes()
    {
        return parameterTypes;
    }

    public ParameterDescriptor getReturnType()
    {
        return returnType;
    }

    @Override
    public String toString()
    {
        return "MethodDescriptor{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", returnType=" + returnType +
                '}';
    }
}