package com.aitusoftware.proxygen.message;

import com.aitusoftware.proxygen.common.MethodDescriptor;
import com.aitusoftware.proxygen.common.ParameterDescriptor;
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
                    "\t\treturn this.orderId\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic OrderDetailsBuilder setQuantity(double quantity) {\n" +
                    "\t\tthis.quantity = quantity;\n" +
                    "\t\treturn this;\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic double getQuantity() {\n" +
                    "\t\treturn this.quantity\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic OrderDetailsBuilder price(double price) {\n" +
                    "\t\tthis.price = price;\n" +
                    "\t\treturn this;\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic double price() {\n" +
                    "\t\treturn this.price\n" +
                    "\t}\n" +
                    "\n" +
                    "\tprivate long orderId;\n" +
                    "\tprivate double quantity;\n" +
                    "\tprivate double price;\n" +
                    "\n" +
                    "\n" +
                    "\tpublic void reset() {\n" +
                    "\t\tthis.orderId = 0;\n" +
                    "\t\tthis.quantity = 0;\n" +
                    "\t\tthis.price = 0;\n" +
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
    public void shouldGenerateBuilderClass() throws Exception
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
        new MessageBuilderGenerator().
                generateMessageBuilder("com.aitusoftware.example",
                        "OrderDetailsBuilder",
                        "OrderDetails",
                        methods,
                        Collections.singletonList("foo.example.Requirement"),
                        writer, null);

        assertThat(writer.toString(), is(GENERATED_SOURCE));
    }
}