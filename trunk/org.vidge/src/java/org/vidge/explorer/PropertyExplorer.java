package org.vidge.explorer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.vidge.controls.tree.hierarchy.IHierarchyProvider;
import org.vidge.form.validator.IValidator;
import org.vidge.form.validator.ReadOnlyValidator;
import org.vidge.form.validator.RequiredValidator;
import org.vidge.form.validator.ValidatorRegistry;
import org.vidge.inface.IForm;
import org.vidge.inface.IObjectWizard;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.StringUtil;
import org.vidge.util.TypeUtil;
import org.vidge.util.VisualControlType;
import org.vidge.util.VisualProperty;

@SuppressWarnings({
		"unchecked", "rawtypes"
})
public class PropertyExplorer extends AbstractPropertyExplorer {

	private static final String HIERARCHY_DATA_PROVIDER = "HierarchyDataProvider";
	private static final String CHECKED = "Checked";
	private static final String SET = "set";
	private static final String NEW_WIZARD = "NewWizard";
	private static final String VALIDATOR2 = "Validator";
	private static final String VALID_VALUES = "ValidValues";
	private static final String GET = "get";
	private boolean needRequiredValidator;
	private final Method getter;
	private Method setter;
	private Method getValidValues;
	private Method getNewWizard;
	private Method getHierarchyProvider;
	private String propertyName;
	private Method getValidator;
	private IValidator<?> validator;
	private IObjectWizard wizard;
	private Method setChecked;
	private Method getChecked;
	protected VisualProperty visualProperty;

	@SuppressWarnings("nls")
	public PropertyExplorer(Object obj, Method method, boolean allowChild) {
		super(allowChild);
		source = obj;
		getter = method;
		visualProperty = method.getAnnotation(VisualProperty.class);
		propertyName = getter.getName().substring(3);
		getValidValues = exploreMethod(GET + propertyName + VALID_VALUES);
		getValidator = exploreMethod(GET + propertyName + VALIDATOR2);
		getNewWizard = exploreMethod(GET + propertyName + NEW_WIZARD);
		setChecked = exploreMethod(SET + propertyName + CHECKED, Boolean.class);
		getChecked = exploreMethod(GET + propertyName + CHECKED);
		validator = (IValidator<?>) invokeInternal(getValidator);
		if ((getValidator == null) || (!(validator instanceof ReadOnlyValidator))) {
			setter = exploreMethod(SET + propertyName, new Class[] {
				getter.getReturnType()
			});
		}
		getHierarchyProvider = exploreMethod(GET + propertyName + HIERARCHY_DATA_PROVIDER);
		propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
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
	public void setValueInt(Object value) {
		if (setter != null) {
			if (value != null) {
				Class<?> clazz = setter.getParameterTypes()[0];
				if (!clazz.equals(value.getClass())) {
					value = TypeUtil.convertValue(value, clazz);
				}
			}
			invokeInternal(setter, value);
		}
	}

	@Override
	public String getLabel() {
		if (!visualProperty.name_visible()) {
			return NN;
		}
		if (label == null) {
			label = getMessage(visualProperty.label(), PROPERTY_LABEL_POSTFIX, StringUtil.capitalize(propertyName)) + (visualProperty.required() ? REQUIRED : NN);
		}
		return label;
	}

	public String getDescription() {
		if (StringUtil.isEmpty(description)) {
			description = getMessage(visualProperty.description(), PROPERTY_DESCRIPTION_POSTFIX, NN);//$NON-NLS-1$
		}
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

	public boolean isReadOnly() {
		return (setter == null);
	}

	@Override
	public IValidator<?> getValidator() {
		if (validator == null) {
			if (isReadOnly()) {
				validator = ReadOnlyValidator.getDefault();
			} else {
				validator = ValidatorRegistry.get(getPropertyClass().getName());
			}
		}
		if (visualProperty.required() || needRequiredValidator) {
			validator = new RequiredValidator(validator);
		}
		return validator;
	}

	@Override
	public boolean hasValidValues() {
		return getValidValues != null;
	}

	public List<?> getValidValues() {
		if (getPropertyClass().isEnum()) {
			List list = new ArrayList();
			Object[] enumConstants = getPropertyClass().getEnumConstants();
			for (int a = 0; a < enumConstants.length; a++) {
				list.add(enumConstants[a]);
			}
			return list;
		}
		return (List<?>) invokeInternal(getValidValues);
	}

	@Override
	public boolean hasHierarchyProvider() {
		return getHierarchyProvider != null;
	}

	public IHierarchyProvider<?> getHierarchyProvider() {
		return (IHierarchyProvider<?>) invokeInternal(getHierarchyProvider);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PropertyExplorer)) {
			return false;
		}
		PropertyExplorer rhs = (PropertyExplorer) obj;
		return this.getter.equals(rhs.getter);
	}

	@Override
	public int hashCode() {
		return getter.hashCode();
	}

	public int compareTo(IPropertyExplorer o) {
		int compareTo = Integer.valueOf(getOrder()).compareTo(Integer.valueOf(o.getOrder()));
		if (compareTo == 0) {
			compareTo = getLabel().compareTo(o.getLabel());
		}
		return compareTo;
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

	private Method exploreMethod(String name, Class<?>... args) {
		try {
			return source.getClass().getMethod(name, args);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	public IObjectWizard getWizard() {
		if ((getNewWizard != null) && (wizard == null)) {
			wizard = (IObjectWizard) invokeInternal(getNewWizard);
		}
		return wizard;
	}

	@Override
	public void setChecked(Boolean selection) {
		if (setChecked != null) {
			invokeInternal(setChecked, selection);
		}
	}

	public boolean getChecked() {
		if (getChecked != null) {
			return (Boolean) invokeInternal(getChecked);
		}
		return false;
	}

	@Override
	public Type getPropertyType() {
		return getter.getGenericReturnType();
	}

	@Override
	public int getOrder() {
		return visualProperty.order();
	}

	@Override
	public int getVisualAreaWidth() {
		return visualProperty.width();
	}

	@Override
	public int getVisualAreaHeight() {
		return visualProperty.height();
	}

	@Override
	public int getImageWidth() {
		return visualProperty.imageWidth();
	}

	@Override
	public int getImageHeight() {
		return visualProperty.imageHeight();
	}

	@Override
	public VisualControlType getVisualControlType() {
		VisualControlType controlType = VisualControlType.DEFAULT;
		if (visualProperty != null) {
			controlType = visualProperty.control();
		}
		return controlType;
	}

	@Override
	public boolean isEmbedded() {
		return visualProperty.embedded();
	}
}