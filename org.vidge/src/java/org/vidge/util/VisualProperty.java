package org.vidge.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface VisualProperty {

	/**
	 * Purposed for custom selection of message from file - if you where not pleased of metod name
	 * 
	 * @return
	 */
	String label() default "";

	VisualControlType control() default VisualControlType.DEFAULT;

	int order() default 9;

	boolean embedded() default false;

	boolean required() default false;

	boolean name_visible() default true;

	String description() default "";

	int width() default 48;

	int height() default 48;

	int imageWidth() default 48;

	int imageHeight() default 48;
}
