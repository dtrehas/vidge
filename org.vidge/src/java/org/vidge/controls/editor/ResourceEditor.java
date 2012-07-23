package org.vidge.controls.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.vidge.PropertyController;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.controls.CustomButton;
import org.vidge.dialog.RawResourceDialog;

public class ResourceEditor extends Composite {

	private Image image;
	private Point preferredSize = new Point(25, 25);
	private Label label;
	public static int ALLOW_KEYS = SWT.KEYCODE_BIT + 48;
	protected byte[] selection;
	protected PropertyController controller;
	private CustomButton setButton;
	private CustomButton clearButton;

	public ResourceEditor(Composite parent, int style, PropertyController controller) {
		super(parent, SWT.BORDER);
		this.controller = controller;
		preferredSize.x = controller.getExplorer().getVisualAreaWidth();
		preferredSize.y = controller.getExplorer().getVisualAreaHeight();
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		this.setLayout(layout);
		label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		makeButton();
		makeClearButton();
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		setLayoutData(layoutData);
		label.addMouseListener(new MouseListener() {

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
		label.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				showDialog();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		createDefaultImage();
	}

	protected void makeButton() {
		setButton = new CustomButton(this, VidgeResources.getInstance().getImage(SharedImages.ACTION_PLUS2), "Select");
		setButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		setButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				showDialog();
			}
		});
	}

	protected void showDialog() {
		if (this.getEnabled()) {
			RawResourceDialog dialog = new RawResourceDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Resource", (byte[]) selection, RawResourceDialog.GET_FILE);
			if (dialog.open() == Window.OK) {
				setSelection((dialog.getSelection()));
			}
		}
	}

	public byte[] getSelection() {
		return selection;
	}

	public void setSelection(byte[] dataIn) {
		setSelectionInt(dataIn);
		controller.getExplorer().setValue(selection);
	}

	public void setSelectionInt(byte[] dataIn) {
		this.selection = dataIn;
		if (selection != null && selection.length > 0) {
			if (image != null) {
				image.dispose();
			}
			try {
				image = new Image(this.getDisplay(), new ImageData(new ByteArrayInputStream(selection)));
			} catch (Exception e) {
				e.printStackTrace();
				createDefaultImage();
			}
		} else {
			createDefaultImage();
		}
		try {
			ImageData imageData = image.getImageData().scaledTo(preferredSize.x, preferredSize.y);
			image = new Image(this.getDisplay(), imageData);
		} catch (Exception e) {
			e.printStackTrace();
			createDefaultImage();
		}
		label.setImage(image);
	}

	private void createDefaultImage() {
		RGB gray = new RGB(125, 125, 125);
		PaletteData dataPalette = new PaletteData(new RGB[] {
				gray, gray, gray
		});
		image = new Image(this.getDisplay(), new ImageData(preferredSize.x, preferredSize.y, 8, dataPalette));
	}

	private byte[] fitData(byte[] array) {
		System.out.println("-------------before--------------" + array.length);
		if (array != null) {
			ImageData imageData = new ImageData(new ByteArrayInputStream(array));
			if (imageData.width > controller.getExplorer().getImageWidth()) {
				imageData = imageData.scaledTo(controller.getExplorer().getImageWidth(), imageData.height);
			}
			if (imageData.height > controller.getExplorer().getImageHeight()) {
				imageData = imageData.scaledTo(imageData.width, controller.getExplorer().getImageHeight());
			}
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] {
				imageData
			};
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			loader.save(outputStream, SWT.IMAGE_PNG);
			byte[] byteArray = outputStream.toByteArray();
			System.out.println("-------------after--------------" + byteArray.length);
			return byteArray;
		}
		return array;
	}

	private void makeClearButton() {
		clearButton = new CustomButton(this, VidgeResources.getInstance().getImage(SharedImages.DELETE), "Delete");
		clearButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		clearButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setSelection(null);
			}
		});
	}

	@Override
	protected void finalize() throws Throwable {
		if (image != null) {
			image.dispose();
		}
		super.finalize();
	}

	public void setText(String string) {
		label.setText(string);
	}
}