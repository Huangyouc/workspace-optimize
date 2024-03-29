package com.example.javalib.a;

import com.google.auto.service.AutoService;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({DemoProcessor.DEMO_ANNOTATION})
public class DemoProcessor extends AbstractProcessor{
    static final String DEMO_ANNOTATION = "com.example.processor.DemoAnnotation";
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return new DemoAnnotationProcessor().process(annotations,roundEnv,processingEnv);
    }
}
