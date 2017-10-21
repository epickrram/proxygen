package com.aitusoftware.proxygen;

import com.aitusoftware.proxygen.common.MethodDescriptor;
import com.aitusoftware.proxygen.common.ParameterDescriptor;
import com.aitusoftware.proxygen.publisher.SubscriberGenerator;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SubscriberGeneratorTest
{
    private static final String EXPECTED_SOURCE =
            "package com.package;\n" +
                    "\n" +
                    "import com.aitusoftware.transport.messaging.proxy.AbstractSubscriber;\n" +
                    "import com.aitusoftware.transport.messaging.proxy.MethodInvoker;\n" +
                    "import com.aitusoftware.transport.reader.RecordHandler;\n" +
                    "import com.aitusoftware.transport.buffer.PageCache;\n" +
                    "import com.aitusoftware.transport.messaging.proxy.Decoder;\n" +
                    "import java.nio.ByteBuffer;\n" +
                    "\n" +
                    "\n" +
                    "public class TestSubscriberImpl extends AbstractSubscriber<TestSubscriber> {\n" +
                    "\n" +
                    "\tpublic TestSubscriberImpl(final TestSubscriber implementation) {\n" +
                    "\t\tsuper(implementation, generateInvokers());\n" +
                    "\t}\n" +
                    "\n" +
                    "\tprivate static final class Invoker_0_say implements MethodInvoker<TestSubscriber> {\n" +
                    "\t\tpublic void invoke(final TestSubscriber implementation, final ByteBuffer buffer) {\n" +
                    "\t\t\tfinal int count = Decoder.decodeInt(buffer);\n" +
                    "\t\t\tfinal StringBuilder csq_0 = CACHED_CSQ_0.get();\n" +
                    "\t\t\tfinal java.lang.CharSequence word = Decoder.decodeCharSequence(buffer, csq_0);\n" +
                    "\t\t\timplementation.say(word, count);\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t}\n" +
                    "\tprivate static final class Invoker_1_composite implements MethodInvoker<TestSubscriber> {\n" +
                    "\t\tpublic void invoke(final TestSubscriber implementation, final ByteBuffer buffer) {\n" +
                    "\t\t\tfinal StringBuilder csq_0 = CACHED_CSQ_0.get();\n" +
                    "\t\t\tfinal java.lang.CharSequence word = Decoder.decodeCharSequence(buffer, csq_0);\n" +
                    "\t\t\torderDetailsFlyweight_1.reset(buffer);\n" +
                    "\t\t\tbuffer.position(buffer.position() + orderDetailsFlyweight_1.length());\n" +
                    "\t\t\tfinal com.example.OrderDetails orderDetails = orderDetailsFlyweight_1;\n" +
                    "\t\t\torderDetailsFlyweight_2.reset(buffer);\n" +
                    "\t\t\tbuffer.position(buffer.position() + orderDetailsFlyweight_2.length());\n" +
                    "\t\t\tfinal com.example.OrderDetails moreOrderDetails = orderDetailsFlyweight_2;\n" +
                    "\t\t\timplementation.composite(word, orderDetails, moreOrderDetails);\n" +
                    "\t\t}\n" +
                    "\n" +
                    "\t\tprivate final com.example.OrderDetailsFlyweight orderDetailsFlyweight_1 = new com.example.OrderDetailsFlyweight();\n" +
                    "\t\tprivate final com.example.OrderDetailsFlyweight orderDetailsFlyweight_2 = new com.example.OrderDetailsFlyweight();\n" +
                    "\t}\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\tprivate static MethodInvoker[] generateInvokers() {\n" +
                    "\t\tfinal MethodInvoker[] invokers = new MethodInvoker[2];\n" +
                    "\t\tinvokers[0] = new Invoker_0_say();\n" +
                    "\t\tinvokers[1] = new Invoker_1_composite();\n" +
                    "\t\treturn invokers;\n" +
                    "\t}\n" +
                    "\n" +
                    "\n" +
                    "\tprivate static final ThreadLocal<StringBuilder> CACHED_CSQ_0 = ThreadLocal.withInitial(StringBuilder::new);\n" +
                    "}\n";

    private final SubscriberGenerator generator = new SubscriberGenerator();

    @Test
    public void shouldGenerateSubscriberImplementation() throws Exception
    {
        final StringWriter writer = new StringWriter();
        generator.generateSubscriber(
                "com.package",
                "TestSubscriberImpl",
                "TestSubscriber",
                new MethodDescriptor[]{
                        new MethodDescriptor(0, "say",
                                new ParameterDescriptor[]{
                                        new ParameterDescriptor("word", CharSequence.class, "java.lang.CharSequence"),
                                        new ParameterDescriptor("count", int.class, "int")
                                }),
                        new MethodDescriptor(0, "composite",
                                new ParameterDescriptor[]{
                                        new ParameterDescriptor("word", CharSequence.class, "java.lang.CharSequence"),
                                        new ParameterDescriptor("orderDetails", null, "com.example.OrderDetails"),
                                        new ParameterDescriptor("moreOrderDetails", null, "com.example.OrderDetails")
                                }),

                },
                Collections.emptyList(),
                writer);

        assertThat(writer.toString(), is(EXPECTED_SOURCE));
    }
}