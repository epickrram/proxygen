package com.aitusoftware.proxygen;

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
                    "\t\t\tfinal java.lang.CharSequence word = Decoder.decodeCharSequence(buffer);\n" +
                    "\t\t\tfinal int count = Decoder.decodeInt(buffer);\n" +
                    "\t\t\timplementation.say(word, count);\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\tprivate static MethodInvoker[] generateInvokers() {\n" +
                    "\t\tfinal MethodInvoker[] invokers = new MethodInvoker[1];\n" +
                    "\t\tinvokers[0] = new Invoker_0_say();\n" +
                    "\t\treturn invokers;\n" +
                    "\t}\n" +
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
                                })},
                Collections.emptyList(),
                writer);

        assertThat(writer.toString(), is(EXPECTED_SOURCE));
    }
}