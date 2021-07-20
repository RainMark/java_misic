package com.example.misic.anno;

import java.util.function.Supplier;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;

/**
 * @author rain
 */
public class AnnotationValue {

    public static ClazzType get(Supplier<Class> supplier) {
        try {
            Class clazz = supplier.get();
            return ClazzType.builder()
                .canonicalName(clazz.getCanonicalName())
                .simpleName(clazz.getSimpleName())
                .build();
        } catch (MirroredTypesException e) {
            DeclaredType classTypeMirror = (DeclaredType) e.getTypeMirrors().get(0);
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            return  ClazzType.builder()
                .canonicalName(classTypeElement.getQualifiedName().toString())
                .simpleName(classTypeElement.getSimpleName().toString())
                .typeMirror(classTypeMirror)
                .build();
        }
    }
}
