package org.vidge.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.vidge.PlainForm;
import org.vidge.PropertyController;
import org.vidge.controls.chooser.Filter;
import org.vidge.controls.chooser.Messages;
import org.vidge.controls.chooser.ObjectChooser;
import org.vidge.explorer.FormExplorer;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IForm;
import org.vidge.inface.IObjectDialog;
import org.vidge.status.IStatusListener;
import org.vidge.status.PropertyStatus;
import org.vidge.util.PositionUtillity;
import org.vidge.util.VisualControlType;

public class ObjectDialog<F> extends TitleAreaDialog implements IObjectDialog<F> {

	private static final Point DEFAULT_SIZE = new Point(800, 500);
	private IForm<F> objectForm;
	private Point size = new Point(700, 500);
	private PlainForm form;
	private boolean readOnly;
	private List objectList;// = new ArrayList();
	private final String title;
	private ObjectChooser chooser;
	private F selection;
	private String message;
	private IEntityExplorer explorer;
	private Filter filter;
	private int style = 0;
	private ArrayList selectionObjects = new ArrayList();
	private static int SELECT_ALL = IDialogConstants.CLIENT_ID << 2;
	private boolean selectAllItems = true;

	public ObjectDialog(Shell shell, IForm<F> objectForm, String title, String message, List<?> objectList, Point size) {
		this(shell, new FormExplorer(objectForm), title, message, size);
		this.objectList = objectList;
	}

	public ObjectDialog(Shell shell, IEntityExplorer entityExplorer, String title, String message, List<?> objectList) {
		this(shell, entityExplorer, title, message, DEFAULT_SIZE);
		this.objectList = objectList;
	}

	public ObjectDialog(Shell shell, IEntityExplorer entityExplorer, String title, String message) {
		this(shell, entityExplorer, title, message, DEFAULT_SIZE);
	}

	public ObjectDialog(Shell shell, IEntityExplorer entityExplorer, String title, String message, List<?> objectList, int style) {
		this(shell, entityExplorer, title, null, DEFAULT_SIZE);
		this.objectList = objectList;
		this.style = style;
	}

	public ObjectDialog(Shell shell, IForm<F> objectForm, String title, String message) {
		this(shell, new FormExplorer(objectForm), title, message, DEFAULT_SIZE);
		this.objectForm = objectForm;
	}

	public ObjectDialog(Shell shell, Point point, IForm<F> objectForm, String title, String message) {
		this(shell, objectForm, title, message, null, point);
	}

	public ObjectDialog(Shell shell, IEntityExplorer entityExplorer, String title, String message, Point size) {
		this(shell, title, message);
		setShellStyle(SWT.CLOSE | SWT.MAX | SWT.MIN | SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());
		explorer = entityExplorer;
		this.size = size;
		if (explorer.getInput() == null) {
			explorer.createInput();
			selection = (F) explorer.getInput();
		}
	}

	public ObjectDialog(Shell shell, String title, String message) {
		super(shell);
		this.title = title;
		this.message = message;
	}

	@Override
	protected Control createContents(Composite parent) {
		if (size == null) {
			size = DEFAULT_SIZE;
		}
		parent.setSize(size);
		PositionUtillity.center(parent);
		Control control = super.createContents(parent);
		if (title != null) {
			getShell().setText(title);
			setTitle(title);
		}
		if (message != null) {
			setMessage(message);
		}
		Button buttonOk = getButton(IDialogConstants.OK_ID);
		buttonOk.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				okPressed();
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
				okPressed();
			}
		});
		return control;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		if ((style & SWT.CHECK) != 0) {
			createButton(parent, SELECT_ALL, Messages.ObjectDialog_0, false);
		}
		if (objectList != null) {
			createButton(parent, IDialogConstants.CLIENT_ID, Messages.ObjectChooserField_CLEAR_FILTERS, false);
		}
		if (readOnly) {
			createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		} else {
			super.createButtonsForButtonBar(parent);
		}
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			if (!keyEnterPressedInTextArea()) {
				explorer.instanceApply();
				okPressed();
			}
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			explorer.instanceCancel();
			explorer.setInput(null);
			cancelPressed();
		} else if (IDialogConstants.CLIENT_ID == buttonId) {
			filter.removeFiltering();
		} else if (SELECT_ALL == buttonId) {
			selectAll();
		}
	}

	private void selectAll() {
		selectAllItems = !selectAllItems;
		selectionObjects.clear();
		for (TableItem item : chooser.getTableViewer().getTable().getItems()) {
			item.setChecked(item.getChecked());
			chooser.getTableViewer().refresh();
		}
	}

	@Override
	protected void okPressed() {
		if ((style & SWT.CHECK) != 0) {
			if (selectAllItems) {
				selectionObjects.addAll(chooser.getInput());
			} else {
				if (objectList != null)
					for (TableItem item : chooser.getTableViewer().getTable().getItems()) {
						if (item.getChecked()) {
							selectionObjects.add(((List) objectList).get(chooser.getTableViewer().getTable().indexOf(item)));
						}
					}
			}
		}
		super.okPressed();
	}

	private boolean keyEnterPressedInTextArea() {
		for (PropertyController adapter : form.getControllerList()) {
			if (adapter.getControlType().equals(VisualControlType.TEXTAREA) && form.getLastLostFocusAdapter().equals(adapter)) {
				Text text = ((Text) adapter.getValueControl());
				if (text.getText().endsWith("..")) { //$NON-NLS-1$
					String substring = text.getText().substring(0, text.getText().length() - 1);
					text.setText(substring);
					return false;
				}
				adapter.getValueControl().forceFocus();
				text.append("\r\n"); //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		if (objectList == null) {
			// if(explorer == null) {
			// explorer = FormRegistry.getEntityExplorer(form.g);
			// }
			form = new PlainForm(explorer);
			form.setReadOnly(readOnly);
			Composite composite = form.getPane(parent, SWT.FILL);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			form.addStatusListener(new IStatusListener() {

				public void statusChanged(PropertyStatus status) {
					// setMessage(status.getMessage());
					if (getButton(IDialogConstants.OK_ID) != null) {
						if (readOnly) {
							getButton(IDialogConstants.OK_ID).setEnabled(readOnly);
						} else {
							getButton(IDialogConstants.OK_ID).setEnabled(status.isValid());
						}
					}
				}
			});
			return composite;
		} else {
			SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
			sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
			createTableSection(sashForm);
			createFilterSection(sashForm);
			sashForm.setWeights(new int[] {
					65, 35
			});
			return sashForm;
		}
	}

	private void createFilterSection(Composite sashForm) {
		final ScrolledComposite scrollComposite = new ScrolledComposite(sashForm, SWT.V_SCROLL | SWT.H_SCROLL);
		scrollComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		final Composite client = new Composite(scrollComposite, SWT.BORDER);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.marginHeight = 5;
		client.setLayout(layout);
		// ToolBar bar = new ToolBar(client, SWT.FLAT);
		// ToolItem item = new ToolItem(bar, SWT.PUSH);
		// item.setImage(Resources.getInstance().getImage(SharedImages.ACTION_PLUS));
		// item.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// // objectForm.setInput(new ObjectDialog);
		// try {
		// // explorer.g
		// Field field = objectForm.getClass().getField("input");
		// objectForm.setInput((F) field.getType().getConstructor().newInstance());
		// ObjectDialog<?> dialog = new ObjectDialog<F>(getShell(), objectForm, DLG_IMG_TITLE_BANNER, DLG_IMG_MESSAGE_ERROR);
		// if (dialog.open() == Window.OK) {
		// }
		// } catch (Exception e1) {
		// e1.printStackTrace();
		// }
		// }
		// });
		// item = new ToolItem(bar, SWT.PUSH);
		// item.setImage(Resources.getInstance().getImage(SharedImages.ACTION_MINUS));
		// item.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// }
		// });
		// item = new ToolItem(bar, SWT.PUSH);
		// item.setImage(Resources.getInstance().getImage(SharedImages.ACTION_CUSTOMIZE));
		// item.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// }
		// });
		new Label(client, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label = new Label(client, SWT.NONE);
		label.setText(Messages.ObjectChooserField_FILTERS);
		Font boldFont = getBoldFont(label);
		label.setFont(boldFont);
		boldFont.dispose();
		new Label(client, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		filter = new Filter(client, chooser);
		filter.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		scrollComposite.setContent(client);
		scrollComposite.setExpandVertical(true);
		scrollComposite.setExpandHorizontal(true);
		scrollComposite.setMinSize(client.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private Font getBoldFont(Label label) {
		Font font = label.getFont();
		FontData[] data = font.getFontData();
		for (int i = 0; i < data.length; i++) {
			data[i].setStyle(SWT.BOLD);
		}
		Font boldFont = new Font(this.getShell().getDisplay(), data);
		return boldFont;
	}

	private void createTableSection(Composite sashForm) {
		chooser = new ObjectChooser(sashForm, (ObjectChooser.TITLE | ObjectChooser.TOOLKIT | ObjectChooser.BORDER | style), explorer, objectList);
		chooser.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		if ((style & SWT.CHECK) != 0) {
			for (TableItem item : chooser.getTableViewer().getTable().getItems()) {
				item.setChecked(true);
			}
		}
		if (selection != null) {
			chooser.setSelection(selection);
		}
		chooser.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				selection = (F) chooser.getSelection();
			}
		});
		chooser.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				selection = (F) chooser.getSelection();
				okPressed();
			}
		});
		chooser.getTableViewer().getTable().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				if (e.keyCode < 255) {
					if (chooser.getSelection() == null) {
						chooser.select(0);
					}
					if (chooser.getSelection() != null) {
						okPressed();
					}
				}
			}
		});
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public IForm<F> getObjectForm() {
		return objectForm;
	}

	public F getSelection() {
		return selection;
	}

	public void addMessage(String message) {
		this.message = message;
	}

	public void setSelection(F selection) {
		this.selection = selection;
	}

	public void setSize(Point point) {
		size = point;
	}

	public List getSelectionObjects() {
		return selectionObjects;
	}

	public void setStyle(int style) {
		this.style = style;
	}
}
