package com.aitusoftware.proxygen.message;

import org.junit.Test;

import java.io.StringWriter;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MessageBuilderGeneratorTest
{
    private static final String GENERATED_SOURCE =
            "package com.aitusoftware.example;\n" +
                    "\n" +
                    "import foo.example.Requirement;\n" +
                    "\n" +
                    "\n" +
                    "public class OrderDetailsBuilder implements OrderDetails {\n" +
                    "\n" +
                    "\tpublic OrderDetailsBuilder orderId(long orderId) {\n" +
                    "\t\tthis.orderId = orderId;\n" +
                    "\t\treturn this;\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic long orderId() {\n" +
                    "\t\treturn this.orderId;\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic OrderDetailsBuilder setQuantity(double quantity) {\n" +
                    "\t\tthis.quantity = quantity;\n" +
                    "\t\treturn this;\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic double getQuantity() {\n" +
                    "\t\treturn this.quantity;\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic OrderDetailsBuilder price(double price) {\n" +
                    "\t\tthis.price = price;\n" +
                    "\t\treturn this;\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic double price() {\n" +
                    "\t\treturn this.price;\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic OrderDetailsBuilder setDescriptor(java.lang.CharSequence descriptor) {\n" +
                    "\t\tthis.descriptor.setLength(0);\n" +
                    "\t\tthis.descriptor.append(descriptor);\n" +
                    "\t\treturn this;\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic java.lang.CharSequence getDescriptor() {\n" +
                    "\t\treturn this.descriptor;\n" +
                    "\t}\n" +
                    "\n" +
                    "\tprivate long orderId;\n" +
                    "\tprivate double quantity;\n" +
                    "\tprivate double price;\n" +
                    "\tprivate StringBuilder descriptor = new StringBuilder();\n" +
                    "\n" +
                    "\n" +
                    "\tpublic void reset() {\n" +
                    "\t\tthis.orderId = 0;\n" +
                    "\t\tthis.quantity = 0;\n" +
                    "\t\tthis.price = 0;\n" +
                    "\t\tthis.descriptor.setLength(0);\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic int length() {\n" +
                    "\t\t return 8 + 8 + 8 + (descriptor.length() * 4) + 4 + 0;\n" +
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
    public void shouldGenerateBuilderClass() throws Exception
    {
        final StringWriter writer = new StringWriter();
        new MessageBuilderGenerator().
                generateMessageBuilder("com.aitusoftware.example",
                        "OrderDetailsBuilder",
                        "OrderDetails",
                        Fixtures.METHODS,
                        Collections.singletonList("foo.example.Requirement"),
                        writer);

        assertThat(writer.toString(), is(GENERATED_SOURCE));
    }
}