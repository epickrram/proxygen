package com.aitusoftware.proxygen.message;

import org.junit.Test;

import java.io.StringWriter;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MessageFlyweightGeneratorTest
{
    private static final String GENERATED_SOURCE =
            "package com.aitusoftware.example;\n" +
                    "\n" +
                    "import foo.example.Requirement;\n" +
                    "import com.aitusoftware.transport.messaging.proxy.Decoder;\n" +
                    "import com.aitusoftware.transport.messaging.Sized;\n" +
                    "import java.nio.ByteBuffer;\n" +
                    "\n" +
                    "\n" +
                    "public class OrderDetailsFlyweight implements OrderDetails, Sized {\n" +
                    "\n" +
                    "\tprivate ByteBuffer buffer;\n" +
                    "\tprivate int offset;\n" +
                    "\n" +
                    "\tpublic double getQuantity() {\n" +
                    "\t\treturn Decoder.decodeDoubleAt(buffer, offset + 0);\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic long orderId() {\n" +
                    "\t\treturn Decoder.decodeLongAt(buffer, offset + 8);\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic double price() {\n" +
                    "\t\treturn Decoder.decodeDoubleAt(buffer, offset + 16);\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic java.lang.CharSequence getDescriptor() {\n" +
                    "\t\treturn Decoder.decodeCharSequenceAt(buffer, offset + 24, descriptor);\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic java.lang.CharSequence getDescriptor2() {\n" +
                    "\t\treturn Decoder.decodeCharSequenceAt(buffer, offset + 24 + (getDescriptor().length() * 2) + 4, descriptor2);\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic OrderDetails heapCopy() {\n" +
                    "\t\tfinal OrderDetailsBuilder builder = new OrderDetailsBuilder();\n" +
                    "\t\tbuilder.orderId(orderId());\n" +
                    "\t\tbuilder.setQuantity(getQuantity());\n" +
                    "\t\tbuilder.price(price());\n" +
                    "\t\tbuilder.setDescriptor(getDescriptor());\n" +
                    "\t\tbuilder.setDescriptor2(getDescriptor2());\n" +
                    "\t\treturn builder;\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic void reset(final ByteBuffer buffer) {\n" +
                    "\t\tthis.buffer = buffer;\n" +
                    "\t\tthis.offset = buffer.position();\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic int length() {\n" +
                    "\t\t return 8 + 8 + 8 + (getDescriptor().length() * 2) + 4 + (getDescriptor2().length() * 2) + 4 + 0;\n" +
                    "\t}\n" +
                    "\n" +
                    "\tprivate final StringBuilder descriptor = new StringBuilder();\n" +
                    "\tprivate final StringBuilder descriptor2 = new StringBuilder();\n" +
                    "}";
        /*
    public interface OrderDetails extends Copyable<OrderDetails>
{
    long orderId();
    double getQuantity();
    double price();
}
     */

    @Test
    public void shouldGenerateFlyweight() throws Exception
    {
        final StringWriter writer = new StringWriter();
        new MessageFlyweightGenerator().
                generateFlyweight("com.aitusoftware.example",
                        "OrderDetailsFlyweight",
                        "OrderDetails",
                        Fixtures.METHODS,
                        Collections.singletonList("foo.example.Requirement"),
                        writer);

        assertThat(writer.toString(), is(GENERATED_SOURCE));
    }
}