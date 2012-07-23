package org.vidge;

import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.vidge.controls.adapters.AbstractFieldAdapter;
import org.vidge.controls.adapters.FieldAdapterBoolean;
import org.vidge.controls.adapters.FieldAdapterColorEditor;
import org.vidge.controls.adapters.FieldAdapterCombo;
import org.vidge.controls.adapters.FieldAdapterDateEditor;
import org.vidge.controls.adapters.FieldAdapterEditableCombo;
import org.vidge.controls.adapters.FieldAdapterFileEditor;
import org.vidge.controls.adapters.FieldAdapterFolderEditor;
import org.vidge.controls.adapters.FieldAdapterFontEditor;
import org.vidge.controls.adapters.FieldAdapterForm;
import org.vidge.controls.adapters.FieldAdapterImage;
import org.vidge.controls.adapters.FieldAdapterLabel;
import org.vidge.controls.adapters.FieldAdapterList;
import org.vidge.controls.adapters.FieldAdapterNumber;
import org.vidge.controls.adapters.FieldAdapterObjectEditor;
import org.vidge.controls.adapters.FieldAdapterObjectWizard;
import org.vidge.controls.adapters.FieldAdapterPartListEditor;
import org.vidge.controls.adapters.FieldAdapterPassword;
import org.vidge.controls.adapters.FieldAdapterSelectList;
import org.vidge.controls.adapters.FieldAdapterText;
import org.vidge.util.VisualControlType;

public class FieldAdapterFactory {

	public static AbstractFieldAdapter getFieldAdapter(VisualControlType controlType, PropertyController controller, Composite parent) {
		switch (controlType) {
			case LABEL:
				return new FieldAdapterLabel(controller, parent);
			case TEXT:
				return new FieldAdapterText(controller, parent);
			case TEXTAREA:
				return new FieldAdapterText(controller, parent);
			case PASSWORD:
				return new FieldAdapterPassword(controller, parent);
			case NUMBER:
				return new FieldAdapterNumber(controller, parent);
			case COMBO:
				return new FieldAdapterCombo(controller, parent);
			case CHECKBOX:
				return new FieldAdapterBoolean(controller, parent);
			case DATE:
				return new FieldAdapterDateEditor(controller, parent);
			case IMAGE:
				return new FieldAdapterImage(controller, parent);
			case OBJECT_EDITOR:
				if (controller.explorer.isEmbedded()) {
					Class<?> propertyClass = controller.getExplorer().getPropertyClass();
					if (List.class.isAssignableFrom(propertyClass)) {
						if (controller.explorer.hasValidValues()) {
							return new FieldAdapterSelectList(controller, parent);
						} else {
							return new FieldAdapterList(controller, parent);
						}
					} else if (Set.class.isAssignableFrom(propertyClass)) {
					} else if (propertyClass.isArray()) {
					} else {
						if (controller.explorer.hasValidValues()) {
							return new FieldAdapterSelectList(controller, parent);
						} else {
							return new FieldAdapterForm(controller, parent);
						}
					}
				}
				return new FieldAdapterObjectEditor(controller, parent);
			case EDITABLE_COMBO:
				return new FieldAdapterEditableCombo(controller, parent);
			case PART_LIST_EDITOR:
				return new FieldAdapterPartListEditor(controller, parent);
			case COLOR:
				return new FieldAdapterColorEditor(controller, parent);
			case FONT:
				return new FieldAdapterFontEditor(controller, parent);
			case FILE:
				return new FieldAdapterFileEditor(controller, parent);
			case FOLDER:
				return new FieldAdapterFolderEditor(controller, parent);
			case OBJECT_WIZARD:
				return new FieldAdapterObjectWizard(controller, parent);
			default:
				return new FieldAdapterLabel(controller, parent);
		}
	}
}
