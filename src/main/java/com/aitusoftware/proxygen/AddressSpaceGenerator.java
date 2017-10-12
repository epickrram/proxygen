package com.aitusoftware.proxygen;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AddressSpaceGenerator
{
    private static final List<String> IMPORTS = Arrays.asList(
            Map.class.getName(),
            HashMap.class.getName(),
            "com.aitusoftware.transport.net.AddressSpace"
    );

    static final String GENERATED_CLASS_NAME = "StaticAddressSpace";

    void generateAddressSpace(
            final String packageName,
            final Map<String, NetAddress> addressSpace,
            final Writer writer) throws IOException
    {
        writer.append("package ").append(packageName).append(";\n\n");
        for (String classToImport : IMPORTS)
        {
            writer.append("import ").append(classToImport).append(";\n");
        }
        writer.append("\n\npublic final class " +
                GENERATED_CLASS_NAME +
                " implements com.aitusoftware.transport.net.AddressSpace {\n\n");
        writer.append("\tprivate final Map<String, String> classNameToHostMap = new HashMap<String, String>();\n");
        writer.append("\tprivate final Map<String, Integer> classNameToPortMap = new HashMap<String, Integer>();\n");
        writer.append("\n\n\tpublic StaticAddressSpace() {\n");
        for (Map.Entry<String, NetAddress> entry : addressSpace.entrySet())
        {
            writer.append("\t\tclassNameToHostMap.put(\"").append(entry.getKey()).
                    append("\", \"").append(entry.getValue().getHost()).append("\");\n");
            writer.append("\t\tclassNameToPortMap.put(\"").append(entry.getKey()).
                    append("\", ").append(Integer.toString(entry.getValue().getPort())).append(");\n");
        }
        writer.append("\t}\n");

        writer.append("\n\n\tpublic int portOf(final Class<?> topicClass) {\n").
                append("\t\treturn classNameToPortMap.get(topicClass.getName());\n").
                append("\t}\n");

        writer.append("\n\n\tpublic String hostOf(final Class<?> topicClass) {\n").
                append("\t\treturn classNameToHostMap.get(topicClass.getName());\n").
                append("\t}\n");

        writer.append("}");
    }
}
