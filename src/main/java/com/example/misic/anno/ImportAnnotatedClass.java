package com.example.misic.anno;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.Data;

/**
 * @author rain
 */

@Data
public class ImportAnnotatedClass {
    private ProcessingEnvironment processingEnv;
    private TypeElement annotatedClass;

    public ImportAnnotatedClass(ProcessingEnvironment processingEnv, TypeElement element) {
        this.processingEnv = processingEnv;
        this.annotatedClass = element;
    }

    private Status validate() {
        if (!annotatedClass.getModifiers().contains(Modifier.PUBLIC)) {
            return Status.error("class %s is not public", annotatedClass.getQualifiedName().toString());
        }
        return Status.success();
    }

    public Status process() throws IOException {
        Status status = validate();
        if (!status.isOk()) {
            return status;
        }
        Import annotation = annotatedClass.getAnnotation(Import.class);
        ClazzType clazz = AnnotationValue.get(annotation::to);
        TypeElement element = processingEnv.getElementUtils().getTypeElement(clazz.getCanonicalName());
        do {
            PreNode preNode = element.getAnnotation(PreNode.class);
            if (preNode != null) {
                ClazzType preNodeValue = AnnotationValue.get(preNode::value);
                status = genCode(element, clazz, preNodeValue, annotatedClass.getSimpleName().toString(), preNode.branch());
                if (!status.isOk()) {
                    return status;
                }
                clazz = preNodeValue;
                element = processingEnv.getElementUtils().getTypeElement(clazz.getCanonicalName());
            } else {
                status = genCode(element, clazz, AnnotationValue.get(annotation::from), "", annotation.branchOfFrom());
                if (!status.isOk()) {
                    return status;
                }
                clazz = null;
            }
        } while (clazz != null);
        return Status.success();
    }

    private Status genCode(TypeElement typeElement, ClazzType supperClazz,
        ClazzType preNodeValue, String preNodeValueSuffix, String preNodeBranch) throws IOException {
        info(typeElement, "gen code ...");
        String preNodeName = preNodeValueSuffix.isEmpty() ? preNodeValue.getSimpleName()
            : preNodeValue.getSimpleName() + "$$" + preNodeValueSuffix;
        PackageElement preNodeackageElement = processingEnv.getElementUtils()
            .getPackageOf(processingEnv.getTypeUtils().asElement(preNodeValue.getTypeMirror()));
        ClassName preNode = ClassName.get(preNodeackageElement.getQualifiedName().toString(), preNodeName);
        AnnotationSpec annotationSpec = AnnotationSpec.builder(PreNode.class)
            .addMember("value", "$T.class", preNode)
            .addMember("branch", "$S", preNodeBranch)
            .build();

        TypeSpec classType = TypeSpec.classBuilder(typeElement.getSimpleName() + "$$" + annotatedClass.getSimpleName())
            .addModifiers(Modifier.PUBLIC)
            .superclass(TypeName.get(supperClazz.getTypeMirror()))
            .addAnnotation(annotationSpec)
            .build();

        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(typeElement);
        JavaFile javaFile = JavaFile.builder(packageElement.getQualifiedName().toString(), classType).build();
        JavaFileObject jfo = processingEnv.getFiler()
            .createSourceFile(typeElement.getQualifiedName() + "$$" + annotatedClass.getSimpleName());
        Writer writer = jfo.openWriter();
        javaFile.writeTo(writer);
        writer.close();
        info(typeElement, javaFile.toString());
        return Status.success();
    }

    private void info(Element e, String message) {
        processingEnv.getMessager().printMessage(Kind.NOTE, message, e);
    }

}
