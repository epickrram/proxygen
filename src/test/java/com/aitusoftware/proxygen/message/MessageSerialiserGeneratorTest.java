package com.aitusoftware.proxygen.message;

import com.aitusoftware.proxygen.common.MethodDescriptor;
import com.aitusoftware.proxygen.common.ParameterDescriptor;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MessageSerialiserGeneratorTest
{
    private static final String GENERATED_SOURCE =
            "package com.aitusoftware.example;\n" +
                    "\n" +
                    "import foo.example.Requirement;\n" +
                    "import com.aitusoftware.transport.messaging.proxy.Encoder;\n" +
                    "import java.nio.ByteBuffer;\n" +
                    "\n" +
                    "\n" +
                    "public class OrderDetailsFlyweight {\n" +
                    "\n" +
                    "\tpublic void serialise(final OrderDetails _instance, final ByteBuffer buffer) {\n" +
                    "\t\tEncoder.encodeLong(buffer, _instance.orderId());\n" +
                    "\t\tEncoder.encodeDouble(buffer, _instance.getQuantity());\n" +
                    "\t\tEncoder.encodeDouble(buffer, _instance.price());\n" +
                    "\t}\n" +
                    "}";

    @Test
    public void shouldGenerateSerialiser() throws Exception
    {
        final MethodDescriptor[] methods = new MethodDescriptor[]
                {
                        new MethodDescriptor(0, "orderId", new ParameterDescriptor[0],
                                new ParameterDescriptor(null, long.class, "long")),
                        new MethodDescriptor(0, "getQuantity", new ParameterDescriptor[0],
                                new ParameterDescriptor(null, double.class, "double")),
                        new MethodDescriptor(0, "price", new ParameterDescriptor[0],
                                new ParameterDescriptor(null, double.class, "double")),
                };
        final StringWriter writer = new StringWriter();
        new MessageSerialiserGenerator().
                generateSerialiser("com.aitusoftware.example",
                        "OrderDetailsFlyweight",
                        "OrderDetails",
                        methods,
                        Collections.singletonList("foo.example.Requirement"),
                        writer);

        assertThat(writer.toString(), is(GENERATED_SOURCE));
    }
}