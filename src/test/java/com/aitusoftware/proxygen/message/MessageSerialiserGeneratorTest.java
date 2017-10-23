package com.aitusoftware.proxygen.message;

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
                    "\tpublic static void serialise(final OrderDetails _instance, final ByteBuffer buffer) {\n" +
                    "\t\tEncoder.encodeLong(buffer, _instance.orderId());\n" +
                    "\t\tEncoder.encodeDouble(buffer, _instance.getQuantity());\n" +
                    "\t\tEncoder.encodeDouble(buffer, _instance.price());\n" +
                    "\t\tEncoder.encodeCharSequence(buffer, _instance.getDescriptor());\n" +
                    "\t\tEncoder.encodeCharSequence(buffer, _instance.getDescriptor2());\n" +
                    "\t}\n" +
                    "}";

    @Test
    public void shouldGenerateSerialiser() throws Exception
    {
        final StringWriter writer = new StringWriter();
        new MessageSerialiserGenerator().
                generateSerialiser("com.aitusoftware.example",
                        "OrderDetailsFlyweight",
                        "OrderDetails",
                        Fixtures.METHODS,
                        Collections.singletonList("foo.example.Requirement"),
                        writer);

        assertThat(writer.toString(), is(GENERATED_SOURCE));
    }
}