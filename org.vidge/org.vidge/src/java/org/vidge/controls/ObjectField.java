package org.vidge.controls;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.langcom.locale.LocalizedString;
import org.vidge.PropertyController;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.controls.editor.ExplorerContentTYpe;
import org.vidge.inface.IObjectDialog;
import org.vidge.util.StringUtil;

public abstract class ObjectField<T> extends Composite {

	protected Text text;
	protected boolean lazy = false;
	protected CustomButton button;
	protected CustomButton clearButton;
	public static int ALLOW_KEYS = SWT.KEYCODE_BIT + 48;
	protected T selection;
	protected PropertyController controller;

	protected abstract IObjectDialog<T> getDialog();

	protected ObjectField(Composite parent, int style) {
		this(parent, style, null);
	}

	@SuppressWarnings("unchecked")
	public ObjectField(Composite parent, int style, PropertyController controller) {
		super(parent, SWT.BORDER);
		this.controller = controller;
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		this.setLayout(layout);
		createContent(style);
		makeButton();
		makeClearButton();
		setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (!lazy) {
			selection = (T) controller.getExplorer().getValue();
		}
		refresh(selection);
		lazy = (controller.getExplorer().getContentType().equals(ExplorerContentTYpe.ARRAY) || controller.getExplorer().getContentType().equals(ExplorerContentTYpe.LIST) || controller.getExplorer()
			.getContentType()
			.equals(ExplorerContentTYpe.MAP));
	}

	protected void init() {
	}

	protected void createContent(int style) {
		text = new Text(this, SWT.NONE);
		if ((style & ALLOW_KEYS) == 0) {
			text.addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					if (controller.getExplorer().getPropertyClass() == LocalizedString.class) {
						if (e.stateMask == 262144 && e.keyCode == 32) {
							e.doit = false;
							showDialog();
						}
					} else {
						e.doit = false;
					}
				}

				public void keyReleased(KeyEvent e) {
					if (controller.getExplorer().getPropertyClass() == LocalizedString.class) {
						// keyPressedInText();
					} else {
						showDialog();
					}
				}
			});
		}
		text.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				showDialog();
			}
		});
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		text.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				focusLostInText();
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});
	}

	protected void focusLostInText() {
	}

	protected void makeButton() {
		button = new CustomButton(this, VidgeResources.getInstance().getImage(SharedImages.ACTION_PLUS2), "Select");
		button.setLayoutData(new GridData());
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				showDialog();
			}
		});
	}

	private void makeClearButton() {
		clearButton = new CustomButton(this, VidgeResources.getInstance().getImage(SharedImages.DELETE), "Delete");
		clearButton.setLayoutData(new GridData());
		clearButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setSelection(null);
				controller.inValidate();
			}
		});
	}

	@Override
	public void setEnabled(boolean enabled) {
		text.setEnabled(enabled);
		button.setEnabled(enabled);
		clearButton.setEnabled(enabled);
	}

	public void refresh(T object) {
		text.setText(object == null ? StringUtil.NN : object.toString());
	}

	public T getSelection() {
		return getSelectionInternal();
	}

	public void setSelection(T object) {
		this.selection = object;
		controller.getExplorer().setValue(selection);
		controller.inValidate();
		refresh(object);
	}

	public void select(int index) {
	}

	protected T getSelectionInternal() {
		return selection;
	}

	protected void showDialog() {
		IObjectDialog<T> dialog = getDialog();
		if (dialog.open() == Window.OK) {
			setSelection(dialog.getSelection());
		}
	}

	public Text getText() {
		return text;
	}

	public boolean isValid() {
		return controller.isValid();
	}

	public void addFocusListener(FocusListener listener) {
		text.addFocusListener(listener);
	}

	public void removeFocusListener(FocusListener listener) {
		text.removeFocusListener(listener);
	}

	public void addHelpListener(HelpListener listener) {
		text.addHelpListener(listener);
	}

	public void removeHelpListener(HelpListener listener) {
		text.removeHelpListener(listener);
	}

	public void addKeyListener(KeyListener listener) {
		text.addKeyListener(listener);
	}

	public void removeKeyListener(KeyListener listener) {
		text.removeKeyListener(listener);
	}

	public void addMouseListener(MouseListener listener) {
		text.addMouseListener(listener);
	}

	public void removeMouseListener(MouseListener listener) {
		text.removeMouseListener(listener);
	}

	public void addMouseMoveListener(MouseMoveListener listener) {
		text.addMouseMoveListener(listener);
	}

	public void removeMouseMoveListener(MouseMoveListener listener) {
		text.removeMouseMoveListener(listener);
	}

	public void addMouseTrackListener(MouseTrackListener listener) {
		text.addMouseTrackListener(listener);
	}

	public void removeMouseTrackListener(MouseTrackListener listener) {
		text.removeMouseTrackListener(listener);
	}

	public void addSelectionListener(SelectionListener listener) {
		text.addSelectionListener(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		text.removeSelectionListener(listener);
	}

	public void addTraverseListener(TraverseListener listener) {
		text.addTraverseListener(listener);
	}

	public void removeTraverseListener(TraverseListener listener) {
		text.removeTraverseListener(listener);
	}

	public boolean forceFocus() {
		return text.forceFocus();
	}

	public boolean isFocusControl() {
		return text.isFocusControl();
	}

	public boolean setFocus() {
		return text.setFocus();
	}
}
