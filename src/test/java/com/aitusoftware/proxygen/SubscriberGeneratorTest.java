package com.aitusoftware.proxygen;

import org.junit.Test;

import java.io.StringWriter;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SubscriberGeneratorTest
{

    private final SubscriberGenerator generator = new SubscriberGenerator();

    @Test
    public void shouldGenerateSubscriberImplementation() throws Exception
    {
        final StringWriter writer = new StringWriter();
        generator.generateSubscriber(
                "com.package",
                "TestSubscriberImpl",
                "TestSubscriber",
                new MethodDescriptor[]{
                        new MethodDescriptor(0, "say",
                                new ParameterDescriptor[]{
                                        new ParameterDescriptor("word", CharSequence.class, "java.lang.CharSequence"),
                                        new ParameterDescriptor("count", int.class, "int")
                                })},
                Collections.emptyList(),
                writer);

        System.out.println(writer);

        assertThat(writer.toString(), is(""));
    }
}