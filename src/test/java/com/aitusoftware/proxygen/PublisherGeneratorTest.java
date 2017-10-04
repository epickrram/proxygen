package com.aitusoftware.proxygen;

import org.junit.Test;

import java.io.StringWriter;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PublisherGeneratorTest {
    private static final String EXPECTED_SOURCE =
            "package com.package;\n" +
                    "\n" +
                    "import com.aitusoftware.transport.messaging.proxy.AbstractPublisher;\n" +
                    "import com.aitusoftware.transport.buffer.WritableRecord;\n" +
                    "import com.aitusoftware.transport.buffer.PageCache;\n" +
                    "import com.aitusoftware.transport.messaging.proxy.Encoder;\n" +
                    "\n" +
                    "\n" +
                    "public class TestPublisher extends AbstractPublisher implements TestPublisherImpl {\n" +
                    "\n" +
                    "\tpublic TestPublisher(final PageCache pageCache) {\n" +
                    "\t\tsuper(pageCache);\n" +
                    "\t}\n" +
                    "\n" +
                    "\tpublic void say(\n" +
                    "\t\tfinal java.lang.CharSequence word, final int count) {\n" +
                    "\t\t\n" +
                    "\t\tfinal int recordLength = (word.length() * 4) + 4  +  4;\n" +
                    "\t\tfinal WritableRecord wr = acquireRecord(recordLength, (byte) 0);\n" +
                    "\t\tEncoder.encodeCharSequence(wr.buffer(), word);\n" +
                    "\t\tEncoder.encodeInt(wr.buffer(), count);\n" +
                    "\t\twr.commit();\n" +
                    "\t}\n" +
                    "\n" +
                    "}\n";

    private final PublisherGenerator generator = new PublisherGenerator();

    @Test
    public void shouldGeneratePublisherImplementation() throws Exception
    {
        final StringWriter writer = new StringWriter();
        generator.generatePublisher(
                "com.package", "TestPublisher",
                "TestPublisherImpl",
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