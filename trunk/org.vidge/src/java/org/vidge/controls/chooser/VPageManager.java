package org.vidge.controls.chooser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;

public class VPageManager {

	private static final int MINWIDTH = 70;
	private static final String COUNT2 = "Count: ";
	private static final int FIRST_ELEMENT = 1;
	private static final int COUNT_ELEMENT = 20;
	private Composite parentPane;
	private Label propsLabel;
	private Combo countCombo;
	private int from, count;

	public int getCount() {
		return count;
	}
	private int[] countValues = new int[] {
			20, 50, 100, 200, 500
	};
	private List<IPageListener> pageListeners = new ArrayList<IPageListener>();
	private int totalItemsCount = 0;
	private ToolBar toolBar;
	private Label countLabel;

	public VPageManager(final Composite parent, int style) {
		from = FIRST_ELEMENT;
		count = COUNT_ELEMENT;
		GridLayout layout = new GridLayout();
		layout.numColumns = 8;
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.horizontalSpacing = 5;
		parentPane = new Composite(parent, SWT.NONE);
		parentPane.setLayout(layout);
		new Label(parentPane, SWT.NONE).setText(COUNT2);
		countLabel = new Label(parentPane, SWT.RIGHT | SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = MINWIDTH;
		gridData.grabExcessHorizontalSpace = true;
		countLabel.setLayoutData(gridData);
		toolBar = new ToolBar(parentPane, SWT.FLAT);
		toolBar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.FIRST));
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getFirstPage();
			}
		});
		item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.PREVIOUS));
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getPreviousPage();
			}
		});
		Label label = new Label(parentPane, SWT.NONE);
		label.setText(Messages.PageManager_0);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		propsLabel = new Label(parentPane, SWT.RIGHT | SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = MINWIDTH;
		gridData.grabExcessHorizontalSpace = true;
		propsLabel.setLayoutData(gridData);
		toolBar = new ToolBar(parentPane, SWT.FLAT);
		toolBar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.NEXT));
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getNextPage();
			}
		});
		item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.LAST));
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getLastPage();
			}
		});
		countCombo = new Combo(parentPane, SWT.DROP_DOWN);
		countCombo.add("20"); //$NON-NLS-1$
		countCombo.add("50"); //$NON-NLS-1$
		countCombo.add("100"); //$NON-NLS-1$
		countCombo.add("200"); //$NON-NLS-1$
		countCombo.add("500"); //$NON-NLS-1$
		countCombo.select(0);
		countCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				try {
					count = Integer.valueOf(countCombo.getText());
					firePageChanged();
				} catch (Exception e1) {
				}
			}
		});
		countCombo.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (!Character.isDigit(e.character)) {
					e.doit = false;
				}
				if (e.character == '\u0008') {
					e.doit = true;
					countCombo.setText("");
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!Character.isDigit(e.character)) {
					e.doit = false;
				}
				if (e.character == '\u0008') {
					e.doit = true;
					countCombo.setText("");
				}
				if (e.doit) {
					try {
						count = Integer.valueOf(countCombo.getText());
						firePageChanged();
					} catch (Exception e1) {
					}
				}
			}
		});
		countCombo.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				count = countValues[countCombo.getSelectionIndex()];
				firePageChanged();
			}
		});
		countCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		showLabel();
		parentPane.setBackgroundMode(SWT.INHERIT_FORCE);
		parentPane.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				Color color = e.display.getSystemColor(SWT.COLOR_DARK_GRAY);
				e.gc.setForeground(color);
				e.gc.drawRectangle(1, 1, parentPane.getSize().x - 3, parentPane.getSize().y - 3);
			}
		});
	}

	public void firePageChanged() {
		for (IPageListener listener : pageListeners) {
			listener.pageChanged(from, count);
		}
	}

	public void addPageListener(IPageListener listener) {
		pageListeners.add(listener);
	}

	public void removePageListener(IPageListener listener) {
		pageListeners.remove(listener);
	}

	private void showLabel() {
		propsLabel.setText("  " + from + "  "); //$NON-NLS-1$//$NON-NLS-2$
	}

	public void getFirstPage() {
		from = FIRST_ELEMENT;
		showLabel();
		firePageChanged();
	}

	public void getNextPage() {
		try {
			if (totalItemsCount == 0) {
				return;
			}
			if ((from + count) <= totalItemsCount) {
				from = from + count;
			}
			showLabel();
			firePageChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getPreviousPage() {
		from = from - count;
		if (from < FIRST_ELEMENT) {
			from = FIRST_ELEMENT;
		}
		showLabel();
		firePageChanged();
	}

	public Control getControl() {
		return parentPane;
	}

	public void setPageItemsCount(int itemsCount) {
		if (itemsCount < count) {
			if (from > 1) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.PageManager_2, Messages.PageManager_3);
			}
		}
	}

	public int getFrom() {
		return from;
	}

	public int getCountOnPage() {
		return count;
	}

	public int getPageNum() {
		if (from > count) {
			return new BigDecimal(from).divide(new BigDecimal(count), BigDecimal.ROUND_UP).intValue();
		} else {
			return FIRST_ELEMENT;
		}
	}

	public void setItemsCount(int totalItemsCount) {
		this.totalItemsCount = totalItemsCount;
		from = FIRST_ELEMENT;
		countLabel.setText("   " + totalItemsCount + "   ");
		showLabel();
		parentPane.pack();
	}

	@SuppressWarnings("rawtypes")
	public List getNext(List input) {
		if ((input == null) || (input.size() == 0)) {
			return input;
		}
		if (totalItemsCount == 0) {
			from = FIRST_ELEMENT;
		}
		totalItemsCount = input.size();
		List result = null;
		if (input.size() <= count) {
			result = input;
		} else {
			int toIndex = from - 1 + count;
			if ((toIndex) >= input.size()) {
				toIndex = input.size();
			}
			if ((from - 1) > toIndex) {
				from = toIndex - count;
				if (from < 0) {
					from = 0;
				}
			}
			result = input.subList(from > 0 ? from - 1 : 0, toIndex);
		}
		showLabel();
		return result;
	}

	protected void getLastPage() {
		from = totalItemsCount - count + 1;
		if (from < FIRST_ELEMENT) {
			from = FIRST_ELEMENT;
		}
		try {
			showLabel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		firePageChanged();
	}

	public void setCount(int count) {
		this.count = count;
		firePageChanged();
	}

	public void setFrom(int indexOf) {
		from = indexOf;
	}
}
