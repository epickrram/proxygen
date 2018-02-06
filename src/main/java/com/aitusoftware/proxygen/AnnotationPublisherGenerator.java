package com.aitusoftware.proxygen;


import com.aitusoftware.proxygen.common.MethodDescriptor;
import com.aitusoftware.proxygen.common.ParameterDescriptor;
import com.aitusoftware.proxygen.publisher.AddressSpaceGenerator;
import com.aitusoftware.proxygen.publisher.NetAddress;
import com.aitusoftware.proxygen.publisher.ProxyClassnames;
import com.aitusoftware.proxygen.publisher.PublisherGenerator;
import com.aitusoftware.proxygen.publisher.SubscriberGenerator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("com.aitusoftware.transport.messaging.Topic")
@SupportedSourceVersion(SourceVersion.RELEASE_9)
public final class AnnotationPublisherGenerator extends AbstractProcessor
{
    private static final String TOPIC_ANNOTATION_CLASS = "com.aitusoftware.transport.messaging.Topic";
    private final PublisherGenerator publisherGenerator = new PublisherGenerator();
    private final SubscriberGenerator subscriberGenerator = new SubscriberGenerator();
    private final Set<String> generated = new HashSet<>();
    private final Map<String, NetAddress> addressSpace = new HashMap<>();
    private boolean generatedAddressSpace = false;

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
    {
        final TypeElement typeElement = processingEnv.getElementUtils().getTypeElement(TOPIC_ANNOTATION_CLASS);
        final Set<? extends Element> topicElements = roundEnv.getElementsAnnotatedWith(typeElement);
        for (Element topicElement : topicElements)
        {
            if (topicElement.getKind() == ElementKind.INTERFACE)
            {
                final List<? extends AnnotationMirror> annotationMirrors = topicElement.getAnnotationMirrors();
                for (AnnotationMirror annotationMirror : annotationMirrors)
                {
                    final Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
                    String listenAddr = "0.0.0.0";
                    int port = -1;
                    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet())
                    {
                        if (entry.getKey().getSimpleName().contentEquals("listenAddress"))
                        {
                            listenAddr = entry.getValue().getValue().toString();
                        }
                        if (entry.getKey().getSimpleName().contentEquals("port"))
                        {
                            port = Integer.parseInt(entry.getValue().getValue().toString());
                        }
                    }

                    addressSpace.put(topicElement.asType().toString(),
                            new NetAddress(listenAddr, port));
                }

                final Name className = topicElement.getSimpleName();
                final Name packageName = processingEnv.getElementUtils().getPackageOf(topicElement).getQualifiedName();
                final String identifier = packageName.toString() + "." + className.toString();
                if (generated.contains(identifier))
                {
                    continue;
                }
                generated.add(identifier);
                final List<? extends Element> enclosedElements = topicElement.getEnclosedElements();
                final List<MethodDescriptor> methods = new ArrayList<>();
                int i = 0;

                for (Element enclosedElement : enclosedElements)
                {
                    if (enclosedElement.getKind() == ElementKind.METHOD)
                    {

                        ExecutableElement methodElement = (ExecutableElement) enclosedElement;
                        List<? extends VariableElement> params = methodElement.getParameters();
                        final List<ParameterDescriptor> parameters = new ArrayList<>();

                        for (VariableElement param : params)
                        {
                            final Name parameterName = param.getSimpleName();
                            final TypeMirror parameterType = param.asType();
                            parameters.add(new ParameterDescriptor(parameterName.toString(),
                                    null, parameterType.toString()));
                        }

                        methods.add(new MethodDescriptor(i++, enclosedElement.getSimpleName().toString(),
                                parameters.toArray(new ParameterDescriptor[parameters.size()])));
                    }
                }

                try
                {
                    final String publisherClassname = ProxyClassnames.toPublisher(className.toString());
                    final String subscriberClassname = ProxyClassnames.toSubscriber(className.toString());

                    final JavaFileObject publisherSourceFile = processingEnv.getFiler().createSourceFile(packageName + "." + publisherClassname, topicElement);
                    final Writer publisherWriter = publisherSourceFile.openWriter();
                    publisherGenerator.generatePublisher(packageName.toString(), publisherClassname,
                            className.toString(),
                            methods.toArray(new MethodDescriptor[methods.size()]),
                            Collections.singletonList(packageName + "." + className), publisherWriter);
                    publisherWriter.close();

                    final JavaFileObject subscriberSourceFile = processingEnv.getFiler().createSourceFile(packageName + "." + subscriberClassname, topicElement);
                    final Writer subscriberWriter = subscriberSourceFile.openWriter();
                    subscriberGenerator.generateSubscriber(packageName.toString(), subscriberClassname,
                            className.toString(),
                            methods.toArray(new MethodDescriptor[methods.size()]),
                            Collections.singletonList(packageName + "." + className), subscriberWriter);
                    subscriberWriter.close();


                }
                catch (IOException e)
                {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                            "Could not create source file: " + e.getMessage(), topicElement);
                }
            }
            else
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "@Topic should only be used on interfaces");
            }
        }

        if (!addressSpace.isEmpty() && !generatedAddressSpace)
        {
            generatedAddressSpace = true;
            try
            {
                final String packageName = PackageNames.getTopLevelPackage(addressSpace.keySet());
                final String fileName = packageName + "." + AddressSpaceGenerator.GENERATED_CLASS_NAME;
                final JavaFileObject addressSpaceSourceFile = processingEnv.getFiler().
                        createSourceFile(fileName);

                try(final Writer addressSpaceWriter = addressSpaceSourceFile.openWriter())
                {
                    new AddressSpaceGenerator().generateAddressSpace(packageName, addressSpace, addressSpaceWriter);
                }

            }
            catch (IOException e)
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "Could not create source file: " + e.getMessage(), null);
            }
        }

        return false;
    }
}
