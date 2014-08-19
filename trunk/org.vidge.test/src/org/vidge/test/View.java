package org.vidge.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.langcom.LangUtils;
import org.langcom.log.LookLog;
import org.vidge.PlainForm;
import org.vidge.Vidge;
import org.vidge.controls.TablePanel;
import org.vidge.controls.TreePanel;
import org.vidge.controls.calendar.CalendarChooser;
import org.vidge.controls.calendar.CalendarEvent;
import org.vidge.controls.calendar.DateChooser;
import org.vidge.controls.calendar.ICalendarListener;
import org.vidge.controls.chooser.IPageListener;
import org.vidge.controls.chooser.NTable;
import org.vidge.controls.chooser.ObjectChooser;
import org.vidge.controls.chooser.VTable;
import org.vidge.dialog.ExtendedWizardDialog;
import org.vidge.explorer.ObjectExplorer;
import org.vidge.form.IFormInputChangeListener;
import org.vidge.status.IStatusListener;
import org.vidge.status.PropertyStatus;
import org.vidge.test.entity.FormTestClass;
import org.vidge.test.entity.FormTestClass2;
import org.vidge.test.form.TestForm;
import org.vidge.test.form.TestForm2;
import org.vidge.test.form.TestForm3;
import org.vidge.util.FormContext;
import org.vidge.util.ValueAction;

public class View extends ViewPart {

	public static final String ID = "org.vidge.test.view";
	private TabFolder folder;

	public void createPartControl(Composite parent) {
		LangUtils.capitalize("ertert");
		LookLog.init();
		LookLog.info(this.getClass().getName(), "1111111111");
		Vidge.registerForm(FormTestClass2.class, TestForm2.class);
		Vidge.registerForm(FormTestClass.class, TestForm.class, FormContext.EDIT.name());
		Vidge.registerForm(FormTestClass.class, TestForm3.class, FormContext.TABLE.name());
		parent.setLayout(new FillLayout());
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		final FormTestClass input = new FormTestClass();
		final TreePanel treePanel = new TreePanel(new ObjectExplorer(input));
		List<FormTestClass> objectList = createSmallTestList();
		treePanel.createViewer(sashForm);
		treePanel.getTree().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				System.out.println("------------erefr---------  " + e.keyCode);
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		treePanel.getSection().setText("Tree structure of object");
		Composite composite = new Composite(sashForm, SWT.NONE);
		composite.setLayout(new GridLayout());
		folder = new TabFolder(composite, SWT.TOP);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		input.setTestList2(objectList);
		pageOfRawClass(input, treePanel);
		pageOfRegisteredForm(input, treePanel);
		pageOfRegisteredFormWithEmbedded(input, treePanel);
		pageOformWithEmbeddedList(input, treePanel);
		// pageOfDisabledForm();
		// pageOfObjectChooser(objectList);
		// pageOfNTable(objectList);
		// Monitor monitor = Display.getCurrent().getMonitors()[0];
		objectList = new ArrayList<FormTestClass>(objectList);
		pageOfTablePanel(objectList);
		createBigList(objectList);
		pageOfVTableWithPages(objectList);
		pageOfVTableWithOuterPageListener(objectList);
		pageOfVidgets();
		sashForm.setWeights(new int[] {
				20, 80
		});
	}

	protected void pageOfVidgets() {
		TabItem item;
		item = new TabItem(folder, SWT.NONE);
		item.setText("Additional Vigets");
		item.setControl(createVigetsPanel(folder));
	}

	protected void createBigList(final List<FormTestClass> objectList) {
		for (int a = 0; a < 10000; a++) {
			objectList.add(new FormTestClass("78888xxx", "ffffffffffffgggggggggg", a, new Long(34L), new Date(), null));
		}
	}

	protected void pageOfNTable(final List<FormTestClass> objectList) {
		TabItem item;
		item = new TabItem(folder, SWT.NONE);
		item.setText("NTable");
		item.setControl(new NTable(folder, FormTestClass.class, objectList));
	}

	protected void pageOfObjectChooser(final List<FormTestClass> objectList) {
		TabItem item;
		item = new TabItem(folder, SWT.NONE);
		item.setText("ObjectChooser");
		item.setControl(new ObjectChooser<FormTestClass>(folder, SWT.NONE, TestForm.class, objectList).getControl());
	}

	protected List<FormTestClass> createSmallTestList() {
		final List<FormTestClass> objectList = new ArrayList<FormTestClass>();
		objectList.add(new FormTestClass("xxx", "ffffffffffffgggggggggg", 456, new Long(34L), new Date(), null));
		objectList.add(new FormTestClass("53xxx", "ffffffffffffgggggggfhfhggg", 456, new Long(34L), new Date(), null));
		objectList.add(new FormTestClass("678xxx", "ffffffffffffggggggfhggggg", 456, new Long(34L), new Date(), null));
		objectList.add(new FormTestClass("78888xxx", "ffffffffffffgggggggggg", 456, new Long(34L), new Date(), null));
		return objectList;
	}

	protected void pageOfDisabledForm() {
		TabItem item;
		item = new TabItem(folder, SWT.NONE);
		item.setText("Disabled Form");
		PlainForm createForm5 = Vidge.createForm(new PreferencesForm());
		item.setControl(createForm5.getPane(folder, SWT.NONE));
		createForm5.setEnabled(false);
	}

	protected void pageOformWithEmbeddedList(final FormTestClass input, final TreePanel treePanel) {
		TabItem item;
		item = new TabItem(folder, SWT.NONE);
		item.setText("Form with Embedded List");
		PlainForm createForm2 = Vidge.createForm(new TestForm3(input));
		createForm2.addStatusListener(new IStatusListener() {

			@Override
			public void statusChanged(PropertyStatus status) {
				treePanel.refresh();
			}
		});
		item.setControl(createForm2.getPane(folder, SWT.NONE));
	}

	protected void pageOfRawClass(final FormTestClass input, final TreePanel treePanel) {
		TabItem item = new TabItem(folder, SWT.NONE);
		item.setText("  Form raw class  ");
		item.setToolTipText("This form is linked with the tree structure");
		PlainForm plainForm = new PlainForm(input);
		plainForm.addStatusListener(new IStatusListener() {

			@Override
			public void statusChanged(PropertyStatus status) {
				treePanel.refresh();
			}
		});
		SashForm sashForm = new SashForm(folder, SWT.VERTICAL);
		plainForm.getPane(sashForm, SWT.NONE);
		Label label = new Label(sashForm, SWT.CENTER);
		label.setImage(Activator.getDefault().getImageDescriptor("/icons/Capture1.JPG").createImage());
		sashForm.setWeights(new int[] {
				70, 30
		});
		item.setControl(sashForm);
	}

	protected void pageOfRegisteredForm(final FormTestClass input, final TreePanel treePanel) {
		TabItem item = new TabItem(folder, SWT.NONE);
		item.setText("  Registered form");
		PlainForm createForm = Vidge.createForm(new TestForm3(input));
		createForm.addStatusListener(new IStatusListener() {

			@Override
			public void statusChanged(PropertyStatus status) {
				treePanel.refresh();
			}
		});
		item.setControl(createForm.getPane(folder, SWT.NONE));
		// SashForm sashForm = new SashForm(folder, SWT.VERTICAL);
		// createForm.getPane(sashForm, SWT.NONE);
		// Label label = new Label(sashForm, SWT.CENTER);
		// label.setImage(Activator.getDefault().getImageDescriptor("/icons/Capture2.JPG").createImage());
		// sashForm.setWeights(new int[] {
		// 60, 40
		// });
		// item.setControl(sashForm);
	}

	protected void pageOfRegisteredFormWithEmbedded(final FormTestClass input, final TreePanel treePanel) {
		TabItem item = new TabItem(folder, SWT.NONE);
		item.setText("  Registered form with embedded");
		item.setToolTipText("This form is linked with the tree structure also");
		PlainForm createForm = Vidge.createForm(new TestForm(input));
		createForm.addStatusListener(new IStatusListener() {

			@Override
			public void statusChanged(PropertyStatus status) {
				treePanel.refresh();
			}
		});
		// item.setControl(createForm.getPane(folder, SWT.NONE));
		SashForm sashForm = new SashForm(folder, SWT.VERTICAL);
		createForm.getPane(sashForm, SWT.NONE);
		Label label = new Label(sashForm, SWT.CENTER);
		label.setImage(Activator.getDefault().getImageDescriptor("/icons/Capture2.JPG").createImage());
		sashForm.setWeights(new int[] {
				60, 40
		});
		item.setControl(sashForm);
	}

	protected void pageOfTablePanel(final List<FormTestClass> objectList) {
		TabItem item;
		item = new TabItem(folder, SWT.NONE);
		item.setText("Table Panel check");
		final List<FormTestClass> list = new ArrayList<FormTestClass>(objectList);
		SashForm sashForm = new SashForm(folder, SWT.VERTICAL);
		TablePanel<FormTestClass> tablePanel = new TablePanel<FormTestClass>(sashForm, FormTestClass.class, list, VTable.NO_NUM_COLUMN | SWT.CHECK);
		tablePanel.setActions(new Action() {

			{
				setText("Edit My");
				setImageDescriptor(Activator.getImageDescriptor("/icons/alt_window_16.gif"));
			}

			@Override
			public void run() {
				// todo
			}
		});
		tablePanel.addContextMenu(new Action() {

			{
				setText("Edit My");
				setImageDescriptor(Activator.getImageDescriptor("/icons/alt_window_16.gif"));
			}

			@Override
			public void run() {
				// todo
			}
		}, 0);
		tablePanel.addFormChangeListener(new IFormInputChangeListener() {

			@Override
			public Object doInputChanged(Object value, ValueAction action, String attribute) {
				switch (action) {
					case SAVE:
						list.add((FormTestClass) value);
						break;
					case DELETE:
						list.remove((FormTestClass) value);
						break;
				}
				return null;
			}

			@Override
			public boolean allowParts() {
				return false;
			}
		});
		Label label = new Label(sashForm, SWT.CENTER);
		label.setImage(Activator.getDefault().getImageDescriptor("/icons/Capture3.JPG").createImage());
		sashForm.setWeights(new int[] {
				60, 40
		});
		item.setControl(sashForm);
	}

	protected void pageOfVTableWithPages(final List<FormTestClass> objectList) {
		TabItem item;
		item = new TabItem(folder, SWT.NONE);
		item.setText("VTable With Pages");
		SashForm sashForm = new SashForm(folder, SWT.VERTICAL);
		VTable control = new VTable(sashForm, FormTestClass.class, objectList, VTable.PAGES);
		Label label = new Label(sashForm, SWT.CENTER);
		label.setImage(Activator.getDefault().getImageDescriptor("/icons/Capture4.JPG").createImage());
		sashForm.setWeights(new int[] {
				85, 15
		});
		item.setControl(sashForm);
	}

	protected void pageOfVTableWithOuterPageListener(final List<FormTestClass> objectList) {
		TabItem item;
		item = new TabItem(folder, SWT.NONE);
		item.setText("VTable With Outer Page Listener ");
		SashForm sashForm = new SashForm(folder, SWT.VERTICAL);
		final VTable vTable = new VTable(sashForm, FormTestClass.class, objectList, VTable.PAGES);
		vTable.setPageListener(new IPageListener() {

			@Override
			public void pageChanged(int from, int count) {
				int toIndex = from + count;
				if (toIndex >= objectList.size()) {
					toIndex = objectList.size() - 1;
				}
				List<FormTestClass> subList = objectList.subList(from, toIndex);
				vTable.setPageInput(subList);
			}
		});
		Label label = new Label(sashForm, SWT.CENTER);
		label.setImage(Activator.getDefault().getImageDescriptor("/icons/Capture5.JPG").createImage());
		sashForm.setWeights(new int[] {
				60, 40
		});
		item.setControl(sashForm);
	}

	private Control createVigetsPanel(Composite composite) {
		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayout(new GridLayout(2, true));
		new Label(parent, SWT.NONE).setText("Date chooser-date");
		DateChooser dateChooser2 = new DateChooser(parent, DateChooser.DATE);
		dateChooser2.addCalendarListener(new ICalendarListener() {

			@Override
			public void dateChanged(CalendarEvent event) {
			}
		});
		new Label(parent, SWT.NONE).setText("DateChooser time-date");
		new DateChooser(parent, DateChooser.TIME_DATE);
		new Label(parent, SWT.NONE).setText("DateChooserr time");
		new DateChooser(parent, DateChooser.TIME);
		new Label(parent, SWT.NONE).setText("DateChooser date-time-seconds");
		final DateChooser dateChooser = new DateChooser(parent, DateChooser.DATE | DateChooser.TIME | DateChooser.SECONDS);
		new Label(parent, SWT.NONE).setText("CalendarChooser");
		new CalendarChooser(parent);
		new Label(parent, SWT.NONE).setText("Wizard with extended wizard dialog");
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Wizard");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Date date = dateChooser.getDate();
				dateChooser.setDate(date);
				date = dateChooser.getDate();
				ExtendedWizardDialog dialog = new ExtendedWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new Wizard() {

					{
						addPage(new WizardPage("page1") {

							private PropertyStatus status;

							@Override
							public void createControl(Composite parent) {
								final FormTestClass input = new FormTestClass();
								PlainForm createForm2 = Vidge.createForm(new TestForm3(input));
								setControl(createForm2.getPane(parent, SWT.NONE));
								createForm2.addStatusListener(new IStatusListener() {

									@Override
									public void statusChanged(PropertyStatus statusIn) {
										status = statusIn;
										setPageComplete(status.isValid());
									}
								});
							}

							@Override
							public String getErrorMessage() {
								if (status == null) {
									return super.getErrorMessage();
								}
								return status.getMessage();
							}
						});
						addPage(new WizardPage("page2") {

							@Override
							public void createControl(Composite parent) {
								final FormTestClass input = new FormTestClass();
								PlainForm createForm2 = Vidge.createForm(new TestForm3(input));
								setControl(createForm2.getPane(parent, SWT.NONE));
							}
						});
						addPage(new WizardPage("page3") {

							@Override
							public void createControl(Composite parent) {
								final FormTestClass input = new FormTestClass();
								PlainForm createForm2 = Vidge.createForm(new TestForm3(input));
								setControl(createForm2.getPane(parent, SWT.NONE));
							}
						});
					}

					@Override
					public boolean performFinish() {
						return true;
					}
				});
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		button = new Button(parent, SWT.PUSH);
		button.setText("Wizard");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				//
				for (int a = 0; a < 500; a++) {
					LookLog.info(this.getClass().getName(), "Looog " + a);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return parent;
	}

	public void setFocus() {
		folder.setFocus();
	}
}