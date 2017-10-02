package com.aitusoftware.proxygen;


import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
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
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("com.aitusoftware.transport.messaging.Topic")
@SupportedSourceVersion(SourceVersion.RELEASE_9)
public final class AnnotationPublisherGenerator extends AbstractProcessor
{
    private static final String TOPIC_ANNOTATION_CLASS = "com.aitusoftware.transport.messaging.Topic";
    private final PublisherGenerator generator = new PublisherGenerator();

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
    {
        final TypeElement typeElement = processingEnv.getElementUtils().getTypeElement(TOPIC_ANNOTATION_CLASS);
        final Set<? extends Element> topicElements = roundEnv.getElementsAnnotatedWith(typeElement);
        for (Element topicElement : topicElements)
        {
            if (topicElement.getKind() == ElementKind.INTERFACE)
            {
                final Name className = topicElement.getSimpleName();
                final Name packageName = processingEnv.getElementUtils().getPackageOf(topicElement).getQualifiedName();
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

                    try {
                        final String generatedClassname = className.toString() + Constants.PROXYGEN_PUBLISHER_SUFFIX;
                        final JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(packageName + "." + generatedClassname, topicElement);
                        final Writer writer = sourceFile.openWriter();
                        generator.generatePublisher(packageName.toString(), generatedClassname,
                                className.toString(),
                                methods.toArray(new MethodDescriptor[methods.size()]),
                                Collections.singletonList(packageName + "." + className), writer);
                        writer.close();
                    } catch (IOException e) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                "Could not create source file: " + e.getMessage(), topicElement);
                    }


                }

                return true;
            }
            else
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "@Topic should only be used on interfaces");
            }
        }
        return false;
    }
}
