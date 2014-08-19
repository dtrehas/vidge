package org.vidge.dialog;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.controls.BrowserPage;
import org.vidge.controls.editor.Messages;
import org.vidge.inface.IObjectDialog;

public class RawResourceDialog extends FrameDialog implements IObjectDialog<byte[]> {

	private BrowserPage browserPage;
	private String url;
	public static int GET_FILE = IDialogConstants.CLIENT_ID << 2;
	private int styleIn = 0;
	private byte[] data;
	private String open;

	public RawResourceDialog(Shell parentShell, String title, byte[] data, int style) {
		super(parentShell, title);
		this.data = data;
		styleIn = style;
		setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		if (data != null) {
			try {
				File tempFile = File.createTempFile("temp_image", ".jpg"); //$NON-NLS-1$ //$NON-NLS-2$
				FileUtils.writeByteArrayToFile(tempFile, data);
				url = tempFile.getPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Composite createContent(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(2, false));
		browserPage = new BrowserPage(composite, url, SWT.BORDER);
		browserPage.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		if ((styleIn & GET_FILE) != 0) {
			ToolBar bar = new ToolBar(composite, SWT.SHADOW_OUT);
			bar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_BEGINNING));
			ToolItem item = new ToolItem(bar, SWT.PUSH);
			item.setImage(VidgeResources.getInstance().getImage(SharedImages.OPEN));
			item.setToolTipText(Messages.RawResourceDialog_Select_file);
			item.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					openFileDialog();
				}
			});
		}
		return composite;
	}

	@Override
	public int open() {
		super.open();
		return open == null ? Window.CANCEL : Window.OK;
	}

	@Override
	public byte[] getSelection() {
		return data;
	}

	private void openFileDialog() {
		FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		open = dialog.open();
		if (open != null) {
			url = open;
			try {
				data = FileUtils.readFileToByteArray(new File(url));
			} catch (IOException e) {
				e.printStackTrace();
			}
			browserPage.setUrl(url);
		}
	}

	private void openFileDialog1() {
		FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		open = dialog.open();
		if (open != null) {
			url = open;
			browserPage.setUrl(url);
			ImageData imageData = new ImageData(url);
			int maxX = 50;
			int maxY = 50;
			try {
				if ((imageData.width > maxX) || (imageData.height > maxY)) {
					MessageDialog.openWarning(this.getShell(), Messages.ImageEditor_1, Messages.ImageEditor_2);
					imageData = imageData.scaledTo(maxX, maxY);
					ImageLoader imageLoader = new ImageLoader();
					imageLoader.load(url);
					imageLoader.data[0] = imageData;
					File temp = File.createTempFile("tmp_", null); //$NON-NLS-1$
					imageLoader.save(temp.getAbsolutePath(), SWT.IMAGE_JPEG);
					data = FileUtils.readFileToByteArray(temp);
					browserPage.setUrl(temp.getPath());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
