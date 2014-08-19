package org.vidge.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.langcom.buffer.CircularBuffer;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.util.StringUtil;

public abstract class VCombo extends Composite {

	public static final String SEMICOLON = StringUtil.SEMICOLON;
	private CLabel lbl;
	private CircularBuffer<Object> buff = new CircularBuffer<Object>(8);
	private int selected = -1;

	public abstract void setHistory(String str);

	public abstract String getHistory();

	public abstract void strSelected(String str, int numberOfStr);

	public VCombo(Composite parent, int style) {
		super(parent, style);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);
		lbl = new CLabel(this, SWT.NONE);
		lbl.setLayoutData(new GridData(GridData.FILL_BOTH));
		String history = getHistory();
		if (history.length() > 0) {
			String[] split = history.split(SEMICOLON);
			for (String string : split) {
				if (string != null) {
					buff.add(string);
				}
			}
		}
		addToolBar(this);
	}

	public VCombo(Composite parent, String header, int style) {
		super(parent, style);
		setLayout(new FillLayout());
		Group group = new Group(this, SWT.SHADOW_NONE);
		group.setText(header);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		group.setLayout(layout);
		lbl = new CLabel(group, SWT.NONE);
		lbl.setLayoutData(new GridData(GridData.FILL_BOTH));
		String history = getHistory();
		if (history.length() > 0) {
			String[] split = history.split(SEMICOLON);
			for (String string : split) {
				if (string != null) {
					buff.add(string);
				}
			}
		}
		addToolBar(group);
	}

	private void addToolBar(Composite parent) {
		ToolBar bar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT | SWT.HORIZONTAL);
		bar.setLayoutData(new GridData(GridData.END));
		ToolItem item = new ToolItem(bar, SWT.PUSH);
		item.setToolTipText("Select item");
		item.setImage(getButtonImage());
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				final Menu menu = new Menu(VCombo.this.getShell(), SWT.POP_UP | SWT.FLAT);
				for (Object string : buff.getBuffer()) {
					if (string != null) {
						final MenuItem item1 = new MenuItem(menu, SWT.PUSH);
						item1.setText(string.toString());
						item1.addSelectionListener(new SelectionListener() {

							@Override
							public void widgetSelected(SelectionEvent e) {
								selected = menu.indexOf(item1);
								lbl.setText(item1.getText());
								strSelected(item1.getText(), selected);
							}

							@Override
							public void widgetDefaultSelected(SelectionEvent e) {
							}
						});
					}
				}
				createDefaultMenu(menu);
				Rectangle rect = lbl.getBounds();
				Point mLoc = new Point(rect.x - 5, rect.y + rect.height - 15);
				menu.setLocation(getDisplay().map(lbl, null, mLoc));
				menu.setVisible(true);
			}
		});
	}

	protected void createDefaultMenu(final Menu menu) {
		final MenuItem item1 = new MenuItem(menu, SWT.PUSH);
		item1.setText("New Location");
		item1.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getNewLocation();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	protected Image getButtonImage() {
		return VidgeResources.getInstance().getImage(SharedImages.FOLDER);
	}

	private void getNewLocation() {
		DirectoryDialog dialog = new DirectoryDialog(getShell());
		String path = dialog.open();
		if (path != null) {
			buff.add(path);
			selected = 0;
			StringBuilder builder = new StringBuilder();
			Object[] buffer = buff.getBuffer();
			for (Object string : buffer) {
				if (string != null) {
					builder.append(string);
					builder.append(SEMICOLON);
				}
			}
			setSelection(selected);
			setHistory(builder.toString());
		}
	}

	public void setInfo(String info) {
		lbl.setText(info);
	}

	public void setSelection(int index) {
		if (index != selected) {
			Object head = buff.getHead();
			if (head != null) {
				lbl.setText(head.toString());
			}
		}
	}
}
