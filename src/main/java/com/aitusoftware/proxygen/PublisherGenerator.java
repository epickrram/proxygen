package com.aitusoftware.proxygen;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

final class PublisherGenerator
{
    private static final List<String> REQUIRED_IMPORTS = Arrays.asList(
            "com.aitusoftware.transport.messaging.proxy.AbstractPublisher",
            "com.aitusoftware.transport.buffer.WritableRecord",
            "com.aitusoftware.transport.buffer.PageCache",
            "com.aitusoftware.transport.messaging.proxy.Encoder"
    );

    void generatePublisher(
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
        int appended = 0;
        for (int i = 0; i < parameterTypes.length; i++)
        {
            if (appended != 0)
            {
                writer.append(" + ");
            }
            final ParameterDescriptor parameterType = parameterTypes[i];
            if (parameterType.getType().isPrimitive())
            {
                staticLength += getPrimitiveTypeSize(parameterType.getType());
            }
            else if (CharSequence.class == parameterType.getType())
            {
                writer.append(parameterType.getName()).append(".length()");
                appended++;
            }
            else
            {
                throw new IllegalArgumentException("Unsupported parameter type: " + parameterType.getType().getName());
            }
        }

        writer.append(" ").append(Integer.toString(staticLength)).append(";\n");
    }

    private int getPrimitiveTypeSize(final Class<?> type)
    {
        if (type == boolean.class || type == byte.class)
        {
            return 1;
        }
        if (type == short.class)
        {
            return 2;
        }
        if (type == int.class || type == char.class || type == float.class)
        {
            return 4;
        }
        if (type == long.class || type == double.class)
        {
            return 8;
        }

        throw new IllegalArgumentException(String.format(
                "Unsupported primitive type: %s", type.getName()));
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
        for (final ParameterDescriptor parameterType : parameterTypes)
        {
            if (parameterType.getType() == null)
            {
                writer.append("\t\tEncoder.encode").append(toMethodSuffix(parameterType.getTypeName())).
                        append("(wr.buffer(), ").append(parameterType.getName()).append(");\n");
            }
            else if (parameterType.getType().isPrimitive() || CharSequence.class == parameterType.getType())
            {
                writer.append("\t\tEncoder.encode").append(toMethodSuffix(parameterType.getType().getSimpleName())).
                        append("(wr.buffer(), ").append(parameterType.getName()).append(");\n");
            }
            else
            {
                throw new IllegalArgumentException("Unsupported parameter type: " + parameterType.getType().getName());
            }
        }
    }

    private String toMethodSuffix(final String name)
    {
        final char first = name.charAt(0);
        if (Character.isLowerCase(first))
        {
            return Character.toUpperCase(first) + name.substring(1);
        }
        return name;
    }
}