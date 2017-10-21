package com.aitusoftware.proxygen.message;

import com.aitusoftware.proxygen.common.Constants;
import com.aitusoftware.proxygen.common.MethodDescriptor;
import com.aitusoftware.proxygen.common.Types;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import static com.aitusoftware.proxygen.common.Types.getPrimitiveTypeSize;
import static com.aitusoftware.proxygen.common.Types.typeNameToType;

public final class MessageBuilderGenerator
{
    private static final List<String> REQUIRED_IMPORTS = Arrays.asList(
            Constants.SIZED_INTERFACE
    );

    public void generateMessageBuilder(
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
            final StringBuilder fieldBuilder = new StringBuilder();
            final StringBuilder resetBuilder = new StringBuilder();
            resetBuilder.append("\tpublic void reset() {\n");

            final StringBuilder lengthBuilder = new StringBuilder();
            lengthBuilder.append("\tpublic int length() {\n").
                    append("\t\t return ");
            for (MethodDescriptor method : methods)
            {
                final MethodTranslator translator = new MethodTranslator(method.getName());

                final Class<?> returnType = typeNameToType(method.getReturnType().getTypeName());

                writer.append("\tpublic ").append(className).append(" ").
                        append(translator.setterName).append("(").append(method.getReturnType().getTypeName()).
                        append(" ").append(translator.fieldName).append(") {\n");

                if (Types.isPrimitive(returnType))
                {
                    lengthBuilder.append(getPrimitiveTypeSize(returnType)).
                            append(" + ");
                    fieldBuilder.append("\tprivate ").append(method.getReturnType().getTypeName()).
                            append(" ").append(translator.fieldName).append(";\n");
                    resetBuilder.append("\t\tthis.").append(translator.fieldName).append(" = ").
                            append(getResetValueForType(method.getReturnType().getTypeName())).
                            append(";\n");
                    writer.append("\t\tthis.").append(translator.fieldName).append(" = ").
                            append(translator.fieldName).append(";\n");
                    writer.append("\t\treturn this;\n");
                    writer.append("\t}\n\n");
                }
                else if(Types.isCharSequence(returnType))
                {
                    lengthBuilder.append("(").append(translator.fieldName).append(".length() * 2) + 4 + ");
                    fieldBuilder.append("\tprivate StringBuilder ").append(translator.fieldName).append(" = new StringBuilder();\n");
                    resetBuilder.append("\t\tthis.").append(translator.fieldName).append(".setLength(0);\n");
                    writer.append("\t\tthis.").append(translator.fieldName).append(".setLength(0);\n");
                    writer.append("\t\tthis.").append(translator.fieldName).append(".append(").
                            append(translator.fieldName).append(");\n");
                    writer.append("\t\treturn this;\n");
                    writer.append("\t}\n\n");
                }
                else
                {
                    throw new IllegalArgumentException("Unsupported type: " + method.getReturnType());
                }

                writer.append("\tpublic ").append(method.getReturnType().getTypeName()).append(" ").
                        append(method.getName()).append("() {\n");

                writer.append("\t\treturn this.").append(translator.fieldName).append(";\n");
                writer.append("\t}\n\n");
            }

            lengthBuilder.append("0;\n").append("\t}\n\n");
            resetBuilder.append("\t}\n\n");

            writer.append(fieldBuilder);
            writer.append("\n\n");

            writer.append(resetBuilder);
            writer.append(lengthBuilder);

            writer.append("}");
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }

    }

    private String getResetValueForType(final String typeName)
    {
        final Class<?> type = Types.typeNameToType(typeName);
        if (type == null)
        {
            return "null";
        }
        if (Types.isBoolean(type))
        {
            return "false";
        }
        else
        {
            return "0";
        }
    }
}