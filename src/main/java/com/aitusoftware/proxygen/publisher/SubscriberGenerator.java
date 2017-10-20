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

public final class SubscriberGenerator
{
    private static final List<String> REQUIRED_IMPORTS = Arrays.asList(
            "com.aitusoftware.transport.messaging.proxy.AbstractSubscriber",
            "com.aitusoftware.transport.messaging.proxy.MethodInvoker",
            "com.aitusoftware.transport.reader.RecordHandler",
            "com.aitusoftware.transport.buffer.PageCache",
            "com.aitusoftware.transport.messaging.proxy.Decoder",
            "java.nio.ByteBuffer"
    );

    private int requiredNumberOfStringBuilders = 0;
    private int maxRequiredStringBuilders = 0;

    public void generateSubscriber(
            final String packageName, final String className, final String interfaceName,
            final MethodDescriptor[] methods, final List<String> imports,
            final Writer writer)
    {
        maxRequiredStringBuilders = 0;
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

            final StringBuilder threadLocalStringBuilderSource = new StringBuilder();

            for (MethodDescriptor descriptor : methods)
            {
                requiredNumberOfStringBuilders = 0;
                appendInvoker(descriptor, methodId, generateInvokersMethodSource,
                        threadLocalStringBuilderSource,
                        interfaceName, writer);
                maxRequiredStringBuilders = Math.max(maxRequiredStringBuilders, requiredNumberOfStringBuilders);
                methodId++;
            }

            generateInvokersMethodSource.append("\t\treturn invokers;\n").
                    append("\t}\n");

            writer.append("\n\n").append(generateInvokersMethodSource);

            for (int i = 0; i < maxRequiredStringBuilders; i++)
            {
                writer.append("\tprivate static final ThreadLocal<StringBuilder> CACHED_CSQ_").
                        append(Integer.toString(i)).append(" = ThreadLocal.withInitial(StringBuilder::new);\n");
            }

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
            final StringBuilder generateInvokersMethodSource,
            final StringBuilder threadLocalStringBuilderSource,
            final String interfaceName, final Writer writer) throws IOException
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
        final ParameterDescriptor[] copy = new ParameterDescriptor[parameterTypes.length];
        System.arraycopy(parameterTypes, 0, copy, 0, copy.length);
        Arrays.sort(copy, ParameterDescriptorSorter.INSTANCE);
        for (final ParameterDescriptor parameterType : copy)
        {

            if (Types.isCharSequence(parameterType.getType()))
            {
                requiredNumberOfStringBuilders++;

                final String variableSuffix = Integer.toString(requiredNumberOfStringBuilders - 1);
                writer.append("\t\t\t").append("final StringBuilder csq_").
                        append(variableSuffix).
                        append(" = CACHED_CSQ_").append(variableSuffix).
                        append(".get();\n");
                writer.append("\t\t\tfinal ").append(parameterType.getTypeName()).
                        append(" ").append(parameterType.getName()).
                        append(" = Decoder.decodeCharSequence").
                        append("(buffer, csq_").append(variableSuffix).append(");\n");

            }
            else if (Types.isPrimitive(parameterType.getType()))
            {
                final String methodSuffix = toMethodSuffix(parameterType.getType().getSimpleName());
                writer.append("\t\t\tfinal ").append(parameterType.getTypeName()).
                        append(" ").append(parameterType.getName()).
                        append(" = Decoder.decode").append(methodSuffix).
                        append("(buffer);\n");
            }
            else
            {
                // TODO need to assign buffer position for each flyweight/CharSequence
                writer.append("\t\t\tfinal ").append(parameterType.getTypeName()).
                        append(Constants.MESSAGE_FLYWEIGHT_SUFFIX).append(" ").
                        append(parameterType.getName()).append(" = new ").
                        append(parameterType.getTypeName()).
                        append(Constants.MESSAGE_FLYWEIGHT_SUFFIX).append("();\n");
                writer.append("\t\t\t").append(parameterType.getName()).
                        append(".reset(buffer);\n");
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