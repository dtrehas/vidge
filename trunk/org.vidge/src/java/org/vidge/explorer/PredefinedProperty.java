package org.vidge.explorer;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.vidge.inface.IForm;
import org.vidge.util.StringUtil;

public class PredefinedProperty extends AbstractPropertyExplorer {

	private final Method getter;
	private String propertyName;

	public PredefinedProperty(Object obj, Method method) {
		super(false);
		source = obj;
		getter = method;
		propertyName = getter.getName().substring(3);
	}

	@Override
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public Class<?> getPropertyClass() {
		return getter.getReturnType();
	}

	@Override
	public Object getValueInt() {
		if (((IForm) source).getInput() == null) {
			return null;
		}
		return invokeInternal(getter);
	}

	@Override
	public Object getValue(Object input) {
		((IForm) source).setInput(input);
		return invokeInternal(getter);
	}

	@Override
	public String getLabel() {
		if (label == null) {
			label = getMessage(propertyName, PROPERTY_LABEL_POSTFIX, StringUtil.capitalize(propertyName));
		}
		return label;
	}

	public String getDescription() {
		return description;
	}

	private String getMessage(String key, String postfix, String defaultValue) {
		if (StringUtil.isEmpty(key)) {
			key = source.getClass().getSimpleName() + DOT + propertyName + DOT + postfix;
		}
		String value = defaultValue;
		try {
			value = ResourceBundle.getBundle(source.getClass().getPackage().getName() + MESSAGES, Locale.getDefault(), source.getClass().getClassLoader()).getString(key);
		} catch (java.util.MissingResourceException e) {
			value = defaultValue;
		}
		return value;
	}

	@Override
	protected void setValueInt(Object value) {
	}

	private Object invokeInternal(Method method, Object... args) {
		if ((source != null) && (method != null)) {
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			try {
				return method.invoke(source, args);
			} catch (Exception e) {
				e.printStackTrace();
				Logger.getLogger(this.getClass().getName()).severe(e.getMessage());
			}
		}
		return null;
	}
}
