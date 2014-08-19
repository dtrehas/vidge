package org.vidge.controls.editor;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.vidge.PropertyController;
import org.vidge.controls.ObjectField;
import org.vidge.controls.tree.ObjectTreeEditorDialog;
import org.vidge.dialog.ObjectListDialog;
import org.vidge.dialog.ObjectListSelectDialog;
import org.vidge.dialog.SingleObjectDialog;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IObjectDialog;
import org.vidge.util.FormContext;
import org.vidge.util.TypeUtil;
import org.vidge.util.ValueAction;

public class ObjectEditor<T> extends ObjectField<T> {

	public ObjectEditor(Composite parent, int style, PropertyController controller) {
		super(parent, style, controller);
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	@Override
	protected void showDialog() {
		// selection = (T) controller.getExplorer().getValue();
		IObjectDialog<T> dialog = null;
		IEntityExplorer explorer = null;
		Class<?> propertyClass = controller.getExplorer().getPropertyClass();
		Type propertyType = TypeUtil.getGenericClass(controller.getExplorer().getPropertyClass(), controller.getExplorer().getPropertyType());
		if (!propertyClass.equals(propertyType)) {
			if (List.class.isAssignableFrom(propertyClass) || Map.class.isAssignableFrom(propertyClass) || propertyClass.isArray()) {
				if (controller.getExplorer().hasHierarchyProvider()) {
					// explorer = TypeUtil.getExplorer(TypeUtil.getType(propertyType, propertyClass), FormContext.TREE.name(), controller.getExplorer());
					// explorer.setContext(controller.getExplorer().getEntityExplorer().getInput());
					dialog = new ObjectTreeEditorDialog(controller.getExplorer().getLabel(), controller.getExplorer(), controller.getExplorer()
						.getHierarchyProvider(), new IOkListener() {

						@Override
						public void okPressed() {
							setSelection((T) controller.getExplorer().getValue());
						}
					});
				} else if (controller.getExplorer().hasValidValues()) {
					explorer = TypeUtil.getExplorer(TypeUtil.getType(propertyType, propertyClass), FormContext.TABLE.name(), controller.getExplorer());
					explorer.setContext(controller.getExplorer().getEntityExplorer().getInput());
					dialog = new ObjectListSelectDialog(controller.getExplorer().getLabel(), explorer, controller.getExplorer(), false);
				} else {
					explorer = TypeUtil.getExplorer(TypeUtil.getType(propertyType, propertyClass), FormContext.TABLE.name(), controller.getExplorer());
					explorer.setContext(controller.getExplorer().getEntityExplorer().getInput());
					dialog = new ObjectListDialog(controller.getExplorer().getLabel(), explorer, controller.getExplorer());
				}
			} else {
				explorer = TypeUtil.getExplorer(TypeUtil.getType(propertyType, propertyClass), FormContext.EDIT.name(), controller.getExplorer());
				explorer.setContext(controller.getExplorer().getEntityExplorer().getInput());
				dialog = new SingleObjectDialog<T>(explorer, controller.getExplorer().getLabel(), null);
			}
		} else {
			if (controller.getExplorer().hasHierarchyProvider()) {
				// explorer = TypeUtil.getExplorer(TypeUtil.getType(propertyType, propertyClass), FormContext.TREE.name(), controller.getExplorer());
				// explorer.setContext(controller.getExplorer().getEntityExplorer().getInput());
				dialog = new ObjectTreeEditorDialog(controller.getExplorer().getLabel(), controller.getExplorer(), controller.getExplorer()
					.getHierarchyProvider(), new IOkListener() {

					@Override
					public void okPressed() {
						setSelection((T) controller.getExplorer().getValue());
					}
				});
			} else if (controller.getExplorer().hasValidValues()) {
				explorer = TypeUtil.getExplorer(TypeUtil.getType(propertyType, propertyClass), FormContext.TABLE.name(), controller.getExplorer());
				explorer.setContext(controller.getExplorer().getEntityExplorer().getInput());
				dialog = new ObjectListSelectDialog(controller.getExplorer().getLabel(), explorer, controller.getExplorer(), true);
			} else {
				explorer = TypeUtil.getExplorer(TypeUtil.getType(propertyType, propertyClass), FormContext.EDIT.name(), controller.getExplorer());
				explorer.setContext(controller.getExplorer().getEntityExplorer().getInput());
				if (getSelection() != null) {
					explorer.explore(getSelection());
				}
				dialog = new SingleObjectDialog<T>(explorer, controller.getExplorer().getLabel(), null);
			}
		}
		if (dialog.open() == Window.OK) {
			if (!dialog.getClass().equals(ObjectTreeEditorDialog.class)) {
				T selection2 = dialog.getSelection();
				explorer.doInputChanged(selection, ValueAction.CHANGED, controller.getExplorer().getLabel());
				if (selection2 != null) {
					if (List.class.isAssignableFrom(propertyClass)) {
						setSelection(selection2);
					} else if (Set.class.isAssignableFrom(propertyClass)) {
						setSelection((T) new HashSet((Collection) selection2));
					} else if (Map.class.isAssignableFrom(propertyClass)) {
						HashMap hashMap = new HashMap();
						for (int a = 0; a < ((List) selection2).size(); a++) {
							hashMap.put(a, ((List) selection2).get(a));
						}
						setSelection((T) hashMap);
					} else if (propertyClass.isArray()) {
						T[] array = (T[]) Array.newInstance(TypeUtil.getType(propertyType, propertyClass), ((List) selection2).size());
						for (int a = 0; a < ((List) selection2).size(); a++) {
							array[a] = (T) ((List) selection2).get(a);
						}
						setSelection((T) array);
					} else {
						if (List.class.isAssignableFrom(selection2.getClass())) {
							if (((List) selection2).size() > 0) {
								setSelection((T) ((List) selection2).get(0));
							} else {
								setSelection(null);
							}
						} else {
							setSelection(selection2);
						}
					}
				}
			}
		} else {
			if (!dialog.getClass().equals(ObjectTreeEditorDialog.class)) {
				explorer.doInputChanged(selection, ValueAction.CANCEL, controller.getExplorer().getLabel());
			}
		}
	}

	public void refresh(T object) {
		Class<?> propertyClass = controller.getExplorer().getPropertyClass();
		if (List.class.isAssignableFrom(propertyClass)) {
			text.setText(Messages.ObjectEditor_0);
		} else if (Map.class.isAssignableFrom(propertyClass)) {
			text.setText(Messages.ObjectEditor_1);
		} else if (propertyClass.isArray()) {
			text.setText(Messages.ObjectEditor_2);
		} else {
			super.refresh(object);
		}
	};

	@Override
	protected IObjectDialog<T> getDialog() {
		return null;
	}

	public boolean getLazy() {
		return lazy;
	}
}