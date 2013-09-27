package org.vidge;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.vidge.controls.TablePanel;
import org.vidge.controls.TreePanel;
import org.vidge.dialog.SingleObjectDialog;
import org.vidge.dialog.VidgeErrorDialog;
import org.vidge.explorer.FormExplorer;
import org.vidge.explorer.ObjectExplorer;
import org.vidge.form.IForm;
import org.vidge.inface.IRegistryRequestListener;

@SuppressWarnings({
		"unchecked", "rawtypes"
})
public class Vidge {

	public static final int ALLACTIONS = SWT.APPLICATION_MODAL;
	public static final int REFRESH = ALLACTIONS << 1;
	public static final int CREATE = ALLACTIONS << 2;
	public static final int EDIT = ALLACTIONS << 3;
	public static final int REMOVE = ALLACTIONS << 4;
	public static final int NO_ACTIONS = ALLACTIONS << 5;
	public static final int NO_VIRTUAL = ALLACTIONS << 6;
	public static final int NO_TOOLKIT = ALLACTIONS << 7;
	public static boolean NO_CLEAR_ACTIONS = false;

	private Vidge() {
	}

	public static PlainForm createForm(IForm<?> input) {
		return new PlainForm(new FormExplorer(input));
	}

	public static PlainForm createForm(Object input) {
		return new PlainForm(new ObjectExplorer(input));
	}

	public static Composite createForm(Composite parent, IForm<?> input, int style) {
		return new PlainForm(new FormExplorer(input)).getPane(parent, style);
	}

	public static Composite createForm(Composite parent, Object input, int style) {
		return new PlainForm(new ObjectExplorer(input)).getPane(parent, style);
	}

	public static Composite createForm(Composite parent, IForm<?> input, FormToolkit formToolkit) {
		return new PlainForm(new FormExplorer(input)).getPane(parent, SWT.NONE, formToolkit);
	}

	public static Composite createForm(Composite parent, Object input, FormToolkit formToolkit) {
		return new PlainForm(new ObjectExplorer(input)).getPane(parent, SWT.NONE, formToolkit);
	}

	public static Composite createForm(Composite parent, IForm<?> input, int style, FormToolkit formToolkit) {
		return new PlainForm(new FormExplorer(input)).getPane(parent, style, formToolkit);
	}

	public static Composite createForm(Composite parent, Object input, int style, FormToolkit formToolkit) {
		return new PlainForm(new ObjectExplorer(input)).getPane(parent, style, formToolkit);
	}

	public static TreePanel createTree(Composite parent, Object input) {
		if (input == null) {
			throw new VidgeException("*** The  root of tree is  not specified");
		}
		TreePanel treePanel = new TreePanel(input);
		treePanel.createViewer(parent);
		return treePanel;
	}

	public static TreePanel createTree(Composite parent, Object input, int style) {
		if (input == null) {
			throw new VidgeException("*** The  root of tree is  not specified");
		}
		TreePanel treePanel = new TreePanel(input, style);
		treePanel.createViewer(parent);
		return treePanel;
	}

	public static TablePanel createTable(Composite parent, Class<?> objClass, List<?> objectList) {
		TablePanel tablePanel = new TablePanel(objClass, objectList);
		tablePanel.createViewer(parent);
		return tablePanel;
	}

	public static TablePanel createTable(Composite parent, Class<?> objClass, List<?> objectList, int style) {
		TablePanel tablePanel = new TablePanel(objClass, objectList, style);
		tablePanel.createViewer(parent);
		return tablePanel;
	}

	public static <T> TablePanel<T> createTable(Composite parent, Class<T> objClass, Class<?> formClass, List<T> objectList, int style) {
		TablePanel tablePanel = new TablePanel(objClass, objectList, formClass, style);
		tablePanel.createViewer(parent);
		return tablePanel;
	}

	public static <T> TablePanel<T> createTable(Composite parent, Class<T> objClass, Class<?> formClass, List<T> objectList) {
		TablePanel tablePanel = new TablePanel(objClass, objectList, formClass, Vidge.NO_TOOLKIT);
		tablePanel.createViewer(parent);
		return tablePanel;
	}

	public static TablePanel createRTable(Composite parent, Class<?> objClass, Class<?> formClass, List<?> objectList, int style) {
		// RTablePanel tablePanel = new RTablePanel(objClass, objectList, formClass, style);
		TablePanel tablePanel = new TablePanel(objClass, objectList, formClass, style | Vidge.NO_VIRTUAL | Vidge.NO_ACTIONS | Vidge.NO_TOOLKIT);
		tablePanel.createViewer(parent);
		return tablePanel;
	}

	public static void setDefaultContext(String context) {
		FormRegistry.setDefaultContext(context);
	}

	public static <T> void registerForm(Class<? extends T> klass, Class<? extends IForm<? extends T>> form) {
		FormRegistry.registerForm(klass, form);
	}

	public static <T> void registerForm(Class<? extends T> klass, Class<? extends IForm<? extends T>> form, String context) {
		FormRegistry.registerContextForm(context, klass, form);
	}

	public static <T> void registerContextForm(String context, Class<? extends T> klass, Class<? extends IForm<? extends T>> form) {
		FormRegistry.registerContextForm(context, klass, form);
	}

	public static boolean addRegistryListener(IRegistryRequestListener e) {
		return FormRegistry.addListener(e);
	}

	public static Image getImage(SharedImages image) {
		return Activator.getDefault().getImageRegistry().get(image.name());
	}

	public static Image getImage(String key) {
		return Activator.getDefault().getImageRegistry().get(key);
	}

	public ImageDescriptor getImageDescriptor(SharedImages image) {
		return Activator.getDefault().getImageRegistry().getDescriptor(image.name());
	}

	public ImageDescriptor getImageDescriptor(String key) {
		return Activator.getDefault().getImageRegistry().getDescriptor(key);
	}

	public static <T> SingleObjectDialog<T> getObjectDialog(IForm<T> form, String title) {
		return new SingleObjectDialog(new FormExplorer(form), title);
	}

	public static void showError(Shell parentShell, String title, String mesg, Throwable err) {
		new VidgeErrorDialog(parentShell, title, mesg, err).open();
	}
}
