package org.vidge.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Let customize an automatic actions appearance and disability by example in the ListDialog
 * 
 * @author user
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FormContextRule {

	boolean create() default true;

	boolean edit() default true;

	boolean remove() default true;

	boolean refresh() default true;
}
