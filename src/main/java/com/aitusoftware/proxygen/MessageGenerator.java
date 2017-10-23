package com.aitusoftware.proxygen;


import com.aitusoftware.proxygen.common.MethodDescriptor;
import com.aitusoftware.proxygen.common.ParameterDescriptor;
import com.aitusoftware.proxygen.message.MessageBuilderGenerator;
import com.aitusoftware.proxygen.message.MessageClassnames;
import com.aitusoftware.proxygen.message.MessageFlyweightGenerator;
import com.aitusoftware.proxygen.message.MessageSerialiserGenerator;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("com.aitusoftware.transport.messaging.Message")
@SupportedSourceVersion(SourceVersion.RELEASE_9)
public final class MessageGenerator extends AbstractProcessor
{
    private static final String MESSAGE_ANNOTATION_CLASS = "com.aitusoftware.transport.messaging.Message";
    private final Set<String> generated = new HashSet<>();

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
    {
        final TypeElement typeElement = processingEnv.getElementUtils().getTypeElement(MESSAGE_ANNOTATION_CLASS);
        final Set<? extends Element> topicElements = roundEnv.getElementsAnnotatedWith(typeElement);
        for (Element messageElement : topicElements)
        {
            if (messageElement.getKind() == ElementKind.INTERFACE)
            {
                final Name className = messageElement.getSimpleName();
                final Name packageName = processingEnv.getElementUtils().getPackageOf(messageElement).getQualifiedName();
                final String identifier = packageName.toString() + "." + className.toString();
                if (generated.contains(identifier))
                {
                    continue;
                }
                generated.add(identifier);
                final List<? extends Element> enclosedElements = messageElement.getEnclosedElements();
                final List<MethodDescriptor> methods = new ArrayList<>();
                int i = 0;

                for (Element enclosedElement : enclosedElements)
                {
                    if (enclosedElement.getKind() == ElementKind.METHOD)
                    {

                        ExecutableElement methodElement = (ExecutableElement) enclosedElement;
                        List<? extends VariableElement> params = methodElement.getParameters();
                        final List<ParameterDescriptor> parameters = new ArrayList<>();

                        if (!params.isEmpty())
                        {
                            processingEnv.getMessager().
                                    printMessage(Diagnostic.Kind.ERROR,
                                            "All methods on @Message must be no-arg non-void-returning methods",
                                            messageElement);
                        }

                        final TypeMirror returnType = methodElement.getReturnType();
                        methods.add(new MethodDescriptor(i++, enclosedElement.getSimpleName().toString(),
                                parameters.toArray(new ParameterDescriptor[parameters.size()]),
                                new ParameterDescriptor(methodElement.getSimpleName().toString(),
                                        null, returnType.toString())));
                    }
                }

                try
                {
                    final String builderClassname = MessageClassnames.toBuilder(className.toString());
                    final String flyweightClassname = MessageClassnames.toFlyweight(className.toString());
                    final String serialiserClassname = MessageClassnames.toSerialiser(className.toString());

                    final JavaFileObject builderSourceFile = processingEnv.getFiler().createSourceFile(packageName + "." + builderClassname, messageElement);
                    final Writer builderWriter = builderSourceFile.openWriter();
                    new MessageBuilderGenerator().generateMessageBuilder(packageName.toString(), builderClassname,
                            className.toString(),
                            methods.toArray(new MethodDescriptor[methods.size()]),
                            Collections.singletonList(packageName + "." + className), builderWriter);
                    builderWriter.close();

                    final JavaFileObject flyweightSourceFile = processingEnv.getFiler().createSourceFile(packageName + "." + flyweightClassname, messageElement);
                    final Writer flyweightWriter = flyweightSourceFile.openWriter();
                    new MessageFlyweightGenerator().generateFlyweight(packageName.toString(), flyweightClassname,
                            className.toString(),
                            methods.toArray(new MethodDescriptor[methods.size()]),
                            Collections.singletonList(packageName + "." + className), flyweightWriter);
                    flyweightWriter.close();

                    final JavaFileObject serialiserSourceFile = processingEnv.getFiler().createSourceFile(packageName + "." + serialiserClassname, messageElement);
                    final Writer serialiserWriter = serialiserSourceFile.openWriter();
                    new MessageSerialiserGenerator().generateSerialiser(packageName.toString(), serialiserClassname,
                            className.toString(),
                            methods.toArray(new MethodDescriptor[methods.size()]),
                            Collections.singletonList(packageName + "." + className), serialiserWriter);
                    serialiserWriter.close();
                }
                catch (IOException e)
                {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                            "Could not create source file: " + e.getMessage(), messageElement);
                }
            }
            else
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "@Message should only be used on interfaces");
            }
        }

        return false;
    }
}