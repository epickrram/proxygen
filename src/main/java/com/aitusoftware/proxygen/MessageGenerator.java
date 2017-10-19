package com.aitusoftware.proxygen;


import com.aitusoftware.proxygen.common.Constants;
import com.aitusoftware.proxygen.common.MethodDescriptor;
import com.aitusoftware.proxygen.common.ParameterDescriptor;
import com.aitusoftware.proxygen.message.FlyweightMessageGenerator;
import com.aitusoftware.proxygen.message.MessageBuilderGenerator;

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
        for (Element topicElement : topicElements)
        {
            if (topicElement.getKind() == ElementKind.INTERFACE)
            {
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

                        // TODO all methods should be no-arg
                        for (VariableElement param : params)
                        {
                            final Name parameterName = param.getSimpleName();
                            final TypeMirror parameterType = param.asType();
                            parameters.add(new ParameterDescriptor(parameterName.toString(),
                                    null, parameterType.toString()));
                        }

                        final TypeMirror returnType = methodElement.getReturnType();
                        methods.add(new MethodDescriptor(i++, enclosedElement.getSimpleName().toString(),
                                parameters.toArray(new ParameterDescriptor[parameters.size()]),
                                new ParameterDescriptor("returnValue",
                                        null, returnType.toString())));
                    }
                }

                try
                {
                    final String builderClassname = className.toString() + Constants.MESSAGE_BUILDER_SUFFIX;
                    final String flyweightClassname = className.toString() + Constants.MESSAGE_FLYWEIGHT_SUFFIX;

                    final JavaFileObject builderSourceFile = processingEnv.getFiler().createSourceFile(packageName + "." + builderClassname, topicElement);
                    final Writer builderWriter = builderSourceFile.openWriter();
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                            "pn: " + packageName + ", bc: " + builderClassname + ", cn: " + className +
                    "m: " + methods + ", bw: " + builderWriter, topicElement);
                    new MessageBuilderGenerator().generateMessageBuilder(packageName.toString(), builderClassname,
                            className.toString(),
                            methods.toArray(new MethodDescriptor[methods.size()]),
                            Collections.singletonList(packageName + "." + className), builderWriter, processingEnv.getMessager());
                    builderWriter.close();

                    final JavaFileObject flyweightSourceFile = processingEnv.getFiler().createSourceFile(packageName + "." + flyweightClassname, topicElement);
                    final Writer flyweightWriter = flyweightSourceFile.openWriter();
                    new FlyweightMessageGenerator().generateFlyweight(packageName.toString(), flyweightClassname,
                            className.toString(),
                            methods.toArray(new MethodDescriptor[methods.size()]),
                            Collections.singletonList(packageName + "." + className), flyweightWriter);
                    flyweightWriter.close();
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
                        "@Message should only be used on interfaces");
            }
        }

        return false;
    }

    private String getTopLevelPackage(final Set<String> classNames)
    {
        String classNameWithMostPackages = null;
        int maxPackageCount = 0;
        for (String className : classNames)
        {
            final int packages = className.split("\\.").length - 1;
            if (packages > maxPackageCount || classNameWithMostPackages == null)
            {
                maxPackageCount = packages;
                classNameWithMostPackages = className;
            }
        }

        final String[] packages = classNameWithMostPackages.split("\\.");
        final StringBuilder topLevelPackage = new StringBuilder(packages[0]);
        int ptr = 1;
        while (classNames.stream().
                filter(cn -> cn.startsWith(topLevelPackage.toString())).
                count() == classNames.size())
        {
            topLevelPackage.append(".").append(packages[ptr++]);
        }

        return topLevelPackage.toString();
    }
}
