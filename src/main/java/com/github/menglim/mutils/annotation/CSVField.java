package com.github.menglim.mutils.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CSVField {

    String value() default "";

    int order() default 0;

    boolean ignore() default false;

    String formatDate() default "dd-MMM-yyyy HH:mm:ss";
}
