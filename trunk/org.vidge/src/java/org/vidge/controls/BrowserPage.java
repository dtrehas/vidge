package org.vidge.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class BrowserPage {

	private FormToolkit toolkit;
	private Composite composite;
	private Browser browser;
	private int style = 0;
	public static final int START = SWT.APPLICATION_MODAL;
	public static final int NO_BAR = START << 1;

	public BrowserPage(Composite parent, String url, int styleIn) {
		style = styleIn;
		toolkit = new FormToolkit(parent.getDisplay());
		composite = toolkit.createComposite(parent);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginTop = 5;
		composite.setLayout(gridLayout);
		createContent();
		setUrl(url);
	}

	private void createContent() {
		if ((style & NO_BAR) != 0) {
			Composite separator = toolkit.createCompositeSeparator(composite);
			GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
			layoutData.heightHint = 2;
			separator.setLayoutData(layoutData);
		}
		browser = new Browser(composite, SWT.NONE);
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		browser.setJavascriptEnabled(true);
	}

	public Composite getControl() {
		return composite;
	}

	public void setUrl(String url) {
		if (url == null) {
			browser.setText("<html><body>URL is null</body></html>");
			return;
		}
		if (url.endsWith(".jpeg") || url.endsWith(".jpg") || url.endsWith(".gif") || url.endsWith(".png") || url.endsWith(".ico") || url.endsWith(".bmp") || url.endsWith(".htm")
			|| url.endsWith(".html") || url.endsWith(".xml") || url.endsWith(".txt") || url.endsWith(".rtf") || url.endsWith(".php")) {
			browser.setUrl(url);
			return;
		}
		url = "<html><body>This file should be opened by outher software :  <a href='" + url + "'>" + url + " </a></body></html>";
		browser.setText(url);
	}
}
