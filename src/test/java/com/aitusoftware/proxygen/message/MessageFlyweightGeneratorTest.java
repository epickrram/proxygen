package com.aitusoftware.proxygen.message;

import com.aitusoftware.proxygen.common.MethodDescriptor;
import com.aitusoftware.proxygen.common.ParameterDescriptor;
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
                    "import com.aitusoftware.transport.messaging.proxy.Encoder;\n" +
                    "\n" +
                    "\n" +
                    "public class OrderDetailsFlyweight implements OrderDetails {\n" +
                    "\n" +
                    "\tprivate ByteBuffer buffer;\n" +
                    "\tprivate int offset;\n" +
                    "\n" +
                    "\tpublic long orderId() {\n" +
                    "\t\treturn Decoder.decodeLongAt(buffer, offset + 0);\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic double getQuantity() {\n" +
                    "\t\treturn Decoder.decodeDoubleAt(buffer, offset + 8);\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic double price() {\n" +
                    "\t\treturn Decoder.decodeDoubleAt(buffer, offset + 16);\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic void reset(final ByteBuffer buffer) {\n" +
                    "\t\tthis.buffer = buffer;\n" +
                    "\t\tthis.offset = buffer.position();\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic int length() {\n" +
                    "\t\t return 8 + 8 + 8 + 0;\n" +
                    "\t}\n" +
                    "\n" +
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
        new MessageFlyweightGenerator().
                generateFlyweight("com.aitusoftware.example",
                        "OrderDetailsFlyweight",
                        "OrderDetails",
                        methods,
                        Collections.singletonList("foo.example.Requirement"),
                        writer);

        assertThat(writer.toString(), is(GENERATED_SOURCE));
    }
}