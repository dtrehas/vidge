package org.vidge.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.langcom.ICompositeObject;
import org.langcom.locale.LocalizedString;
import org.vidge.FormRegistry;
import org.vidge.PlainForm;
import org.vidge.explorer.ObjectExplorer;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;

public class TypeUtil {

	private TypeUtil() {
	}

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	public static Object convertValue(Object valueIn, Class clazz) {
		Object value = valueIn;
		if (clazz.equals(int.class) && (value instanceof BigDecimal)) {
			value = ((BigDecimal) value).intValue();
		} else if (clazz.equals(BigDecimal.class) && (value instanceof String)) {
			value = new BigDecimal(value.toString());
		} else if (clazz.equals(String.class) && (value != null) && (!(value instanceof String))) {
			value = value.toString();
		} else if (clazz.equals(LocalizedString.class) && (value != null) && (value instanceof String)) {
			value = new LocalizedString((String) value);
		} else if (clazz.equals(Enum.class) && (value != null) && (value instanceof String)) {
			value = Enum.valueOf(clazz, value.toString().trim());
		} else if (clazz.equals(File.class) && (value != null) && (value instanceof String)) {
			value = new File((String) value);
		} else if (clazz.equals(String.class) && (value != null) && (value instanceof File)) {
			value = ((File) value).getPath();
		}
		return value;
	}

	@SuppressWarnings({
			"nls", "rawtypes"
	})
	public static Class getClass(String propertyClassName) {
		try {
			if (propertyClassName.equalsIgnoreCase("int")) {
				propertyClassName = Integer.class.getName();
			} else if (propertyClassName.equalsIgnoreCase("boolean")) {
				propertyClassName = Boolean.class.getName();
			} else if (propertyClassName.equalsIgnoreCase("long")) {
				propertyClassName = Long.class.getName();
			} else if (propertyClassName.equalsIgnoreCase("double")) {
				propertyClassName = Double.class.getName();
			} else if (propertyClassName.equalsIgnoreCase("float")) {
				propertyClassName = Float.class.getName();
			}
			return Class.forName(propertyClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static VisualControlType checkControlType(PlainForm plainForm, IPropertyExplorer explorer) {
		VisualControlType controlType = VisualControlType.TEXT;
		if (plainForm.isReadOnly() || explorer.isReadOnly()) {
			controlType = VisualControlType.LABEL;
		} else {
			if (explorer.getVisualControlType().equals(VisualControlType.DEFAULT)) {
				Class<?> propertyClass = explorer.getPropertyClass();
				if (!isTextControl(propertyClass)) {
					if ((propertyClass.equals(Boolean.class) || propertyClass.equals(boolean.class))) {
						controlType = VisualControlType.CHECKBOX;
					} else if ((propertyClass.equals(Date.class))) {
						controlType = VisualControlType.DATE;
					} else if ((propertyClass.equals(Color.class))) {
						controlType = VisualControlType.COLOR;
					} else if ((propertyClass.equals(Image.class))) {
						controlType = VisualControlType.IMAGE;
					} else if ((propertyClass.equals(Font.class))) {
						controlType = VisualControlType.FONT;
					} else if ((propertyClass.equals(File.class))) {
						controlType = VisualControlType.FILE;
					} else if (ICompositeObject.class.isAssignableFrom(propertyClass)) {
						controlType = VisualControlType.PART_LIST_EDITOR;
					} else if (propertyClass.isAssignableFrom(Collection.class)) {
						controlType = VisualControlType.OBJECT_EDITOR;
					} else if (propertyClass.isAssignableFrom(Map.class)) {
						controlType = VisualControlType.LABEL;
					} else if (propertyClass.isArray()) {
						controlType = VisualControlType.OBJECT_EDITOR;
					} else {
						if (explorer.getWizard() != null) {
							controlType = VisualControlType.OBJECT_WIZARD;
						} else {
							controlType = VisualControlType.OBJECT_EDITOR;
						}
					}
				} else {
					if (explorer.getValidValues() != null || propertyClass.isEnum()) {
						controlType = VisualControlType.COMBO;
					} else if (isNumeric(propertyClass)) {
						controlType = VisualControlType.NUMBER;
					}
				}
			} else {
				controlType = explorer.getVisualControlType();
			}
		}
		return controlType;
	}

	private static boolean isTextControl(Class<?> klass) {
		if (klass != null) {
			if (klass.equals(String.class) || klass.equals(Integer.class) || klass.equals(Long.class) || klass.equals(Character.class) || klass.equals(BigDecimal.class) || klass.equals(Double.class)
				|| klass.equals(Float.class) || klass.equals(BigInteger.class) || klass.equals(Byte.class) || klass.equals(int.class) || klass.equals(double.class) || klass.equals(float.class)
				|| klass.equals(long.class) || klass.equals(char.class) || klass.equals(byte.class) || klass.isEnum()) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNumeric(Class<?> klass) {
		if (klass != null) {
			if (klass.equals(Integer.class) || klass.equals(Long.class) || klass.equals(BigDecimal.class) || klass.equals(Double.class) || klass.equals(Float.class) || klass.equals(BigInteger.class)
				|| klass.equals(int.class) || klass.equals(double.class) || klass.equals(float.class) || klass.equals(long.class)) {
				return true;
			}
		}
		return false;
	}

	public static Class<?> resolveClass(Field field, Class<?> returnType) {
		return getType(field.getGenericType(), returnType);
	}

	public static Class<?> getType(Type type, Class<?> returnType) {
		Class<?> klazz = returnType;
		if (Collection.class.isAssignableFrom(returnType)) {
			klazz = getGenericClass(returnType, type);
		} else if (Map.class.isAssignableFrom(returnType)) {
			klazz = getGenericClass(returnType, type);
		} else if (returnType.isArray()) {
			klazz = returnType.getComponentType();
		}
		return klazz;
	}



	@SuppressWarnings("rawtypes")
	public static IEntityExplorer getExplorer(Class<?> inputClass, String context, IPropertyExplorer propertyExplorer) {
		IEntityExplorer entityExplorer = FormRegistry.getEntityExplorer(context, inputClass);
		if (entityExplorer == null && propertyExplorer != null) {
			Object value = propertyExplorer.getValue();
			if (value == null) {
				entityExplorer = new ObjectExplorer(instantiateClass(inputClass));
			} else {
				entityExplorer = new ObjectExplorer(instantiateClass(getType(propertyExplorer.getPropertyType(), inputClass)));
			}
		}
		return entityExplorer;
	}

	public static Object instantiateClass(Class<?> klass) {
		try {
			return klass.newInstance();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> resolveClass(Method method, Class<?> returnType) {
		return getType(method.getGenericReturnType(), returnType);
	}

	public static Class<?> getGenericClass(Class<?> clazz, Type genericReturnType) {
		String className = genericReturnType + "";
		if (!className.contains("?")) {
			if (className.contains("<") && className.contains(">")) {
				String substring = className.substring(className.indexOf("<") + 1, className.indexOf(">"));
				try {
					return Class.forName(substring.trim());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				className = className.replaceAll("class", "").replaceAll(";", "").trim();
				if (className.contains("[L")) {
					className = className.substring(2);
				}
				try {
					return Class.forName(className);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return clazz;
	}
}
