package com.aitusoftware.proxygen.message;

import com.aitusoftware.proxygen.common.MethodDescriptor;
import com.aitusoftware.proxygen.common.ParameterDescriptor;
import com.aitusoftware.proxygen.common.Types;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import static com.aitusoftware.proxygen.common.Types.getPrimitiveTypeSize;
import static com.aitusoftware.proxygen.common.Types.typeNameToType;

public final class FlyweightMessageGenerator
{
    private static final List<String> REQUIRED_IMPORTS = Arrays.asList(
            "com.aitusoftware.transport.messaging.proxy.Decoder",
            "java.nio.ByteBuffer"
    );

    public void generateFlyweight(
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
                    append(" implements ").append(interfaceName).
                    append(" {\n\n");
            writer.append("\tprivate ByteBuffer buffer;\n");
            writer.append("\tprivate int offset;\n\n");
            final StringBuilder resetBuilder = new StringBuilder();
            resetBuilder.append("\tpublic void reset(final ByteBuffer buffer) {\n").
                    append("\t\tthis.buffer = buffer;\n").
                    append("\t\tthis.offset = buffer.position();\n").
                    append("\t}\n\n");

            final StringBuilder lengthBuilder = new StringBuilder();
            lengthBuilder.append("\tpublic int length() {\n").
                    append("\t\t return ");


            int paramOffset = 0;

            for (MethodDescriptor method : methods)
            {
                lengthBuilder.append(getPrimitiveTypeSize(typeNameToType(method.getReturnType().getTypeName()))).
                        append(" + ");
                final Class<?> returnType = Types.typeNameToType(method.getReturnType().getTypeName());
                writer.append("\tpublic ").append(method.getReturnType().getTypeName()).append(" ").
                        append(method.getName()).append("() {\n");

                writer.append("\t\treturn ").append(bufferAccessFor(method.getReturnType(), paramOffset)).append("\n");
                writer.append("\t}\n\n");
                paramOffset += Types.getPrimitiveTypeSize(returnType);
            }
            lengthBuilder.append("0;\n").append("\t}\n\n");


            // TODO add heapCopy method

            writer.append(resetBuilder);
            writer.append(lengthBuilder);

            writer.append("}");
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }

    }

    private CharSequence bufferAccessFor(final ParameterDescriptor returnType, final int bufferOffset)
    {
        return "Decoder.decode" + Types.toMethodSuffix(returnType.getTypeName()) + "At(buffer, offset + " + bufferOffset + ");";
    }
}
