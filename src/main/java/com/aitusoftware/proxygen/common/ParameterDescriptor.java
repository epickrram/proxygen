package com.aitusoftware.proxygen.common;

public final class ParameterDescriptor
{
    private final String name;
    private Class<?> type;
    private final String typeName;

    public ParameterDescriptor(final String name, final Class<?> type, final String typeName)
    {
        this.name = name;
        this.type = type;
        this.typeName = typeName;

        if (type == null)
        {
            this.type = Types.typeNameToType(typeName);
        }
    }

    public String getName()
    {
        return name;
    }

    public Class<?> getType()
    {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public String toString()
    {
        return "ParameterDescriptor{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}