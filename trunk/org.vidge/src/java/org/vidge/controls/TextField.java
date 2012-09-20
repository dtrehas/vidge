package org.vidge.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.vidge.PropertyController;
import org.vidge.SharedImages;
import org.vidge.Vidge;
import org.vidge.VidgeResources;

public class TextField extends Composite {

	protected Text text;
	protected CustomButton clearButton;
	private final PropertyController controller;

	public TextField(Composite parent, int style, PropertyController controller) {
		super(parent, SWT.BORDER);
		this.controller = controller;
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		this.setLayout(layout);
		text = new Text(this, style);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		if ((style & SWT.SINGLE) != 0) {
			buildButtons();
		} else {
			if ((style & SWT.MULTI) == 0)
				text.setEditable(false);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		text.setEnabled(enabled);
	}

	private void buildButtons() {
		if (!Vidge.NO_CLEAR_ACTIONS) {
			clearButton = new CustomButton(this, VidgeResources.getInstance().getImage(SharedImages.DELETE), "Delete");
			clearButton.setLayoutData(new GridData());
			clearButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					controller.getExplorer().getValidator().validateComplete(null);
					controller.getFieldAdapter().setVisualValue("");
				}
			});
		}
	}

	public String getText() {
		return text.getText();
	}

	public Text getTextControl() {
		return text;
	}

	public void setText(String string) {
		text.setText(string);
	}

	public void addModifyListener(ModifyListener listener) {
		text.addModifyListener(listener);
	}

	public void addVerifyListener(VerifyListener listener) {
		text.addVerifyListener(listener);
	}

	public void removeModifyListener(ModifyListener listener) {
		text.removeModifyListener(listener);
	}

	public void removeVerifyListener(VerifyListener listener) {
		text.removeVerifyListener(listener);
	}

	@Override
	public void addFocusListener(FocusListener listener) {
		text.addFocusListener(listener);
	}

	@Override
	public void removeFocusListener(FocusListener listener) {
		text.removeFocusListener(listener);
	}

	@Override
	public void addHelpListener(HelpListener listener) {
		text.addHelpListener(listener);
	}

	@Override
	public void removeHelpListener(HelpListener listener) {
		text.removeHelpListener(listener);
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		text.addKeyListener(listener);
	}

	@Override
	public void removeKeyListener(KeyListener listener) {
		text.removeKeyListener(listener);
	}

	@Override
	public void addMouseListener(MouseListener listener) {
		text.addMouseListener(listener);
	}

	@Override
	public void removeMouseListener(MouseListener listener) {
		text.removeMouseListener(listener);
	}

	@Override
	public void addMouseMoveListener(MouseMoveListener listener) {
		text.addMouseMoveListener(listener);
	}

	@Override
	public void removeMouseMoveListener(MouseMoveListener listener) {
		text.removeMouseMoveListener(listener);
	}

	@Override
	public void addMouseTrackListener(MouseTrackListener listener) {
		text.addMouseTrackListener(listener);
	}

	@Override
	public void removeMouseTrackListener(MouseTrackListener listener) {
		text.removeMouseTrackListener(listener);
	}

	public void addSelectionListener(SelectionListener listener) {
		text.addSelectionListener(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		text.removeSelectionListener(listener);
	}

	@Override
	public void addTraverseListener(TraverseListener listener) {
		text.addTraverseListener(listener);
	}

	@Override
	public void removeTraverseListener(TraverseListener listener) {
		text.removeTraverseListener(listener);
	}

	@Override
	public boolean forceFocus() {
		return text.forceFocus();
	}

	@Override
	public boolean isFocusControl() {
		return text.isFocusControl();
	}

	@Override
	public boolean setFocus() {
		return text.setFocus();
	}
}
