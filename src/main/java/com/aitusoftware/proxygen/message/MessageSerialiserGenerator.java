package com.aitusoftware.proxygen.message;

import com.aitusoftware.proxygen.common.MethodDescriptor;
import com.aitusoftware.proxygen.common.MethodDescriptorByReturnTypeSorter;
import com.aitusoftware.proxygen.common.ParameterDescriptor;
import com.aitusoftware.proxygen.common.Types;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

public final class MessageSerialiserGenerator
{
    private static final List<String> REQUIRED_IMPORTS = Arrays.asList(
            "com.aitusoftware.transport.messaging.proxy.Encoder",
            "java.nio.ByteBuffer"
    );

    public void generateSerialiser(
            final String packageName, final String className,
            final String interfaceName,
            final MethodDescriptor[] methods, final List<String> imports,
            final Writer writer)
    {
        try
        {
            writer.append("package ").append(packageName).append(";\n\n");
            for (String _import : imports)
            {
                writer.append("import ").append(_import).append(";\n");
            }

            for (String _import : REQUIRED_IMPORTS)
            {
                writer.append("import ").append(_import).append(";\n");
            }

            writer.append("\n\npublic class ").append(className).
                    append(" {\n\n").
                    append("\tpublic static void serialise(final ").
                    append(interfaceName).append(" _instance, final ByteBuffer buffer) {\n");

            final MethodDescriptor[] copy = new MethodDescriptor[methods.length];
            System.arraycopy(methods, 0, copy, 0, copy.length);
            Arrays.sort(copy, MethodDescriptorByReturnTypeSorter.INSTANCE);
            for (MethodDescriptor method : copy)
            {
                final Class<?> returnType = Types.typeNameToType(method.getReturnType().getTypeName());
                if (Types.isPrimitive(returnType))
                {
                    writer.append("\t\t").append(bufferAccessFor(method.getReturnType())).
                            append("_instance.").append(method.getName()).append("());\n");
                }
                else if (Types.isCharSequence(returnType))
                {
                    writer.append("\t\t").append(bufferAccessFor(method.getReturnType())).
                            append("_instance.").append(method.getName()).append("());\n");
                }
            }

            writer.append("\t}\n");

            writer.append("}");
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }

    }

    private CharSequence bufferAccessFor(final ParameterDescriptor returnType)
    {
        return "Encoder.encode" + Types.toMethodSuffix(returnType.getTypeName()) + "(buffer, ";
    }

}
