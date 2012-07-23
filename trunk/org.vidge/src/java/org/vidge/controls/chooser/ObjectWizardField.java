package org.vidge.controls.chooser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.vidge.PropertyController;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.controls.CustomButton;
import org.vidge.controls.editor.ObjectEditor;

public class ObjectWizardField<T> extends ObjectEditor<T> {

	private Button checkButton;

	public ObjectWizardField(Composite parent, int style, PropertyController controller) {
		super(parent, style, controller);
	}

	@Override
	protected void createContent(int style) {
		try {
			if (controller.getExplorer().getChecked()) {
				((GridLayout) this.getLayout()).numColumns = 5;
				checkButton = new Button(this, SWT.CHECK);
				checkButton.setLayoutData(new GridData(GridData.BEGINNING));
				checkButton.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						controller.getExplorer().setChecked(checkButton.getSelection());
						if (checkButton.getSelection()) {
							controller.getExplorer().setValue(null);
							text.setText("");
						}
						controller.inValidate();
					}
				});
			} else {
				((GridLayout) this.getLayout()).numColumns = 4;
			}
			super.createContent(style);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void init() {
		checkButton.setSelection(controller.getExplorer().getChecked());
	}

	@Override
	protected void makeButton() {
		button = new CustomButton(this, VidgeResources.getInstance().getImage(SharedImages.SQUARE), "Create");
		button.setLayoutData(new GridData());
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object open = controller.getExplorer().getWizard().open(controller.getExplorer().getValue());
				if (open != null) {
					setSelection((T) open.toString());
				}
			}
		});
		super.makeButton();
	}
}