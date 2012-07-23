package org.vidge.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FormContextRule {

	boolean create() default true;

	boolean edit() default true;

	boolean remove() default true;

	boolean refresh() default true;
}
