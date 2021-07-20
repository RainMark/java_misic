package com.example.misic.anno;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.google.auto.service.AutoService;

/**
 * @author rain
 */

@AutoService(Processor.class)
public class ImportProcessor extends AbstractProcessor {
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(Import.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Import.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                error(element, Status.error("@Import just support class type"));
                return true;
            }
            ImportAnnotatedClass annotatedClass = new ImportAnnotatedClass(processingEnv, (TypeElement) element);
            Status status = null;
            try {
                status = annotatedClass.process();
            } catch (IOException e) {
                error(element, Status.error(e.getMessage()));
                return true;
            }
            if (!status.isOk()) {
                error(element, status);
                return true;
            }
        }
        return true;
    }

    private void error(Element e, Status status) {
        messager.printMessage(Kind.ERROR, status.getMessage(), e);
    }
}
