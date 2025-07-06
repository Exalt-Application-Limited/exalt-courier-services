package com.gogidix.courier.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking methods that should be traced with custom span names.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Traced {
    /**
     * The name of the span to create.
     * If not specified, defaults to className.methodName
     */
    String value() default "";
    
    /**
     * Whether to include method parameters as tags in the span.
     */
    boolean includeParams() default true;
}