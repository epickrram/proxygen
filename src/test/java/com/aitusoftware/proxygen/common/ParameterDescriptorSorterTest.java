package com.aitusoftware.proxygen.common;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ParameterDescriptorSorterTest
{
    @Test
    public void shouldSortPrimitivesAccordingToSizeThenObjectsAccordingToName() throws Exception
    {
        final ParameterDescriptor[] params = new ParameterDescriptor[]{
                new ParameterDescriptor("word", CharSequence.class, "java.lang.CharSequence"),
                new ParameterDescriptor("bByteArg", null, "byte"),
                new ParameterDescriptor("longArg", null, "long"),
                new ParameterDescriptor("orderDetails", null, "com.example.OrderDetails"),
                new ParameterDescriptor("doubleArg", null, "double"),
                new ParameterDescriptor("aByteArg", null, "byte"),
        };

        Arrays.sort(params, ParameterDescriptorSorter.INSTANCE);

        final ParameterDescriptor[] sorted = new ParameterDescriptor[]{
                new ParameterDescriptor("aByteArg", null, "byte"),
                new ParameterDescriptor("bByteArg", null, "byte"),
                new ParameterDescriptor("doubleArg", null, "double"),
                new ParameterDescriptor("longArg", null, "long"),
                new ParameterDescriptor("word", CharSequence.class, "java.lang.CharSequence"),
                new ParameterDescriptor("orderDetails", null, "com.example.OrderDetails"),
        };

        assertThat(Arrays.stream(params).map(ParameterDescriptor::getName).collect(Collectors.toList()),
                is(Arrays.stream(sorted).map(ParameterDescriptor::getName).collect(Collectors.toList())));
    }
}