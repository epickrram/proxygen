package com.aitusoftware.proxygen.publisher;

import com.aitusoftware.proxygen.common.Constants;
import com.aitusoftware.proxygen.common.MethodDescriptor;
import com.aitusoftware.proxygen.common.ParameterDescriptor;
import com.aitusoftware.proxygen.common.ParameterDescriptorSorter;
import com.aitusoftware.proxygen.common.Types;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

public final class PublisherGenerator
{
    private static final List<String> REQUIRED_IMPORTS = Arrays.asList(
            "com.aitusoftware.transport.messaging.proxy.AbstractPublisher",
            "com.aitusoftware.transport.buffer.WritableRecord",
            "com.aitusoftware.transport.buffer.PageCache",
            "com.aitusoftware.transport.messaging.proxy.Encoder"
    );

    public void generatePublisher(
            final String packageName, final String className, final String interfaceName,
            final MethodDescriptor[] methods, final List<String> imports,
            final Writer writer)
    {
        try
        {
            writer.append("package ").append(packageName).append(";\n\n");
            for (String cls : imports)
            {
                writer.append("import ").append(cls).append(";\n");
            }
            for (String cls : REQUIRED_IMPORTS)
            {
                writer.append("import ").append(cls).append(";\n");
            }

            writer.append("\n\n");
            writer.append("public class ").append(className).
                    append(" extends AbstractPublisher implements ").
                    append(interfaceName).append(" {\n\n");

            appendConstructor(className, writer);

            byte methodId = 0;
            for (MethodDescriptor descriptor : methods)
            {
                appendMethod(descriptor, methodId, writer);
                methodId++;
            }

            writer.append("}\n");
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    private void appendConstructor(final String className, final Writer writer) throws IOException
    {
        writer.append("\tpublic ").append(className).append("(final PageCache pageCache) {\n");
        writer.append("\t\tsuper(pageCache);\n");
        writer.append("\t}\n\n");
    }

    private void appendMethod(
            final MethodDescriptor descriptor, final byte methodId, final Writer writer) throws IOException
    {
        writer.append("\tpublic void ").append(descriptor.getName()).append("(\n\t\t");
        appendParameters(descriptor.getParameterTypes(), writer);
        writer.append(") {\n");
        writer.append("\t\t\n");
        appendLengthCalculation(descriptor.getParameterTypes(), writer);
        writer.append("\t\tfinal WritableRecord wr = acquireRecord(recordLength, (byte) ").
                append(Byte.toString(methodId)).
                append(");\n");
        encodeArguments(descriptor.getParameterTypes(), writer);
        writer.append("\t\twr.commit();\n");
        writer.append("\t}\n\n");
    }

    private void appendLengthCalculation(
            final ParameterDescriptor[] parameterTypes, final Writer writer) throws IOException
    {
        writer.append("\t\tfinal int recordLength = ");
        int staticLength = 0;
        for (int i = 0; i < parameterTypes.length; i++)
        {
            final ParameterDescriptor parameterType = parameterTypes[i];
            if (Types.isPrimitive(parameterType.getType()))
            {
                staticLength += Types.getPrimitiveTypeSize(parameterType.getType());
            }
            else if (CharSequence.class == parameterType.getType())
            {
                writer.append("(").append(parameterType.getName()).append(".length() * 4) + 4 ");
                writer.append(" + ");
            }
            else
            {
                writer.append("((Sized) ").append(parameterType.getName()).
                        append(").length() + ");
            }
        }

        writer.append(" ").append(Integer.toString(staticLength)).append(";\n");
    }

    private void appendParameters(
            final ParameterDescriptor[] parameterTypes, final Writer writer) throws IOException
    {
        for (int i = 0; i < parameterTypes.length; i++)
        {
            if (i != 0)
            {
                writer.append(", ");
            }
            final ParameterDescriptor parameterType = parameterTypes[i];
            writer.append("final ").append(parameterType.getTypeName()).
                    append(" ").append(parameterType.getName());
        }
    }

    private void encodeArguments(
            final ParameterDescriptor[] parameterTypes, final Writer writer) throws IOException
    {
        final ParameterDescriptor[] copy = new ParameterDescriptor[parameterTypes.length];
        System.arraycopy(parameterTypes, 0, copy, 0, copy.length);
        Arrays.sort(copy, ParameterDescriptorSorter.INSTANCE);
        for (final ParameterDescriptor parameterType : copy)
        {
            if (Types.isPrimitive(parameterType.getType()) || Types.isCharSequence(parameterType.getType()))
            {
                writer.append("\t\tEncoder.encode").append(Types.toMethodSuffix(parameterType.getType().getSimpleName())).
                        append("(wr.buffer(), ").append(parameterType.getName()).append(");\n");
            }
            else
            {
                // TODO add serialiser to imports, only refer to simple name
                writer.append("\t\t").append(parameterType.getTypeName()).
                        append(Constants.MESSAGE_SERIALISER_SUFFIX).append(".serialise(").append(parameterType.getName()).
                        append(", wr.buffer());\n");
            }
        }
    }
}