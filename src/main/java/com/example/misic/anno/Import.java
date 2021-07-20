package com.example.misic.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author rain
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Import {
    Class from();
    Class to();
    String branchOfFrom() default "";
    String branchOfTo() default "";
}
