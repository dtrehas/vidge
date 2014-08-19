package org.vidge.explorer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.vidge.VidgeException;
import org.vidge.controls.tree.hierarchy.IHierarchyProvider;
import org.vidge.form.IForm;
import org.vidge.form.IFormObjectWizard;
import org.vidge.form.validator.IValidator;
import org.vidge.form.validator.ReadOnlyValidator;
import org.vidge.form.validator.RequiredValidator;
import org.vidge.form.validator.ValidatorRegistry;
import org.vidge.util.APropertyContext;
import org.vidge.util.ContextProperty;
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
	private Method getter;
	private Method setter;
	private Method getValidValues;
	private Method getNewWizard;
	private Method getHierarchyProvider;
	private String propertyName;
	private Method getValidator;
	private IValidator<?> validator;
	private IFormObjectWizard wizard;
	private Method setChecked;
	private Method getChecked;
	protected VisualProperty visualProperty;
	private APropertyContext context;
	private Double order;
	private Boolean enableTotal = false;
	private Boolean enableAverage = false;

	public PropertyExplorer() {
		super(false);
	}

	@SuppressWarnings("nls")
	public PropertyExplorer(Object obj, Method method, boolean allowChild) {
		super(allowChild);
		source = obj;
		getter = method;
		visualProperty = method.getAnnotation(VisualProperty.class);
		context = method.getAnnotation(APropertyContext.class);
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
		getLabel();
		contentType = getType(getPropertyClass());
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
		if (visualProperty.name_visible()) {
			if (label == null) {
				label = getMessage(visualProperty.label(), PROPERTY_LABEL_POSTFIX, StringUtil.capitalize(propertyName))
					+ (visualProperty.required() ? REQUIRED : StringUtil.NN);
			}
		}
		if (label == null) {
			label = StringUtil.NN;
		}
		return label;
	}

	public String getDescription() {
		if (StringUtil.isEmpty(description)) {
			description = getMessage(visualProperty.description(), PROPERTY_DESCRIPTION_POSTFIX, StringUtil.NN);//$NON-NLS-1$
		}
		return description;
	}

	protected String getMessage(String key, String postfix, String defaultValue) {
		if (StringUtil.isEmpty(key)) {
			key = source.getClass().getSimpleName() + DOT + propertyName + DOT + postfix;
		}
		String value = defaultValue;
		try {
			value = ResourceBundle.getBundle(source.getClass().getPackage().getName() + MESSAGES, Locale.getDefault(), source.getClass().getClassLoader())
				.getString(key);
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

	private Object invokeInternal(Method method, Object... args) {
		if ((source != null) && (method != null)) {
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			try {
				return method.invoke(source, args);
			} catch (Exception e) {
				// VidgeErrorDialog.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", e.getMessage(), e);
				throw new VidgeException(e);
				// e.printStackTrace();
				// Logger.getLogger(this.getClass().getName()).severe(method + " - " + args.length + "=" + e.getMessage());
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

	public IFormObjectWizard getWizard() {
		if ((getNewWizard != null) && (wizard == null)) {
			wizard = (IFormObjectWizard) invokeInternal(getNewWizard);
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
	public double getOrder() {
		if (order != null) {
			return order;
		}
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
	public boolean isNeedTableEditor() {
		return visualProperty.table_editor();
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

	public APropertyContext getContext() {
		return context;
	}

	@Override
	public Control getEditor(Control parent) {
		if (getPropertyClass().equals(Boolean.class) || getPropertyClass().equals(boolean.class)) {
			final Button button = new Button((Composite) parent, SWT.CHECK);
			button.setSelection((Boolean) getValue());
			button.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			button.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					setValue(button.getSelection());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			return button;
		}
		final Text text = new Text((Composite) parent, SWT.BORDER);
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setValue(text.getText());
			}
		});
		return text;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setOrder(Double order) {
		this.order = order;
	}

	public void setTotal(boolean enableTotal) {
		this.enableTotal = enableTotal;
	}

	public void setAverage(boolean enableAverage) {
		this.enableAverage = enableAverage;
	}

	public boolean getEnableTotal() {
		if (context != null && enableTotal == null) {
			return context.doTotal();
		}
		return enableTotal;
	}

	public boolean getEnableAverage() {
		if (context != null && enableAverage == null) {
			return context.doAverage();
		}
		return enableAverage;
	}

	public ContextProperty doAs() {
		if (context == null) {
			return ContextProperty.ASIS;
		}
		return context.doAs();
	}
}