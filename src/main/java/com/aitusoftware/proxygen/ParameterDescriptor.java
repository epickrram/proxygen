package com.aitusoftware.proxygen;

final class ParameterDescriptor
{
    private final String name;
    private Class<?> type;
    private final String typeName;

    ParameterDescriptor(final String name, final Class<?> type, final String typeName)
    {
        this.name = name;
        this.type = type;
        this.typeName = typeName;

        if (type == null)
        {
            switch (typeName)
            {
                case "int":
                    this.type = int.class;
                    break;
                case "long":
                    this.type = long.class;
                    break;
                case "byte":
                    this.type = byte.class;
                    break;
                case "short":
                    this.type = short.class;
                    break;
                case "char":
                    this.type = char.class;
                    break;
                case "boolean":
                    this.type = boolean.class;
                    break;
                case "double":
                    this.type = double.class;
                    break;
                case "float":
                    this.type = float.class;
                    break;
                case "java.lang.CharSequence":
                    this.type = CharSequence.class;
                    break;
                default:
                    break;
            }
        }
    }

    String getName()
    {
        return name;
    }

    Class<?> getType()
    {
        return type;
    }

    String getTypeName() {
        return typeName;
    }
}