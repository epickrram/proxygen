package com.aitusoftware.proxygen.common;

import java.util.Comparator;

public enum MethodDescriptorByReturnTypeSorter implements Comparator<MethodDescriptor>
{
    INSTANCE;

    @Override
    public int compare(final MethodDescriptor o1, final MethodDescriptor o2)
    {
        return ParameterDescriptorSorter.INSTANCE.compare(o1.getReturnType(), o2.getReturnType());
    }
}
