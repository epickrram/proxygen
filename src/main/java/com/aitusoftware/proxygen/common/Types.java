package com.aitusoftware.proxygen.common;

public enum Types
{
    INSTANCE;

    public static boolean isByte(final Class<?> cls)
    {
        return is(cls, byte.class);
    }

    public static boolean isBoolean(final Class<?> cls)
    {
        return is(cls, boolean.class);
    }

    public static boolean isShort(final Class<?> cls)
    {
        return is(cls, short.class);
    }

    public static boolean isInt(final Class<?> cls)
    {
        return is(cls, int.class);
    }

    public static boolean isChar(final Class<?> cls)
    {
        return is(cls, char.class);
    }

    public static boolean isFloat(final Class<?> cls)
    {
        return is(cls, float.class);
    }

    public static boolean isLong(final Class<?> cls)
    {
        return is(cls, long.class);
    }

    public static boolean isDouble(final Class<?> cls)
    {
        return is(cls, double.class);
    }

    public static int getPrimitiveTypeSize(final Class<?> type)
    {
        if (isBoolean(type) || isByte(type))
        {
            return 1;
        }
        if (isShort(type))
        {
            return 2;
        }
        if (isInt(type) || isChar(type) || isFloat(type))
        {
            return 4;
        }
        if (isLong(type) || isDouble(type))
        {
            return 8;
        }

        throw new IllegalArgumentException(String.format(
                "Unsupported primitive type: %s", type.getName()));
    }

    private static boolean is(final Class<?> cls, final Class<?> refClass)
    {
        return cls == refClass;
    }

    public static Class<?> typeNameToType(final String typeName)
    {
        Class<?> tmp = null;
        switch (typeName)
        {
            case "int":
                tmp = int.class;
                break;
            case "long":
                tmp = long.class;
                break;
            case "byte":
                tmp = byte.class;
                break;
            case "short":
                tmp = short.class;
                break;
            case "char":
                tmp = char.class;
                break;
            case "boolean":
                tmp = boolean.class;
                break;
            case "double":
                tmp = double.class;
                break;
            case "float":
                tmp = float.class;
                break;
            case "java.lang.CharSequence":
                tmp = CharSequence.class;
                break;
            default:
                break;
        }

        return tmp;
    }

    public static String toMethodSuffix(final String name)
    {
        final char first = name.charAt(0);
        if (Character.isLowerCase(first))
        {
            return Character.toUpperCase(first) + name.substring(1);
        }
        return name;
    }
}
