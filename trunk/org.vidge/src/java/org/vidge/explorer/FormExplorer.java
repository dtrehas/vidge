package org.vidge.explorer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;
import org.langcom.locale.LocalizedString;
import org.vidge.VidgeException;
import org.vidge.dialog.ObjectListSelectDialog;
import org.vidge.form.IDialogForm;
import org.vidge.form.IForm;
import org.vidge.form.IFormAbstractFactory;
import org.vidge.form.IFormDataProvider;
import org.vidge.form.IFormFactory;
import org.vidge.form.IFormInputChangeListener;
import org.vidge.form.IFormView;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.FormContextRule;
import org.vidge.util.ValueAction;
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
		form = inputForm;
		explore(inputForm);
	}

	public FormExplorer(Class<? extends IForm<T>> formClass) {
		try {
			form = formClass.newInstance();
			explore(form);
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
		// if (IFormView.class.isAssignableFrom(form.getClass())) {
		// Method method;
		// try {
		// method = form.getClass().getMethod("getString", Object.class);
		// Assert.isNotNull(method);
		// list.add(new PredefinedProperty(form, method));
		// } catch (Exception e) {
		// throw new VidgeException(e);
		// }
		// }
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
	public String getHeader() {
		if (IDialogForm.class.isAssignableFrom(form.getClass())) {
			return ((IDialogForm) form).getHeader();
		}
		return null;
	}

	@Override
	public String getDescription() {
		if (IDialogForm.class.isAssignableFrom(form.getClass())) {
			return ((IDialogForm) form).getDescription();
		}
		return null;
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

	@SuppressWarnings("unchecked")
	@Override
	public IEntityExplorer copy() {
		try {
			return new FormExplorer<T>(form.getClass().newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
		Object newInstance = newInstance(getInput.getReturnType(), getContext());
		form.setInput((T) newInstance);
		return newInstance;
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
		} else if (IFormAbstractFactory.class.isAssignableFrom(form.getClass())) {
			List<Class<?>> ancestorList = ((IFormAbstractFactory) form).getAncestorList();
			List<String> list = new ArrayList<String>();
			for (Class<?> clazz : ancestorList) {
				list.add(((IFormAbstractFactory) form).getAncestorFriendlyName(clazz));
			}
			ObjectListSelectDialog<String> dialog = new ObjectListSelectDialog<String>("Type select", String.class, list, true);
			if (dialog.open() == Window.OK) {
				List<String> selection = dialog.getSelection();
				if (selection == null) {
					return null;
				}
				String string = selection.get(0);
				int indexOf = list.indexOf(string);
				Class<?> class1 = ancestorList.get(indexOf);
				try {
					return class1.newInstance();
				} catch (Exception e) {
					throw new VidgeException(e);
				}
			} else {
				return null;
			}
		}
		try {
			return super.newInstance(inputClass, context);
		} catch (Exception e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Create Input ", e.getMessage());
			throw new VidgeException("***Error by instantiation of item : Please check existence of default constructor " + inputClass, e);
		}
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

	@Override
	public Color getBackground(Object element) {
		// if (IColorForm.class.isAssignableFrom(form.getClass())) {
		// return ((IColorForm) form).getBackground(element);
		// }
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		// if (IColorForm.class.isAssignableFrom(form.getClass())) {
		// return ((IColorForm) form).getForeground(element);
		// }
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void checkInput() {
		if (IFormFactory.class.isAssignableFrom(form.getClass())) {
			Object newInstance = ((IFormFactory) form).checkInstance(getContext());
			form.setInput((T) newInstance);
		}
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	@Override
	public String getString(Object value) {
		if (IFormView.class.isAssignableFrom(form.getClass())) {
			return ((IFormView) form).getString(value);
		}
		return super.getString(value);
	}
}
