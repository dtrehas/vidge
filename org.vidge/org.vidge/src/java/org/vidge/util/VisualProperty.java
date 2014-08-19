package org.vidge.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface VisualProperty {

	/**
	 * Purposed for custom selection of message from file - if you where not pleased of metod name - this is a key for searching a value into properties file with postfix 'label'
	 * 
	 * @return
	 */
	String label() default StringUtil.NN;

	VisualControlType control() default VisualControlType.DEFAULT;

	double order() default 9;

	boolean embedded() default false;

	boolean required() default false;

	boolean name_visible() default true;

	boolean table_editor() default false;

	/**
	 * Purposed for custom selection of message from file - if you where not pleased of metod name - this is a key for searching a value into properties file with postfix 'description'
	 * 
	 * @return
	 */
	String description() default StringUtil.NN;

	int width() default -1;

	int height() default -1;

	int imageWidth() default 16;

	int imageHeight() default 16;
}
