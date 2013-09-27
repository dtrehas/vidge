package org.vidge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.vidge.controls.adapters.AbstractFieldAdapter;
import org.vidge.controls.adapters.FieldAdapterList;
import org.vidge.explorer.FormExplorer;
import org.vidge.explorer.ObjectExplorer;
import org.vidge.form.IForm;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.status.IStatusListener;
import org.vidge.status.PropertyStatus;
import org.vidge.util.StringUtil;

@SuppressWarnings({
		"unchecked", "rawtypes"
})
public class PlainForm {

	private List<PropertyController> controllerList = new ArrayList<PropertyController>();
	private final ListenerList listenerList = new ListenerList(ListenerList.IDENTITY);
	private boolean readOnly = false;
	private PropertyController lastController;
	private int currentFocusedIndex = 0;
	private IEntityExplorer entityExplorer;
	private Composite pane;
	private String helpMessage = Messages.PlainForm_0;
	private final boolean flushable;
	private PropertyStatus currentStatus = new PropertyStatus(true, helpMessage);

	public PlainForm(IForm<?> input) {
		this(new FormExplorer(input), false);
	}

	public PlainForm(Object input) {
		this(new ObjectExplorer(input), false);
	}

	public PlainForm(IForm<?> input, boolean flushable) {
		this(new FormExplorer(input), flushable);
	}

	public PlainForm(Object input, boolean flushable) {
		this(new ObjectExplorer(input), flushable);
	}

	public PlainForm(IEntityExplorer expIn) {
		this(expIn, false);
	}

	public PlainForm(IEntityExplorer expIn, boolean flushable) {
		entityExplorer = expIn;
		this.flushable = flushable;
		for (IPropertyExplorer explorer : entityExplorer.getPropertyList()) {
			explorer.setFlushable(flushable);
			PropertyController controller = new PropertyController(this, explorer);
			controllerList.add(controller);
		}
		entityExplorer.addPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				refreshView();
			}
		});
		Collections.sort(controllerList);
		inValidate(null);
	}

	public void refreshView() {
		for (PropertyController controller : controllerList) {
			AbstractFieldAdapter fieldAdapter = controller.getFieldAdapter();
			if (fieldAdapter != null)
				fieldAdapter.refreshControl();
		}
	}

	public List<PropertyController> getControllerList() {
		return controllerList;
	}

	public Composite getPane(Composite parent, int style, FormToolkit formToolkit) {
		ScrolledComposite scrolled = createPanel(parent, style, formToolkit);
		if (getControllerList().size() == 0) {
			Label label = new Label(pane, SWT.NONE);
			label.setText("No one VisualProperty!"); //$NON-NLS-1$
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		} else {
			for (PropertyController controller : getControllerList()) {
				try {
					if (controller.getExplorer().isEmbedded()) {
						final Control valueControl = controller.getValueControl(pane);
						if (formToolkit != null) {
							formToolkit.adapt(valueControl, !readOnly, !readOnly);
						}
						GridData layoutData = null;
						if (controller.getFieldAdapter().getClass().equals(FieldAdapterList.class)) {
							layoutData = new GridData(GridData.FILL_BOTH);
						} else {
							layoutData = new GridData(GridData.FILL_HORIZONTAL);
						}
						layoutData.horizontalSpan = 2;
						valueControl.setLayoutData(layoutData);
					} else {
						Control nameControl = controller.getNameControl(pane);
						if (formToolkit != null) {
							formToolkit.adapt(nameControl, false, false);
						}
						nameControl.setLayoutData(new GridData(GridData.BEGINNING));
						final Control valueControl = controller.getValueControl(pane);
						if (formToolkit != null) {
							formToolkit.adapt(valueControl, !readOnly, !readOnly);
						}
						if (valueControl.getLayoutData() == null) {
							valueControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
						}
					}
				} catch (RuntimeException e) {
					Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to create controls for property ", e);
				}
			}
		}
		pane.redraw();
		scrolled.setMinSize(pane.computeSize(300, SWT.DEFAULT));
		return scrolled;
	}

	private ScrolledComposite createComposite(Composite parent, int style) {
		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		Composite composite = new Composite(scrolledComposite, style);
		scrolledComposite.setContent(composite);
		return scrolledComposite;
	}

	private ScrolledComposite createPanel(Composite parent, int style, FormToolkit formToolkit) {
		ScrolledComposite scrolled = createComposite(parent, style);
		if (formToolkit != null) {
			formToolkit.adapt(scrolled);
		}
		scrolled.setLayoutData(new GridData(GridData.FILL_BOTH));
		pane = (Composite) scrolled.getContent();
		if (formToolkit != null) {
			formToolkit.adapt(pane);
		}
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 8;
		pane.setLayout(gridLayout);
		addPaintListener();
		return scrolled;
	}

	private void addPaintListener() {
		pane.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				for (int a = 0; a < controllerList.size(); a++) {
					PropertyController controller = controllerList.get(a);
					Control valueControl = controller.getNameControl();
					if (valueControl == null || !valueControl.isEnabled()) {
						continue;
					}
					if (!controller.isValid()) {
						try {
							Color color = e.display.getSystemColor(SWT.COLOR_RED);
							e.gc.setForeground(color);
							e.gc.drawRoundRectangle(0, valueControl.getLocation().y - 5, pane.getSize().x - 1, valueControl.getSize().y + 9, 8, 8);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					if (a == currentFocusedIndex) {
						try {
							Color color = e.display.getSystemColor(SWT.COLOR_BLUE);
							e.gc.setForeground(color);
							int width = valueControl.getLocation().x - controller.getNameControl().getLocation().x;
							if ((width > 2) && (((Label) controller.getNameControl()).getText().length() > 0)) {
								e.gc.drawRoundRectangle(2, controller.getNameControl().getLocation().y - 1, width, controller.getNameControl()
									.getSize().y + 3, 8, 8);
							}
						} catch (Exception e1) {
						}
					}
				}
			}
		});
	}

	public synchronized Composite getPane(Composite parent, int style) {
		return getPane(parent, style, null);
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setLastLostFocus(PropertyController adapter) {
		this.lastController = adapter;
		currentFocusedIndex = -1;
	}

	public PropertyController getLastLostFocusAdapter() {
		return lastController;
	}

	public void setFirstFocus() {
		currentFocusedIndex = 0;
		setNextFocus();
	}

	public void setNextFocus() {
		if (controllerList.size() > 0) {
			if (currentFocusedIndex == controllerList.size()) {
				currentFocusedIndex = 0;
			}
			boolean b = controllerList.get(currentFocusedIndex).setFocus();
			currentFocusedIndex++;
			while ((!b) && (currentFocusedIndex < controllerList.size())) {
				b = controllerList.get(currentFocusedIndex).setFocus();
				currentFocusedIndex++;
			}
		}
	}

	public void setFocusedIndex(PropertyController controller) {
		currentFocusedIndex = controllerList.indexOf(controller);
		pane.redraw();
	}

	public void addStatusListener(IStatusListener listener) {
		listenerList.add(listener);
	}

	public void removeStatusListener(IStatusListener listener) {
		listenerList.remove(listener);
	}

	public void inValidate(PropertyController owner) {
		StringBuilder builder = new StringBuilder();
		int count = 1;
		boolean valid = true;
		for (int a = 0; a < controllerList.size(); a++) {
			PropertyController controller = controllerList.get(a);
			AbstractFieldAdapter fieldAdapter = controller.getFieldAdapter();
			if (fieldAdapter != null && !controller.equals(owner) && fieldAdapter.isVolatile()) {
				fieldAdapter.inValidate();
				if (!fieldAdapter.isValid()) {
					builder.append(StringUtil.SP + count + StringUtil.DOTSP);
					builder.append(fieldAdapter.getHelpMessage());
					builder.append(StringUtil.NL);
					count++;
					valid = false;
				}
			}
		}
		if (builder.length() > 0) {
			helpMessage = builder.toString();
		} else {
			helpMessage = Messages.PlainForm_0;
		}
		currentStatus = new PropertyStatus(valid, helpMessage);
	}

	public PropertyStatus getCurrentStatus() {
		return currentStatus;
	}

	public void statusChanged(PropertyController owner) {
		inValidate(owner);
		for (Object listener : listenerList.getListeners()) {
			try {
				((IStatusListener) listener).statusChanged(currentStatus);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		pane.redraw();
	}

	public void flush() {
		for (int a = 0; a < controllerList.size(); a++) {
			controllerList.get(a).getExplorer().flush();
		}
	}

	public boolean isFlushable() {
		return flushable;
	}

	public void setEnabled(boolean enabled) {
		for (int a = 0; a < controllerList.size(); a++) {
			PropertyController controller = controllerList.get(a);
			AbstractFieldAdapter fieldAdapter = controller.getFieldAdapter();
			fieldAdapter.setEnabled(enabled);
		}
		pane.redraw();
	}

	public boolean isValid() {
		return currentStatus.isValid();
	}

	public void clear() {
		for (int a = 0; a < controllerList.size(); a++) {
			PropertyController controller = controllerList.get(a);
			AbstractFieldAdapter fieldAdapter = controller.getFieldAdapter();
			fieldAdapter.clear();
		}
	}
}
