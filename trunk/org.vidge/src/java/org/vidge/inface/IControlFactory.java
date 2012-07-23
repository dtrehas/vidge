package org.vidge.inface;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.vidge.controls.DateEditor;
import org.vidge.controls.calendar.DateChooser;

public interface IControlFactory {
	//for scrolling
	ScrolledComposite createComposite(Composite parent, int style);

	Label createLabel(Composite parent, int style);

	Text createText(Composite parent, int style);

	List createList(Composite parent, int style);

	Button createCheckbox(Composite parent, int style);

	Combo createCombo(Composite parent, int style);

	Button createButton(Composite parent, int style);

	DateChooser createDateChooser(Composite parent, int style);
	
	DateEditor createDateEditor(Composite parent, int style);
}
