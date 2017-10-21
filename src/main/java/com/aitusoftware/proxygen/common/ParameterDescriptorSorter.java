package com.aitusoftware.proxygen.common;

import java.util.Comparator;

public enum ParameterDescriptorSorter implements Comparator<ParameterDescriptor>
{
    INSTANCE;

    @Override
    public int compare(final ParameterDescriptor o1, final ParameterDescriptor o2)
    {
        if (Types.isPrimitive(o1.getType()) && Types.isPrimitive(o2.getType()))
        {
            final int o1Length = Types.getPrimitiveTypeSize(o1.getType());
            final int o2Length = Types.getPrimitiveTypeSize(o2.getType());
            if (o1Length == o2Length)
            {
                return o1.getName().compareTo(o2.getName());
            }
            return Integer.compare(o1Length, o2Length);
        }
        else if (Types.isPrimitive(o1.getType()))
        {
            return -1;
        }
        else if (Types.isPrimitive(o2.getType()))
        {
            return 1;
        }
        else if (Types.isCharSequence(o1.getType()) && !Types.isCharSequence(o2.getType()))
        {
            return -1;
        }
        else if (!Types.isCharSequence(o1.getType()) && Types.isCharSequence(o2.getType()))
        {
            return 1;
        }
        return o1.getTypeName().compareTo(o2.getTypeName());
    }
}
