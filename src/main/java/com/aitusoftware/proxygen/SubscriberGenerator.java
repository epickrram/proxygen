package com.aitusoftware.proxygen;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

final class SubscriberGenerator
{
    private static final List<String> REQUIRED_IMPORTS = Arrays.asList(
            "com.aitusoftware.transport.messaging.proxy.AbstractSubscriber",
            "com.aitusoftware.transport.messaging.proxy.MethodInvoker",
            "com.aitusoftware.transport.reader.RecordHandler",
            "com.aitusoftware.transport.buffer.PageCache",
            "com.aitusoftware.transport.messaging.proxy.Decoder",
            "java.nio.ByteBuffer"
    );

    void generateSubscriber(
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
                    append(" extends AbstractSubscriber<").append(interfaceName).append(">").
                    append(" {\n\n");

            appendConstructor(className, interfaceName, writer);

            byte methodId = 0;
            final StringBuilder generateInvokersMethodSource = new StringBuilder();
            generateInvokersMethodSource.append("\n\n\tprivate static MethodInvoker[] generateInvokers() {\n").
                    append("\t\tfinal MethodInvoker[] invokers = new MethodInvoker[").
                    append(methods.length).append("];\n");

            for (MethodDescriptor descriptor : methods)
            {
                appendInvoker(descriptor, methodId, generateInvokersMethodSource, interfaceName, writer);
                methodId++;
            }

            generateInvokersMethodSource.append("\t\treturn invokers;\n").
                    append("\t}\n");

            writer.append("\n\n").append(generateInvokersMethodSource);

            writer.append("}\n");
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    private void appendConstructor(final String className, final String interfaceName, final Writer writer) throws IOException
    {
        writer.append("\tpublic ").append(className).append("(final ").append(interfaceName).
                append(" implementation) {\n");
        writer.append("\t\tsuper(implementation, generateInvokers());\n");
        writer.append("\t}\n\n");
    }

    private void appendInvoker(
            final MethodDescriptor descriptor, final byte methodId,
            final StringBuilder generateInvokersMethodSource, final String interfaceName, final Writer writer) throws IOException
    {
        final String invokerClassname = "Invoker_" + Byte.toString(methodId) + "_" + descriptor.getName();
        writer.append("\tprivate static final class ").
                append(invokerClassname).
                append(" implements MethodInvoker<").append(interfaceName).
                append("> {\n");

        writer.append("\t\tpublic void invoke(final ").append(interfaceName).
                append(" implementation, final ByteBuffer buffer) {\n");

        appendParameters(descriptor.getParameterTypes(), writer);

        writer.append("\t\t\timplementation.").append(descriptor.getName()).append("(");
        final ParameterDescriptor[] parameterTypes = descriptor.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
        {
            final ParameterDescriptor param = parameterTypes[i];
            if (i != 0)
            {
                writer.append(", ");
            }

            writer.append(param.getName());
        }
        writer.append(");\n");

        writer.append("\t\t}\n");


        writer.append("\t}\n");

        generateInvokersMethodSource.append("\t\tinvokers[").append(methodId).
                append("] = new ").append(invokerClassname).
                append("();\n");
    }

    private void appendParameters(
            final ParameterDescriptor[] parameterTypes, final Writer writer) throws IOException
    {
        for (final ParameterDescriptor parameterType : parameterTypes)
        {
            writer.append("\t\t\tfinal ").append(parameterType.getTypeName()).
                    append(" ").append(parameterType.getName()).
                    append(" = Decoder.decode").append(toMethodSuffix(parameterType.getType().getSimpleName())).
                    append("(buffer);\n");
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