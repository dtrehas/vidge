package org.vidge.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.vidge.FormRegistry;
import org.vidge.PlainForm;
import org.vidge.Vidge;
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
import org.vidge.status.IStatusListener;
import org.vidge.status.PropertyStatus;
import org.vidge.test.entity.FormTestClass;
import org.vidge.test.entity.FormTestClass2;
import org.vidge.test.form.TestForm;
import org.vidge.test.form.TestForm2;
import org.vidge.test.form.TestForm3;
import org.vidge.util.FormContext;

public class View extends ViewPart {

	public static final String ID = "org.vidge.test.view";
	private TabFolder folder;

	public void createPartControl(Composite parent) {
		Vidge.registerForm(FormTestClass2.class, TestForm2.class);
		parent.setLayout(new FillLayout());
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		final FormTestClass input = new FormTestClass();
		final TreePanel treePanel = new TreePanel(new ObjectExplorer(input));
		treePanel.createViewer(sashForm);
		Composite composite = new Composite(sashForm, SWT.NONE);
		composite.setLayout(new GridLayout());
		folder = new TabFolder(composite, SWT.TOP);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		pageOfRawClass(input, treePanel);
		pageOfRegisteredFormWithEmbedded(input, treePanel);
		pageOformWithEmbeddedList(input, treePanel);
		pageOfDisabledForm();
		final List<FormTestClass> objectList = createSmallTestList(input);
		pageOfObjectChooser(objectList);
		pageOfNTable(objectList);
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
		item.setText("Vigets");
		item.setControl(createVigetsPanel(folder));
	}

	protected void pageOfVTableWithOuterPageListener(final List<FormTestClass> objectList) {
		TabItem item;
		item = new TabItem(folder, SWT.NONE);
		item.setText("VTable With Outer Page Listener ");
		final VTable vTable = new VTable(folder, FormTestClass.class, objectList, VTable.PAGES);
		item.setControl(vTable);
		// vTable.setTotalItemsCount(objectList.size());
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
	}

	protected void pageOfVTableWithPages(final List<FormTestClass> objectList) {
		TabItem item;
		item = new TabItem(folder, SWT.NONE);
		item.setText("VTable With Pages");
		item.setControl(new VTable(folder, FormTestClass.class, objectList, VTable.PAGES));
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

	protected List<FormTestClass> createSmallTestList(final FormTestClass input) {
		final List<FormTestClass> objectList = new ArrayList<FormTestClass>();
		objectList.add(input);
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

	protected void pageOfRegisteredFormWithEmbedded(final FormTestClass input, final TreePanel treePanel) {
		TabItem item;
		FormRegistry.registerContextForm(FormContext.TABLE.name(), FormTestClass.class, TestForm.class);
		item = new TabItem(folder, SWT.NONE);
		item.setText("  Registered form with embedded");
		PlainForm createForm = Vidge.createForm(new TestForm(input));
		createForm.addStatusListener(new IStatusListener() {

			@Override
			public void statusChanged(PropertyStatus status) {
				treePanel.refresh();
			}
		});
		item.setControl(createForm.getPane(folder, SWT.NONE));
	}

	protected void pageOfRawClass(final FormTestClass input, final TreePanel treePanel) {
		TabItem item = new TabItem(folder, SWT.NONE);
		item.setText("  Form raw class  ");
		PlainForm plainForm = new PlainForm(input);
		plainForm.addStatusListener(new IStatusListener() {

			@Override
			public void statusChanged(PropertyStatus status) {
				treePanel.refresh();
			}
		});
		item.setControl(plainForm.getPane(folder, SWT.NONE));
	}

	private Control createVigetsPanel(Composite composite) {
		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayout(new GridLayout(2, true));
		DateChooser dateChooser2 = new DateChooser(parent, DateChooser.DATE);
		dateChooser2.addCalendarListener(new ICalendarListener() {

			@Override
			public void dateChanged(CalendarEvent event) {
				System.out.println("------------sdf-----------" + event.getCalendar().getTime());
			}
		});
		new DateChooser(parent, DateChooser.TIME_DATE);
		new DateChooser(parent, DateChooser.TIME);
		final DateChooser dateChooser = new DateChooser(parent, DateChooser.DATE | DateChooser.TIME | DateChooser.SECONDS);
		new CalendarChooser(parent);
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Wizard");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Date date = dateChooser.getDate();
				System.out.println("---------------sdf--------  " + date);
				dateChooser.setDate(date);
				date = dateChooser.getDate();
				System.out.println("---------------sdf--------  " + date);
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
		button.setText("Wizard2");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ExtendedWizardDialog dialog = new ExtendedWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new Wizard() {

					{
						addPage(new WizardPage("page1") {

							private PropertyStatus status;
							{
								setTitle("Title");
							}

							@Override
							public void createControl(Composite parent) {
								final FormTestClass input = new FormTestClass();
								PlainForm createForm2 = Vidge.createForm(new TestForm3(input));
								Composite pane = createForm2.getPane(parent, SWT.NONE);
								pane.setLayoutData(new GridData(GridData.FILL_BOTH));
								createForm2.addStatusListener(new IStatusListener() {

									@Override
									public void statusChanged(PropertyStatus statusIn) {
										status = statusIn;
										setPageComplete(status.isValid());
									}
								});
								setControl(pane);
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
				}, SWT.VERTICAL);
				dialog.open();
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