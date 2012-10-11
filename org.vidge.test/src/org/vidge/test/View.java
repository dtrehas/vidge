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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.vidge.FormRegistry;
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
import org.vidge.status.IStatusListener;
import org.vidge.status.PropertyStatus;
import org.vidge.test.entity.FormTestClass;
import org.vidge.test.entity.FormTestClass2;
import org.vidge.test.form.TestForm;
import org.vidge.test.form.TestForm2;
import org.vidge.test.form.TestForm3;
import org.vidge.test.form.TestForm4;
import org.vidge.util.FormContext;

public class View extends ViewPart {

	public static final String ID = "org.vidge.test.view";
	private TabFolder folder;

	public void createPartControl(Composite parent) {
		Vidge.registerForm(FormTestClass2.class, TestForm2.class);
		// Vidge.registerForm(FormTestClass.class, TestForm.class);
		// Vidge.registerForm(FormTestClass.class, TestForm3.class);
		// Vidge.registerForm(FormTestClass.class, TestForm4.class);
		parent.setLayout(new FillLayout());
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		final FormTestClass input = new FormTestClass();
		final TreePanel treePanel = new TreePanel(new ObjectExplorer(input));
		treePanel.createViewer(sashForm);
		// Vidge.createTree(sashForm, input);
		Composite composite = new Composite(sashForm, SWT.NONE);
		composite.setLayout(new GridLayout());
		folder = new TabFolder(composite, SWT.TOP);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
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
		item = new TabItem(folder, SWT.NONE);
		item.setText("  Form3  ");
		PlainForm createForm2 = Vidge.createForm(new TestForm3(input));
		createForm2.addStatusListener(new IStatusListener() {

			@Override
			public void statusChanged(PropertyStatus status) {
				treePanel.refresh();
			}
		});
		item.setControl(createForm2.getPane(folder, SWT.NONE));
		item = new TabItem(folder, SWT.NONE);
		item.setText("  Form4  ");
		PlainForm createForm4 = Vidge.createForm(new TestForm4(input));
		createForm4.addStatusListener(new IStatusListener() {

			@Override
			public void statusChanged(PropertyStatus status) {
				treePanel.refresh();
			}
		});
		item.setControl(createForm4.getPane(folder, SWT.NONE));
		item = new TabItem(folder, SWT.NONE);
		item.setText("  Form5  ");
		PlainForm createForm5 = Vidge.createForm(new PreferencesForm());
		item.setControl(createForm5.getPane(folder, SWT.NONE));
		createForm5.setEnabled(false);
		item = new TabItem(folder, SWT.NONE);
		item.setText("  Chooser  ");
		final List<FormTestClass> objectList = new ArrayList<FormTestClass>();
		objectList.add(input);
		objectList.add(new FormTestClass("xxx", "ffffffffffffgggggggggg", 456, new Long(34L), new Date(), null));
		objectList.add(new FormTestClass("53xxx", "ffffffffffffgggggggfhfhggg", 456, new Long(34L), new Date(), null));
		objectList.add(new FormTestClass("678xxx", "ffffffffffffggggggfhggggg", 456, new Long(34L), new Date(), null));
		objectList.add(new FormTestClass("78888xxx", "ffffffffffffgggggggggg", 456, new Long(34L), new Date(), null));
		item.setControl(new ObjectChooser<FormTestClass>(folder, SWT.NONE, TestForm.class, objectList).getControl());
		//
		item = new TabItem(folder, SWT.NONE);
		item.setText("  NTable ");
		item.setControl(new NTable(folder, FormTestClass.class, objectList));
		//
		//
		item = new TabItem(folder, SWT.NONE);
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		Composite createComposite = toolkit.createComposite(folder);
		createComposite.setLayout(new FillLayout());
		item.setText("  VTable Toolkit ");
		TablePanel createTable = Vidge.createTable(createComposite, FormTestClass.class, objectList, VTable.PAGES);
		item.setControl(createComposite);
		//
		item = new TabItem(folder, SWT.NONE);
		item.setText("  VTable ");
		for (int a = 0; a < 10000; a++) {
			objectList.add(new FormTestClass("78888xxx", "ffffffffffffgggggggggg", a, new Long(34L), new Date(), null));
		}
		item.setControl(new VTable(folder, FormTestClass.class, objectList, VTable.PAGES));
		//
		item = new TabItem(folder, SWT.NONE);
		item.setText("  VTable Page2 ");
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
		//
		item = new TabItem(folder, SWT.NONE);
		item.setText("  Vigets ");
		item.setControl(createVigetsPanel(folder));
		// /
		sashForm.setWeights(new int[] {
				30, 70
		});
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