package org.vidge.explorer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.langcom.locale.LocalizedString;
import org.vidge.inface.IForm;
import org.vidge.inface.IFormDataProvider;
import org.vidge.inface.IFormFactory;
import org.vidge.inface.IFormInputChangeListener;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.inface.ValueAction;
import org.vidge.util.FormContextRule;
import org.vidge.util.VisualProperty;

public class FormExplorer<T> extends EntityExplorer {

	private static final String GET_INPUT = "getInput";
	private static final String ADD_PROPERTY_CHANGE_LISTENER = "addPropertyChangeListener";
	private Method addListener;
	private Method getInput;
	private IForm<T> form;
	private FormContextRule contextRule;

	public FormExplorer() {
	}

	public FormExplorer(IForm<T> inputForm) {
		explore(inputForm);
	}

	public FormExplorer(Class<? extends IForm<T>> formClass) {
		try {
			explore(formClass.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void explore(Object objIn) {
		super.explore(objIn);
		contextRule = form.getClass().getAnnotation(FormContextRule.class);
		List<IPropertyExplorer> list = new ArrayList<IPropertyExplorer>();
		for (Method method : form.getClass().getMethods()) {
			if (method.isAnnotationPresent(VisualProperty.class)) {
				list.add(new PropertyExplorer(form, method, allowParts()));
			}
		}
		Collections.sort(list, new Comparator<IPropertyExplorer>() {

			@Override
			public int compare(IPropertyExplorer o1, IPropertyExplorer o2) {
				return o2.getOrder() - o1.getOrder();
			}
		});
		setAttributes(list);
		try {
			getInput = form.getClass().getMethod(GET_INPUT);
			addListener = form.getClass().getMethod(ADD_PROPERTY_CHANGE_LISTENER, IPropertyChangeListener.class);
		} catch (Exception e) {
		}
	}

	@Override
	public String getLabel() {
		if (form.getInput() == null) {
			return "";
		} else if (LocalizedString.class.isAssignableFrom(form.getInput().getClass())) {
			return ((LocalizedString) form.getInput()).getDefaultLocalString();
		}
		return form.getInput().toString();
	}

	@Override
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		if (addListener != null) {
			try {
				addListener.invoke(form, listener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public IForm<T> getForm() {
		return form;
	}

	@Override
	public Object getInput() {
		return form.getInput();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setInput(Object inputIn) {
		if (inputIn != null) {
			if (IForm.class.isAssignableFrom(inputIn.getClass())) {
				super.setInput(inputIn);
				form = (IForm<T>) inputIn;
			} else {
				form.setInput((T) inputIn);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object createInput() {
		try {
			Object newInstance = newInstance(getInput.getReturnType(), getContext());
			form.setInput((T) newInstance);
			return newInstance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean removeInput() {
		try {
			boolean result = removeInstance(getInput.getReturnType(), getContext());
			if (result) {
				form.setInput(null);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Class<?> getInputClass() {
		return getInput.getReturnType();
	}

	@Override
	public Object doInputChanged(Object value, ValueAction action, String attribute) {
		if (IFormInputChangeListener.class.isAssignableFrom(form.getClass())) {
			value = ((IFormInputChangeListener) form).doInputChanged(value, action, attribute);
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getData() {
		if (IFormDataProvider.class.isAssignableFrom(form.getClass())) {
			return ((IFormDataProvider<T>) form).getData();
		}
		return null;
	}

	@Override
	public boolean allowParts() {
		if (IFormInputChangeListener.class.isAssignableFrom(form.getClass())) {
			return ((IFormInputChangeListener) form).allowParts();
		}
		return true;
	}

	@Override
	public boolean create() {
		return contextRule == null ? true : contextRule.create();
	}

	@Override
	public boolean edit() {
		return contextRule == null ? true : contextRule.edit();
	}

	@Override
	public boolean remove() {
		return contextRule == null ? true : contextRule.remove();
	}

	@Override
	public boolean refresh() {
		return contextRule == null ? true : contextRule.refresh();
	}

	@Override
	public Object newInstance(Class<?> inputClass, Object context) {
		if (IFormFactory.class.isAssignableFrom(form.getClass())) {
			return ((IFormFactory) form).newInstance(inputClass, context);
		}
		return super.newInstance(inputClass, context);
	}

	@Override
	public boolean removeInstance(Class<?> inputClass, Object context) {
		if (IFormFactory.class.isAssignableFrom(form.getClass())) {
			return ((IFormFactory) form).removeInstance(inputClass, context);
		}
		return super.removeInstance(inputClass, context);
	}

	@Override
	public void instanceApply() {
		if (IFormFactory.class.isAssignableFrom(form.getClass())) {
			((IFormFactory) form).instanceApply();
		}
	}

	@Override
	public void instanceCancel() {
		if (IFormFactory.class.isAssignableFrom(form.getClass())) {
			((IFormFactory) form).instanceCancel();
		}
	}
}
