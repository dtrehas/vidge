package org.vidge.controls.chooser;

import java.util.Collection;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.langcom.ICompositeObject;
import org.langcom.locale.LocalizedString;
import org.vidge.FormRegistry;
import org.vidge.PropertyController;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.dialog.ObjectDialog;
import org.vidge.inface.IEntityExplorer;

public class PartListChooser<T> {

	protected Button addButton;
	protected Button removeButton;
	protected Button editButton;
	protected ObjectChooser<T> chooser;
	private final PropertyController controller;
	private ICompositeObject compositeObject;
	private IEntityExplorer entityExplorer;
	private Section section;

	public PartListChooser(Composite parent, PropertyController controller) {
		this.controller = controller;
		compositeObject = (ICompositeObject) controller.getExplorer().getValue();
		Class klass = controller.getExplorer().getPropertyClass();
		if (compositeObject == null) {
			try {
				compositeObject = (ICompositeObject) klass.getConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		entityExplorer = FormRegistry.getEntityExplorer(compositeObject.getPartClass());
		createContent(parent, klass);
	}

	public Composite getControl() {
		return section;
	}

	protected void createContent(Composite parent, Class<T> klass) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setTextClient(getClient(section));
		section.setText(controller.getExplorer().getLabel());
		toolkit.createCompositeSeparator(section);
		chooser = new ObjectChooser<T>(section, ObjectChooser.TOOLKIT | ObjectChooser.TITLE | ObjectChooser.NO_PAGES, entityExplorer, getObjectList());
		section.setClient(chooser.getControl());
		chooser.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				updateButtons();
			}
		});
		chooser.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		updateButtons();
		refresh();
	}

	public ICompositeObject getResult() {
		compositeObject.clear();
		StringBuilder builder = new StringBuilder();
		for (Object obj : chooser.getInput()) {
			builder.append(obj.toString());
		}
		((LocalizedString) compositeObject).setStringInt(builder.toString());
		return compositeObject;
	}

	private Control getClient(Composite parent) {
		ToolBar bar = new ToolBar(parent, SWT.FLAT);
		ToolItem item = new ToolItem(bar, SWT.PUSH);
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_PLUS));
		item.setToolTipText("Add Item");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				managePart();
			}
		});
		item = new ToolItem(bar, SWT.PUSH);
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_MINUS));
		item.setToolTipText("Remove Item");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeButtonAction();
			}
		});
		item = new ToolItem(bar, SWT.PUSH);
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_EDIT));
		item.setToolTipText("Edit Item");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				managePart();
			}
		});
		item = new ToolItem(bar, SWT.PUSH);
		item.setToolTipText("Refresh");
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_REFRESH));
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});
		return bar;
	}

	private void refresh() {
		chooser.setInput(getObjectList());
		chooser.refresh();
	}

	protected Collection<T> getObjectList() {
		return compositeObject.getParts();
	}

	private void managePart() {
		T input = chooser.getSelection();
		if (input == null) {
			try {
				input = (T) compositeObject.getPartClass().getConstructor().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		entityExplorer.explore(input);
		ObjectDialog dialog = new ObjectDialog(this.getControl().getShell(), entityExplorer, "Part", null);
		if (dialog.open() == Window.OK) {
			compositeObject.putPart(input);
			controller.getExplorer().setValue(compositeObject);
			refresh();
		}
	}

	protected void updateButtons() {
	}

	protected void removeButtonAction() {
		if (chooser.getSelection() != null) {
			compositeObject.removePart(chooser.getSelection());
			controller.getExplorer().setValue(compositeObject);
			refresh();
		}
	}
}
