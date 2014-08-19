package org.vidge.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class ActionBar {

	public static ToolBar buildBar(Composite parent, int style, Action... actionArray) {
		ToolBar bar = new ToolBar(parent, style);
		for (final Action action : actionArray) {
			ToolItem item = new ToolItem(bar, SWT.PUSH);
			item.setText(action.getText());
			item.setToolTipText(action.getToolTipText());
			item.setImage(action.getImageDescriptor().createImage());
			item.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					action.run();
				}
			});
		}
		return bar;
	}
}
