package com.aitusoftware.proxygen.message;

import com.aitusoftware.proxygen.common.Constants;
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

public final class MessageFlyweightGenerator
{
    private static final List<String> REQUIRED_IMPORTS = Arrays.asList(
            "com.aitusoftware.transport.messaging.proxy.Decoder",
            "com.aitusoftware.transport.messaging.Sized",
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
                    append(", ").append("Sized").
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

            final StringBuilder fieldBuilder = new StringBuilder();

            int paramOffset = 0;

            for (MethodDescriptor method : methods)
            {
                final MethodTranslator translator = new MethodTranslator(method.getName());
                final Class<?> returnType = typeNameToType(method.getReturnType().getTypeName());
                if (Types.isPrimitive(returnType))
                {
                    lengthBuilder.append(getPrimitiveTypeSize(returnType)).
                            append(" + ");
                    writer.append("\tpublic ").append(method.getReturnType().getTypeName()).append(" ").
                            append(method.getName()).append("() {\n");

                    writer.append("\t\treturn ").append(bufferAccessFor(method.getReturnType(), paramOffset)).append("\n");
                    writer.append("\t}\n\n");
                    paramOffset += Types.getPrimitiveTypeSize(returnType);
                }
                else if (Types.isCharSequence(returnType))
                {
                    writer.append("\tpublic ").append(method.getReturnType().getTypeName()).append(" ").
                            append(method.getName()).append("() {\n");

                    writer.append("\t\treturn ").append("Decoder.decode").
                            append(Types.toMethodSuffix(method.getReturnType().getTypeName())).
                            append("At(buffer, offset + ").append(String.valueOf(paramOffset)).
                            append(", ").append(translator.fieldName).append(");").append("\n");
                    writer.append("\t}\n\n");

                    fieldBuilder.append("\tprivate final StringBuilder ").append(translator.fieldName).
                            append(" = new StringBuilder();\n");
                    lengthBuilder.append("(").append(method.getName()).append("().length() * 2) + 4").
                            append(" + ");
                }
            }
            lengthBuilder.append("0;\n").append("\t}\n\n");

            writer.append("\tpublic ").append(interfaceName).append(" heapCopy() {\n");
            writer.append("\t\tfinal ").append(interfaceName).append(Constants.MESSAGE_BUILDER_SUFFIX);
            writer.append(" builder = new ").append(interfaceName).append(Constants.MESSAGE_BUILDER_SUFFIX).append("();\n");
            for (MethodDescriptor method : methods)
            {
                final MethodTranslator translator = new MethodTranslator(method.getName());
                writer.append("\t\tbuilder.").append(translator.setterName).append("(").
                        append(method.getName()).append("());\n");
            }
            writer.append("\t\treturn builder;\n");

            writer.append("\t}\n\n");

            writer.append(resetBuilder);
            writer.append(lengthBuilder);
            writer.append(fieldBuilder);

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
