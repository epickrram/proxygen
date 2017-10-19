package com.aitusoftware.proxygen.message;

final class MethodTranslator
{
    final String setterName;
    final String fieldName;

    MethodTranslator(final String method)
    {
        if (method.startsWith("get"))
        {
            setterName = "set" + method.substring(3);
            final String suffix = method.substring(3);
            fieldName = Character.toLowerCase(suffix.charAt(0)) + suffix.substring(1);
        }
        else
        {
            setterName = method;
            fieldName = method;
        }

    }
}
