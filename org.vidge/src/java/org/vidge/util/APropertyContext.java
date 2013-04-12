package org.vidge.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface APropertyContext {

	boolean doAverage() default true;

	boolean doTotal() default true;

	ContextProperty doAs() default ContextProperty.BIGDECIMAL;
}
